package com.example.springsecurity.repositories;

import com.example.springsecurity.models.CodeFellowUsers;
import com.example.springsecurity.models.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface PostsRepo extends JpaRepository<Posts,Long> {

    List<Posts> findByUserInOrderByCreatedAtDesc(Set<CodeFellowUsers> followedUsers);
}
