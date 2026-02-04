package com.example.court_reserve.config;

import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.SportType;
import com.example.court_reserve.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final CourtService courtService;

    @Override
    public void run(String... args) throws Exception {
        if (courtService.count() == 0) {
            System.out.println(">> Populando banco de dados com quadras de teste...");

            Arrays.asList(
                    Court.builder().sportType(SportType.FOOTBALL).pricePerHour(120.0).isAvailable(true).build(),
                    Court.builder().sportType(SportType.TENNIS).pricePerHour(80.0).isAvailable(true).build(),
                    Court.builder().sportType(SportType.BASKETBALL).pricePerHour(100.0).isAvailable(true).build(),
                    Court.builder().sportType(SportType.VOLLEYBALL).pricePerHour(90.0).isAvailable(true).build(),
                    Court.builder().sportType(SportType.FOOTBALL).pricePerHour(150.0).isAvailable(false).build(),
                    Court.builder().sportType(SportType.TENNIS).pricePerHour(210.0).isAvailable(true).build(),
                    Court.builder().sportType(SportType.BASKETBALL).pricePerHour(115.0).isAvailable(true).build(),
                    Court.builder().sportType(SportType.VOLLEYBALL).pricePerHour(85.0).isAvailable(true).build(),
                    Court.builder().sportType(SportType.FOOTBALL).pricePerHour(130.0).isAvailable(true).build(),
                    Court.builder().sportType(SportType.TENNIS).pricePerHour(190.0).isAvailable(true).build()
            ).forEach(courtService::save);

            System.out.println(">> " + courtService.count() + " quadras inseridas com sucesso!");
        }
    }
}