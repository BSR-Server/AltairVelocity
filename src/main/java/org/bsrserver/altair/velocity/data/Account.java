package org.bsrserver.altair.velocity.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class Account implements Serializable {
    private Integer accountId;
    private String username;
    private Role role;
    private Boolean isBanned;
    private Boolean isActive;

    public boolean hasRole(Role role) {
        return role.ordinal() >= this.role.ordinal();
    }
}
