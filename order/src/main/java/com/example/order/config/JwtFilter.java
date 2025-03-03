package com.example.order.config;

import com.example.order.dto.TokenDto;
import com.example.order.utils.TemplateResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class JwtFilter implements Filter {
    private final RestTemplate restTemplate;
    private TemplateResponse templateResponse;
    @Autowired
    public JwtFilter(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.templateResponse = new TemplateResponse();
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String username = jwt.getClaimAsString("user_name");
            request.setAttribute("username", username);
            ObjectMapper mapper = new ObjectMapper();

            if (username == null)templateResponse.error("username is null on token");
            else {
                try {
                    String url = "http://localhost:9091/api/v1/user/username/" + username;
                    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        JsonNode root = mapper.readTree(response.getBody());
                        Long userId = root.get("data").get("id").asLong();
                        UUID merchantId = UUID.fromString(root.get("data").get("merchantId").asText());
                        String fullname = root.get("data").get("fullname").asText();
                        request.setAttribute("userId", userId);
                        request.setAttribute("merchantId", merchantId);
                        request.setAttribute("fullname", fullname);
                    }
                } catch (Exception e) {
                    log.error("Error : " + e);
                    templateResponse.error("token data process error : " + e);
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
