package org.bsrserver.altair.velocity.accounts;

import org.bsrserver.altair.velocity.AltairVelocity;
import org.bsrserver.altair.velocity.data.Account;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class AccountOnlinePlayersManager {
    private final AltairVelocity altairVelocity;
    private final HashMap<Integer, Integer> onlinePlayersCount = new HashMap<>();

    public AccountOnlinePlayersManager(AltairVelocity altairVelocity) {
        this.altairVelocity = altairVelocity;
    }

    public int getOnlinePlayersCount(Account account) {
        return onlinePlayersCount.getOrDefault(account.getAccountId(), 0);
    }

    public void login(UUID uuid) {
        Optional<Account> accountOptional = altairVelocity.getDataManager().getAccount(uuid);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            onlinePlayersCount.put(account.getAccountId(), getOnlinePlayersCount(account) + 1);
        }
    }

    public void logout(UUID uuid) {
        Optional<Account> accountOptional = altairVelocity.getDataManager().getAccount(uuid);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            int newOnlineAccountCount = getOnlinePlayersCount(account) - 1;
            if (newOnlineAccountCount <= 0) {
                onlinePlayersCount.remove(account.getAccountId());
            } else {
                onlinePlayersCount.put(account.getAccountId(), newOnlineAccountCount);
            }
        }
    }
}
