package com.example.remote_user_fetcher.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ExternalEndpoint {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String url;
    private String httpMethod;
    @Lob
    private String headersJson;
    private String listJsonPath; // JSONPath to the array/list in response
}
