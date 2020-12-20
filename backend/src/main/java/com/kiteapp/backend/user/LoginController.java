package com.kiteapp.backend.user;

import com.kiteapp.backend.shared.CurrentUser;
import org.hibernate.mapping.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class LoginController {

    @PostMapping("/api/1.0/login")
    Map<String, Object> handleLogin(@CurrentUser User loggedInUser) {
        return Collections.singletonMap("id", loggedInUser.getId());
    }
}
