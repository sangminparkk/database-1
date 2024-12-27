package com.chandler.database1.repository;

import com.chandler.database1.connection.Member;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static com.chandler.database1.connection.ConnectionConst.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class RepositoryV1Test {

    RepositoryV1 repositoryV1;

    @BeforeEach
    void beforeEach() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repositoryV1 = new RepositoryV1(dataSource);
    }

    @Test
    public void connection() throws Exception {
        // given
        Member member = new Member("memberD", 15000);

        // when
        Member savedMember = repositoryV1.save(member);

        // then
        assertEquals(savedMember.getMember_Id(), member.getMember_Id());
        assertEquals(savedMember.getMoney(), member.getMoney());
    }


}