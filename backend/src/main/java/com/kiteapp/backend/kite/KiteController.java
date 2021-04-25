package com.kiteapp.backend.kite;

import com.kiteapp.backend.kite.vm.KiteVM;
import com.kiteapp.backend.shared.CurrentUser;
import com.kiteapp.backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/1.0")
public class KiteController {

    @Autowired
    KiteService kiteService;

    @PostMapping("/kites")
    KiteVM createKites(@Valid @RequestBody Kite kite, @CurrentUser User user) {
        return new KiteVM(kiteService.save(user, kite));
    }

    @GetMapping("/kites")
    Page<KiteVM> getAllKites(Pageable pageable) {
        return kiteService.getAllKites(pageable).map(KiteVM::new);
    }

    @GetMapping("/users/{username}/kites")
    Page<KiteVM> getKitesOfUser(@PathVariable String username, Pageable pageable) {
        return kiteService.getKitesOfUser(username, pageable).map(KiteVM::new);
    }
}
