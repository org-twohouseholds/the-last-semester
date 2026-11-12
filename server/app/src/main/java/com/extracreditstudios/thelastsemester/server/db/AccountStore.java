package com.extracreditstudios.thelastsemester.server.db;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
}
