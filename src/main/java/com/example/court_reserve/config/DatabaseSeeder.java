package com.example.court_reserve.config;

import com.example.court_reserve.entity.Booking;
import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.Role;
import com.example.court_reserve.entity.SportType;
import com.example.court_reserve.entity.User;
import com.example.court_reserve.repository.BookingRepository;
import com.example.court_reserve.repository.CourtRepository;
import com.example.court_reserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public void run(String... args) throws Exception {
        if (courtRepository.count() == 0) {
            seedUsers();
            seedCourts();
            seedBookings();
        }
    }

    private void seedUsers() {
            User admin = User.create(
                    "Admin do Sistema",
                    "admin@exemplo.com",
                    new BCryptPasswordEncoder().encode("123456"),
                    Role.ADMIN
            );
            userRepository.save(admin);

            User client = User.create(
                    "Cliente Jogador",
                    "cliente@exemplo.com",
                    new BCryptPasswordEncoder().encode("123456"),
                    Role.CLIENT
            );
            userRepository.save(client);
    }

    private void seedCourts() {
            log.info(">> Populando banco de dados com quadras de teste...");

            List<Court> courts = Arrays.asList(
                    Court.create("Arena Gol de Placa", SportType.FOOTBALL, 120.0, true),
                    Court.create("Ace Tennis Club", SportType.TENNIS, 80.0, true),
                    Court.create("Ginásio Jordan", SportType.BASKETBALL, 100.0, true),
                    Court.create("Centro de Vôlei Saque", SportType.VOLLEYBALL, 90.0, true),
                    Court.create("Estádio Manutenção", SportType.FOOTBALL, 150.0, false),
                    Court.create("Grand Slam Arena", SportType.TENNIS, 210.0, true),
                    Court.create("Quadra dos Campeões", SportType.BASKETBALL, 115.0, true),
                    Court.create("Vôlei de Praia Indoor", SportType.VOLLEYBALL, 85.0, true),
                    Court.create("Campo Society 7", SportType.FOOTBALL, 130.0, true),
                    Court.create("Quadra Rápida Open", SportType.TENNIS, 190.0, true)
            );
            courtRepository.saveAll(courts);

            log.info(">> {} quadras inseridas com sucesso!", courts.size());
    }

    private void seedBookings() {
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
                // Usando uma data futura para não falhar na validação de "agendamento no passado"
                LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
                LocalDateTime end = start.plusHours(1);

                Booking booking = Booking.create(user, footballCourt, start, end);

                bookingRepository.save(booking);
                log.info(">> Agendamento de teste criado para: {}", start);
            }
    }
}