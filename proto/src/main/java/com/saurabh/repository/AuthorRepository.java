package com.saurabh.repository;

import com.saurabh.model.GrpcAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface AutherRepository extends JpaRepository<GrpcAuthor, Long> {
}