package com.example.remote_user_fetcher.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TemporaryUser {
    @Id
    @GeneratedValue
    private Long id;
    private String userId;
    private String fullName;
    private String email;
    @Column(columnDefinition = "TEXT")
    private String rawJson;
    private String source;
}