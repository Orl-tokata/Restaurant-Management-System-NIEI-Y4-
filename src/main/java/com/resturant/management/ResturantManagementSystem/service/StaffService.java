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

    public Staff saveStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public Staff updateStaff(Long id, Staff staffDetails) {
        return staffRepository.findById(id)
                .map(staff -> {
                    staff.setSName(staffDetails.getSName());
                    staff.setSPhone(staffDetails.getSPhone());
                    staff.setSRole(staffDetails.getSRole());
                    return staffRepository.save(staff);
                })
                .orElseThrow(() -> new RuntimeException("Staff not found with id " + id));
    }

    public void deleteStaff(Long id) {
        staffRepository.deleteById(id);
    }
}
