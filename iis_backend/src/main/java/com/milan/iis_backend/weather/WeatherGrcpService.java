package com.milan.iis_backend.weather;

import com.milan.iis_backend.grpc.CityQuery;
import com.milan.iis_backend.grpc.CityTemperature;
import com.milan.iis_backend.grpc.CityTemperatureResponse;
import com.milan.iis_backend.grpc.WeatherServiceGrpc;
import com.milan.iis_backend.service.interfaces.grcp.CityTemp;
import com.milan.iis_backend.service.interfaces.grcp.WeatherService;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@AllArgsConstructor
public class WeatherGrcpService extends WeatherServiceGrpc.WeatherServiceImplBase {
    private final WeatherService weatherService;

    @Override
    public void searchCityTemperature(CityQuery request, StreamObserver<CityTemperatureResponse> responseObserver) {
        try {
            List<CityTemp> list = weatherService.search(request.getQuery());

            CityTemperatureResponse.Builder response = CityTemperatureResponse.newBuilder();
            for (CityTemp temp: list) {
                response.addMatches(CityTemperature.newBuilder()
                        .setCity(temp.city())
                        .setTemperature(temp.temperature())
                        .setUnit("°C")
                        .build());
            }
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
    }
}
