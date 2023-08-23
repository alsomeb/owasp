package com.test.owasp.services;

import com.test.owasp.models.AppUser;
import com.test.owasp.models.AppUserDTO;
import com.test.owasp.models.CreateNewUserResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VulnerableService {

    private JdbcTemplate jdbcTemplate;

    private final PasswordEncoder passwordEncoder;

    private final JdbcUserDetailsManager jdbcUserDetailsManager;


    //Inte ett prepared statement, därför funkar injection
    public List<AppUser> findUserByUsername(String username) {
        // This is where the SQL injection vulnerability is introduced.
        String sql = "SELECT * FROM USERS WHERE username = '" + username + "'";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new AppUser(rs.getString("username"), rs.getString("password")));
    }


    public CreateNewUserResponseDTO createNewUser(AppUserDTO appUserDTO) {

        // New User
        var newUser = User.withUsername(appUserDTO.username())
                .password(appUserDTO.password())
                .roles("USER")
                .passwordEncoder(passwordEncoder::encode)
                .build();

        // Save User
        jdbcUserDetailsManager.createUser(newUser);

        // Return DTO Object
        return new CreateNewUserResponseDTO(newUser.getUsername());

    }
}
