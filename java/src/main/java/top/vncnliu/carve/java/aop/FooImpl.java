package top.vncnliu.carve.java.aop;

/**
 * User: liuyq
 * Date: 2018/7/3
 * Description:
 */
public class FooImpl implements Foo {
    @Override
    public String test() {
        System.out.println("foo");
        return "foo";
    }
}
