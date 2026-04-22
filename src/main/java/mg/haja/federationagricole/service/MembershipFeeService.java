package mg.haja.federationagricole.service;

import mg.haja.federationagricole.model.CreateMembershipFee;
import mg.haja.federationagricole.model.MembershipFee;
import mg.haja.federationagricole.repository.MembershipFeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@Service
public class MembershipFeeService {

    private final MembershipFeeRepository repository;

    public MembershipFeeService(MembershipFeeRepository repository) {
        this.repository = repository;
    }

    public List<MembershipFee> getByCollectivity(String collectivityId) {
        try {
            if (!repository.collectivityExists(collectivityId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found");
            }
            return repository.findByCollectivityId(collectivityId);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public List<MembershipFee> create(String collectivityId, List<CreateMembershipFee> requests) {
        try {
            if (!repository.collectivityExists(collectivityId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found");
            }
            for (CreateMembershipFee req : requests) {
                if (req.getFrequency() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unrecognized frequency");
                }
                if (req.getAmount() < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be >= 0");
                }
            }
            return repository.saveAll(collectivityId, requests);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
