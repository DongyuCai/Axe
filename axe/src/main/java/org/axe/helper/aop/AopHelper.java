package org.axe.helper.aop;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.axe.annotation.aop.Aspect;
import org.axe.helper.ioc.BeanHelper;
import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.base.Helper;
import org.axe.interface_.proxy.Proxy;
import org.axe.proxy.base.ProxyManger;
import org.axe.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 方法拦截助手类
 * Created by CaiDongYu on 2016/4/14.
 */
public final class AopHelper implements Helper{
    private static final Logger LOGGER = LoggerFactory.getLogger(AopHelper.class);

    @Override
    public void init() {
    	synchronized (this) {
    		try {
                Map<Class<?>,Set<Class<?>>> proxyMap = createProxyMap();
                Map<Class<?>,List<Proxy>> targetMap = createTargetMap(proxyMap);
                for (Map.Entry<Class<?>,List<Proxy>> targetEntry:targetMap.entrySet()){
                    Class<?> targetClass = targetEntry.getKey();
                    List<Proxy> proxyList = targetEntry.getValue();
                    //真正的创建目标类的代理对象
                    Object proxy = ProxyManger.createProxy(targetClass,proxyList);
                    BeanHelper.setBean(targetClass,proxy);
                }
            } catch (Exception e){
                LOGGER.error("aop failure",e);
            }
		}
    }
    
    /**
     * 返回代理类与目标类集合的映射
     * 比如代理类A，代理了B、C、D三个类
     * 通过A类上的注解Aspect指定一个目标注解比如叫M
     * B、C、D类都拥有这个注解M，就可以了。
     */
    private static Map<Class<?>,Set<Class<?>>> createProxyMap() throws Exception {
        Map<Class<?>,Set<Class<?>>> proxyMap = new HashMap<>();
        //找到切面抽象类的实现类，就是说，都是切面类
        Set<Class<?>> proxyClassSet = ClassHelper.getClassSetBySuper(Proxy.class);
        for(Class<?> proxyClass : proxyClassSet){
            //继承了 AspectProxy 不算，还得是有指定了切面目标类
            if(proxyClass.isAnnotationPresent(Aspect.class)){
                Aspect aspect = proxyClass.getAnnotation(Aspect.class);
                //TODO:目前AOP实现，还只是针对有目标注解的类做切面，不能细化到方法，这样一旦拦截，会拦截所有方法
                //比如事务切面，实际上是切了整个Server类所有方法，但是在切面加强的时候会判断，判断这个方法开不开事务。
                Set<Class<?>> targetClassSet = createTargetClassSet(aspect);
                proxyMap.put(proxyClass,targetClassSet);
            }
        }
        return proxyMap;
    }

    private static Set<Class<?>> createTargetClassSet(Aspect aspect) throws Exception{
        Set<Class<?>> targetClassSet = new HashSet<>();
        //切面目标的指定，依然是通过注解来匹配
        Class<? extends Annotation>[] annotations = aspect.value();
        //排除Aspect注解本身，因为Aspect注解就是用来指定目标切面注解的
        if(annotations != null){
            for(Class<? extends Annotation> annotation:annotations){
                if(!annotation.equals(Aspect.class)){
                    //取出含有目标注解的类，作为目标类
                    targetClassSet.addAll(ClassHelper.getClassSetByAnnotation(annotation));
                }
            }
        }
        return targetClassSet;
    }

    /**
     * 将proxy -> targetClassSet 映射关系逆转
     * 变成 targetClass -> proxyList 关系
     * 也就是说现在是 一个类，对应有多少个proxy去处理
     */
    private static Map<Class<?>,List<Proxy>> createTargetMap(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception{
        Map<Class<?>,List<Proxy>> targetMap = new HashMap<>();
        for (Map.Entry<Class<?>,Set<Class<?>>> proxyEntry:proxyMap.entrySet()){
            Class<?> proxyClass = proxyEntry.getKey();
            Set<Class<?>> targetClassSet = proxyEntry.getValue();
            Proxy proxy = ReflectionUtil.newInstance(proxyClass);
            for (Class<?> targetClass : targetClassSet){
                if (targetMap.containsKey(targetClass)){
                    targetMap.get(targetClass).add(proxy);
                } else {
                    List<Proxy> proxyList = new ArrayList<Proxy>();
                    proxyList.add(proxy);
                    targetMap.put(targetClass,proxyList);
                }
            }
        }
        return targetMap;
    }

	@Override
	public void onStartUp() throws Exception {}
}
