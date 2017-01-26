package com.task.repository;

import com.task.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by blanke on 17-1-25.
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    User findByToken(String token);
}
