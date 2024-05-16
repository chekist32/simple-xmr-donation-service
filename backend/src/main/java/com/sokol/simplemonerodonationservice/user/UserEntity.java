package com.sokol.simplemonerodonationservice.user;

import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue
    private Integer id;
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
    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @JoinColumn(
            name = "donation_user_data_id",
            unique = true,
            nullable = false
    )
    private DonationUserDataEntity donationUserData;

    protected UserEntity() { }

    public UserEntity(String email,
                      String username,
                      String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Integer getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity user = (UserEntity) o;

        if (!Objects.equals(id, user.id)) return false;
        if (!Objects.equals(username, user.username)) return false;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
