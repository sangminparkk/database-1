package com.chandler.database1.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.chandler.database1.connection.ConnectionConst.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class SpringExceptionTraslatorTest {

    private DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @Test
    void sqlExceptionErrorCode() {
        String sql = "select bad grammer";

        try {
            Connection con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeQuery();
        } catch (SQLException e) {
            assertEquals(e.getErrorCode(), 42122);
            int errorCode = e.getErrorCode(); // 문제점 : 벤더에 따라서 errorCode가 다름
            log.info("errorCode={}", errorCode);
            log.info("error=", e);
        }
    }

    @Test
    void exceptionTranslator() {
        String sql = "select bad grammer";

        try {
            Connection con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeQuery();
        } catch (SQLException e) {
            // spring 예외 변환기
            assertEquals(e.getErrorCode(), 42122);

            SQLErrorCodeSQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException resultEx = translator.translate("select", sql, e);//TODO: 에러 분석 후 Exception 반환

            log.info("resultEx={} >>>", resultEx);
            assertEquals(resultEx.getClass(), BadSqlGrammarException.class);
        }
    }

}
