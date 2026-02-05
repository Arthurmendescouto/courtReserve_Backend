package com.example.court_reserve.config;

import com.example.court_reserve.entity.Booking;
import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.SportType;
import com.example.court_reserve.entity.User;
import com.example.court_reserve.repository.BookingRepository;
import com.example.court_reserve.repository.CourtRepository;
import com.example.court_reserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedCourts();
        seedBookings();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            User user = User.builder()
                    .email("teste@exemplo.com")
                    .password(new BCryptPasswordEncoder().encode("123456"))
                    .build();
            userRepository.save(user);
        }
    }

    private void seedCourts() {
        if (courtRepository.count() == 0) {
            System.out.println(">> Populando banco de dados com quadras de teste...");

            List<Court> courts = Arrays.asList(
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
            );
            courtRepository.saveAll(courts);

            System.out.println(">> " + courts.size() + " quadras inseridas com sucesso!");
        }
    }

    private void seedBookings() {
        if (bookingRepository.count() == 0) {
            // Pega o usuário e as quadras criadas
            User user = userRepository.findAll().get(0);
            List<Court> courts = courtRepository.findAll();

            // Encontra a primeira quadra de Futebol disponível
            Court footballCourt = courts.stream()
                    .filter(c -> c.getSportType() == SportType.FOOTBALL && c.isAvailable())
                    .findFirst()
                    .orElse(null);

            if (footballCourt != null) {
                // Cria um agendamento para AMANHÃ das 14:00 às 15:00
                // Fixando a data para bater com o exemplo do Swagger (21/12/2024)
                LocalDateTime start = LocalDateTime.of(2024, 12, 21, 14, 0, 0);
                LocalDateTime end = start.plusHours(1);

                Booking booking = Booking.builder()
                        .user(user)
                        .court(footballCourt)
                        .startDateTime(start)
                        .endDateTime(end)
                        .build();

                bookingRepository.save(booking);
                System.out.println(">> Agendamento de teste criado para: " + start);
            }
        }
    }
}