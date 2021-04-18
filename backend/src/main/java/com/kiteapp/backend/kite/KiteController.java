package com.kiteapp.backend.kite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/1.0")
public class KiteController {

    @Autowired
    KiteService kiteService;

    @PostMapping("/kites")
    void createKites(@Valid @RequestBody Kite kite) {
        kiteService.save(kite);
    }
}