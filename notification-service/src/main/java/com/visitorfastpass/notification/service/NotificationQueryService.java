package com.visitorfastpass.notification.service;

import com.visitorfastpass.notification.api.dto.NotificationResponse;
import com.visitorfastpass.notification.api.dto.PageResponse;
import com.visitorfastpass.notification.domain.Notification;
import com.visitorfastpass.notification.repository.NotificationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationQueryService {
    private final NotificationRepository repository;

    public NotificationQueryService(NotificationRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> findAll(int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        var result = repository.findAll(PageRequest.of(Math.max(page, 0), safeSize,
                        Sort.by(Sort.Direction.DESC, "createdAt"))).map(this::toResponse);
        return PageResponse.from(result);
    }

    public NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getEventId(), n.getVisitId(), n.getPassNumber(),
                n.getRecipientName(), n.getRecipientEmail(), n.getDigitalPassUrl(), n.getNotificationType(), n.getStatus(),
                n.getScheduledAt(), n.getRetryCount(), n.getFailureReason(), n.getCreatedAt(), n.getSentAt());
    }
}
