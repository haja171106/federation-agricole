package mg.haja.federationagricole.service;

import mg.haja.federationagricole.DTO.CollectivityLocalStatistics;
import mg.haja.federationagricole.DTO.CollectivityOverallStatistics;
import mg.haja.federationagricole.repository.StatisticsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    public StatisticsService(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    public List<CollectivityLocalStatistics> getLocalStatistics(
            String collectivityId, LocalDate from, LocalDate to) {

        if (from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "'from' must be before or equal to 'to'");
        }

        try {
            return statisticsRepository.findLocalStatistics(collectivityId, from, to);
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public List<CollectivityOverallStatistics> getOverallStatistics(
            LocalDate from, LocalDate to) {

        if (from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "'from' must be before or equal to 'to'");
        }

        try {
            return statisticsRepository.findOverallStatistics(from, to);
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}