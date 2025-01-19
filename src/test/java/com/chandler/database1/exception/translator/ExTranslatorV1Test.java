package com.chandler.database1.exception.translator;

import com.chandler.database1.connection.ConnectionConst;
import com.chandler.database1.connection.Member;
import com.chandler.database1.exception.MyDbException;
import com.chandler.database1.exception.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static com.chandler.database1.connection.ConnectionConst.*;

@Slf4j
public class ExTranslatorV1Test {

    //조립
    Repository repository;
    Service service;

    @BeforeEach
    void init() {
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicateKeySave() {
        service.create("myId");
        service.create("myId");
    }

    @RequiredArgsConstructor
    static class Service {

        private final Repository repository;

        void create(String memberId) {
            try {
                repository.save(new Member(memberId, 0));
                log.info("savedId={}", memberId);
            } catch (MyDuplicateKeyException e) {
                log.info("키 중복, 복구 시도");
                String retryId = generateNewId(memberId);
                log.info("retryId={}", retryId);
                repository.save(new Member(retryId, 0));
            } catch (MyDbException e) { // 여러개 캐치 가능함을 보여줌
                log.info("데이터 접근 계층 예외", e);
                throw new MyDbException(e);
            }

        }

        private static String generateNewId(String memberId) {
            return memberId + new Random().nextInt(10000);
        }


    }

    @RequiredArgsConstructor
    static class Repository {

        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "insert into member(member_id, money) values (?,?)";

            Connection con = null;
            PreparedStatement pstmt = null;

            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, member.getMember_Id());
                pstmt.setInt(2, member.getMoney());

                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {
                if (e.getErrorCode() == 23505) {// 단점: DB vendor 에 따라 해당 에러 코드를 계속 변경해줘야 함
                    throw new MyDuplicateKeyException(e);//TODO: 에러 변환
                }
                throw new MyDbException(e);
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }

        }

    }


}
