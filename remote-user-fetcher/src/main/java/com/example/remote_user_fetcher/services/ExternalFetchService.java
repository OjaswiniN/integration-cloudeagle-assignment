package com.example.remote_user_fetcher.services;

import com.example.remote_user_fetcher.models.ExternalEndpoint;
import com.example.remote_user_fetcher.models.FieldMapping;
import com.example.remote_user_fetcher.models.TemporaryUser;
import com.example.remote_user_fetcher.repositories.ExternalEndpointRepository;
import com.example.remote_user_fetcher.repositories.FieldMappingRepository;
import com.example.remote_user_fetcher.repositories.TemporaryUserRepository;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class ExternalFetchService {
    private final ExternalEndpointRepository endpointRepo;
    private final FieldMappingRepository mappingRepo;
    private final TemporaryUserRepository userRepo;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ExternalFetchService(ExternalEndpointRepository endpointRepo,
                                FieldMappingRepository mappingRepo,
                                TemporaryUserRepository userRepo) {
        this.endpointRepo = endpointRepo;
        this.mappingRepo = mappingRepo;
        this.userRepo = userRepo;
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    // Return Mono for reactive handling
    public Mono<List<TemporaryUser>> fetchAndStore(String endpointName) {
        return Mono.fromCallable(() -> loadEndpoint(endpointName))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(endpoint -> callExternalApiReactive(endpoint)
                        .flatMap(rawResponse -> processAndSaveUsers(rawResponse, endpoint))
                );
    }

    private ExternalEndpoint loadEndpoint(String endpointName) {
        return endpointRepo.findByName(endpointName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown endpoint: " + endpointName));
    }

    private Mono<String> callExternalApiReactive(ExternalEndpoint endpoint) {
        Map<String, String> headers = parseHeaders(endpoint.getHeadersJson());

        return webClient.method(HttpMethod.valueOf(endpoint.getHttpMethod()))
                .uri(endpoint.getUrl())
                .headers(h -> headers.forEach(h::add))
                .retrieve()
                .bodyToMono(String.class)
                .defaultIfEmpty("{}");
    }

    private Mono<List<TemporaryUser>> processAndSaveUsers(String rawResponse, ExternalEndpoint endpoint) {
        return Mono.fromCallable(() -> {
            List<Object> items = extractItems(rawResponse, endpoint.getListJsonPath());
            List<FieldMapping> mappings = mappingRepo.findByEndpoint(endpoint);
            List<TemporaryUser> usersToSave = mapItemsToUsers(items, mappings, endpoint.getName());
            return userRepo.saveAll(usersToSave);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // Rest of your helper methods remain the same...
    private Map<String, String> parseHeaders(String headersJson) {
        if (headersJson == null || headersJson.isBlank()) return Collections.emptyMap();
        try {
            return objectMapper.readValue(headersJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Invalid headers JSON", e);
        }
    }

    private List<Object> extractItems(String response, String listPath) {
        String path = Optional.ofNullable(listPath).orElse("$");
        Object document = com.jayway.jsonpath.Configuration.defaultConfiguration()
                .jsonProvider().parse(response);

        try {
            // Use JsonPath configuration that handles single items
            Configuration conf = Configuration.defaultConfiguration()
                    .addOptions(Option.ALWAYS_RETURN_LIST);

            List<Object> result = JsonPath.using(conf).parse(document).read(path);
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<TemporaryUser> mapItemsToUsers(List<Object> items, List<FieldMapping> mappings, String source) {
        return items.stream()
                .map(item -> convertItemToUser(item, mappings, source))
                .collect(Collectors.toList());
    }

    private TemporaryUser convertItemToUser(Object item, List<FieldMapping> mappings, String source) {
        TemporaryUser user = new TemporaryUser();
        user.setRawJson(item.toString());
        user.setSource(source);

        Map<String, Consumer<String>> fieldSetters = Map.of(
                "userId", user::setUserId,
                "fullName", user::setFullName,
                "email", user::setEmail
        );

        for (FieldMapping fm : mappings) {
            Object value = extractValue(item, fm.getSourceJsonPath());
            if (value != null && fieldSetters.containsKey(fm.getTargetField())) {
                fieldSetters.get(fm.getTargetField()).accept(value.toString());
            }
        }
        return user;
    }

    private Object extractValue(Object item, String path) {
        try {
            return JsonPath.read(item, path);
        } catch (Exception ex) {
            return null;
        }
    }
}