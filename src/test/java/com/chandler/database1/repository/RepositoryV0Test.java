package com.chandler.database1.repository;

import com.chandler.database1.connection.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryV0Test {

    private static final Logger log = LoggerFactory.getLogger(RepositoryV0Test.class);
    RepositoryV0 repository = new RepositoryV0();

    @Test
    public void crud() throws SQLException {
        // given
        Member member = new Member("memberV1", 15000);

        // when
        repository.save(member);
        Member findMember = repository.findById(member.getMember_Id());
        log.info("findMember = {}", findMember);

        repository.update(member.getMember_Id(), 20000);
        repository.delete(member.getMember_Id());
        assertThrows(NoSuchElementException.class, () -> repository.findById(member.getMember_Id()));
    }

}