package org.xnotro.sethome;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetHomeUtils {
    Core plugin;

    public SetHomeUtils(Core plugin) {
        this.plugin = plugin;
    }
    
    public Location getPlayerLocation(Player player) {
        return player.getLocation();
    }
    
    public UUID getPlayerUUID(Player player) {
        return player.getUniqueId();
    }

    public void setHome(Player player) {
        plugin.homesYaml.set("Homes." + getPlayerUUID( player ) + ".X", getPlayerLocation( player ).getX());
        plugin.homesYaml.set("Homes." + getPlayerUUID( player ) + ".Y", getPlayerLocation( player ).getY());
        plugin.homesYaml.set("Homes." + getPlayerUUID( player ) + ".Z", getPlayerLocation( player ).getZ());
        plugin.homesYaml.set("Homes." + getPlayerUUID( player ) + ".Yaw", getPlayerLocation( player ).getYaw());
        plugin.homesYaml.set("Homes." + getPlayerUUID( player ) + ".Pitch", getPlayerLocation( player ).getPitch());
        plugin.homesYaml.set("Homes." + getPlayerUUID( player ) + ".World", getPlayerLocation( player ).getWorld().getName());
        plugin.saveHomesFile();
    }

    public void sendHome(Player player) {
        player.teleport(getHomeLocation(player));
    }

    public Location getHomeLocation(Player player) {
        return new Location( Bukkit.getWorld(plugin.homesYaml.getString("Homes." + getPlayerUUID( player ) + ".World")),
                plugin.homesYaml.getDouble("Homes." + getPlayerUUID( player ) + ".X"),
                plugin.homesYaml.getDouble("Homes." + getPlayerUUID( player ) + ".Y"),
                plugin.homesYaml.getDouble("Homes." + getPlayerUUID( player ) + ".Z"),
                (float)plugin.homesYaml.getLong("Homes." + getPlayerUUID( player ) + ".Yaw"),
                (float)plugin.homesYaml.getLong("Homes." + getPlayerUUID( player ) + ".Pitch"));
    }

    public boolean homeIsNull(Player player) {
        return plugin.homesYaml.getString("Homes." + getPlayerUUID( player )) == null;
    }
}
