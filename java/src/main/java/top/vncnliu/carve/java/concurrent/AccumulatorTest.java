package top.vncnliu.carve.java.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAccumulator;

/**
 * User: liuyq
 * Date: 2018/6/29
 * Description:
 */
public class AccumulatorTest {

    static double a = 1.111;
    static int c = 0;
    static AtomicInteger b = new AtomicInteger(0);
    static DoubleAccumulator doubleAccumulator = new DoubleAccumulator((x, y) -> x + y, 1.111);
    static int threadNum = 100;

    public static void main(String[] args) {

        CountDownLatch child = new CountDownLatch(threadNum);
        CountDownLatch main = new CountDownLatch(1);

        for (int i = 0; i < threadNum; i++) {
            new Add(child,main).start();
        }
        main.countDown();
        try {
            child.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(a);
        System.out.println(b.get());
        System.out.println(c);
        System.out.println(doubleAccumulator.get());
    }

    static class Add extends Thread {

        CountDownLatch child;
        CountDownLatch main;

        public Add(CountDownLatch child, CountDownLatch main) {
            this.child = child;
            this.main = main;
        }

        @Override
        public void run() {
            try {
                main.await();
                a=a+1.111;
                c+=1;
                doubleAccumulator.accumulate(1.111);
                b.getAndIncrement();
                child.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
