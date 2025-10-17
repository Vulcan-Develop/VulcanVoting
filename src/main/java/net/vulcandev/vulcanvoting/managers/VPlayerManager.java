package net.vulcandev.vulcanvoting.managers;

import com.google.gson.reflect.TypeToken;
import net.vulcandev.vulcanvoting.VulcanVoting;
import net.vulcandev.vulcanvoting.objects.VPlayer;
import net.xantharddev.vulcanlib.libs.Colour;
import net.xantharddev.vulcanlib.libs.DataUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player voting data including vote counts, rewards, and service cooldowns.
 * Handles vote processing, cooldown tracking, and player data persistence.
 */
public class VPlayerManager {
    private final VulcanVoting plugin;
    private final Map<UUID, VPlayer> vPlayers = new ConcurrentHashMap<>();
    private final File vPlayerFile;
    private final File serviceVotesFile;
    private final List<String> voteRewards;
    private final Map<UUID, Map<String, Long>> serviceVotes = new HashMap<>();
    private final String[] playerMsg;
    private final String[] playerAllMsg;

    /**
     * Gets an unmodifiable view of all VPlayers.
     *
     * @return unmodifiable map of UUID to VPlayer
     */
    public Map<UUID, VPlayer> getVPlayers() {
        return Collections.unmodifiableMap(vPlayers);
    }

    /**
     * Gets or creates a VPlayer for the given UUID.
     *
     * @param uuid the player's UUID
     * @return the VPlayer instance
     */
    public VPlayer getVPlayer(UUID uuid) {
        return vPlayers.computeIfAbsent(uuid, id -> new VPlayer());
    }

    /**
     * Constructs a new VPlayerManager and loads player data.
     *
     * @param plugin the VulcanVoting plugin instance
     */
    public VPlayerManager(VulcanVoting plugin) {
        this.plugin = plugin;
        this.vPlayerFile = new File(plugin.getDataFolder(), "data/vplayer.json");
        this.serviceVotesFile = new File(plugin.getDataFolder(), "data/servicedata.json");
        this.voteRewards = plugin.getConf().getStringList("vote-rewards");
        this.playerMsg = plugin.getConf().getStringList("vote-messages.player").toArray(new String[0]);
        this.playerAllMsg = plugin.getConf().getStringList("vote-messages.all-players").toArray(new String[0]);
        loadVPlayers();
        loadServiceCooldowns();
    }

    /**
     * Processes a vote request from a player.
     * Handles reward distribution, messaging, vote party updates, and queuing for offline players.
     *
     * @param player the player who voted
     * @param serviceName the name of the voting service
     * @param address the IP address of the vote
     * @param timeStamp the timestamp of the vote
     */
    public void processVoteRequest(OfflinePlayer player, String serviceName, String address, long timeStamp) {
        if (player.isOnline()) {
            // Handle rewards and messages on the main thread
            Bukkit.getScheduler().runTask(plugin, () -> {
                // Execute vote rewards
                for (String cmd : voteRewards) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                }

                // Send message to the voter
                Player votedPlayer = player.getPlayer();
                for (String msg : playerMsg) {
                    votedPlayer.sendMessage(Colour.colour(msg.replace("%player%", player.getName())));
                }

                // Broadcast to other players who have messages enabled
                for (Player p : Bukkit.getOnlinePlayers()) {
                    UUID uuid = p.getUniqueId();
                    if (uuid.equals(votedPlayer.getUniqueId())) continue;

                    VPlayer vPlayer = plugin.getVPlayerManager().getVPlayer(uuid);
                    if (vPlayer.isVoteMsgToggled()) {
                        for (String msg : playerAllMsg) {
                            p.sendMessage(Colour.colour(msg.replace("%player%", player.getName())));
                        }
                    }
                }

                // Fire VulcanAPI event if enabled
                if (plugin.isApiEnabled()) {
                    Bukkit.getPluginManager().callEvent(new net.vulcandev.vulcanapi.vulcanvoting.PlayerVoteEvent(votedPlayer, serviceName, votedPlayer.getName(), address, timeStamp));
                }
            });

            // Update vote party progress
            plugin.getVotePartyManager().addVoteParty();
            int currentVPAmount = plugin.getVotePartyManager().getCurrentAmount();
            int setVPAmount = plugin.getVotePartyManager().getVotepartyAmount();
            if (currentVPAmount >= setVPAmount) {
                plugin.getVotePartyManager().setCurrentAmount(0);
                plugin.getVotePartyManager().handleVoteParty();
            }
        } else {
            // Queue vote for offline player
            plugin.getQueuedVotes().addQueuedVote(player.getUniqueId(), serviceName);
        }
    }

    /**
     * Checks if a player is on cooldown for a specific voting service.
     *
     * @param uuid the player's UUID
     * @param serviceName the name of the voting service
     * @param cooldownMillis the cooldown duration in milliseconds
     * @return true if the player is on cooldown, false otherwise
     */
    public boolean isOnCooldown(UUID uuid, String serviceName, long cooldownMillis) {
        Map<String, Long> voteCooldown = serviceVotes.get(uuid);
        if (voteCooldown == null) return false;

        long lastVoteTime = voteCooldown.getOrDefault(serviceName.toLowerCase(), 0L);
        long currentTime = System.currentTimeMillis();

        return (currentTime - lastVoteTime) < cooldownMillis;
    }

    /**
     * Applies a cooldown for a player on a specific voting service.
     *
     * @param uuid the player's UUID
     * @param serviceName the name of the voting service
     */
    public void applyServiceCooldown(UUID uuid, String serviceName) {
        serviceVotes.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(serviceName.toLowerCase(), System.currentTimeMillis());
    }

    /**
     * Checks if a vote is a fake/test vote based on the address.
     *
     * @param address the IP address of the vote
     * @return true if the vote is fake, false otherwise
     */
    public boolean isFakeVote(String address) {
        return address.equals("fake") || address.equals("test") || address.equals("127.0.0.1");
    }

    /**
     * Saves all VPlayer data to disk.
     *
     * @param async whether to save asynchronously
     */
    public void saveVPlayers(boolean async) {
        DataUtils.saveToJson(vPlayerFile, vPlayers, async);
    }

    /**
     * Loads VPlayer data from disk.
     */
    private void loadVPlayers() {
        Type type = new TypeToken<Map<UUID, VPlayer>>() {}.getType();
        Map<UUID, VPlayer> vPlayerMap = DataUtils.loadFromJson(vPlayerFile, type, ConcurrentHashMap::new);
        vPlayers.clear();
        vPlayers.putAll(vPlayerMap);
    }

    /**
     * Saves service cooldown data to disk.
     *
     * @param async whether to save asynchronously
     */
    public void saveServiceCooldowns(boolean async) {
        DataUtils.saveToJson(serviceVotesFile, serviceVotes, async);
    }

    /**
     * Loads service cooldown data from disk.
     */
    private void loadServiceCooldowns() {
        Type type = new TypeToken<Map<UUID, Map<String, Long>>>() {}.getType();
        Map<UUID, Map<String, Long>> serviceCooldownMap = DataUtils.loadFromJson(serviceVotesFile, type, HashMap::new);
        serviceVotes.clear();
        serviceVotes.putAll(serviceCooldownMap);
    }
}
