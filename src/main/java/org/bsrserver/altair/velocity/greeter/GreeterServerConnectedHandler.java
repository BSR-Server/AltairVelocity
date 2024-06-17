package org.bsrserver.altair.velocity.greeter;

import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bsrserver.altair.velocity.AltairVelocity;
import org.bsrserver.altair.velocity.data.ServerInfo;
import org.bsrserver.altair.velocity.event.IServerConnectedHandler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class GreeterServerConnectedHandler implements IServerConnectedHandler {
    private String getOpenDays(ServerInfo serverInfo) {
        int daysBetween = (int) ChronoUnit.DAYS.between(serverInfo.getFoundationDate(), LocalDate.now());
        return "这是 " + serverInfo.getGivenName() + " 开服的第 " + daysBetween + " 天\n\n";
    }

    private ServerInfo getServerInfo(AltairVelocity altairVelocity, String serverName) {
        return altairVelocity
                .getDataManager()
                .getServerInfo(serverName)
                .orElseGet(() -> new ServerInfo(0, serverName, serverName, LocalDate.now(), Integer.MAX_VALUE));
    }

    private static Component getServerNameComponent(ServerInfo currentServerInfo, ServerInfo serverInfo) {
        // component
        Component serverNameComponent;

        // current server or other server
        if (currentServerInfo.getServerName().equals(serverInfo.getServerName())) {
            serverNameComponent = Component.text("[§l" + serverInfo.getGivenName() + "§r]")
                    .hoverEvent(HoverEvent.showText(Component.text("当前服务器")));
        } else {
            serverNameComponent = Component.text("[§a" + serverInfo.getGivenName() + "§r]")
                    .clickEvent(ClickEvent.runCommand("/server " + serverInfo.getServerName()))
                    .hoverEvent(HoverEvent.showText(Component.text("点击加入服务器 §b" + serverInfo.getGivenName())));
        }

        // return
        return serverNameComponent;
    }

    private Component getServerListComponent(AltairVelocity altairVelocity, ServerInfo currentServerInfo) {
        List<ServerListServerComponent> serverArrayList = new ArrayList<>();

        // for each server
        for (RegisteredServer registeredServer : altairVelocity.getProxyServer().getAllServers()) {
            ServerInfo serverInfo = getServerInfo(altairVelocity, registeredServer.getServerInfo().getName());
            Component serverNameComponent = getServerNameComponent(currentServerInfo, serverInfo);

            // save to list
            serverArrayList.add(new ServerListServerComponent(serverInfo.getPriority(), serverNameComponent));
        }

        // sort array and return joined component
        return Component.join(
                JoinConfiguration.separator(Component.text(" ")),
                serverArrayList.stream()
                        .sorted()
                        .map(ServerListServerComponent::getComponent)
                        .toList()
        );
    }

    @Override
    public void onServerConnectedEvent(AltairVelocity altairVelocity, ServerConnectedEvent event) {
        // get server info
        ServerInfo currentServerInfo = getServerInfo(altairVelocity, event.getServer().getServerInfo().getName());

        // create greeting message
        Component message = Component.text("-".repeat(40) + "\n")
                .append(Component.text("§e§l" + event.getPlayer().getUsername()))
                .append(Component.text("§r, 欢迎回到 §bBSR 服务器§r！\n"))
                .append(Component.text(getOpenDays(currentServerInfo)))
                .append(Component.text("[§a一言§r] " + altairVelocity.getDataManager().getRandomQuotation() + "\n\n"))
                .append(getServerListComponent(altairVelocity, currentServerInfo))
                .append(Component.text("\n"))
                .append(Component.text("-".repeat(40)));

        // send to player
        event.getPlayer().sendMessage(message);
    }
}
