package com.remorse.pvptoggle.database;

import com.remorse.pvptoggle.PvPToggle;
import org.bukkit.entity.Player;

import java.sql.*;

public class PvPDatabase{

    private final Connection connection;

    public PvPDatabase(String path) throws SQLException {
        PvPToggle.log.info("Attempting to connect to database.");
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                            CREATE TABLE IF NOT EXISTS players (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            uuid TEXT NOT NULL UNIQUE,
                            pvp_enabled BOOLEAN NOT NULL DEFAULT 0)
                    """);
            PvPToggle.log.info("Connection successful.");
        }
    }

    public void closeConnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Add Player to database
    public void addPlayer(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid,pvp_enabled) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET pvp_enabled=excluded.pvp_enabled;")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setBoolean(2, false);
            preparedStatement.executeUpdate();
        }
    }

    // Check if Player exists in database
    public boolean playerExists(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    // Update PVP enabled status
    public void updatePvpStatus(Player player, boolean status) throws SQLException {
        // If the player doesn't exist in database create them
        if (!playerExists(player)) {
            PvPToggle.log.info("Player " + player.getName() + " did not exist - Creating database entry");
            addPlayer(player);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET pvp_enabled = ? WHERE uuid = ? ")) {
            preparedStatement.setBoolean(1, status);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    // Return true or false if PVP is enabled
    public boolean getPlayerPvpStatus(Player player) throws SQLException {
        if (!playerExists(player)) {
            PvPToggle.log.info("Player " + player.getName() + " did not exist - Creating database entry");
            addPlayer(player);
        }
        boolean status = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT pvp_enabled FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                status = resultSet.getBoolean("pvp_enabled");
            }
        }
        return status;
    }
}