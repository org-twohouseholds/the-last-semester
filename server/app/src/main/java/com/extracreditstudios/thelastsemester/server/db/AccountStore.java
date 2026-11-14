package com.extracreditstudios.thelastsemester.server.db;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import io.javalin.http.ConflictResponse;

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

    public static String findPasswordHash(HikariDataSource ds, String session_token_hash)
        throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT password_hash FROM account WHERE session_token_hash = ?")) {
            ps.setString(1, session_token_hash);
            try (ResultSet result = ps.executeQuery()) {
                return result.next() ? result.getString("password_hash") : null;
            }
        }
    }

    public static void deleteAccount(HikariDataSource ds, String session_token_hash,
                                     String password_hash) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM account WHERE session_token_hash = ? " +
                     "AND CAST(password_hash AS BINARY) = CAST(? AS BINARY)")) {
            ps.setString(1, session_token_hash);
            ps.setString(2, password_hash);
            if (ps.executeUpdate() == 0) {
                throw new ConflictResponse("Account changed before deletion");
            }
        }
    }
}
