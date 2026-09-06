package com.visitorfastpass.notification.api;

import com.visitorfastpass.notification.api.dto.NotificationResponse;
import com.visitorfastpass.notification.api.dto.PageResponse;
import com.visitorfastpass.notification.service.NotificationProcessingService;
import com.visitorfastpass.notification.service.NotificationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@Tag(name = "Admin Notifications")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class AdminNotificationController {
    private final NotificationQueryService queryService;
    private final NotificationProcessingService processingService;

    public AdminNotificationController(NotificationQueryService queryService,
                                       NotificationProcessingService processingService) {
        this.queryService = queryService;
        this.processingService = processingService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('NOTIFICATION_VIEW_ALL')")
    @Operation(summary = "View email delivery and simulation history")
    public PageResponse<NotificationResponse> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return queryService.findAll(page, size);
    }

    @PostMapping("/{notificationId}/retry")
    @PreAuthorize("hasAuthority('NOTIFICATION_VIEW_ALL')")
    @Operation(summary = "Retry or simulate a stored pass email")
    public NotificationResponse retry(@PathVariable long notificationId) {
        return queryService.toResponse(processingService.retry(notificationId));
    }
}
