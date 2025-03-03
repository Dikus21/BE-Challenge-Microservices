package com.example.user.service;

import com.example.user.entity.User;
import com.example.user.request.LoginModel;
import com.example.user.request.RegisterModel;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public interface UserService {
    Map registerManual(RegisterModel objModel) ;
    Map registerMerchant (User request, UUID merchantId);
    Map registerByGoogle(RegisterModel objModel);
    public Map login(LoginModel objLogin);
    Map<Object, Object> getById(Long id);
    Map<Object, Object> delete(User request);
    Map<Object, Object> update(User request);
    Map<Object, Object> getIdByUserName(String username);
    Map<Object, Object> getDetailProfile(Principal principal);
}

