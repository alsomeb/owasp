package com.test.owasp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    /*
        The OPTIONS request is a preflight request made by the browser to check whether the actual request is safe to send or not.
        By permitting all OPTIONS requests, you're allowing the browser to perform the necessary CORS checks before making the actual request,
        which helps to avoid the CORS error when accessing h2-console.

     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.OPTIONS)).permitAll()
                        .anyRequest()
                        .authenticated())
                // Disable CSRF
                .csrf(AbstractHttpConfigurer::disable) // Annars kommer vi ej åt H2-console, då måste vi ha CSRF token
                // Basic Auth enabled with default Pop Up Modal for Login Credentials in Browser
                .httpBasic(Customizer.withDefaults())
                // Allow any req that comes from the same origin to frame this App (For h2 Console, annars funkar ej den som den skall!)
                .headers(headersConfig ->
                        headersConfig.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()))
                .build();
    }


        /*
     * Configure an embedded H2 database as the data source.
     */
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION) // Skapa Default DB med USERS + AUTHORITIES Tables
                .build();
    }



        /*
     * Configure the UserDetailsService using JdbcUserDetailsManager to manage users in the database.
     */
    @Bean
    public UserDetailsService userDetailsService(JdbcUserDetailsManager jdbcUserDetailsManager, PasswordEncoder passwordEncoder) {
        var admin = User.withUsername("admin")
                .password(adminPassword)
                .passwordEncoder(passwordEncoder::encode) // hiding password with ENV variable
                .roles("ADMIN", "USER")
                .build();

        // Insert the created user
        // Create the admin user using JdbcUserDetailsManager
        jdbcUserDetailsManager.createUser(admin);

        return jdbcUserDetailsManager;
    }

    /*
     AuthenticationManager:
     - The AuthenticationManager is a central component in Spring Security responsible for authenticating users.
     
     - It verifies user credentials during the authentication process.
     
     - Takes an authentication request, validates credentials, and returns an authenticated Authentication object if successful.
     
     - Handles the process of determining if a user is who they claim to be.


     Our Custom Authentication Manager:
     - We create a custom AuthenticationManager bean using a ProviderManager and a DaoAuthenticationProvider.

     - The DaoAuthenticationProvider uses a UserDetailsService to retrieve user details and a PasswordEncoder to validate passwords.

    - This custom AuthenticationManager is used to authenticate users during the login process and ensures secure password handling.
         */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder); // Use same encoder for creating and authentication
        return new ProviderManager(authProvider);
    }


        /*
     * Create a JdbcUserDetailsManager bean for user management operations.
     In summary, our custom AuthenticationManager bean setup ensures that user authentication in our application will go through a process of querying the database 
     (via your UserDetailsService) for user details and validating the password (using our PasswordEncoder). 
     If the user's credentials match the stored information, an authenticated Authentication object is returned.
     */
    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

       /*
     * Create a BCryptPasswordEncoder bean for securely encoding and verifying passwords.
     */
    @Bean
    public PasswordEncoder bcryptEncoder() {
        return new BCryptPasswordEncoder();
    }
}
