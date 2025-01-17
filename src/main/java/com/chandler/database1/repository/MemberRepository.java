package com.chandler.database1.repository;

import com.chandler.database1.connection.Member;

public interface MemberRepository {

    Member save(Member member);
    Member findById(String memberId);
    void update(String memberId, int money);
    void delete(String memberId);

}
