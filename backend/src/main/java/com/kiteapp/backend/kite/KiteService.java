package com.kiteapp.backend.kite;

import com.kiteapp.backend.user.User;
import com.kiteapp.backend.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class KiteService {

    KiteRepository kiteRepository;

    UserService userService;

    public KiteService(KiteRepository kiteRepository, UserService userService ) {
        this.kiteRepository = kiteRepository;
        this.userService = userService;
    }

    public Kite save(User user, Kite kite) {
        kite.setTimestamp(new Date());
        kite.setUser(user);
        return kiteRepository.save(kite);
    }

    public Page<Kite> getAllKites(Pageable pageable) {
        return kiteRepository.findAll(pageable);
    }

    public Page<Kite> getKitesOfUser(String username, Pageable pageable) {
        User inDB = userService.getByUsername(username);
        return kiteRepository.findByUser(inDB, pageable);
    }
}
