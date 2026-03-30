package com.milan.iis_backend.service.interfaces.grcp;

import java.util.List;

public interface WeatherService {
    public List<CityTemp> search(String query) throws Exception;
}

