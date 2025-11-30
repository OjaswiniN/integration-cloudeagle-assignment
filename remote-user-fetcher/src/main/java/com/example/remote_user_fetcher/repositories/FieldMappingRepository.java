package com.example.remote_user_fetcher.repositories;

import com.example.remote_user_fetcher.models.ExternalEndpoint;
import com.example.remote_user_fetcher.models.FieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FieldMappingRepository extends JpaRepository<FieldMapping, Long> {
    List<FieldMapping> findByEndpoint(ExternalEndpoint endpoint);
}
