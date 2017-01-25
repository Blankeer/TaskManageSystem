package com.task.repository;

import com.task.bean.Content;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by blanke on 17-1-25.
 */
public interface ContentRepository extends JpaRepository<Content, Integer> {

}
