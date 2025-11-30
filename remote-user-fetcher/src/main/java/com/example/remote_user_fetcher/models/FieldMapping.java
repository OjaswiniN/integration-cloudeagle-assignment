package com.example.remote_user_fetcher.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FieldMapping {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ExternalEndpoint endpoint;
    private String targetField;
    private String sourceJsonPath;
}
