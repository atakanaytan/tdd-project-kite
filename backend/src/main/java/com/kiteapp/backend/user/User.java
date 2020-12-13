package com.kiteapp.backend.user;

import com.kiteapp.backend.UniqueUsername;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue
    private long id;

    @NotBlank(message = "{user.username.NotBlank}")
    @Size(min = 8, max = 255, message = "{user.username.Size}")
    @UniqueUsername
    private String username;

    @NotBlank(message = "{user.displayname.NotBlank}")
    @Size(min = 8, max = 255, message = "{user.displayname.Size}"
    )
    private String displayName;


    @NotBlank(message = "{user.password.NotBlank}")
    @Size(min = 8, max = 255, message = "{user.password.Size}")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message="{user.password.Pattern}")
    private String password;
}
