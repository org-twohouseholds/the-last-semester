package com.extracreditstudios.thelastsemester.server.db;

import com.extracreditstudios.thelastsemester.server.security.Passwords;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.http.ConflictResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

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

   
public static void changePassword(
        HikariDataSource ds,
        String sessionTokenHash,
        String oldPassword,
        String newPassword) throws SQLException {

    String oldHash;

    try (Connection c = ds.getConnection();
         PreparedStatement ps = c.prepareStatement(
                 "SELECT password_hash FROM account WHERE session_token_hash = ?")) {

        ps.setString(1, sessionTokenHash);

        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                throw new UnauthorizedResponse("Invalid session");
            }
            oldHash = rs.getString(1);
        }
    }

    if (!Passwords.verify(oldHash, oldPassword)) {
        throw new ForbiddenResponse("Invalid password");
    }

    String newHash = Passwords.hash(newPassword);

    try (Connection c = ds.getConnection();
         PreparedStatement ps = c.prepareStatement(
                 "UPDATE account SET password_hash = ? "
                 + "WHERE session_token_hash = ? AND password_hash = ?")) {

        ps.setString(1, newHash);
        ps.setString(2, sessionTokenHash);
        ps.setString(3, oldHash);

        if (ps.executeUpdate() == 0) {
            throw new ConflictResponse("Password was changed concurrently");
        }
    }
}


}
