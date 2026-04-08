package com.milan.iis_backend.module_grcp.grcpmodels;

import java.util.List;

public interface WeatherService {
    public List<CityTemp> search(String query) throws Exception;
}

