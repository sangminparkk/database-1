package com.chandler.database1.repository;

import com.chandler.database1.connection.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
public class RepositoryV3 {

    private final DataSource dataSource;

    public Member save(Member member) throws SQLException, InterruptedException {
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
        } catch (SQLException | InterruptedException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstat, null);
        }
    }

    public Member findById(String memberId) throws SQLException, InterruptedException {
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
        } catch (SQLException | InterruptedException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstat, rs);
        }
    }

    public void update(String memberId, int money) throws SQLException, InterruptedException {
        String sql = "update member set money=? where member_Id=?";

        Connection con = null;
        PreparedStatement pstat = null;

        try {
            con = getConnection();
            pstat = con.prepareStatement(sql);
            pstat.setInt(1, money);
            pstat.setString(2, memberId);
            int resultSize = pstat.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException | InterruptedException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstat, null);
        }
    }

    public void delete(String memberId) throws SQLException, InterruptedException {
        String sql = "delete from member where member_Id=?";

        Connection con = null;
        PreparedStatement pstat = null;

        try {
            con = getConnection();
            pstat = con.prepareStatement(sql);
            pstat.setString(1, memberId);
            int resultSize = pstat.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException | InterruptedException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstat, null);
        }
    }

    private void close(Connection con, Statement stat, ResultSet rs)  {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stat);
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() throws SQLException, InterruptedException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        log.info("getConnection={}, class={}" , connection, connection.getClass());

        return connection;
    }
}
