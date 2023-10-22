package com.needus.ecommerce.controllers.admin;

import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.service.user.UserInformationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminCustomerController {

    //Field Injections
    @Autowired
    private UserInformationService userInformationService;

    @GetMapping("/users/list")
    public String users(Model model, HttpServletRequest request){
        model.addAttribute("requestURI", request.getRequestURI());
        List<UserInformation> user = userInformationService.findAllUsers();
        model.addAttribute("user",user);
        return "admin/customerList";
    }
    @PostMapping("/users/block/{id}")
    public String userBlock(@PathVariable(name = "id")UUID id, RedirectAttributes redirectAttributes,
    HttpServletRequest request, HttpServletResponse response){
        userInformationService.blockUser(id,request,response);
        if(userInformationService.findUserById(id).isEnabled()) {
            redirectAttributes.addFlashAttribute("successMsg", "User is enabled");
        }
        else{
            redirectAttributes.addFlashAttribute("successMsg", "User is disabled");
        }
        return "redirect:/admin/users/list";
    }
    @PostMapping("/users/delete/{id}")
    public String userDelete(@PathVariable(name = "id")UUID id, RedirectAttributes redirectAttributes){
        userInformationService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("successMsg","User is deleted Successfully");
        return "redirect:/admin/users/list";
    }

}

