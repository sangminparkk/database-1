package com.chandler.database1.service;

import com.chandler.database1.connection.Member;
import com.chandler.database1.repository.MemberRepository;
import com.chandler.database1.repository.RepositoryV3;
import com.chandler.database1.repository.RepositoryV4_1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceV4Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @TestConfiguration
    static class testConfig {

        private final DataSource dataSource;

        public testConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepository memberRepository() {
            return new RepositoryV4_1(dataSource);
        }

        @Bean
        MemberService memberService() {
            return new MemberServiceV4(memberRepository());
        }
    }

    @AfterEach
    void after() {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체 성공")
    public void accountTransfer(){
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

    @Test
    void aopCheck() {
        log.info("memberService={}", memberService.getClass());
        log.info("memberRepository={}", memberRepository.getClass());
        assertTrue(AopUtils.isAopProxy(memberService));
        assertFalse(AopUtils.isAopProxy(memberRepository));
    }

}