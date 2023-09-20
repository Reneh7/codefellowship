package com.example.springsecurity.repositories;

import com.example.springsecurity.models.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepo extends JpaRepository<Posts,Long> {

}
