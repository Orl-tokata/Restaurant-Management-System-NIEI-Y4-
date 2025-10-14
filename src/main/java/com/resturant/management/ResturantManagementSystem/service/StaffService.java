package com.resturant.management.ResturantManagementSystem.service;

import com.resturant.management.ResturantManagementSystem.entity.Staff;
import com.resturant.management.ResturantManagementSystem.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Optional<Staff> getStaffById(Long id) {
        return staffRepository.findById(id);
    }

    public Staff createStaff(Staff staff) {
        // ✅ ensure ID is null (avoid OptimisticLocking error)
        staff.setStaffID(null);
        return staffRepository.save(staff);
    }

    public Staff updateStaff(Long id, Staff updated) {
        Staff existing = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        existing.setSname(updated.getSname());
        existing.setSphone(updated.getSphone());
        existing.setSrole(updated.getSrole());

        return staffRepository.save(existing);
    }

    public void deleteStaff(Long id) {
        if (!staffRepository.existsById(id)) {
            throw new RuntimeException("Staff not found");
        }
        staffRepository.deleteById(id);
    }
}