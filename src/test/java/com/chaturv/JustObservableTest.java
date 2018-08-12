package com.chaturv;

import io.reactivex.Observable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JustObservableTest {

    private String result = "";

    @Test
    public void testJustObservable() {
        String expected = "Jimmy";
        Observable<String> observable = Observable.just(expected);
        observable.subscribe(s -> result = s);
        Assertions.assertEquals(result, expected);
    }
}
