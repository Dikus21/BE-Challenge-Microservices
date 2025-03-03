package com.example.user.controller;

import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.request.RegisterMerchant;
import com.example.user.request.RegisterModel;
import com.example.user.service.email.EmailSender;
import com.example.user.service.UserService;
import com.example.user.utils.EmailTemplate;
import com.example.user.utils.SimpleStringUtils;
import com.example.user.utils.TemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/v1/user-register/")
public class RegisterController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    public EmailSender emailSender;
    @Autowired
    public EmailTemplate emailTemplate;
//    @Autowired
//    public MerchantService merchantService;

    @Value("${expired.token.password.minute:}")//FILE_SHOW_RUL
    private int expiredToken;

    @Autowired
    public UserService serviceReq;

    @Autowired
    public SimpleStringUtils simpleStringUtils;

    @Autowired
    public TemplateResponse templateResponse;
    @PostMapping("/register-user")
    public ResponseEntity<Map> saveRegisterManual(@Valid @RequestBody RegisterModel objModel) throws RuntimeException {
        Map map = new HashMap();

        User user = userRepository.checkExistingEmail(objModel.getUsername());
        if (null != user) {
            return new ResponseEntity<Map>(templateResponse.error("Username sudah ada"), HttpStatus.OK);

        }
        map = serviceReq.registerManual(objModel);

        return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

    @PutMapping("/register-merchant")
    public ResponseEntity<Map> saveRegisterManual(@RequestBody RegisterMerchant objModel) throws RuntimeException {
        Map map = new HashMap();

        Optional<User> user = userRepository.findById(objModel.getUserId());
        if (!user.isPresent()) {
            return new ResponseEntity<Map>(templateResponse.error("User not found"), HttpStatus.BAD_REQUEST);
        } else if (user.get().getMerchantId() != null) return new ResponseEntity<Map>(templateResponse.error("Account merchant already exist"), HttpStatus.BAD_REQUEST);
        map = serviceReq.registerMerchant(user.get(), objModel.getMerchantId());

        return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

    @PostMapping("/register-google")
    public ResponseEntity<Map> saveRegisterByGoogle(@Valid @RequestBody RegisterModel objModel) throws RuntimeException {
        Map map = new HashMap();

        User user = userRepository.checkExistingEmail(objModel.getUsername());
        if (null != user) {
            return new ResponseEntity<Map>(templateResponse.error("Username sudah ada"), HttpStatus.OK);

        }
        map = serviceReq.registerByGoogle(objModel);
        //gunanya send email
        Map mapRegister =  sendEmailegister(objModel);
        return new ResponseEntity<Map>(mapRegister, HttpStatus.OK);

    }



    // Step 2: sendp OTP berupa URL: guna updeta enable agar bisa login:
    @PostMapping("/send-otp")//send OTP
    public Map sendEmailegister(
            @RequestBody RegisterModel user) {
        String message = "Thanks, please check your email for activation.";

        if (user.getUsername() == null) return templateResponse.error("No email provided");
        User found = userRepository.findOneByUsername(user.getUsername());
        if (found == null) return templateResponse.error("Email not found"); //throw new BadRequest("Email not found");

        String template = emailTemplate.getRegisterTemplate();
        if (StringUtils.isEmpty(found.getOtp())) {
            User search;
            String otp;
            do {
                otp = SimpleStringUtils.randomString(6, true);
                search = userRepository.findOneByOTP(otp);
            } while (search != null);
            Date dateNow = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateNow);
            calendar.add(Calendar.MINUTE, expiredToken);
            Date expirationDate = calendar.getTime();

            found.setOtp(otp);
            found.setOtpExpiredDate(expirationDate);
            template = template.replaceAll("\\{\\{USERNAME}}", (found.getFullname() == null ? found.getUsername() : found.getFullname()));
            template = template.replaceAll("\\{\\{VERIFY_TOKEN}}",  otp);
            userRepository.save(found);
        } else {
            template = template.replaceAll("\\{\\{USERNAME}}", (found.getFullname() == null ? found.getUsername() : found.getFullname()));
            template = template.replaceAll("\\{\\{VERIFY_TOKEN}}",  found.getOtp());
        }
        emailSender.sendAsync(found.getUsername(), "Register", template);
        return templateResponse.success(message);
    }

    @GetMapping("/register-confirm-otp/{token}")
    public ResponseEntity<Map> saveRegisterManual(@PathVariable(value = "token") String tokenOtp) throws RuntimeException {



        User user = userRepository.findOneByOTP(tokenOtp);
        if (null == user) {
            return new ResponseEntity<Map>(templateResponse.error("OTP tidak ditemukan"), HttpStatus.OK);
        }
//validasi jika sebelumnya sudah melakukan aktifasi

        if(user.isEnabled()){
            return new ResponseEntity<Map>(templateResponse.error("Akun Anda sudah aktif, Silahkan melakukan login"), HttpStatus.OK);
        }
        String today = simpleStringUtils.convertDateToString(new Date());

        String dateToken = simpleStringUtils.convertDateToString(user.getOtpExpiredDate());
        if(Long.parseLong(today) > Long.parseLong(dateToken)){
            return new ResponseEntity<Map>(templateResponse.error("Your token is expired. Please Get token again."), HttpStatus.OK);
        }
        //update user
        user.setEnabled(true);
        userRepository.save(user);

        return new ResponseEntity<Map>(templateResponse.success("Sukses, Silahkan Melakukan Login"), HttpStatus.OK);
    }


}
