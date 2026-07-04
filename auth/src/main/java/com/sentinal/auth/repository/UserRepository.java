package com.sentinal.auth.repository;

import com.sentinal.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    public Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
