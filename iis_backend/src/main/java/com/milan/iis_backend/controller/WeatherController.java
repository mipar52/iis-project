package com.milan.iis_backend.controller;

import com.milan.iis_backend.grpc.CityQuery;
import com.milan.iis_backend.service.interfaces.grcp.CityTemp;
import com.milan.iis_backend.service.interfaces.grcp.WeatherService;
import com.milan.iis_backend.weather.WeatherGrcpService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
@AllArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;
  //  private final WeatherGrcpService weatherGrcpService;

    @GetMapping("/search")
    public List<CityTemp> serach(@RequestParam(required = false) String query) throws Exception {
        return weatherService.search(query);
    }

//    @GetMapping("/search/grcp")
//    public List<CityTemp> searchViaGrcp(@RequestParam(required = false) String query) throws Exception {
//        var cityQuery = CityQuery.newBuilder().setQuery(query == null ? "" :query).build();
//        weatherGrcpService.searchCityTemperature(cityQuery);
//    }
}
