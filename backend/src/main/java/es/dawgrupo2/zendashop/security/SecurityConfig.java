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
public class SecurityConfig {

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
    @Order(1)
    public SecurityFilterChain restFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**");
        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests(authorize -> authorize

            // Admins Access
            .requestMatchers(HttpMethod.POST, "/api/v1/garments/", "/api/v1/garments/*/images/").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/v1/garments/*").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/garments/*", "/api/v1/orders/*", "/api/v1/garments/*/images/*").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/v1/users/", "/api/v1/orders/", "/api/v1/statistics/income", "/api/v1/statistics/orders", "/api/v1/statistics/labels").hasRole("ADMIN")

            // Users Access
            .requestMatchers(HttpMethod.GET, "/api/v1/users/me/orders", "/api/v1/users/me", "/api/v1/users/me/cart",  "/api/v1/users/*", "/api/v1/users/*/cart", "/api/v1/users/*/orders", "/api/v1/orders/*/orderItems/", "/api/v1/orders/*/orderItems/*", "/api/v1/orders/*", "/api/v1/orders/*/invoice", "/api/v1/statistics/users/*").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/v1/garments/*/opinions/", "/api/v1/orders/", "/api/v1/orders/*/orderItems/", "/api/v1/users/{id}/images/").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/v1/users/me", "/api/v1/garments/*/opinions/*", "/api/v1/orders/*/orderItems/*", "/api/v1/orders/*", "/api/v1/users/*", "/api/v1/images/*/media").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/garments/*/opinions/*", "/api/v1/orders/*", "/api/v1/orders/*/orderItems/*", "/api/v1/users/*", "/api/v1/users/*/images/*").hasAnyRole("USER", "ADMIN")

            // Public Access
            .requestMatchers(HttpMethod.GET,  "/api/v1/garments/", "/api/v1/garments/offers", "/api/v1/garments/*", "/api/v1/garments/*/opinions/", "/api/v1/garments/*/opinions/*", "/api/v1/images/*", "/api/v1/images/*/media").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/users/").permitAll()

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
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http
            .authorizeHttpRequests(authorize -> authorize
                // ADMIN ACCESS: Only administrators
                .requestMatchers("/garment/new", "/garment/*/edit", "/garment/*/delete", "/loadMoreOrders", "/order/*/edit", "/order/*/delete", "/order/*/edit").hasRole("ADMIN")
                .requestMatchers("/orders", "/users", "/statistics").hasRole("ADMIN")

                // USER ACCESS: Only logged-in users
                .requestMatchers("/cart/**","/user/*", "/garment/*/opinion/new", "/profile","/user/*/edit", "/user/*/delete","/garment/*/opinion/*/form", "/garment/*/opinion/*/edit", "/orders/*/invoice", "/garment/*/opinion/*/delete", "/myorders", "/order/*/process", "/loadMoreMyOrders",  "/order/*").hasAnyRole("USER", "ADMIN")

                // PUBLIC ACCESS
                .requestMatchers("/", "/error", "/error/**","/register", "/garment/*", "/garment/*/image", "/*.css", "/*.js", "/*.png", "/*.jpg", "/*.svg", "/sample_images/**", "/user/*/avatar", "/loadMoreGarments")
                .permitAll()
                
                .anyRequest().permitAll())
            .formLogin(formLogin -> formLogin
                // Custom login page managed by LoginController
                .loginPage("/login")
                .failureUrl("/loginerror")
                .defaultSuccessUrl("/")
                .permitAll())
            .logout(logout -> logout
                // Define the logout behavior and redirection
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll());

        return http.build();
    }
}
