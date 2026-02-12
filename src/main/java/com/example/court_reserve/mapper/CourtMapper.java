package com.example.court_reserve.mapper;

import com.example.court_reserve.controller.request.CourtRequest;
import com.example.court_reserve.controller.response.CourtResponse;
import com.example.court_reserve.entity.Court;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CourtMapper {
    public static Court toCourt(CourtRequest request){
        return Court.create(
                request.name(),
                request.sportType(),
                request.pricePerHour(),
                request.isAvailable()
        );
    }
    public static CourtResponse toCourtResponse(Court court){
        return CourtResponse.builder()
                .id(court.getId())
                .name(court.getName())
                .sportType(court.getSportType())
                .pricePerHour(court.getPricePerHour())
                .isAvailable(court.isAvailable())
                .build();

    }
}
        //Long id, SportType sportType, Double pricePerHour, Boolean isAvailable