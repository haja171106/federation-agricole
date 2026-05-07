package mg.haja.federationagricole.service;

import mg.haja.federationagricole.DTO.CreateActivityMemberAttendance;
import mg.haja.federationagricole.DTO.CreateCollectivityActivity;
import mg.haja.federationagricole.Entity.ActivityMemberAttendance;
import mg.haja.federationagricole.Entity.CollectivityActivity;
import mg.haja.federationagricole.repository.ActivityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<CollectivityActivity> createActivities(String collectivityId,
                                                       List<CreateCollectivityActivity> requests) {
        try {
            if (!activityRepository.collectivityExists(collectivityId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Collectivity not found: " + collectivityId);
            }

            for (CreateCollectivityActivity req : requests) {
                if (req.executiveDate != null && req.recurrenceRule != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Cannot provide both executiveDate and recurrenceRule");
                }
                if (req.label == null || req.label.isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Activity label is required");
                }
                if (req.activityType == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Activity type is required");
                }
            }

            return requests.stream()
                    .map(req -> {
                        try {
                            return activityRepository.save(collectivityId, req);
                        } catch (IllegalArgumentException e) {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                        } catch (SQLException e) {
                            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                        }
                    })
                    .toList();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public List<CollectivityActivity> getActivities(String collectivityId) {
        try {
            if (!activityRepository.collectivityExists(collectivityId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Collectivity not found: " + collectivityId);
            }
            return activityRepository.findByCollectivityId(collectivityId);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public List<ActivityMemberAttendance> saveAttendance(String collectivityId,
                                                         String activityId,
                                                         List<CreateActivityMemberAttendance> requests) {
        try {
            if (!activityRepository.collectivityExists(collectivityId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Collectivity not found: " + collectivityId);
            }
            if (!activityRepository.activityExists(activityId, collectivityId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Activity not found: " + activityId);
            }

            return activityRepository.saveAttendance(activityId, requests);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    public List<ActivityMemberAttendance> getAttendance(String collectivityId,
                                                        String activityId) {
        try {
            if (!activityRepository.collectivityExists(collectivityId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Collectivity not found: " + collectivityId);
            }
            if (!activityRepository.activityExists(activityId, collectivityId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Activity not found: " + activityId);
            }

            return activityRepository.findAttendance(collectivityId, activityId);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}