package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.user.ConfirmationToken;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.exceptions.TechnicalIssueException;
import com.needus.ecommerce.repository.user.UserInformationRepository;
import com.needus.ecommerce.service.verification.ConfirmationTokenService;
import com.needus.ecommerce.service.user.UserInformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;

@Controller
@Slf4j
@EnableWebSecurity
@Configuration
public class MainController {
    @Autowired
    UserInformationService userInformationService;
    @Autowired
    ConfirmationTokenService tokenService;
    @Autowired
    UserInformationRepository repository;

//    @Bean
//    public AuthenticationProvider authenticationProvider(){
//        return new CustomAuthenticationManager();
//    }
//    private void authenticateUserAndSetSession(UserInformation user, HttpServletRequest request) {
//        String username = user.getUsername();
//        String password = user.getPassword();
//        UsernamePasswordAuthenticationToken token =
//            new UsernamePasswordAuthenticationToken(username, password);
//        token.setDetails(new WebAuthenticationDetails(request));
//        Authentication authenticatedUser = authenticationProvider().authenticate(token);
//        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
//        request.getSession().setAttribute("Sample", SecurityContextHolder.getContext().getAuthentication());
//    }
    @GetMapping("/")
    public String index(){
        return "redirect:/shop/home";
    }
    @GetMapping("/login")
    public String login(){
        log.info("Inside login");
        return "login";
    }
    @GetMapping("/signup")
    public String signup(){
        log.info("Inside signup");
        return "register";
    }
    @PostMapping ("/register")
    public String register
        (@ModelAttribute UserInformation user, Model model, RedirectAttributes ra)
    {
        log.info("registering the user");
        if(repository.existsByUsername(user.getUsername())){
            return "redirect:/signup?userNameError=true";
        }
        if(repository.existsByEmail(user.getEmail())){
            return "redirect:/signup?emailError=true";
        }
        try {
            userInformationService.register(user);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new TechnicalIssueException("Something went wrong while saving the user in the userService side",e);
        }
        log.info("user registered");
        ra.addFlashAttribute("message","Success! A verification email has been sent to your email");
        return "redirect:/login?registrationSuccess=true";
    }
//    @PostMapping("/user-forgot-password")
//    public String
    @GetMapping("/activation")
    public String activation(@RequestParam("token") String token,Model model){
        ConfirmationToken confirmationToken = tokenService.findByToken(token);
        if(confirmationToken == null){
            model.addAttribute("message","Your verification token is invalid");

        }
        else{
            UserInformation userInformation = confirmationToken.getUserInformation();
            if(!userInformation.isEnabled()){
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                if(confirmationToken.getExpiryDate().before(currentTime)){
                    model.addAttribute("message","Your verification token has been expired");
                }
                else{
                    userInformation.setEnabled(true);
                    userInformationService.save(userInformation);
                    model.addAttribute("message","Your account is activated");
                }

            }
            else{
                model.addAttribute("message","Your account is already activated");
            }
        }
        return "activation";
    }
//    @GetMapping("/change-password")
//    public String changePassword(){
//        return "changePasswordForm";
//    }
//
//
//    @PostMapping("/send-otp")
//    public String sendOtp(
//        @RequestParam(name="username") String username,
//        RedirectAttributes ra
//    ) {
//        log.info("inside sendOtp :: " + username);
//        if(!userInformationService.usersExistsByUsername(username){
//            ra.addFlashAttribute("message","Username is not found");
//            return "redirect:/change-password";
//        }
//        String phoneNumber = userInformationService.findUserByName(username).getPhoneNumber();
//        smsService.sendSMS(phoneNumber);
//        return "redirect:/otp-submission";
//    }
//    @GetMapping("/otp-submission")
//    public String otpSubmission(){
//        return "otpSubmission";
//    }
//    @PostMapping("/validate-otp")
//    public String validateOtp(
//        @RequestParam(name="otpCode") String otpCode
//    ) {
//        log.info("inside validateOtp :: "+otpValidationRequest.getUsername()+" "+otpValidationRequest.getOtpNumber());
//        if(smsService.validateOtp(otpValidationRequest)){
//            return "redirect:/changePassword";
//        }
//        else {
//            return "redirect:/otp-submission";
//        }
//    }

    @RequestMapping("/error/404")
    public String handleExternalError() {
        return "404";
    }
    @RequestMapping("/error/500")
    public String handleInternalError() {
        return "404";
    }
}
