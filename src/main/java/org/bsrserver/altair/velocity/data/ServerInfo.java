package org.bsrserver.altair.velocity.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ServerInfo {
    private final Integer serverId;
    private final String serverName;
    private final String givenName;
    private final LocalDate foundationDate;
    private final int priority;
}
