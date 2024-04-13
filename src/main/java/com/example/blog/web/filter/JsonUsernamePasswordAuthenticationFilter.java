package com.example.blog.web.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.List;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public JsonUsernamePasswordAuthenticationFilter(
            SecurityContextRepository securityContextRepository
            , SessionAuthenticationStrategy sessionAuthenticationStrategy
    ){
        // 継承元クラスのコンストラクタを呼ぶ
        super();
        setSecurityContextRepository(securityContextRepository);
        // JSESSIONIDを都度生成：セッション固定化攻撃への対策
        setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        // 成功
        setAuthenticationSuccessHandler((req, res, auth) -> {
            res.setStatus(HttpServletResponse.SC_OK);
        });
        // 失敗
        setAuthenticationFailureHandler((req, res, exception) -> {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        return UsernamePasswordAuthenticationToken.authenticated(
                "dummy-user"
                ,"dummy-password"
                , List.of()
        );
    }
}