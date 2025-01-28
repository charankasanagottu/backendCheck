package com.kakz.tradeIn.repository;


import com.kakz.tradeIn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
}
