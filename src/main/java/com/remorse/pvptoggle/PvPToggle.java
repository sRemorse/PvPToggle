package com.remorse.pvptoggle;

import com.remorse.pvptoggle.commands.PvPToggleCommand;
import com.remorse.pvptoggle.database.PvPDatabase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Logger;

public final class PvPToggle extends JavaPlugin {

    public static PvPDatabase pvpDatabase;
    public static Logger log;

    @Override
    public void onEnable() {
        // Plugin startup logic
        log = getLogger();

        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, this::connectToDatabase);
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try{
            pvpDatabase.closeConnect();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    public void registerCommands(){
        getCommand("pvp").setExecutor(new PvPToggleCommand());
    }

    public void disablePluginLoad(){
        Bukkit.getPluginManager().disablePlugin(this); // Disable plugin if unable to connect to DB
        log.info("Disabling plugin.");
    }

    public void connectToDatabase(){
        try{
            if (!getDataFolder().exists()){
                log.info("No data folder found, creating one.");
                getDataFolder().mkdirs();
            }
            pvpDatabase = new PvPDatabase(getDataFolder().getAbsolutePath() + "/PvPToggle.db");
        }catch (SQLException exception){
            exception.printStackTrace();
            log.warning("Failed to connect to the database!" + exception.getMessage());
            disablePluginLoad();
        }
    }

}
