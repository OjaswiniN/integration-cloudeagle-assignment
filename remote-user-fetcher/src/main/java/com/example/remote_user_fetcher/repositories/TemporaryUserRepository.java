package com.example.remote_user_fetcher.repositories;

import com.example.remote_user_fetcher.models.TemporaryUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TemporaryUserRepository extends JpaRepository<TemporaryUser, Long> {
}
