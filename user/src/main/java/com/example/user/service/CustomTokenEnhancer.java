package com.example.user.service;

import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

//@Service
//public class CustomTokenEnhancer implements TokenEnhancer {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
//        Map<String, Object> additionalInfo = new HashMap<>();
//
//        // Here, add your custom information. For example:
//        String test = getMerchantNameFromUser(authentication);
//        additionalInfo.put("merchant_name", test);
//
//        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
//        return accessToken;
//    }
//
//    private String getMerchantNameFromUser(OAuth2Authentication authentication) {
//        if (authentication != null && authentication.getPrincipal() instanceof User) {
//            User userDetails = (User) authentication.getPrincipal();
//            return userDetails.getMerchantName();
//        }
//        return null;
//    }
//}
