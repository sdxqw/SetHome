package org.xnotro.sethome;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Core extends JavaPlugin {

    Logger logger = getLogger();
    private final File homesFile = new File(getDataFolder(), "Homes.yml");
    YamlConfiguration homesYaml;
    private final FileConfiguration config;
    private final SetHomeUtils utils;

    public Core() {
        homesYaml = YamlConfiguration.loadConfiguration( homesFile );
        config = getConfig();
        utils = new SetHomeUtils(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase( "sethome" )) {
            if (!(sender instanceof Player)) {
                logger.log( Level.WARNING, "> you can't use this command on console!." );
                return true;
            }

            Player player = (Player) sender;

            if (args.length == 0) {
                setPlayerHome( player );
            }
        }

        else if (command.getName().equalsIgnoreCase( "home" )) {
            if (!(sender instanceof Player)) {
                logger.log( Level.WARNING, "> you can't use this command on console!." );
                return true;
            }

            Player player = (Player) sender;

            if (args.length == 0) {
                if (utils.homeIsNull( player )) {
                    String noHomeSetted = config.getString( "noHomeSetted" );
                    player.sendMessage( ChatColor.translateAlternateColorCodes( '&', noHomeSetted ) );
                } else {
                    sendPlayerToHome( player );
                }
            }
        }
        return true;
    }

    @Override
    public void onDisable() {
        logger.log( Level.INFO,"> plugin disabled" );
    }

    @Override
    public void onEnable() {
        logger.log( Level.INFO,"> plugin enabled." );
        getCommand( "sethome" ).setExecutor( this );
        getCommand( "home" ).setExecutor( this );
        config.options().copyDefaults(true);
        saveDefaultConfig();

        try {
            config.save(getDataFolder() + File.separator + "config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!homesFile.exists()) {
            saveHomesFile();
        }

        Metrics metrics = new Metrics( this , 14466);

        new Updater(this, 100287).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                logger.log(Level.INFO,">  There is not a new update available.");
            } else {
                logger.log(Level.WARNING,">  There is a new update available. Here: https://www.spigotmc.org/resources/☄%EF%B8%8F-sethome-☄%EF%B8%8F-1-8-to-1-18.100287/updates");
            }
        });
    }

    public void saveHomesFile() {
        try {
            homesYaml.save( homesFile );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setPlayerHome(Player player) {
        utils.setHome( player );
        String setHomeMsg = config.getString( "setHomeMessage").replace("%player%", player.getDisplayName() );
        player.sendMessage( ChatColor.translateAlternateColorCodes( '&', setHomeMsg) );
    }

    void sendPlayerToHome(Player player) {
        utils.sendHome( player );
        String sendHomeMsg = config.getString( "teleportMessage").replace("%player%", player.getDisplayName() );
        player.sendMessage(ChatColor.translateAlternateColorCodes( '&', sendHomeMsg) );
    }
}
