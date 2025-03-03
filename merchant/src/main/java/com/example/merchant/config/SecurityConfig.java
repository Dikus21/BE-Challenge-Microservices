package com.example.merchant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Value("${security.jwt.secret_key}")
    private String jwtSecretKey;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
//                .antMatchers("/v1/merchant/**", "/v1/product/**").hasAuthority("ROLE_USER_OD") // Only allow "ROLE_USER_O"
                .antMatchers("/v1/merchant/**", "/v1/product/**").permitAll()
                .antMatchers("/v1/merchant/save").hasAnyAuthority("ROLE_USER")
                .antMatchers("/v1/merchant/update", "/v1/merchant/delete", "/v1/merchant/update",
                        "/v1/merchant/{id}", "/v1/merchant/income").hasAnyAuthority("ROLE_MERCHANT")
                .antMatchers("/v1/merchant/list").hasAnyAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Assuming HMAC for JWT
        SecretKey secretKey = new SecretKeySpec(jwtSecretKey.getBytes(StandardCharsets.UTF_8), "HMACSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix("");
        converter.setAuthoritiesClaimName("authorities");

        return new JwtAuthenticationConverter() {
            @Override
            protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
                Collection<GrantedAuthority> authorities = converter.convert(jwt);
                // Add custom logic if needed
                return authorities;
            }
        };
    }

}
