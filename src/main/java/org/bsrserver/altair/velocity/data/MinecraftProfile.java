package org.bsrserver.altair.velocity.data;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
public class MinecraftProfile implements Serializable {
    private Integer accountId;
    private UUID uuid;
    private Instant scheduledDeletionAt;
}
