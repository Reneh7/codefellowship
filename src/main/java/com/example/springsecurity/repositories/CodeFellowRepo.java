package com.example.springsecurity.repositories;

import com.example.springsecurity.models.CodeFellowUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeFellowRepo extends JpaRepository<CodeFellowUsers,Long> {

    CodeFellowUsers findByUsername(String username);
}
