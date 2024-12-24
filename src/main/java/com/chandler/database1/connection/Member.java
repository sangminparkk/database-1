package com.chandler.database1.connection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString
public class Member {

    private String member_Id;
    private int money;

    public Member(String member_Id, int money) {
        this.member_Id = member_Id;
        this.money = money;
    }
}
