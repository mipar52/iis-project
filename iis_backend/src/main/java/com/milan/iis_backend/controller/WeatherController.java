package com.milan.iis_backend.controller;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @GrpcClient("weather")
    private com.milan.iis_backend.grpc.WeatherServiceGrpc.WeatherServiceBlockingStub stub;

    @GetMapping("/search")
    public CityTemperatureResponseDto search(@RequestParam("q") String q) {
        var resp = stub.searchCityTemperature(com.milan.iis_backend.grpc.CityQuery.newBuilder()
                .setQuery(q == null ? "" : q)
                .build());

        var matches = resp.getMatchesList().stream()
                .map(m -> new CityTemperatureDto(m.getCity(), m.getTemperature(), m.getUnit(), m.getWeather()))
                .toList();

        return new CityTemperatureResponseDto(matches);
    }

    public record CityTemperatureResponseDto(java.util.List<CityTemperatureDto> matches) {}
    public record CityTemperatureDto(String city, String temperature, String unit, String weather) {}
}