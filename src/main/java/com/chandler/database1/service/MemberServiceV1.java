package com.chandler.database1.service;

import com.chandler.database1.connection.Member;
import com.chandler.database1.repository.RepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final RepositoryV1 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException, InterruptedException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember); // for log
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMember_Id().equals("ex")) {
            throw new IllegalArgumentException("에러 발생시 계좌이체 실패");
        }
    }

}
