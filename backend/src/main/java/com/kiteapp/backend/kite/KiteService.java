package com.kiteapp.backend.kite;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class KiteService {

    KiteRepository kiteRepository;

    public KiteService(KiteRepository kiteRepository) {
        this.kiteRepository = kiteRepository;
    }

    public void save(Kite kite) {
        kite.setTimestamp(new Date());
        kiteRepository.save(kite);
    }

}
