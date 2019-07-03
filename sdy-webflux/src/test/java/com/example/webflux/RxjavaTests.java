package com.example.webflux;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;


/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/27.
 */
@Slf4j
public class RxjavaTests {

    @Test
    public void test1() {
        Observable<String> myStrings = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        myStrings.map(s -> s.length()).subscribe(s -> System.out.println(s));
    }

    @Test
    public void test2() {
        Observable<String> myStrings = Observable.create(emitter -> {
            emitter.onNext("apple");
            emitter.onNext("bear");
            emitter.onNext("change");
            emitter.onComplete();
        });
        myStrings.map(s -> s.length()).subscribe(s -> System.out.println(s));
    }

    @Test
    public void test3() {
        Observable<String> myStrings = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        myStrings.subscribe(s -> System.out.println(s + "   0"));
        myStrings.subscribe(s -> System.out.println(s + "  1"));
    }

    @Test
    public void test4() {
        ConnectableObservable<String> source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                .publish();
        source.subscribe(s -> System.out.println("o1: " + s));
        source.map(String::length).subscribe(i -> System.out.println("o2: " + i));
        source.connect();
        // Hot的流产生的数据是动态的，好比收音机电台，错过了播放的时段，过去的数据就取不到了。 以上不会打出 o3
        source.map(String::length).subscribe(i -> System.out.println("o3: " + i));
    }

    @Test
    // 什么是背压. 指的是异步环境中，生产者速度大于消费者速度时，消费者反过来控制生产者速度的策略（也称为回压）, 是一种流控的方式，可以更有效的利用资源，
    // 同时防止错误的雪崩
    public void test5() {

    }

    @Test
    public void test6() {
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            System.out.println("Observable emit 1");
            emitter.onNext(1);
            System.out.println("Observable emit 2");
            emitter.onNext(2);
            System.out.println("Observable emit 3");
            emitter.onNext(3);
            emitter.onComplete();
            System.out.println("Observable emit 4");
            emitter.onNext(4);
        }).subscribe(new Observer<Integer>() {
            private int i;
            private Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("consumer: " + integer);
                i++;
                if (i == 2) {
                    // 在RxJava 2.x 中，新增的Disposable可以做到切断的操作，让Observer观察者不再接收上游事件
                    disposable.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                System.err.println("onError: value: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.err.println("onCommplete！");
            }
        });
    }

    @Test
    public void test7() throws InterruptedException {
        Observable.create(emitter -> {
            int i = 0;
            while (true) {
                Thread.sleep(10);

                i++;
                emitter.onNext(i);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(i -> {
                    Thread.sleep(20);
                    System.out.println(i);
                });

        TimeUnit.HOURS.sleep(2l);
    }

    @Test
    public void test8() throws InterruptedException {
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .subscribe(a -> {
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("rx_test " + " back_pressure：" + a);
                });
        TimeUnit.HOURS.sleep(1l);
    }

    @Test
    public void test9() throws InterruptedException {
        Flowable.create((FlowableOnSubscribe<Integer>) e -> {
            System.out.println("发射----> 1");
            e.onNext(1);
            System.out.println("发射----> 2");
            e.onNext(2);
            System.out.println("发射----> 3");
            e.onNext(3);
            System.out.println("发射----> 完成");
            e.onComplete();
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("接收----> " + integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("接收----> 完成");
                    }
                });

        TimeUnit.HOURS.sleep(1l);
    }

    @Test
    public void test10() {
            Flowable
                    .create((FlowableOnSubscribe<Integer>) e -> {
                        for (int i = 1; i < 130; i++) {
                            System.out.println("发射---->" + i);
                            e.onNext(i);
                        }
                        e.onComplete();
                    }, BackpressureStrategy.ERROR)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<Integer>() {
                        @Override
                        public void onSubscribe(Subscription s) {
                            s.request(1);
                        }

                        @Override
                        public void onNext(Integer integer) {
                            System.out.println("接收------>" + integer);
                        }

                        @Override
                        public void onError(Throwable t) {
                            t.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                            System.out.println("接收------>完成");
                        }
                    });
    }


    @Test
    public void eventBusTest(){

        EventBus eventBus = new EventBus();
        eventBus.register(new EventBusDemo());
        eventBus.post("xxx");

    }

    public class EventBusDemo {
        @Subscribe
        public void sendMessageByMail(String message) {
            System.out.println("发送一个邮件 " + message);
        }
        @Subscribe
        public void sendMessageByPhone(String message) {
            System.out.println("发送一个短信 " + message);
        }
    }
}
