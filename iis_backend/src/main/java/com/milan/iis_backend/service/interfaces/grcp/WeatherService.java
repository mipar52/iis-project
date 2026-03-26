package com.milan.iis_backend.service.interfaces.grcp;

import com.milan.iis_backend.grpc.CityTemperature;

import java.util.List;

public interface WeatherService {
    public List<CityTemp> search(String query) throws Exception;
}

