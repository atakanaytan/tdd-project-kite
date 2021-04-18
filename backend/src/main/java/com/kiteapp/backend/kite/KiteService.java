package com.kiteapp.backend.kite;

import com.kiteapp.backend.user.User;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class KiteService {

    KiteRepository kiteRepository;

    public KiteService(KiteRepository kiteRepository) {
        this.kiteRepository = kiteRepository;
    }

    public void save(User user, Kite kite) {
        kite.setTimestamp(new Date());
        kite.setUser(user);
        kiteRepository.save(kite);
    }

}
