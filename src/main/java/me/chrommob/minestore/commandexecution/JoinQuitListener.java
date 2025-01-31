package me.chrommob.minestore.commandexecution;

import me.chrommob.minestore.MineStore;
import me.chrommob.minestore.authorization.AuthManager;
import me.chrommob.minestore.commands.PunishmentManager;
import me.chrommob.minestore.data.Config;
import me.chrommob.minestore.mysql.MySQLData;
import me.chrommob.minestore.mysql.data.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static me.chrommob.minestore.commandexecution.Command.runLater;


public class JoinQuitListener implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        AuthManager.sendAuthMessage(event.getPlayer().getName());
        try {
            String name = event.getPlayer().getName().toLowerCase();
            if (runLater.get(name.toLowerCase()).isEmpty()) {
                runLater.remove(name.toLowerCase());
            } else {
                Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("MineStore"), () -> {
                    runLater.get(name.toLowerCase()).forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
                    Bukkit.getLogger().info("§c[MINESTORE LOG] Comando eseguito: §e" + runLater.get(name.toLowerCase() + " §a[LOGIN]"));
                    runLater.remove(name.toLowerCase());
                    PunishmentManager.update();
                },300L);
            }
        } catch (Exception ignored) {
        }
        if (Config.isDebug()) {
            MineStore.instance.getLogger().info("JoinQuitListener.java onPlayerJoin " + Config.isVaultPresent() + " " + MySQLData.isEnabled());
        }
        if (Config.isVaultPresent() && MySQLData.isEnabled()) {
            if (Config.isDebug()) {
                MineStore.instance.getLogger().info("JoinQuitListener.java onPlayerJoin creating profile " + event.getPlayer().getName());
            }
            String name = event.getPlayer().getName();
            UUID uuid = event.getPlayer().getUniqueId();
            MineStore.instance.getUserManager().createProfile(uuid, name);
        }
    }
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (Config.isVaultPresent() && MySQLData.isEnabled()) {
            MineStore.instance.getUserManager().removeProfile(event.getPlayer().getUniqueId());
        }
    }
}
