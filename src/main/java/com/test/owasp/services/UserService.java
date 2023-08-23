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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class UserService {

    private JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JdbcUserDetailsManager jdbcUserDetailsManager;

    //Nu används prepared statement så att injection misslyckas.
    public List<AppUser> findUserByUsername(String username) {
        List<AppUser> appUsers = new ArrayList<>();
        String sql = "SELECT * FROM USERS WHERE username = ?";

        try (PreparedStatement statement = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection().prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                appUsers.add(new AppUser(rs.getString("username"), rs.getString("password")));
            }

        } catch (SQLException e) {
            System.out.println(e.getSQLState());
        }
        return appUsers;
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
