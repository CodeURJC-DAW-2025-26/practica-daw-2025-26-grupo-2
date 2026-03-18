package es.dawgrupo2.zendashop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.dawgrupo2.zendashop.security.jwt.JwtRequestFilter;
import es.dawgrupo2.zendashop.security.jwt.UnauthorizedHandlerJwt;

@Configuration
@EnableWebSecurity
@Order(1)
public class RestSecurityConfig {

    @Autowired
    RepositoryUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**");
        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests(authorize -> authorize
            // Public Access
            .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**", "/*.css", "/*.js", "/*.png").permitAll()
            .requestMatchers(HttpMethod.GET, "/", "/api/*/garments/**", "/api/*/garments/*/image", "/api/users/*/avatar", "/api/*/images/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/*/auth/login", "/api/*/auth/refresh", "/api/users/register").permitAll()
                
            // Users Access
            .requestMatchers(HttpMethod.GET, "/api/myorders", "/api/cart/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/garments/*/opinions", "/api/orders").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/users/profile", "/api/opinions/*").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/opinions/*").hasAnyRole("USER", "ADMIN")

            // Admins Access
            .requestMatchers(HttpMethod.POST, "/api/garments").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/garments/*", "/api/orders/*").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/garments/*", "/api/orders/*").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/users", "/api/statistics", "/api/orders").hasRole("ADMIN")

            .anyRequest().authenticated()
        );
        http.formLogin(formLogin -> formLogin.disable());
        http.csrf(csrf -> csrf.disable());
        http.httpBasic(basic -> basic.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
