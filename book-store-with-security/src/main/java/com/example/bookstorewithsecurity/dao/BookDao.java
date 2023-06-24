package com.example.bookstorewithsecurity.dao;


import com.example.bookstorewithsecurity.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookDao extends JpaRepository<Book,Integer> {
}
