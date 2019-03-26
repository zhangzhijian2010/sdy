package lock.readwrite;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/26.
 */
public class Semaphore {

    private boolean signal = false;

    public synchronized void take() {
        this.signal = true;
        this.notify();
    }

    public synchronized void release() throws InterruptedException {
        while (!this.signal) wait();
        this.signal = false;
    }


    @Test
    public void test1() {


        Semaphore semaphore = new Semaphore();
        SendingThread sender = new SendingThread(semaphore);

        ReceivingThread receiver = new ReceivingThread(semaphore);
        receiver.start();
        sender.start();

    }

    @Data
    @AllArgsConstructor
    public class SendingThread extends Thread {
        Semaphore semaphore = null;

        public void run() {
            while (true) {
                this.semaphore.take();
                System.out.println("take");
            }
        }
    }

    @Data
    @AllArgsConstructor
    public class ReceivingThread extends Thread{
        Semaphore semaphore = null;


        public void run() {
            while (true) {
                try {
                    this.semaphore.release();
                    System.out.println("release");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
