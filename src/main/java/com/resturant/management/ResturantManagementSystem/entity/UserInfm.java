package com.resturant.management.ResturantManagementSystem.entity;


import com.resturant.management.ResturantManagementSystem.model.Role;
import com.resturant.management.ResturantManagementSystem.util.DateTimeUtil;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "USER_INFM")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfm implements UserDetails {

    @Column(name = "BIZ_KEY")
    private String bizKey;

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_NM")
    private String userNm;

    @Column(name = "USER_PWD")
    private String userPwd;

    @Column(name = "TEL")
    private String tel;

    @Column(name = "EML")
    private String eml;

    @Column(name = "USER_IMG")
    private String usrImg;

    @Column(name = "LOCK_YN")
    private String lockYn;

    @Column(name = "LOGIN_FAILED_CNT")
    private String loginFailedCnt;

    @Column(name = "ACT_YN")
    private String actYn;

    @Column(name = "REG_ID")
    private String regId;

    @Column(name = "LST_LGN_DTM")
    private String lstLgnDtm;

    @Column(name = "REG_DTM")
    private String regDtm;

    @Column(name = "MOD_ID")
    private String modId;

    @Column(name = "MOD_DTM")
    private String modDtm;

    @PrePersist
    protected void onCreate() {
        regDtm = DateTimeUtil.FORMAT_MMM_DD_YYYY;
    }

    @PreUpdate
    protected void onUpdate() {
        modDtm = DateTimeUtil.FORMAT_MMM_DD_YYYY;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return userPwd;
    }

    @Override
    public String getUsername() {
        return userId;
    }
}
