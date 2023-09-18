package com.example.springsecurity.controllers;

import com.example.springsecurity.models.CodeFellowUsers;
import com.example.springsecurity.repositories.CodeFellowRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;

@Controller
public class CodeFellowController {
    @Autowired
    CodeFellowRepo codeFellowRepo;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/")
    public String homePage(Principal p, Model m) {
        if(p != null){
            String username=p.getName();
            CodeFellowUsers codeFellowUser= codeFellowRepo.findByUsername(username);

            m.addAttribute("username",username);
        }
        return "homePage.html";
    }

    @GetMapping("/signup")
    public String signUpPage(){
        return "signup.html";
    }
    @PostMapping("/signup")
    public RedirectView createUser(String username, String password, String firstName, String lastName,String dateOfBirth,String bio){
        CodeFellowUsers codeFellowUser= new CodeFellowUsers();

        codeFellowUser.setUsername(username);
        codeFellowUser.setBio(bio);
        codeFellowUser.setFirstName(firstName);
        codeFellowUser.setLastName(lastName);
        codeFellowUser.setDateOfBirth(dateOfBirth);

        String encryptedPassword= passwordEncoder.encode(password);
        codeFellowUser.setPassword(encryptedPassword);

        codeFellowRepo.save(codeFellowUser);
        authWithHttpServletRequest(username,password);
        return new RedirectView("/");
    }

    public void authWithHttpServletRequest(String username, String password){
        try {
            request.login(username,password);
        }catch (ServletException e){
            e.printStackTrace();
        }
    }
    @GetMapping("/login")
    public String loginPage(){
        return "login.html";
    }
    @GetMapping("/logout")
    public String logoutPage(){
        return "homePage.html";
    }

    @GetMapping("/userInfo")
    public String userPage(Principal p,Model m){
        if (p != null) {
            String username = p.getName();
            CodeFellowUsers codeFellowUser = codeFellowRepo.findByUsername(username);
            m.addAttribute("user", codeFellowUser);
        }
        return "userPage";
    }
}
