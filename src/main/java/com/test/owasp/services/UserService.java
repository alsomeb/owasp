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
public class UserService {

    private JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JdbcUserDetailsManager jdbcUserDetailsManager;

    //Nu används prepared statement så att injection misslyckas.
    public List<AppUser> findUserByUsername(String username) {

        String sql = "SELECT * FROM USERS WHERE username = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new AppUser(rs.getString("username"), rs.getString("password")), username);
    }

    // använder JDBCUserDetailsManger med best-practice prepared statements och CRUD funktioner för skapa USer osv
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
