package com.travelbuddy.service;

import com.travelbuddy.exception.ResourceNotFoundException;
import com.travelbuddy.model.Membership;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.MembershipRepository;
import com.travelbuddy.repository.TravelGroupRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TravelGroupService {

    private final TravelGroupRepository travelGroupRepository;
    private final MembershipRepository membershipRepository;

    public TravelGroupService(
            TravelGroupRepository travelGroupRepository,
            MembershipRepository membershipRepository) {
        this.travelGroupRepository = travelGroupRepository;
        this.membershipRepository = membershipRepository;
    }


    public List<TravelGroup> getAllGroups() {
        return travelGroupRepository.findAll();
    }

    public TravelGroup getGroupById(Long id) {
        return travelGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
    }

    public TravelGroup createGroup(TravelGroup group) {

        // 1️⃣ Save group
        TravelGroup savedGroup = travelGroupRepository.save(group);

        // 2️⃣ Create ADMIN membership for creator
        Membership adminMembership = new Membership();
        adminMembership.setUser(group.getCreatedBy());
        adminMembership.setGroup(savedGroup);
        adminMembership.setRole("ADMIN");
        adminMembership.setJoinedAt(LocalDateTime.now());

        membershipRepository.save(adminMembership);

        return savedGroup;
    }


    public TravelGroup updateGroup(Long id, TravelGroup updatedGroup) {
        TravelGroup existingGroup = getGroupById(id);
        existingGroup.setName(updatedGroup.getName());
        existingGroup.setDescription(updatedGroup.getDescription());
        existingGroup.setDestination(updatedGroup.getDestination());
        return travelGroupRepository.save(existingGroup);
    }

 // ---------------- DELETE GROUP (ADMIN ONLY) ----------------
    public void deleteGroup(Long groupId, User user) {

        TravelGroup group = travelGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Membership membership = membershipRepository
                .findByUserIdAndGroupId(user.getId(), groupId)
                .orElseThrow(() -> new RuntimeException("You are not a member of this group"));

        if (!"ADMIN".equals(membership.getRole())) {
            throw new RuntimeException("Only admins can delete this group");
        }

        travelGroupRepository.delete(group);
    }

}
