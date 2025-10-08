package com.resturant.management.ResturantManagementSystem.entity;


import com.resturant.management.ResturantManagementSystem.model.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "USERS_INFM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfm implements UserDetails {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String username;
//    private String upass;
//    private String uName;
//    private String uRole;
//
//    @Lob
//    private byte[] pImage;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // Auto-generated ID as primary key

    @Column(name = "biz_key", nullable = false, unique = true, length = 10)
    private String bizKey;

    private String userId;
    private String userNm;
    private String userPwd;
    private String tel;
    private String eml;
    private String usrImg;
    private String lockYn;
    private Integer loginFailedCnt;
    private String actYn;
    private String regId;
    private String lstLgnDtm;
    private String regDtm;
    private String modId;
    private String modDtm;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public String getPassword() {
        return userPwd;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"Y".equals(lockYn);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "Y".equals(actYn);
    }

    // Helper methods for login lockout functionality
    public void incrementFailedLoginAttempts() {
        this.loginFailedCnt = (this.loginFailedCnt == null) ? 1 : this.loginFailedCnt + 1;
        if (this.loginFailedCnt >= 5) {
            this.lockYn = "Y";
        }
    }

    public void resetFailedLoginAttempts() {
        this.loginFailedCnt = 0;
        this.lockYn = "N";
    }

    public boolean isLocked() {
        return "Y".equals(this.lockYn);
    }
}
