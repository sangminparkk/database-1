package com.chandler.database1.service;

import com.chandler.database1.connection.Member;
import com.chandler.database1.repository.RepositoryV1;
import com.chandler.database1.repository.RepositoryV2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static com.chandler.database1.connection.ConnectionConst.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class MemberServiceV2Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";
    private RepositoryV2 memberRepository;
    private MemberServiceV2 memberServiceV2;

    @BeforeEach
    void before() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new RepositoryV2(dataSource);
        memberServiceV2 = new MemberServiceV2(dataSource, memberRepository);
    }

    @AfterEach
    void after() throws SQLException, InterruptedException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체 성공")
    public void accountTransfer() throws Exception {
        // given
        Member fromMember = new Member(MEMBER_A, 10000);
        Member toMember = new Member(MEMBER_B, 10000);
        memberRepository.save(fromMember);
        memberRepository.save(toMember);

        // when
        log.info("START TX");
        memberServiceV2.accountTransfer(fromMember.getMember_Id(), toMember.getMember_Id(), 2000);
        log.info("END TX");
        Member fromId = memberRepository.findById(fromMember.getMember_Id());
        Member toId = memberRepository.findById(toMember.getMember_Id());

        // then
        assertEquals(fromId.getMoney(), 8000);
        assertEquals(toId.getMoney(), 12000);
    }

    @Test
    @DisplayName("이체 중 예외 발생") //TODO: 롤백 테스트 안됨
    public void accountTransfer_member_ex() throws Exception {
        // given
        Member fromMember = new Member(MEMBER_A, 10000);
        Member toMember = new Member(MEMBER_EX, 10000);
        memberRepository.save(fromMember);
        memberRepository.save(toMember);

        //when
        log.info("START TX");
        assertThrows(IllegalStateException.class, () -> memberServiceV2.accountTransfer(fromMember.getMember_Id(), toMember.getMember_Id(), 2000));
        log.info("END TX");
        Member fromId = memberRepository.findById(fromMember.getMember_Id());
        Member toId = memberRepository.findById(toMember.getMember_Id());

        // then
        assertEquals(fromId.getMoney(), 8000);
        assertEquals(toId.getMoney(), 10000);
    }

}