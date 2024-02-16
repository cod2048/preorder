package com.hanghae.module_user.user.repository;

import com.hanghae.module_user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
