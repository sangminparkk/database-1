package com.chandler.database1.service;

import com.chandler.database1.connection.Member;
import com.chandler.database1.repository.MemberRepository;
import com.chandler.database1.repository.RepositoryV4_1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV4 implements MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void accountTransfer(String fromId, String toId, int money) {
        transferProcess(fromId, toId, money);
    }

    private void transferProcess(String fromId, String toId, int money) {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMember_Id().equals("ex")) {
            throw new IllegalStateException("에러 발생시 계좌이체 실패");
        }
    }

}
