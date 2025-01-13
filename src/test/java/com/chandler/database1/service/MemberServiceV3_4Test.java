package com.chandler.database1.service;

import com.chandler.database1.connection.Member;
import com.chandler.database1.repository.RepositoryV3;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ReflectiveScan;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static com.chandler.database1.connection.ConnectionConst.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceV3_4Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private RepositoryV3 memberRepository;

    @Autowired
    private MemberServiceV3_3 memberService;

    @TestConfiguration
    static class testConfig {

        private final DataSource dataSource;

        public testConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        RepositoryV3 memberRepository() {
            return new RepositoryV3(dataSource);
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(memberRepository());
        }
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
        assertEquals(fromId.getMoney(), 10000); //TODO: 롤백이 안됐다?
        assertEquals(toId.getMoney(), 10000);
    }

    @Test
    void aopCheck() {
        log.info("memberService={}", memberService.getClass()); //.MemberServiceV3_3$$SpringCGLIB$$0 >> 프록시 객체 생성
        log.info("memberRepository={}", memberRepository.getClass());
        assertTrue(AopUtils.isAopProxy(memberService)); // @Transactional is present
        assertFalse(AopUtils.isAopProxy(memberRepository));
    }

}