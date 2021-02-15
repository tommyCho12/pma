package com.jrp.pma.controllers;

import com.jrp.pma.entities.UserAccount;
import com.jrp.pma.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class SecurityController
{
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserAccountService userAccountService;

    @GetMapping("/register")
    public String register(Model model){
        UserAccount userAccount = new UserAccount();
        model.addAttribute("userAccount", userAccount);

        return "security/register";
    }

    @PostMapping("/register/save")
    public String saveUser(Model model, @Valid UserAccount user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userAccountService.save(user);

        return "redirect:/";
    }
}
