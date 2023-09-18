package com.example.springsecurity.configs;

import com.example.springsecurity.models.CodeFellowUsers;
import com.example.springsecurity.repositories.CodeFellowRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CodeFellowUserDetails implements UserDetailsService {
    @Autowired
    CodeFellowRepo codeFellowRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CodeFellowUsers codeFellowUser=codeFellowRepo.findByUsername(username);
        if(codeFellowUser==null)
        {
            System.out.println("User Not Found" + username);
            throw new UsernameNotFoundException("User" + username + "Was Not Found");
        }
        System.out.println("Found User: " + codeFellowUser.getUsername());
        return codeFellowUser;
    }
}
