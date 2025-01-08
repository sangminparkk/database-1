package com.chandler.database1.service;

import com.chandler.database1.connection.Member;
import com.chandler.database1.repository.RepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final RepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException, InterruptedException {
        Connection con = getConnection();

        // 트랜잭션 시작
        try {
            con.setAutoCommit(false);
            transferProcess(con, fromId, toId, money);
            con.commit(); // 성공시 커밋

        } catch (Exception e) {
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }

    private void transferProcess(Connection con, String fromId, String toId, int money) throws SQLException, InterruptedException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); //TODO: 커넥션 풀을 고려해서 setAutoCommit을 true 설정 필요
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private static void validation(Member toMember) {
        if (toMember.getMember_Id().equals("ex")) {
            throw new IllegalArgumentException("에러 발생시 계좌이체 실패");
        }
    }

}
