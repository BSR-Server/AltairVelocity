package org.bsrserver.altair.velocity.data;

import lombok.Data;

import java.util.List;

@Data
public class ServerGroup {
    private Integer groupId;
    private String name;
    private List<Integer> parents;
    private List<Integer> servers;
    private List<Integer> accounts;
}
