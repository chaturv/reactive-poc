package com.chaturv;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmitterObservableTest {

    @Test
    public void testEmitterOnNextOnly() {
        List<String> expected = Arrays.asList("foo", "bar");
        List<String> results = new ArrayList<>();

        Observable<String> observable = Observable.create(emitter -> expected.forEach(s -> emitter.onNext(s)));
        observable.subscribe(s -> results.add(s));

        Assertions.assertIterableEquals(expected, results);
    }

    private int resultsSize;
    private String actualErrorMsg;

    @Test
    public void testEmitterOnComplete() {
        List<String> expected = Arrays.asList("foo", "bar");
        List<String> results = new ArrayList<>();

        Observable<String> observable = Observable.create(emitter -> {
            expected.forEach(s -> emitter.onNext(s));
            emitter.onComplete();
        });

        observable.subscribe(s -> results.add(s), throwable -> {}, () -> resultsSize = results.size());
        Assertions.assertEquals(expected.size(), resultsSize);
    }

    @Test
    public void testDisposableObserver() {
        List<String> emitted = Arrays.asList("foo", "bar", "foobar");
        List<String> expected = Arrays.asList("foo", "bar");
        String expectedErrMsg = "can not process: foobar";

        List<String> results = new ArrayList<>();

        Observable<String> observable = Observable.create(emitter -> {
            try {
                emitted.forEach(s -> emitter.onNext(s));
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        });

        observable.subscribeWith(new DisposableObserver<String>() {
            @Override
            public void onNext(@NonNull String s) {
                if (s.equals("foobar")) {
                    throw new IllegalArgumentException(expectedErrMsg);
                } else {
                    results.add(s);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                actualErrorMsg = e.getMessage();
            }

            @Override
            public void onComplete() {
                resultsSize = results.size();
            }
        });

        Assertions.assertEquals(expected.size(), resultsSize);
        Assertions.assertIterableEquals(expected, results);
        Assertions.assertEquals(expectedErrMsg, actualErrorMsg);
    }
}
