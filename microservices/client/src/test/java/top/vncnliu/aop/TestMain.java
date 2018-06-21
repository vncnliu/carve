package top.vncnliu.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * User: liuyq
 * Date: 2018/5/7
 * Description:
 */
public class TestMain {

    public List getList(final List list) {
        return (List) Proxy.newProxyInstance(TestMain.class.getClassLoader(), new Class[] { List.class },
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("add".equals(method.getName())) {
                            throw new UnsupportedOperationException();
                        }
                        else {
                            return method.invoke(list, args);
                        }
                    }
                });
    }
}
