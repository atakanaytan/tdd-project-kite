package com.kiteapp.backend.kite.vm;

import com.kiteapp.backend.kite.Kite;
import com.kiteapp.backend.user.viewModel.UserVM;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KiteVM {

    private long id;

    private String content;

    private long date;

    private UserVM user;

    public KiteVM(Kite kite) {
        this.setId(kite.getId());
        this.setContent(kite.getContent());
        this.setDate(kite.getTimestamp().getTime());
        this.setUser(new UserVM(kite.getUser()));
    }
}
