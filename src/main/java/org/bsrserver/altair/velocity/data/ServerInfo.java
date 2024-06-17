package org.bsrserver.altair.velocity.data;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class ServerInfo implements Serializable {
    private final Integer serverId;
    private final String serverName;
    private final String givenName;
    private final LocalDate foundationDate;
    private final int priority;
}
