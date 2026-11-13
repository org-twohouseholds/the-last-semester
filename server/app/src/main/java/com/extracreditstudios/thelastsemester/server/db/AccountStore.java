package com.extracreditstudios.thelastsemester.server.db;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

import io.javalin.http.ConflictResponse;
import io.javalin.http.NotFoundResponse;

public class AccountStore {
    public static void createAccount(HikariDataSource ds, String username, String password_hash,
                                     String session_token_hash) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO account (username, password_hash, session_token_hash) VALUES " +
                     "(?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, password_hash);
            ps.setString(3, session_token_hash);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getErrorCode() == 1062) { // duplicate key
                if (e.getMessage().contains("uq_account_username")) {
                    throw new ConflictResponse("Username already taken");
                }
            }
            throw e; // session token collision (very unlikely) only fails one request
        }
    }

    public static Account getAccount(HikariDataSource ds, String username) throws SQLException {
        try (Connection c = ds.getConnection();
        PreparedStatement ps = c.prepareStatement("SELECT * FROM account WHERE username = ?")){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(rs.getString("username"), rs.getString("password_hash"));
            }
        }
        throw new NotFoundResponse("Account with username: " + username + " not found");
    }

    public static void updateSessionToken(HikariDataSource ds, String username, String session_token_hash) throws SQLException {
        try(Connection c = ds.getConnection();
            PreparedStatement ps = c.prepareStatement(
                "UPDATE account SET session_token_hash = ? WHERE username = ?")) {
            ps.setString(1, session_token_hash);
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }
}
