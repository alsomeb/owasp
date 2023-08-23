package com.test.owasp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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


    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION) // Skapa Default DB med USERS + AUTHORITIES Tables
                .build();
    }


    /*
        Storing in User Details in H2 Database without Hash
     */

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        var user = User.withUsername("tester")
                .password("{bcrypt}$2a$12$BQBofpYcnwDyL8AWKXDgM.nNHDes9z.JrMZmA0l/rQP0FOl0v0xcK")
                .roles("USER")
                .build();

        var user2 = User.withUsername("admin")
                .password("{bcrypt}$2a$12$HFotosus.5Ae/rjvcFIwUeYekJ3eg9leG6.uVVAz3wasxvZcD6qM2")
                .roles("ADMIN", "USER")
                .build();

        // Create UserDetailsManager (Provides CRUD operations for both users and groups.)
        // Insert the 2 created users
        var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.createUser(user);
        jdbcUserDetailsManager.createUser(user2);

        return jdbcUserDetailsManager;
    }
}
