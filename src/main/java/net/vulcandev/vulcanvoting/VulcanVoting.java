package net.vulcandev.vulcanvoting;

import lombok.Getter;
import me.plugin.libs.YamlDocument;
import net.vulcandev.vulcanvoting.commands.FakeVoteCmd;
import net.vulcandev.vulcanvoting.commands.VoteCmd;
import net.vulcandev.vulcanvoting.listeners.VotifierListener;
import net.vulcandev.vulcanvoting.managers.QueuedVotes;
import net.vulcandev.vulcanvoting.managers.VPlayerManager;
import net.vulcandev.vulcanvoting.managers.VotePartyManager;
import net.vulcandev.vulcanvoting.placeholders.VotingPlaceHolders;
import net.xantharddev.vulcanlib.ConfigFile;
import net.xantharddev.vulcanlib.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class VulcanVoting extends JavaPlugin {
    private static VulcanVoting instance;
    @Getter
    private VotePartyManager votePartyManager;
    @Getter
    private VPlayerManager vPlayerManager;
    @Getter
    private QueuedVotes queuedVotes;

    private YamlDocument conf;
    private String licenseKey;

    public static VulcanVoting get() { return instance; }
    public YamlDocument getConf() { return conf; }

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
        registerCommands();
        setupIntegrations();
    }

    @Override
    public void onDisable() {
        vPlayerManager.saveVPlayers(false);
        vPlayerManager.saveServiceCooldowns(false);
        votePartyManager.saveCurrentAmount(false);
        queuedVotes.saveQueuedVotes(false);
    }



    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new VotifierListener(this), this);
    }

    private void registerCommands() {
        getCommand("fakevote").setExecutor(new FakeVoteCmd(this));
        getCommand("vote").setExecutor(new VoteCmd(this));
    }


    private void setupIntegrations() {
        setupPlaceholderAPI();
    }

    private void setupPlaceholderAPI() {
        Plugin placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (placeholderAPI != null && placeholderAPI.isEnabled()) {
            new VotingPlaceHolders(this).register();
        }
    }

    private void loadLicenseConfig() {
        File licenseFile = new File(getDataFolder(), "license.yml");

        if (!licenseFile.exists()) {
            saveResource("license.yml", false);
        }

        FileConfiguration licenseConfig = YamlConfiguration.loadConfiguration(licenseFile);
        licenseKey = licenseConfig.getString("licenseKey");
    }
}
