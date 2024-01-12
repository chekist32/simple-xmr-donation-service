package com.sokol.simplemonerodonationservice.user;

import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue
    private Integer Id;
    @Column(
            nullable = false,
            unique = true
    )
    private String username;
    @Column(
            nullable = false,
            unique = true
    )
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = Set.of(Role.USER);
    @Column(nullable = false)
    private boolean isEnabled = false;
    @Column(
            nullable = false,
            unique = true
    )
    private String token = UUID.randomUUID().toString();
    @OneToOne
    @JoinColumn(
            name = "donation_user_data_id",
            unique = true
    )
    private DonationUserDataEntity donationUserData;

    public UserEntity() { }

    public UserEntity(String email,
                      String username,
                      String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Integer getId() {
        return Id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public DonationUserDataEntity getDonationUserData() {
        return donationUserData;
    }

    public void setDonationUserData(DonationUserDataEntity donationUserData) {
        this.donationUserData = donationUserData;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return UserUtils.RoleSetToGrantedAuthoritySetMapper(roles);
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
