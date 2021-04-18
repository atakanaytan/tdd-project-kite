package com.kiteapp.backend.user;

import com.kiteapp.backend.kite.Kite;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.beans.Transient;
import java.util.Collection;
import java.util.List;

@Data
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private long id;

    @NotBlank(message = "{user.username.NotBlank}")
    @Size(min = 4, max=255)
    @UniqueUsername
    private String username;

    @NotBlank(message = "{user.displayname.NotBlank}")
    @Size(min = 4, max=255)
    private String displayName;


    @NotBlank(message = "{user.password.NotBlank}")
    @Size(min = 8, max=255)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message="{user.password.Pattern}")
    private String password;

    private String image;

    @OneToMany(mappedBy = "user")
    private List<Kite> kites;

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
