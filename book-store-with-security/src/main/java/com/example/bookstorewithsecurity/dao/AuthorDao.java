package com.example.bookstorewithsecurity.dao;


import com.example.bookstorewithsecurity.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorDao extends JpaRepository<Author,Integer> {
}
