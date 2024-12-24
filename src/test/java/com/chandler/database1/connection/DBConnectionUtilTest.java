package com.chandler.database1.connection;

import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectionUtilTest {

    @Test
    public void connection() {
        Connection connection = DBConnectionUtil.getConnection();
        assertNotNull(connection);
    }
}