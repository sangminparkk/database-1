package com.chandler.database1.service;

import com.chandler.database1.connection.Member;
import com.chandler.database1.repository.RepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static com.chandler.database1.connection.ConnectionConst.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class MemberServiceV3_2Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";
    private RepositoryV3 memberRepository;
    private MemberServiceV3_2 memberService;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new RepositoryV3(dataSource);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberService = new MemberServiceV3_2(transactionManager, memberRepository);
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
        memberService.accountTransfer(fromMember.getMember_Id(), toMember.getMember_Id(), 2000);
        Member fromId = memberRepository.findById(fromMember.getMember_Id());
        Member toId = memberRepository.findById(toMember.getMember_Id());

        // then
        assertEquals(fromId.getMoney(), 8000);
        assertEquals(toId.getMoney(), 12000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    public void accountTransfer_member_ex() throws Exception {
        // given
        Member fromMember = new Member(MEMBER_A, 10000);
        Member toMember = new Member(MEMBER_EX, 10000);
        memberRepository.save(fromMember);
        memberRepository.save(toMember);

        //when
        assertThrows(IllegalStateException.class, () -> memberService.accountTransfer(fromMember.getMember_Id(), toMember.getMember_Id(), 2000));
        Member fromId = memberRepository.findById(fromMember.getMember_Id());
        Member toId = memberRepository.findById(toMember.getMember_Id());

        // then
        assertEquals(fromId.getMoney(), 10000);
        assertEquals(toId.getMoney(), 10000);
    }

}