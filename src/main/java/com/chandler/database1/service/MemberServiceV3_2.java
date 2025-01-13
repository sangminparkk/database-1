package com.chandler.database1.service;

import com.chandler.database1.connection.Member;
import com.chandler.database1.repository.RepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

@Slf4j
public class MemberServiceV3_2 {

    private final TransactionTemplate txTemplate;
    private final RepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, RepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) {
        txTemplate.executeWithoutResult((status) ->  {
            try {
                transferProcess(fromId, toId, money);
            } catch (SQLException | InterruptedException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private void transferProcess(String fromId, String toId, int money) throws SQLException, InterruptedException {
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
