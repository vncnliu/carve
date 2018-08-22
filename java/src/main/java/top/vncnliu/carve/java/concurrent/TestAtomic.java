package top.vncnliu.carve.java.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: liuyq
 * Date: 2018/6/29
 * Description:
 */
public class TestAtomic {
    private static AtomicInteger atomic = new AtomicInteger(0);
    //初始化数据，等到所有线程都执行完操作之后，在进行下一步
    private static CountDownLatch latch = new CountDownLatch(100);

    /**
     * 固定三个线程来计算值
     */
    private static ExecutorService service = Executors.newFixedThreadPool(3);

    /**
     * 采用多线程的方式来计算0-100的和
     * @param args
     */
    public static void main(String[] args) {
        //通过多线程的方式来对数据进行操作
        for(int i = 0, len = 100; i <= len; i++) {
            final int scale = i;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    atomic.addAndGet(scale);
                    latch.countDown();
                }
            };
            service.submit(r);
        }

        try {
            //等到所有的线程都执行了countDown一次之后，在继续向下执行
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //输出最终的值
        System.out.println("0~100的和为:" + (atomic.get()));
    }

}
