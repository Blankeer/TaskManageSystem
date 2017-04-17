package com.task.repository;

import com.task.bean.User;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by blanke on 17-1-25.
 */
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    User findByEmail(String email);
    User findByToken(String token);
}
