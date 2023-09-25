package com.example.springsecurity.controllers;

import com.example.springsecurity.models.CodeFellowUsers;
import com.example.springsecurity.models.Posts;
import com.example.springsecurity.repositories.CodeFellowRepo;
import com.example.springsecurity.repositories.PostsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class CodeFellowController {
    @Autowired
    CodeFellowRepo codeFellowRepo;

    @Autowired
    PostsRepo postsRepo;
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
    public RedirectView createUser(String username, String password, String firstName, String lastName,String dateOfBirth,String bio,String image){
        CodeFellowUsers codeFellowUser= new CodeFellowUsers();

        codeFellowUser.setUsername(username);
        codeFellowUser.setBio(bio);
        codeFellowUser.setFirstName(firstName);
        codeFellowUser.setLastName(lastName);
        codeFellowUser.setDateOfBirth(dateOfBirth);

        if (image == null || image.isEmpty()) {
            codeFellowUser.setImage("https://media.istockphoto.com/vectors/default-profile-picture-avatar-photo-placeholder-vector-illustration-vector-id1214428300?k=6&m=1214428300&s=170667a&w=0&h=hMQs-822xLWFz66z3Xfd8vPog333rNFHU6Q_kc9Sues=");
        } else {
            codeFellowUser.setImage(image);
        }

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

    @GetMapping("/myProfile")
    public String myProfile(Principal p,Model m){
        if (p != null) {
            String username = p.getName();
            CodeFellowUsers codeFellowUser = codeFellowRepo.findByUsername(username);
            m.addAttribute("user", codeFellowUser);
        }
        return "userPage";
    }
    @GetMapping("/updateProfile")
    public String editProfilePage(Principal p,Model m) {
        if (p != null) {
            String username = p.getName();
            CodeFellowUsers codeFellowUser = codeFellowRepo.findByUsername(username);
            m.addAttribute("user", codeFellowUser);
        }
        return "editUserPage";
    }
    @PutMapping("/updateProfile")
    public RedirectView editMyProfile (Principal p, String username, String firstName, String lastName, String dateOfBirth, String bio,String image, RedirectAttributes redirectAttributes){
        if ((p != null) && (p.getName().equals(username))) {
            CodeFellowUsers codeFellowUser=codeFellowRepo.findByUsername(username);
            codeFellowUser.setUsername(username);
            codeFellowUser.setFirstName(firstName);
            codeFellowUser.setLastName(lastName);
            codeFellowUser.setDateOfBirth(dateOfBirth);
            codeFellowUser.setBio(bio);
            codeFellowUser.setImage(image);
            codeFellowRepo.save(codeFellowUser);
        }else{
            redirectAttributes.addFlashAttribute("errorMessage","Cannot edit another user profile page");
        }
        return new RedirectView("/myProfile");
    }

    @GetMapping("/allUsers")
    public String allUsers(Principal p, Model m) {
        List<CodeFellowUsers> allUsers = codeFellowRepo.findAll();
        if (p != null) {
            String username = p.getName();
            CodeFellowUsers codeFellowUser = codeFellowRepo.findByUsername(username);
            m.addAttribute("user", codeFellowUser);
            allUsers = allUsers.stream()
                    .filter(user -> !user.getId().equals(codeFellowUser.getId()))
                    .collect(Collectors.toList());
        }

        m.addAttribute("users", allUsers);
        return "findUsers";
    }

    @GetMapping("/users/{id}")
    public String getUserInfo(Model m, @PathVariable Long id) {
        CodeFellowUsers codeFellowUser = codeFellowRepo.findById(id).orElseThrow();
        m.addAttribute("user", codeFellowUser);
        return "specificUserProfile";
    }

    @GetMapping("/discoverUsers")
    public String discoverUsers(Principal p, Model m) {
        CodeFellowUsers codeFellowUser = null;
        if (p != null) {
            String username = p.getName();
            codeFellowUser = codeFellowRepo.findByUsername(username);
            m.addAttribute("user", codeFellowUser);
        }
        List<CodeFellowUsers> allUsers = codeFellowRepo.findAll();

        if (codeFellowUser != null) {
            final CodeFellowUsers userToExclude = codeFellowUser;
            allUsers = allUsers.stream()
                    .filter(user -> !user.getId().equals(userToExclude.getId()))
                    .collect(Collectors.toList());
        }

        m.addAttribute("users", allUsers);
        return "discoverUsers";
    }

    @PostMapping("/followUser/{userId}")
    public RedirectView followUser(Principal p, @PathVariable Long userId) {
        if (p != null) {
            String username = p.getName();
            CodeFellowUsers loggedInUser = codeFellowRepo.findByUsername(username);
            CodeFellowUsers userToFollow = codeFellowRepo.findById(userId).orElse(null);

            if (userToFollow != null) {
                loggedInUser.getFollowedUsers().add(userToFollow);
                codeFellowRepo.save(loggedInUser);
            }
        }
        return new RedirectView("/users/" + userId);
    }

    @PostMapping("/unfollowUser/{userId}")
    public RedirectView unfollowUser(Principal p, @PathVariable Long userId) {
        if (p != null) {
            String username = p.getName();
            CodeFellowUsers loggedInUser = codeFellowRepo.findByUsername(username);
            CodeFellowUsers userToUnfollow = codeFellowRepo.findById(userId).orElse(null);

            if (userToUnfollow != null) {
                loggedInUser.getFollowedUsers().remove(userToUnfollow);
                codeFellowRepo.save(loggedInUser);
            }
        }
        return new RedirectView("/users/" + userId);
    }



    @GetMapping("/feed")
    public String viewFeed(Principal p, Model m) {
        if (p != null) {
            String username = p.getName();
            CodeFellowUsers loggedInUser = codeFellowRepo.findByUsername(username);
            Set<CodeFellowUsers> followedUsers = loggedInUser.getFollowedUsers();
            List<Posts> feedPosts = postsRepo.findByUserInOrderByCreatedAtDesc(followedUsers);
            m.addAttribute("feedPosts", feedPosts);
        }
        return "feed";
    }


}
