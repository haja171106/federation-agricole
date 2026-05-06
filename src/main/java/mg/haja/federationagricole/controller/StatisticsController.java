package mg.haja.federationagricole.controller;

import mg.haja.federationagricole.DTO.CollectivityLocalStatistics;
import mg.haja.federationagricole.DTO.CollectivityOverallStatistics;
import mg.haja.federationagricole.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * GET /collectivites/{id}/statistics?from=2026-01-01&to=2026-05-06
     * Stats locales : montant encaissé + impayé potentiel par membre.
     */
    @GetMapping("/collectivites/{id}/statistics")
    public ResponseEntity<List<CollectivityLocalStatistics>> getLocalStatistics(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(statisticsService.getLocalStatistics(id, from, to));
    }

    /**
     * GET /collectivites/statistics?from=2026-01-01&to=2026-05-06
     * Stats globales : % membres à jour + nouveaux adhérents par collectivité.
     */
    @GetMapping("/collectivites/statistics")
    public ResponseEntity<List<CollectivityOverallStatistics>> getOverallStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(statisticsService.getOverallStatistics(from, to));
    }
}