package com.kiteapp.backend.user;

import com.kiteapp.backend.UniqueUsername;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.beans.Transient;
import java.util.Collection;

@Data
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private long id;

    @NotBlank(message = "{user.username.NotBlank}")
    @Size(min = 8, max = 255, message = "{user.username.Size}")
    @UniqueUsername
    private String username;

    @NotBlank(message = "{user.displayname.NotBlank}")
    @Size(min = 8, max = 255, message = "{user.displayname.Size}")
    private String displayName;


    @NotBlank(message = "{user.password.NotBlank}")
    @Size(min = 8, max = 255, message = "{user.password.Size}")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message="{user.password.Pattern}")
    private String password;

    private String image;

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("Role_USER");
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return true;
    }
}
