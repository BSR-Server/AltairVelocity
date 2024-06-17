package org.bsrserver.altair.velocity.data;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Cache implements Serializable {
    private final HashMap<Integer, Account> accountHashMap;
    private final List<String> quotations;
    private final HashMap<UUID, MinecraftProfile> minecraftProfileHashMap;
    private final HashMap<String, ServerInfo> serverInfoHashMap;
    private final List<ServerGroup> serverGroups;
}
