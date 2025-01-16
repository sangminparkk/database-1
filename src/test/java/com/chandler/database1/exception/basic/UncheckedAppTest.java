package com.chandler.database1.exception.basic;

import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UncheckedAppTest {

    @Test
    void unchecked(){
        Controller controller = new Controller();
        assertThrows(RuntimeSQLException.class, controller::request);
    }

    static class Controller {
        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("예외처리");
        }
    }

    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e); //밖으로 던질때 Runtime으로 변경해서 던짐
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) { //TODO: 이전 예외까지 담아서 처리할 수 있음.굿
            super(cause);
        }
    }
}
