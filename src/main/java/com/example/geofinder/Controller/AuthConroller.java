package com.example.geofinder.Controller;

import com.example.geofinder.Model.UserDto;
import com.example.geofinder.Model.UserModel;
import com.example.geofinder.Service.UserServiceImpl;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

public class AuthConroller {

    final UserServiceImpl service;

    public AuthConroller(UserServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String logPage() {
        return "login";
    }
    @GetMapping("/registerPage")
    public String getReg(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "registerPage";
    }
    @PostMapping("/registerPage")
    public String addUser(@ModelAttribute("user") UserDto userDto, BindingResult result) {
        if (!userDto.getEmail().isEmpty() && service.unique(userDto.getEmail())) {
            UserModel userModel = service.findUserByEmail(userDto.getEmail());
            if (userModel != null || userDto.getEmail().isEmpty())
                result.rejectValue("email", "300", "User already registered !!!");
            service.saveUser(userDto);
        } else result.rejectValue("email", "300", "Email is not initialization !!!");

        return "redirect:/login";
    }
}
