package top.vncnliu.carve.java.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * User: liuyq
 * Date: 2018/7/3
 * Description:
 */
public class Main {
    public static void main(String[] args) {
        InvocationHandler handler = new MyInvocationHandler(new FooImpl());
        Foo f = (Foo) Proxy.newProxyInstance(Foo.class.getClassLoader(),new Class<?>[] { Foo.class },handler);
        System.out.println(f.test());
    }
}
