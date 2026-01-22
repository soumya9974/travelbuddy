package com.travelbuddy.service;

import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.TravelGroupRepository;
import com.travelbuddy.repository.MembershipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelGroupServiceTest {

    @Mock
    private TravelGroupRepository travelGroupRepository;

    @Mock
    private MembershipRepository membershipRepository;

    @InjectMocks
    private TravelGroupService travelGroupService;

    @Test
    void createGroup_shouldSaveGroupAndAdminMembership() {
        User user = new User();
        user.setId(1L);

        TravelGroup group = new TravelGroup();
        group.setName("Paris Trip");

        when(travelGroupRepository.save(any())).thenReturn(group);

        TravelGroup result = travelGroupService.createGroup(group);

        assertEquals("Paris Trip", result.getName());
        verify(membershipRepository).save(any());
    }

    @Test
    void getGroupById_notFound_shouldThrowException() {
        when(travelGroupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> travelGroupService.getGroupById(1L));
    }
}
