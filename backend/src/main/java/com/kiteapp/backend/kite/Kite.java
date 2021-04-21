package com.kiteapp.backend.kite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kiteapp.backend.user.User;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Entity
public class Kite {

    @Id
    @GeneratedValue
    private long id;

    @NotNull(message = "Cannot be null")
    @Size(min = 10, max = 5000)
    @Column(length = 5000)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @ManyToOne
    private User user;
}
