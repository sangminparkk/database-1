package com.chandler.database1.repository;

import com.chandler.database1.connection.DBConnectionUtil;
import com.chandler.database1.connection.Member;
import com.zaxxer.hikari.pool.ProxyConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class RepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstat = null;

        try {
            con = getConnection();
            pstat = con.prepareStatement(sql);
            pstat.setString(1, member.getMember_Id());
            pstat.setInt(2, member.getMoney());
            pstat.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstat, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_Id = ?";

        Connection con = null;
        PreparedStatement pstat = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstat = con.prepareStatement(sql);
            pstat.setString(1, memberId);
            rs = pstat.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMember_Id(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found, memberId = " + memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstat, rs);
        }
    }

    private void close(Connection con, Statement stat, ResultSet rs)  {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("rs error", e);
            }
        }

        if (stat != null) {
            try {
                stat.close();
            } catch (SQLException e) {
                log.error("stat error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.error("conn error", e);
            }
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
