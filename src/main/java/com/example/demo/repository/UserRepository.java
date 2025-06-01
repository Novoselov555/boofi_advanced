package com.example.demo.repository;

import com.example.demo.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.role = 'USER'")
    List<User> findUsersWithRoleUser();

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.role = 'USER'")
    void deleteAllUsers();

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
