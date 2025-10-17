package net.vulcandev.vulcanvoting;

import lombok.Getter;
import me.plugin.libs.YamlDocument;
import net.vulcandev.vulcanvoting.guis.VoteGUI;
import net.vulcandev.vulcanvoting.listeners.VotifierListener;
import net.vulcandev.vulcanvoting.managers.QueuedVotes;
import net.vulcandev.vulcanvoting.managers.VPlayerManager;
import net.vulcandev.vulcanvoting.managers.VotePartyManager;
import net.vulcandev.vulcanvoting.placeholders.VotingPlaceHolders;
import net.xantharddev.vulcanlib.ConfigFile;
import net.xantharddev.vulcanlib.Logger;
import net.xantharddev.vulcanlib.command.VulcanCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Main plugin class for VulcanVoting.
 * Manages voting functionality including vote rewards, vote parties, and player vote tracking.
 */
@Getter
public final class VulcanVoting extends JavaPlugin {
    private static VulcanVoting instance;
    private VotePartyManager votePartyManager;
    private VPlayerManager vPlayerManager;
    private QueuedVotes queuedVotes;
    private YamlDocument conf;
    private boolean apiEnabled = false;

    public static VulcanVoting get() {
        return instance;
    }

    /**
     * Called when the plugin is enabled.
     * Initializes managers, loads configuration, and sets up integrations.
     */
    @Override
    public void onEnable() {
        instance = this;

        conf = ConfigFile.createConfig(this, "config.yml");

        File dataFolder = new File(getDataFolder(), "data");
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            Logger.log(this, "&cError when trying to create plugin data folder, disabling...");
            return;
        }

        vPlayerManager = new VPlayerManager(this);
        votePartyManager = new VotePartyManager(this);
        queuedVotes = new QueuedVotes(this);
        registerListeners();
        setupIntegrations();

        VulcanCommand.create("vote")
                .description("Open the vote GUI")
                .playerOnly()
                .execute((sender, ctx) -> {
                    Player player = (Player) sender;
                    new VoteGUI(this, player).open();
                })
                .build().register(this);
    }

    /**
     * Called when the plugin is disabled.
     * Saves all data to disk synchronously.
     */
    @Override
    public void onDisable() {
        vPlayerManager.saveVPlayers(false);
        vPlayerManager.saveServiceCooldowns(false);
        votePartyManager.saveCurrentAmount(false);
        queuedVotes.saveQueuedVotes(false);
    }

    /**
     * Registers event listeners for the plugin.
     */
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new VotifierListener(this), this);
    }

    /**
     * Sets up third-party plugin integrations.
     */
    private void setupIntegrations() {
        setupPlaceholderAPI();
        initializeAPI();
    }

    /**
     * Initializes VulcanAPI integration if available.
     * Sets the apiEnabled flag to true if successful.
     */
    private void initializeAPI() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("VulcanAPI");
        if (plugin == null || !plugin.isEnabled()) {
            Logger.log(this, "&cFailed to initialize VulcanAPI, please put the API on your server if you wish to use it.");
            Logger.log(this, "&cDisabling API integration...");
            return;
        }

        Logger.log(this, "VulcanAPI integration enabled.");
        apiEnabled = true;
    }

    /**
     * Sets up PlaceholderAPI integration if available.
     * Registers the VotingPlaceHolders expansion.
     */
    private void setupPlaceholderAPI() {
        Plugin placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (placeholderAPI != null && placeholderAPI.isEnabled()) {
            new VotingPlaceHolders(this).register();
        }
    }
}
