package org.smart4j.framework.proxy;

/**
 * 代理借口
 * Created by CaiDongYu on 2016/4/11.
 */
public interface Proxy {

    /**
     * 执行链式代理
     */
    Object doProxy(ProxyChain proxyChain) throws Throwable;
}
