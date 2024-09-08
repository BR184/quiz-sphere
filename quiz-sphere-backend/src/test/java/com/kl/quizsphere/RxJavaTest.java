package com.kl.quizsphere;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

/**
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-09-07 17:38:56
 */
@SpringBootTest
public class RxJavaTest {

    Observer ob = new Observer() {
        @Override
        public void onSubscribe(@NotNull Disposable disposable) {
            System.out.println("onSubscribe"+Thread.currentThread());
        }

        @Override
        public void onNext(@NotNull Object o) {
            System.out.println("next->"+o+Thread.currentThread());
        }

        @Override
        public void onError(@NotNull Throwable throwable) {
            System.out.println(throwable.getMessage()+Thread.currentThread());
        }

        @Override
        public void onComplete() {
            System.out.println("Complete"+Thread.currentThread());
        }
    };

    @Test
    public void test() throws Exception {
        //测试基础的 RxJava 类
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Object> observableEmitter) throws Exception {
                observableEmitter.onNext("111");
                observableEmitter.onNext("222");
                observableEmitter.onNext("333");
                observableEmitter.onComplete();

            }
        }).subscribe(this::update);

        Thread.sleep(1000000);
    }

    @Test
    public void test2() throws Exception{
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Object> observableEmitter) throws Exception {
                observableEmitter.onNext("111");
                observableEmitter.onNext("222");
                observableEmitter.onNext("333");
                observableEmitter.onNext("444");
                observableEmitter.onNext("555");
                System.out.println("一开始的线程"+Thread.currentThread());
                observableEmitter.onNext("666");
                observableEmitter.onNext("777");
                observableEmitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).flatMap(
                a -> {
                    ArrayList<String> list = new ArrayList<>();
                    for (char c : ((String) a).toCharArray()) {
                        list.add(String.valueOf(c));
                        System.out.println("转换：" + c + "位于：" + Thread.currentThread());
                    }
                    return Observable.fromIterable(list);
                }
        ).subscribe();
        Thread.sleep(100000);
    }

    public void update(Object str) {
        System.out.println("嘿嘿！我接受到了" + (String) str + "位于：" + Thread.currentThread());
    }
}
