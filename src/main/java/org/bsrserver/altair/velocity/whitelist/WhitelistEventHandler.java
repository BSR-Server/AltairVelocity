package org.bsrserver.altair.velocity.whitelist;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.bsrserver.altair.velocity.AltairVelocity;
import org.bsrserver.altair.velocity.data.Account;
import org.bsrserver.altair.velocity.data.Role;
import org.bsrserver.altair.velocity.event.ILoginEventHandler;
import org.bsrserver.altair.velocity.event.IServerPreConnectEventHandler;

import java.util.Optional;

public class WhitelistEventHandler implements ILoginEventHandler, IServerPreConnectEventHandler {
    private static final int MAX_ONLINE_PLAYERS = 1;
    private static final Component ACCOUNT_BANNED_MESSAGE = Component.text("§cYour account has been banned!");
    private static final Component ACCOUNT_INACTIVE_MESSAGE = Component.text("§cYour account is inactive, please contact admin!");
    private static final Component ACCOUNT_LOGGED_IN_ELSEWHERE_MESSAGE = Component.text("§cYour account has been logged in elsewhere!");
    private static final Component NOT_WHITELISTED_MESSAGE = Component.text("§cYou are not white-listed on this server!");

    private void denyOrDisconnectPlayerAtLoginEvent(LoginEvent event, Component message) {
        event.setResult(ResultedEvent.ComponentResult.denied(message));
        event.getPlayer().disconnect(message);
    }

    private void denyOrDisconnectPlayerAtServerPreConnectEvent(ServerPreConnectEvent event, Component message) {
        event.setResult(ServerPreConnectEvent.ServerResult.denied());
        if (event.getPlayer().getCurrentServer().isPresent()) {
            event.getPlayer().sendMessage(message);
        } else {
            event.getPlayer().disconnect(message);
        }
    }

    private WhitelistCheckResult checkAccount(AltairVelocity altairVelocity, Player player) {
        // get account
        Optional<Account> accountOptional = altairVelocity.getDataManager().getAccount(player.getUniqueId());
        if (accountOptional.isEmpty()) {
            return WhitelistCheckResult.ACCOUNT_NOT_FOUND;
        }
        Account account = accountOptional.get();

        // check account status
        if (account.getIsBanned()) {
            return WhitelistCheckResult.ACCOUNT_BANNED;
        } else if (!account.getIsActive()) {
            return WhitelistCheckResult.ACCOUNT_INACTIVE;
        }

        // check account is already online
        if (
                !account.hasRole(Role.ROLE_ADMIN)
                        && altairVelocity.getAccountOnlinePlayersManager().getOnlinePlayersCount(account) >= MAX_ONLINE_PLAYERS
        ) {
            for (Player onlinePlayer : altairVelocity.getProxyServer().getAllPlayers()) {
                // ignore player that trying to connect
                if (onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                    continue;
                }

                // check other players online
                Optional<Account> playerAccountOptional = altairVelocity.getDataManager().getAccount(onlinePlayer.getUniqueId());
                if (playerAccountOptional.isPresent() && playerAccountOptional.get().getAccountId().equals(account.getAccountId())) {
                    onlinePlayer.disconnect(ACCOUNT_LOGGED_IN_ELSEWHERE_MESSAGE);
                }
            }
        }

        // default is fine
        return WhitelistCheckResult.ACCOUNT_FINE;
    }

    private WhitelistCheckResult checkWhitelisted(AltairVelocity altairVelocity, Player player, String serverName) {
        if (altairVelocity.getDataManager().isWhitelisted(player.getUniqueId(), serverName)) {
            return WhitelistCheckResult.WHITELISTED;
        } else {
            return WhitelistCheckResult.NOT_WHITELISTED;
        }
    }

    private WhitelistCheckResult checkAll(AltairVelocity altairVelocity, Player player, String serverName) {
        // check account
        WhitelistCheckResult accountCheckResult = checkAccount(altairVelocity, player);
        if (!accountCheckResult.equals(WhitelistCheckResult.ACCOUNT_FINE)) {
            return accountCheckResult;
        }

        // check whitelisted
        return checkWhitelisted(altairVelocity, player, serverName);
    }

    @Override
    public void onLoginEvent(AltairVelocity altairVelocity, LoginEvent event) {
        WhitelistCheckResult result = checkAccount(
                altairVelocity,
                event.getPlayer()
        );
        switch (result) {
            case ACCOUNT_NOT_FOUND -> denyOrDisconnectPlayerAtLoginEvent(event, NOT_WHITELISTED_MESSAGE);
            case ACCOUNT_BANNED -> denyOrDisconnectPlayerAtLoginEvent(event, ACCOUNT_BANNED_MESSAGE);
            case ACCOUNT_INACTIVE -> denyOrDisconnectPlayerAtLoginEvent(event, ACCOUNT_INACTIVE_MESSAGE);
        }
    }

    @Override
    public void onServerPreConnectedEvent(AltairVelocity altairVelocity, ServerPreConnectEvent event) {
        WhitelistCheckResult result = checkWhitelisted(
                altairVelocity,
                event.getPlayer(),
                event.getOriginalServer().getServerInfo().getName()
        );
        switch (result) {
            case NOT_WHITELISTED -> denyOrDisconnectPlayerAtServerPreConnectEvent(event, NOT_WHITELISTED_MESSAGE);
        }
    }
}
