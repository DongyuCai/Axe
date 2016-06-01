package org.axe.annotation.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ioc 注解
 * 其实效果与@Service一样，但是@Service有@Tns切面了，所以分开
 * @Component还没有什么切面加强，比较干净，是纯的Bean
 * Created by CaiDongYu on 2016年5月11日 上午10:20:16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

}
