package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.user.ConfirmationToken;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.exceptions.TechnicalIssueException;
import com.needus.ecommerce.repository.user.UserInformationRepository;
import com.needus.ecommerce.service.security.OtpService;
import com.needus.ecommerce.service.verification.ConfirmationTokenService;
import com.needus.ecommerce.service.user.UserInformationService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Controller
@Slf4j
public class MainController {
    @Autowired
    UserInformationService userInformationService;
    @Autowired
    ConfirmationTokenService tokenService;
    @Autowired
    UserInformationRepository repository;
    @Autowired
    OtpService otpService;

    @GetMapping("/")
    public String index(){
        return "redirect:/shop/home";
    }
    @GetMapping("/login")
    public String login(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (!(authentication instanceof AnonymousAuthenticationToken)){
            if(roles.contains("ADMIN")){
                return "redirect:/admin/dashboard/sales-report";
            }
            if(roles.contains("USER")){
                return "redirect:/shop/home";
            }
        }
        log.info("Inside login");
        return "login";
    }
    @GetMapping("/signup")
    public String signup(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (!(authentication instanceof AnonymousAuthenticationToken)){
            if(roles.contains("ADMIN")){
                return "redirect:/admin/dashboard/sales-report";
            }
            if(roles.contains("USER")){
                return "redirect:/shop/home";
            }
        }
        log.info("Inside signup");
        return "register";
    }

    @PostMapping ("/register")
    public String register
        (@ModelAttribute UserInformation user, Model model, RedirectAttributes ra) {
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

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model,HttpSession session){
        session.removeAttribute("otp");
        return "forgotPassword";
    }
    @PostMapping("/change-password")
    public String changePassword(HttpSession session,
                                 @ModelAttribute UserInformation userInformation,
                                 RedirectAttributes ra) throws AccessDeniedException {
        if(session.getAttribute("username")==null){
            throw new AccessDeniedException("User Submitting unauthorized request");
        }
       String username =  session.getAttribute("username").toString();
       UserInformation user = userInformationService.findUserByName(username);
       log.info("password : "+userInformation.getPassword());
       userInformationService.changePassword(user,userInformation.getPassword());
       session.removeAttribute("username");
        ra.addFlashAttribute("successMessage","Password Changed successfully");
        return "/login";
    }
    @PostMapping("/check-username")
    @ResponseBody
    public ResponseEntity<Map<String,Boolean>> checkUsername(@RequestBody Map<String,String> data,
                                                             HttpSession session){
        String username = data.get("username");
        UserInformation user;
        try {
            user = userInformationService.findUserByName(username);
        }
        catch (Exception e){
            log.error("Something went wrong while fetching user");
            throw new TechnicalIssueException("Something went wrong while fetching user");
        }
        if(Objects.isNull(user)){
            return new ResponseEntity<>(Map.of("success",false), HttpStatus.OK);
        }
        if(Objects.isNull(user.getPhoneNumber())){
            return new ResponseEntity<>(Map.of("success",false), HttpStatus.OK);
        }
        session.setAttribute("otp",otpService.sendOtp("+91"+user.getPhoneNumber()));
        return new ResponseEntity<>(Map.of("success",true), HttpStatus.OK);
    }

    @PostMapping("/check-otp")
    @ResponseBody
    public ResponseEntity<Map<String,Boolean>> checkOtp(@RequestBody Map<String,String> data,
                                                        HttpSession session){
       String otp =  session.getAttribute("otp").toString();
       String otpReceived = data.get("otp");
       String username = data.get("user");
       if(!otpReceived.equals(otp)){
           return new ResponseEntity<>(Map.of("success",false), HttpStatus.OK);
       }
       session.setAttribute("username",username);
       session.removeAttribute("otp");
        return new ResponseEntity<>(Map.of("success",true), HttpStatus.OK);
    }
    @RequestMapping("/error/404")
    public String handleExternalError() {
        return "404";
    }
    @RequestMapping("/error/500")
    public String handleInternalError() {
        return "500";
    }
}
