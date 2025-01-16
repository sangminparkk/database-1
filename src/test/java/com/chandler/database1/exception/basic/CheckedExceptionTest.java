package com.chandler.database1.exception.basic;

import com.chandler.database1.repository.RepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class CheckedExceptionTest {

    @Test
    void check_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void check_throw(){
        Service service = new Service();
        assertThrows(MyCheckedException.class, () -> service.callThrow()); // 예외가 발생해야 테스트 성공인 경우 검증 방법
    }
    
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    static class Service {
        Repository repository = new Repository();

        //TODO: 예외 잡아서 처리
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        //TODO: 예외를 밖으로 던지기
        public void callThrow() throws MyCheckedException {
            repository.call();
        }

    }

    static class Repository {
        public void call() throws MyCheckedException {
            throw new MyCheckedException("MyCheckedException ex");
        }
    }
}
