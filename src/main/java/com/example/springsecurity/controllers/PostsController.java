package com.example.springsecurity.controllers;

import com.example.springsecurity.models.CodeFellowUsers;
import com.example.springsecurity.models.Posts;
import com.example.springsecurity.repositories.CodeFellowRepo;
import com.example.springsecurity.repositories.PostsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;
@Controller
public class PostsController {
    @Autowired
    CodeFellowRepo codeFellowRepo;
    @Autowired
    PostsRepo postsRepo;

    @GetMapping("/viewCreatedPost")
    public String viewCreatedPost(Principal p, Model model) {
        if (p != null) {
            String username = p.getName();
            CodeFellowUsers codeFellowUser = codeFellowRepo.findByUsername(username);

            model.addAttribute("user", codeFellowUser);
        }
        return "userPage";
    }
    @PostMapping("/createPost")
    public RedirectView createPost(Principal p, @RequestParam String body) {
        if (p != null) {
            String username = p.getName();
            CodeFellowUsers codeFellowUser = codeFellowRepo.findByUsername(username);

            Posts post = new Posts();

            post.setBody(body);
            post.setUser(codeFellowUser);
            post.setCreatedAt(LocalDate.now());

            postsRepo.save(post);
        }
        return new RedirectView("/myProfile");
    }

}
