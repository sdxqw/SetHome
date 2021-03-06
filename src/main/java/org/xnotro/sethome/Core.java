package org.xnotro.sethome;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

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

        String prefix = config.getString( "prefix" );

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
                    player.sendMessage( ChatColor.translateAlternateColorCodes( '&', prefix + " " + noHomeSetted ) );
                } else {
                    String teleportToHomeDelayString = this.config.getString("teleportToHomeDelay");
                    int teleportToHomeDelay = Integer.parseInt(teleportToHomeDelayString);
                    if(teleportToHomeDelay == 0) {
                        sendPlayerToHome( player );
                        return false;
                    }

                    if(teleportToHomeDelay > 0) {
                        String teleportDelayMsg = config.getString( "teleportToHomeDelayMessage").replace("%delay%", teleportToHomeDelayString ).replace( "%player%", player.getDisplayName());
                        player.sendMessage( ChatColor.translateAlternateColorCodes( '&', prefix + " " +teleportDelayMsg) );
                        BukkitScheduler scheduler = getServer().getScheduler();
                        scheduler.scheduleSyncDelayedTask( this, () -> sendPlayerToHome( player ), 20L * (long)teleportToHomeDelay);
                    }
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
                logger.log(Level.WARNING,">  There is a new update available. Here: https://www.spigotmc.org/resources/100287/updates");
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
        String prefix = config.getString( "prefix" );
        utils.setHome( player );
        String setHomeMsg = config.getString( "setHomeMessage").replace("%player%", player.getDisplayName() );
        player.sendMessage( ChatColor.translateAlternateColorCodes( '&', prefix + " " +setHomeMsg) );
    }

    void sendPlayerToHome(Player player) {
        String prefix = config.getString( "prefix" );
        utils.sendHome( player );
        String sendHomeMsg = config.getString( "teleportMessage").replace("%player%", player.getDisplayName() );
        player.sendMessage(ChatColor.translateAlternateColorCodes( '&', prefix + " " + sendHomeMsg) );
    }
}
