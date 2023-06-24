package com.example.bookstorewithsecurity.dao;

import com.example.bookstorewithsecurity.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleDao extends JpaRepository<Role,Integer> {
    Optional<Role> findRoleByName(String name);
}
