package com.example.user.service.impl;

import com.example.user.entity.Role;
import com.example.user.entity.User;
import com.example.user.repository.RoleRepository;
import com.example.user.repository.UserRepository;
import com.example.user.request.LoginModel;
import com.example.user.request.RegisterModel;
import com.example.user.service.Oauth2UserDetailsService;
import com.example.user.service.UserService;
import com.example.user.utils.PasswordValidatorUtil;
import com.example.user.utils.TemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;

import java.security.Principal;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Value("${BASEURL}")
    private String baseUrl;
    @Autowired
    RoleRepository repoRole;
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    @Autowired
    UserRepository repoUser;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    public TemplateResponse templateResponse;
    @Autowired
    public PasswordValidatorUtil passwordValidatorUtil = new PasswordValidatorUtil();
    @Autowired
    private Oauth2UserDetailsService userDetailsService;


    @Override
    public Map login(LoginModel loginModel) {
        /**
         * bussines logic for login here
         * **/
        try {
            Map<String, Object> map = new HashMap<>();

            User checkUser = userRepository.findOneByUsername(loginModel.getUsername());

            if ((checkUser != null) && (encoder.matches(loginModel.getPassword(), checkUser.getPassword()))) {
                if (!checkUser.isEnabled()) {
                    return templateResponse.error("User is not enable, check your email");
                }
            }
            if (checkUser == null) {
                return templateResponse.error("user not found");
            }
            if (!(encoder.matches(loginModel.getPassword(), checkUser.getPassword()))) {
                return templateResponse.error("wrong password");
            }
            String url = baseUrl + "/oauth/token?username=" + loginModel.getUsername() +
                    "&password=" + loginModel.getPassword() +
                    "&grant_type=password" +
                    "&client_id=my-client-web" +
                    "&client_secret=password";
            ResponseEntity<Map> response = restTemplateBuilder.build().exchange(url, HttpMethod.POST, null, new
                    ParameterizedTypeReference<Map>() {
                    });

            if (response.getStatusCode() == HttpStatus.OK) {
                User user = userRepository.findOneByUsername(loginModel.getUsername());
                List<String> roles = new ArrayList<>();

                for (Role role : user.getRoles()) {
                    roles.add(role.getName());
                }
                //save token
//                checkUser.setAccessToken(response.getBody().get("access_token").toString());
//                checkUser.setRefreshToken(response.getBody().get("refresh_token").toString());
//                userRepository.save(checkUser);

                map.put("access_token", response.getBody().get("access_token"));
                map.put("token_type", response.getBody().get("token_type"));
                map.put("refresh_token", response.getBody().get("refresh_token"));
                map.put("expires_in", response.getBody().get("expires_in"));
                map.put("scope", response.getBody().get("scope"));
                map.put("jti", response.getBody().get("jti"));
                map.put("message","Success");
                map.put("code",200);

                return map;
            } else {
                return templateResponse.error("user not found");
            }
        } catch (HttpStatusCodeException e) {
            e.printStackTrace();
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return templateResponse.error("invalid login : " + e.getMessage());
            }
            return templateResponse.error(e);
        } catch (Exception e) {
            e.printStackTrace();

            return templateResponse.error(e);
        }
    }

    @Override
    public Map registerManual (RegisterModel objModel) {
        Map map = new HashMap();
        try {
            String[] roleNames = {"ROLE_USER", "ROLE_USER_O", "ROLE_USER_OD"}; // admin
            User user = new User();
            user.setUsername(objModel.getUsername().toLowerCase());
            user.setFullname(objModel.getFullname());

//            //step 1 :
//            user.setEnabled(false); // matikan user
            if (objModel.getPassword().isEmpty()) return templateResponse.error("Password is required");
            if (!passwordValidatorUtil.validatePassword(objModel.getPassword())) {
                return templateResponse.error(passwordValidatorUtil.getMessage());
            }
            String password = encoder.encode(objModel.getPassword().replaceAll("\\s+", ""));
            List<Role> r = repoRole.findByNameIn(roleNames);
            user.setRoles(r);
            user.setPassword(password);
            User obj = repoUser.save(user);

            return templateResponse.success(obj);

        } catch (Exception e) {
            logger.error("Eror registerManual=", e);
            return templateResponse.error("eror:"+e);
        }

    }
    @Override
    public Map registerMerchant (User request, UUID merchantId) {
        Map map = new HashMap();
        try {
            Optional<User> checkDataDBUser = userRepository.findById(request.getId());
            String[] roleNames = {"ROLE_MERCHANT", "ROLE_MERCHANT_P"}; // admin
            List<Role> r = repoRole.findByNameIn(roleNames);
            List<Role> existingRole = checkDataDBUser.get().getRoles();
            existingRole.addAll(r);
            checkDataDBUser.get().setRoles(existingRole);
            checkDataDBUser.get().setMerchantId(merchantId);
            User obj = repoUser.save(checkDataDBUser.get());

            return templateResponse.success(obj);

        } catch (Exception e) {
            logger.error("Eror registerManual=", e);
            return templateResponse.error("eror:"+e);
        }

    }
    @Override
    public Map registerByGoogle(RegisterModel objModel) {
        Map map = new HashMap();
        try {
            String[] roleNames = {"ROLE_USER, ROLE_USER_O, ROLE_USER_OD"};
            User user = new User();
            user.setUsername(objModel.getUsername().toLowerCase());
            user.setFullname(objModel.getFullname());
            //step 1 :
            user.setEnabled(false); // matikan user
            String password = encoder.encode(objModel.getPassword().replaceAll("\\s+", ""));
            List<Role> r = repoRole.findByNameIn(roleNames);
            user.setRoles(r);
            user.setPassword(password);
            User obj = repoUser.save(user);
            return templateResponse.success(obj);

        } catch (Exception e) {
            logger.error("Eror registerManual=", e);
            return templateResponse.error("eror:"+e);
        }
    }

    @Override
    public Map<Object, Object> update(User request) {
        try {
            log.info("Update User");
            if (request.getId() == null) return templateResponse.error("Id is required");
            Optional<User> checkDataDBUser = userRepository.findById(request.getId());
            if (!checkDataDBUser.isPresent()) return templateResponse.error("Id is not Registered");
            if (!request.getFullname().isEmpty()) {
                checkDataDBUser.get().setFullname(request.getFullname());
            }

            log.info("Update User Success");
            return templateResponse.success(userRepository.save(checkDataDBUser.get()));
        } catch (Exception e) {
            log.error("Update User Error: " + e.getMessage());
            return templateResponse.error("Update User: " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> delete(User request) {
        try {
            log.info("Delete User");
            if (request.getId() == null) return templateResponse.error("Id is required");
            Optional<User> checkDataDBUser = userRepository.findById(request.getId());
            if (!checkDataDBUser.isPresent()) return templateResponse.error("User not Found");

            log.info("User Deleted");
            checkDataDBUser.get().setDeletedDate(new Date());
            return templateResponse.success(userRepository.save(checkDataDBUser.get()));
        } catch (Exception e) {
            log.error("Delete User Error: " + e.getMessage());
            return templateResponse.error("Delete User : " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> getById(Long id) {
        try {
            log.info("Get User");
            if (id == null) return templateResponse.error("Id is required");
            Optional<User> checkDataDBUser = userRepository.findById(id);
            if (!checkDataDBUser.isPresent()) return templateResponse.error("User not Found");

            log.info("User Found");
            return templateResponse.success(checkDataDBUser.get());
        } catch (Exception e) {
            log.error("Get User Error: " + e.getMessage());
            return templateResponse.error("Get User: " + e.getMessage());
        }
    }

    @Override
    public Map getDetailProfile(Principal principal) {
        User idUser = getUserIdToken(principal, userDetailsService);
        try {
            User obj = userRepository.save(idUser);
            return templateResponse.success(obj);
        } catch (Exception e){
            return templateResponse.error(e);
        }
    }

    private User getUserIdToken(Principal principal, Oauth2UserDetailsService userDetailsService) {
        UserDetails user = null;
        String username = principal.getName();
        if (!StringUtils.isEmpty(username)) {
            user = userDetailsService.loadUserByUsername(username);
        }

        if (null == user) {
            throw new UsernameNotFoundException("User not found");
        }
        User idUser = userRepository.findOneByUsername(user.getUsername());
        if (null == idUser) {
            throw new UsernameNotFoundException("User name not found");
        }
        return idUser;
    }

    @Override
    public Map<Object, Object> getIdByUserName(String username) {
        try {
            log.info("Get Id");
            User user = userRepository.findOneByUsername(username);
            if (user == null) return templateResponse.error("User not found");
            return templateResponse.success(user);
        } catch (Exception e) {
            return templateResponse.error(e);
        }
    }

}

