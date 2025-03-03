package com.example.merchant.config;

import com.example.merchant.utils.TemplateResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
            ObjectMapper mapper = new ObjectMapper();

            if (username == null)templateResponse.error("username is null on token");
            else {
                try {
                    String url = "http://localhost:9091/api/v1/user/username/" + username;
                    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        JsonNode root = mapper.readTree(response.getBody());
                        Long userId = root.get("data").get("id").asLong();
                        request.setAttribute("userId", userId);
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
