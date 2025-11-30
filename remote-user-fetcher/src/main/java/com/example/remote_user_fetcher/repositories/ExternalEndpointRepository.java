package com.example.remote_user_fetcher.repositories;

import com.example.remote_user_fetcher.models.ExternalEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExternalEndpointRepository extends JpaRepository<ExternalEndpoint, Long> {
    Optional<ExternalEndpoint> findByName(String name);
}
