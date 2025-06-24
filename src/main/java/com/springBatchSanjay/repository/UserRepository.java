package com.springBatchSanjay.repository;

import com.springBatchSanjay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {


}
