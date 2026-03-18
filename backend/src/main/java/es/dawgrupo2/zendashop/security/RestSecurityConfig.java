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
            .requestMatchers(HttpMethod.GET, "/", "/api/v1/garments/*", "/api/v1/garments/*/image", "/api/v1/images/*/media").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/users/register").permitAll()
                
            // Users Access
            .requestMatchers(HttpMethod.GET, "/api/v1/me/orders", "/api/v1/me", "/api/v1/me/cart",  "/api/v1/users/*/image", "/api/v1/orders/*/orderItems/", "/api/v1/orders/*/orderItems/*", "/api/v1/orders/*").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/v1/garments/*/opinions/", "/api/v1/orders/*", "/api/v1/orders/*/orderItems/*").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/v1/me", "/api/v1/opinions/*", "/api/v1/orders/*/orderItems/*").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/opinions/*", "/api/v1/orders/*", "/api/v1/orders/*/orderItems/*").hasAnyRole("USER", "ADMIN")

            // Admins Access
            .requestMatchers(HttpMethod.POST, "/api/v1/garments").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/v1/garments/*", "/api/v1/orders/*").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/garments/*", "/api/v1/orders/*").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/v1/users/", "/api/v1/statistics/", "/api/v1/orders/").hasRole("ADMIN")

            .anyRequest().permitAll()
        );
        http.formLogin(formLogin -> formLogin.disable());
        http.csrf(csrf -> csrf.disable());
        http.httpBasic(basic -> basic.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandlerJwt));

        return http.build();
    }
}
