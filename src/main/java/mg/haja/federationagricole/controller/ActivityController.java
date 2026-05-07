package mg.haja.federationagricole.controller;

import mg.haja.federationagricole.DTO.CreateActivityMemberAttendance;
import mg.haja.federationagricole.DTO.CreateCollectivityActivity;
import mg.haja.federationagricole.Entity.ActivityMemberAttendance;
import mg.haja.federationagricole.Entity.CollectivityActivity;
import mg.haja.federationagricole.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities/{id}/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<List<CollectivityActivity>> createActivities(
            @PathVariable String id,
            @RequestBody List<CreateCollectivityActivity> requests) {

        return ResponseEntity.ok(activityService.createActivities(id, requests));
    }

    @GetMapping
    public ResponseEntity<List<CollectivityActivity>> getActivities(
            @PathVariable String id) {

        return ResponseEntity.ok(activityService.getActivities(id));
    }

    @PostMapping("/{activityId}/attendance")
    public ResponseEntity<List<ActivityMemberAttendance>> saveAttendance(
            @PathVariable String id,
            @PathVariable String activityId,
            @RequestBody List<CreateActivityMemberAttendance> requests) {

        return ResponseEntity.status(201)
                .body(activityService.saveAttendance(id, activityId, requests));
    }

    @GetMapping("/{activityId}/attendance")
    public ResponseEntity<List<ActivityMemberAttendance>> getAttendance(
            @PathVariable String id,
            @PathVariable String activityId) {

        return ResponseEntity.ok(activityService.getAttendance(id, activityId));
    }
}