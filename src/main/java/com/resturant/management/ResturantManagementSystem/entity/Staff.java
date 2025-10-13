package com.resturant.management.ResturantManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@Table(name = "STAFF_INFM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffID;

    private String sName;
    private String sPhone;
    private String sRole;

}
