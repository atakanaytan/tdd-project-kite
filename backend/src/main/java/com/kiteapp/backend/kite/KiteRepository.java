package com.kiteapp.backend.kite;

import com.kiteapp.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface KiteRepository extends JpaRepository<Kite, Long> {

    Page<Kite> findByUser(User user, Pageable pageable);

}
