// package es.dawgrupo2.zendashop.security;

// import org.springframework.context.annotation.Bean;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// @EnableWebSecurity
// public class WebSecurityConfig {

//     @Autowired
//     RepositoryUserDetailsService userDetailsService;

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public DaoAuthenticationProvider authenticationProvider() {
//         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
//         authProvider.setPasswordEncoder(passwordEncoder());
//         return authProvider;
//     }

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http.authenticationProvider(authenticationProvider());

//         http
//                 .authorizeHttpRequests(authorize -> authorize
//                         // ADMIN ACCESS: Only administrators
//                         .requestMatchers("/garment/new", "/garment/*/edit", "/garment/*/delete",  "/loadMoreOrders", "/order/*/edit", "/order/*/delete", "/order/*/edit").hasRole("ADMIN")
//                         .requestMatchers("/orders", "/users", "/statistics").hasRole("ADMIN")

//                         // USER ACCESS: Only logged-in users
//                         .requestMatchers("/cart/**","/user/*", "/garment/*/opinion/new", "/profile","/user/*/edit", "/user/*/delete","/garment/*/opinion/*/form", "/garment/*/opinion/*/edit", "/orders/*/invoice", "/garment/*/opinion/*/delete", "/myorders", "/order/*/process", "/loadMoreMyOrders",  "/order/*").hasAnyRole("USER", "ADMIN")

//                         // PUBLIC ACCESS
//                         .requestMatchers("/", "/error", "/error/**","/register", "/garment/*", "/garment/*/image", "/*.css", "/*.js", "/*.png", "/*.jpg", "/*.svg", "/sample_images/**", "/user/*/avatar", "/loadMoreGarments")
//                         .permitAll()
                        
//                         .anyRequest().permitAll())

//                 .formLogin(formLogin -> formLogin
//                         // Custom login page managed by LoginController
//                         .loginPage("/login")
//                         .failureUrl("/loginerror")
//                         .defaultSuccessUrl("/")
//                         .permitAll())
//                 .logout(logout -> logout
//                         // Define the logout behavior and redirection
//                         .logoutUrl("/logout")
//                         .logoutSuccessUrl("/")
//                         .permitAll());

//         return http.build();
//     }
// }
