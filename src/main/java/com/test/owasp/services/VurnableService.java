package com.test.owasp.services;

import com.test.owasp.models.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VurnableService {

    private JdbcTemplate jdbcTemplate;

    //Inte ett prepared statement, därför funkar injection
    public List<User> findUserByUsername(String username) {
        // This is where the SQL injection vulnerability is introduced.
        String sql = "SELECT * FROM USERS WHERE username = '" + username + "'";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new User(rs.getString("username"), rs.getString("password")));
    }
}
