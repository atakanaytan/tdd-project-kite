package com.kiteapp.backend.kite;

import com.kiteapp.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class KiteService {

    KiteRepository kiteRepository;

    public KiteService(KiteRepository kiteRepository) {
        this.kiteRepository = kiteRepository;
    }

    public Kite save(User user, Kite kite) {
        kite.setTimestamp(new Date());
        kite.setUser(user);
        return kiteRepository.save(kite);
    }

    public Page<Kite> getAllKites(Pageable pageable) {
        return kiteRepository.findAll(pageable);
    }
}
