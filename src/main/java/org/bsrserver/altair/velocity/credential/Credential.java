package org.bsrserver.altair.velocity.credential;

public record Credential(
        String username,
        String password,
        boolean isGot
) {
}
