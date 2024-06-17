package org.bsrserver.altair.velocity.greeter;

import lombok.Data;
import net.kyori.adventure.text.Component;

@Data
public class ServerListServerComponent implements Comparable<ServerListServerComponent> {
    private final int priority;
    private final Component component;

    @Override
    public int compareTo(ServerListServerComponent o) {
        return this.priority - o.priority;
    }
}
