package com.resturant.management.ResturantManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "STAFF_INFM")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffID;

    private String sname;
    private String sphone;
    private String srole;


    @ManyToOne
    @JoinColumn(
            name = "USER_ID",
            referencedColumnName = "USER_ID",
            foreignKey = @ForeignKey(name = "FK_STAFF_USER")
    )
    private UserInfm user;
}