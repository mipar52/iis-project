package com.milan.iis_backend.okta;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class OktaRestClientConfig {
    @Bean
    public RestClient oktaClient(@Value("${okta.domain}") String oktaDomain, @Value("${okta.token}") String oktaToken) {
        return RestClient.builder()
                .baseUrl(oktaDomain)
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "SSWS " + oktaToken)
                .build();
    }
}

