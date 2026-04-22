package mg.haja.federationagricole.service;

import mg.haja.federationagricole.model.CollectivityTransaction;
import mg.haja.federationagricole.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<CollectivityTransaction> getByPeriod(String collectivityId, LocalDate from, LocalDate to) {
        try {
            if (from == null || to == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query parameters 'from' and 'to' are mandatory");
            }
            if (from.isAfter(to)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'from' must be before 'to'");
            }
            if (!repository.collectivityExists(collectivityId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found");
            }
            return repository.findByCollectivityAndPeriod(collectivityId, from, to);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
