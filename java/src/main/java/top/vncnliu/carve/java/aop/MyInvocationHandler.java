package top.vncnliu.carve.java.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * User: liuyq
 * Date: 2018/7/3
 * Description:
 */
public class MyInvocationHandler implements InvocationHandler {

    private Foo foo;

    public MyInvocationHandler(Foo foo) {
        this.foo = foo;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("TEST");
        return method.invoke(foo,args);
    }
}
