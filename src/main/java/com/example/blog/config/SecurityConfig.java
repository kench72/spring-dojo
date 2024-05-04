package com.example.blog.config;

import com.example.blog.web.filter.CsrfCookieFilter;
import com.example.blog.web.filter.JsonUsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
            , SecurityContextRepository securityContextRepository
            , SessionAuthenticationStrategy sessionAuthenticationStrategy
            , AuthenticationManager authenticationManager
            , ObjectMapper objectMapper
    ) throws Exception {
        http
                // .csrf(csrf -> csrf.ignoringRequestMatchers("/login"))
                .csrf((csrf) -> csrf
                        // ① Cookie経由でCsrfトークンを渡して
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // ② Httpヘッダー情報にセットされているCsrfトークンを確認する
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                // Filterの差し替え（UsernamePasswordAuthenticationFilterの替わりにJsonUsernamePasswordAuthenticationFilter）
                .addFilterAt(
                        new JsonUsernamePasswordAuthenticationFilter(
                                securityContextRepository
                                , sessionAuthenticationStrategy
                                , authenticationManager
                                , objectMapper
                        ),
                        UsernamePasswordAuthenticationFilter.class)
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers("articles/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/users").permitAll()
                        .anyRequest().authenticated()
                )
                // 認証エラーが発生した際は403エラーを返却。
                // デフォルトの挙動がLoginFormに誘導する挙動となっているので修正。
                .exceptionHandling(customizer -> customizer.accessDeniedHandler((req, res, ex) -> {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }));
        // お試しで作成していたGET /loginは廃止（→formLoginも不要）
        //.formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService
    ) {
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new ChangeSessionIdAuthenticationStrategy();
    }

    //@Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.builder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

}