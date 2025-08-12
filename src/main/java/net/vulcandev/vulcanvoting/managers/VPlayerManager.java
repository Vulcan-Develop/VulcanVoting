package net.vulcandev.vulcanvoting.managers;

import com.google.gson.reflect.TypeToken;
import net.vulcandev.vulcanvoting.VulcanVoting;
import net.vulcandev.vulcanvoting.objects.VPlayer;
import net.xantharddev.vulcanlib.libs.Colour;
import net.xantharddev.vulcanlib.libs.DataUtils;
import net.xantharddev.vulcanlib.libs.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VPlayerManager {
    private final VulcanVoting plugin;
    private final Map<UUID, VPlayer> vPlayers = new ConcurrentHashMap<>();
    private final File vPlayerFile;
    private final File serviceVotesFile;
    private final List<String> voteRewards;
    private final Map<UUID, Map<String, Long>> serviceVotes = new HashMap<>();
    private final String[] playerMsg;
    private final String[] playerAllMsg;

    public Map<UUID, VPlayer> getVPlayers() { return Collections.unmodifiableMap(vPlayers); }

    public VPlayer getVPlayer(UUID uuid) {
        return vPlayers.computeIfAbsent(uuid, id -> new VPlayer());
    }

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

    public void processVoteRequest(String username, boolean isFake, boolean isOnline, String serviceName) {

        if (isOnline) {
            System.out.println("Voting debug: processing vote request for " + username + " on " + serviceName);
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (String cmd : voteRewards) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", username));
                }

                Player votedPlayer = Bukkit.getPlayer(username);
                for(String msg : playerMsg) {
                    votedPlayer.sendMessage(Colour.colour(msg.replace("%player%", username)));
                }

                for(Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    if(uuid.equals(votedPlayer.getUniqueId())) continue;
                    VPlayer vPlayer = plugin.getVPlayerManager().getVPlayer(uuid);
                    if(vPlayer.isVoteMsgToggled()) {
                        for(String msg : playerAllMsg) {
                            player.sendMessage(Colour.colour(msg.replace("%player%", username)));
                        }
                    }
                }
            });

            plugin.getVotePartyManager().addVoteParty();
            int currentVPAmount = plugin.getVotePartyManager().getCurrentAmount();
            int setVPAmount = plugin.getVotePartyManager().getVotepartyAmount();
            if(currentVPAmount >= setVPAmount) {
                plugin.getVotePartyManager().setCurrentAmount(0);
                plugin.getVotePartyManager().handleVoteParty();
            }

        } else {
            plugin.getQueuedVotes().addQueuedVote(Utils.getOfflinePlayer(username).getUniqueId(), serviceName);
            System.out.println("Voting debug: adding queued vote for " + username + " on " + serviceName);
        }
    }

    public boolean isOnCooldown(UUID uuid, String serviceName, long cooldownMillis) {
        Map<String, Long> voteCooldown = serviceVotes.get(uuid);
        if(voteCooldown == null) return false;

        long lastVoteTime = voteCooldown.getOrDefault(serviceName.toLowerCase(), 0L);
        long currentTime = System.currentTimeMillis();

        return (currentTime - lastVoteTime) < cooldownMillis;
    }

    public void applyServiceCooldown(UUID uuid, String serviceName) {
        serviceVotes.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(serviceName.toLowerCase(), System.currentTimeMillis());

    }

    public boolean isFakeVote(String address) {
        return address.equals("fake") || address.equals("test") || address.equals("127.0.0.1");
    }



    public void saveVPlayers(boolean async) {
        DataUtils.saveToJson(vPlayerFile, vPlayers, async);
    }


    private void loadVPlayers() {
        Type type = new TypeToken<Map<UUID, VPlayer>>() {}.getType();
        Map<UUID, VPlayer> vPlayerMap = DataUtils.loadFromJson(vPlayerFile, type, ConcurrentHashMap::new);
        vPlayers.clear();
        vPlayers.putAll(vPlayerMap);
    }

    public void saveServiceCooldowns(boolean async) {
        DataUtils.saveToJson(serviceVotesFile, serviceVotes, async);
    }

    private void loadServiceCooldowns() {
        Type type = new TypeToken<Map<UUID, Map<String, Long>>>() {}.getType();
        Map<UUID, Map<String, Long>> serviceCooldownMap = DataUtils.loadFromJson(serviceVotesFile, type, HashMap::new);
        serviceVotes.clear();
        serviceVotes.putAll(serviceCooldownMap);
    }
}
