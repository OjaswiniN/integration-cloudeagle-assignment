package com.example.remote_user_fetcher.controller;

import com.example.remote_user_fetcher.models.TemporaryUser;
import com.example.remote_user_fetcher.services.ExternalFetchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/fetch")
public class FetchController {

    private final ExternalFetchService fetchService;

    public FetchController(ExternalFetchService fetchService) {
        this.fetchService = fetchService;
    }

    @PostMapping("/{endpointName}")
    public Mono<ResponseEntity<List<TemporaryUser>>> fetch(@PathVariable String endpointName) {
        return fetchService.fetchAndStore(endpointName)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.badRequest().build());
    }
}
