package com.chandler.database1.exception.basic;

import org.junit.jupiter.api.Test;

public class CheckedExceptionTest {

    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    static class Service {

    }

    static class Repository {
        public void call() {
            throw new MyCheckedException("ex");
        }
    }
}
