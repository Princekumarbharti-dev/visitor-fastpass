package com.visitorfastpass.visitor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.visitorfastpass.visitor.api.dto.ApprovalRequest;
import com.visitorfastpass.visitor.api.dto.VisitorRegistrationRequest;
import com.visitorfastpass.visitor.client.IdentityClient.HostResponse;
import com.visitorfastpass.visitor.domain.PassProvisioningStatus;
import com.visitorfastpass.visitor.domain.Visit;
import com.visitorfastpass.visitor.domain.VisitEvent;
import com.visitorfastpass.visitor.domain.VisitStatus;
import com.visitorfastpass.visitor.domain.Visitor;
import com.visitorfastpass.visitor.exception.ApiException;
import com.visitorfastpass.visitor.repository.VisitEventRepository;
import com.visitorfastpass.visitor.repository.VisitRepository;
import com.visitorfastpass.visitor.repository.VisitorRepository;
import com.visitorfastpass.visitor.security.CurrentActor.Actor;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class VisitCommandServiceTest {
    @Mock VisitorRepository visitorRepository;
    @Mock VisitRepository visitRepository;
    @Mock VisitEventRepository eventRepository;
    @Mock PublicReferenceGenerator referenceGenerator;

    private VisitCommandService service;

    @BeforeEach
    void setUp() {
        service = new VisitCommandService(visitorRepository, visitRepository, eventRepository, referenceGenerator);
    }

    @Test
    void eachRegistrationCreatesANewVisitorRecord() {
        when(visitorRepository.save(any(Visitor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(eventRepository.save(any(VisitEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(referenceGenerator.next()).thenReturn("VF-ABC234567", "VF-XYZ765432");
        HostResponse host = new HostResponse(12L, "Asha Sharma", "Engineering", "Manager");

        var first = service.register(validRegistration(), host);
        var second = service.register(validRegistration(), host);

        assertThat(first.publicReference()).isNotEqualTo(second.publicReference());
        verify(visitorRepository, times(2)).save(any(Visitor.class));
        verify(visitRepository, times(2)).save(any(Visit.class));
    }

    @Test
    void assignedHostCanApprovePendingVisit() {
        Visit visit = pendingVisit(12L);
        when(visitRepository.findById(41L)).thenReturn(Optional.of(visit));
        when(eventRepository.save(any(VisitEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Actor actor = new Actor(5L, 12L, "HOST", "Asha Sharma");

        service.approve(41L, new ApprovalRequest("Approved"), actor);

        assertThat(visit.getStatus()).isEqualTo(VisitStatus.APPROVED);
        assertThat(visit.getPassProvisioningStatus()).isEqualTo(PassProvisioningStatus.PENDING);
    }

    @Test
    void differentHostCannotApproveVisit() {
        when(visitRepository.findById(41L)).thenReturn(Optional.of(pendingVisit(12L)));
        Actor otherHost = new Actor(9L, 99L, "HOST", "Another Host");

        assertThatThrownBy(() -> service.approve(41L, new ApprovalRequest(null), otherHost))
                .isInstanceOf(ApiException.class)
                .hasMessage("This visit request is assigned to another host");
    }

    @Test
    void approvedVisitCannotBeApprovedAgain() {
        Visit visit = pendingVisit(12L);
        visit.approve(null);
        when(visitRepository.findById(41L)).thenReturn(Optional.of(visit));
        Actor actor = new Actor(5L, 12L, "HOST", "Asha Sharma");

        assertThatThrownBy(() -> service.approve(41L, new ApprovalRequest(null), actor))
                .isInstanceOf(ApiException.class)
                .hasMessage("Only pending visits can be approved");
    }

    @Test
    void generatedPassVisitChecksInAndOutWithAuditEvents() {
        Visit visit = pendingVisit(12L);
        visit.approve(null);
        visit.passGenerated("FP-20260704-ABC234");
        when(visitRepository.findById(41L)).thenReturn(Optional.of(visit));
        when(eventRepository.save(any(VisitEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Actor reception = new Actor(7L, 33L, "RECEPTION", "Front Desk");

        service.checkIn(41L, reception);
        service.checkOut(41L, reception);

        assertThat(visit.getStatus()).isEqualTo(VisitStatus.COMPLETED);
        assertThat(visit.getCheckInTime()).isNotNull();
        assertThat(visit.getCheckOutTime()).isNotNull();
        verify(eventRepository, times(2)).save(any(VisitEvent.class));
    }

    private Visit pendingVisit(long hostId) {
        Visitor visitor = new Visitor("Riya Mehta", "9876543210", "riya@example.com", "Acme");
        Visit visit = new Visit("VF-ABC234567", visitor, hostId, "Asha Sharma",
                "Product demonstration", OffsetDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(visit, "id", 41L);
        return visit;
    }

    private VisitorRegistrationRequest validRegistration() {
        return new VisitorRegistrationRequest("Riya Mehta", "9876543210", "riya@example.com",
                "Acme", "Product demonstration", 12L, OffsetDateTime.now().plusDays(1));
    }
}
