package com.example.court_reserve.config;

import com.example.court_reserve.entity.Booking;
import com.example.court_reserve.entity.Court;
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

import java.time.LocalDateTime;
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
        // Limpa o banco de dados antigo para garantir que as novas quadras (com nomes) sejam criadas
        bookingRepository.deleteAll();
        courtRepository.deleteAll();
        userRepository.deleteAll();

        seedUsers();
        seedCourts();
        seedBookings();
    }

    private void seedUsers() {
            User user = User.builder()
                    .name("Usuário Teste")
                    .email("teste@exemplo.com")
                    .password(new BCryptPasswordEncoder().encode("123456"))
                    .build();
            userRepository.save(user);
    }

    private void seedCourts() {
            log.info(">> Populando banco de dados com quadras de teste...");

            List<Court> courts = Arrays.asList(
                    Court.builder().name("Arena Gol de Placa").sportType(SportType.FOOTBALL).pricePerHour(120.0).isAvailable(true).build(),
                    Court.builder().name("Ace Tennis Club").sportType(SportType.TENNIS).pricePerHour(80.0).isAvailable(true).build(),
                    Court.builder().name("Ginásio Jordan").sportType(SportType.BASKETBALL).pricePerHour(100.0).isAvailable(true).build(),
                    Court.builder().name("Centro de Vôlei Saque").sportType(SportType.VOLLEYBALL).pricePerHour(90.0).isAvailable(true).build(),
                    Court.builder().name("Estádio Manutenção").sportType(SportType.FOOTBALL).pricePerHour(150.0).isAvailable(false).build(),
                    Court.builder().name("Grand Slam Arena").sportType(SportType.TENNIS).pricePerHour(210.0).isAvailable(true).build(),
                    Court.builder().name("Quadra dos Campeões").sportType(SportType.BASKETBALL).pricePerHour(115.0).isAvailable(true).build(),
                    Court.builder().name("Vôlei de Praia Indoor").sportType(SportType.VOLLEYBALL).pricePerHour(85.0).isAvailable(true).build(),
                    Court.builder().name("Campo Society 7").sportType(SportType.FOOTBALL).pricePerHour(130.0).isAvailable(true).build(),
                    Court.builder().name("Quadra Rápida Open").sportType(SportType.TENNIS).pricePerHour(190.0).isAvailable(true).build()
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
                log.info(">> Agendamento de teste criado para: {}", start);
            }
    }
}