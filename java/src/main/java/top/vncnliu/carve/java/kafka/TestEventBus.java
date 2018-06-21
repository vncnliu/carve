package top.vncnliu.carve.java.kafka;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javax.swing.event.ChangeEvent;
import java.util.concurrent.CountDownLatch;

/**
 * add description<br>
 * created on 2018/1/26<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class TestEventBus {

    public static int sum = 0;

    static final CountDownLatch countDownLatchC = new CountDownLatch(6000);

    public void main() throws InterruptedException {
        final EventBus eventBus = new EventBus();
        eventBus.register(new EventBusChangeRecorder());
        eventBus.register(new EventBusChangeRecorder2());

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 1000; i++) {
                    eventBus.post(new ChangeEvent(i));
                }
            }
        },"worker1");
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 1000; i < 2000; i++) {
                    eventBus.post(new ChangeEvent(i));
                }
            }
        },"worker2");
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 2000; i < 3000; i++) {
                    eventBus.post(new ChangeEvent(i));
                }
            }
        },"worker3");
        thread1.start();
        thread2.start();
        thread3.start();
        countDownLatch.countDown();
        countDownLatchC.await();
        System.out.println(sum);
    }

    // Class is typically registered by the container.
    class EventBusChangeRecorder {
        @Subscribe
        public void recordCustomerChange(ChangeEvent e) {
            sum++;
            System.out.println("1||"+Thread.currentThread().getName()+"||"+e);
            countDownLatchC.countDown();
        }
    }

    // Class is typically registered by the container.
    class EventBusChangeRecorder2 {
        @Subscribe
        public void recordCustomerChange2(ChangeEvent e) {
            sum++;
            System.out.println("2||"+Thread.currentThread().getName()+"||"+e);
            countDownLatchC.countDown();
        }
    }

}
