package com.blueming.myrest.configs;

import com.blueming.myrest.accounts.AccountService;
import com.blueming.myrest.common.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    AccountService accountService;
    @Autowired
    TokenStore tokenStore;

    @Autowired
    AppProperties appProperties;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(accountService)
                .tokenStore(tokenStore);
    }

    // OAuth2 인증 서버 보안(Password) 정보를 설정
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 패스워드 인코딩
        security.passwordEncoder(passwordEncoder);
    }

    // 토큰 발행 메서드
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // inMemory는 메모리에 토큰 올리고 JDBC를 쓰면 DB에 토큰 저장
        // 처음 토큰 받을 때는 password로 grant 타입을 주고, 이후 만료된 후에는 refresh_token 방식 사용
        // 토큰 만료 시간 10 * 60 -> 10분
        // refresh_token 만료 시간은 6 * 10 * 60 -> 1시간
        // id : myApp / pw : pass
        clients.inMemory().withClient(appProperties.getClientId())
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("read", "write")
                .secret(this.passwordEncoder.encode(appProperties.getClientSecret()))
                .accessTokenValiditySeconds(10 * 60)
                .refreshTokenValiditySeconds(6 * 10 * 60);
    }
}
