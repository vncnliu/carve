package top.vncnliu.carve.java.concurrent;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: liuyq
 * Date: 2018/6/28
 * Description:
 */
public class TestValueOffect {

    public static void main(String[] args) {
        try {
            Constructor<Unsafe> con = (Constructor<Unsafe>) Class.forName("sun.misc.Unsafe").getDeclaredConstructor();
            con.setAccessible(true);
            User user = new User();
            Unsafe UNSAFE = con.newInstance(null);
            Field filed = user.getClass().getDeclaredField("age");
            long s1=System.currentTimeMillis();
            for(int i=0;i<100000;i++){
                user.setAge(i);
            }
            System.out.println(System.currentTimeMillis()-s1);
            long ageOffset = UNSAFE.objectFieldOffset(filed);
            long s2=System.currentTimeMillis();
            for(int i=0;i<100000;i++){
                UNSAFE.putInt(user, ageOffset, i);
            }
            System.out.println(System.currentTimeMillis()-s2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class User {

        private int age;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
