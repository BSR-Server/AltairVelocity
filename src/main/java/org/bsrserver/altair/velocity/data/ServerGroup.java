package org.bsrserver.altair.velocity.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ServerGroup {
    private Integer groupId;
    private String name;
    private List<Integer> parents;
    private List<Integer> servers;
    private List<Integer> accounts;
    private List<ServerGroup> parentObjects;
    private List<ServerInfo> serverObjects;

    public List<ServerInfo> getAllServers() {
        // get servers
        List<ServerInfo> servers = new ArrayList<>(this.serverObjects);

        // merge children servers
        for (ServerGroup parent : parentObjects) {
            servers.addAll(parent.getAllServers());
        }

        // return all servers
        return servers;
    }
}
