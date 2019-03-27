package com.example.webflux;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import org.junit.Test;


/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/27.
 */
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
}
