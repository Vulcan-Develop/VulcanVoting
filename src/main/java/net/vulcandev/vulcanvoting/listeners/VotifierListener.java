package net.vulcandev.vulcanvoting.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.vulcandev.vulcanvoting.VulcanVoting;
import net.vulcandev.vulcanvoting.objects.VPlayer;
import net.xantharddev.vulcanlib.libs.Pair;
import net.xantharddev.vulcanlib.libs.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

/**
 * Listener for vote and player join events.
 * Handles incoming votes from Votifier and processes queued votes when players join.
 */
public class VotifierListener implements Listener {
    private final VulcanVoting plugin;
    private final long cooldownMillis = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

    public VotifierListener(VulcanVoting plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles incoming vote events from Votifier.
     * Processes votes asynchronously, checking cooldowns and handling rewards.
     *
     * @param event the VotifierEvent containing vote information
     */
    @EventHandler
    public void onVotifierEvent(VotifierEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Vote vote = event.getVote();
            OfflinePlayer offlinePlayer = Utils.getOfflinePlayer(vote.getUsername());

            // Ignore votes from players who have never joined
            if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) return;

            UUID uuid = offlinePlayer.getUniqueId();
            // Check if player is on cooldown for this voting service
            if (plugin.getVPlayerManager().isOnCooldown(uuid, vote.getServiceName(), cooldownMillis)) return;

            // Determine if this is a fake/test vote
            boolean fakeVote = plugin.getVPlayerManager().isFakeVote(vote.getAddress().toLowerCase());

            VPlayer vPlayer = plugin.getVPlayerManager().getVPlayer(uuid);

            // Only increment total votes for real votes
            if (!fakeVote) {
                vPlayer.addTotalVote();
            }

            // Process the vote request
            plugin.getVPlayerManager().processVoteRequest(offlinePlayer, vote.getServiceName(), vote.getAddress(), Long.parseLong(vote.getTimeStamp()));
            plugin.getVPlayerManager().applyServiceCooldown(uuid, vote.getServiceName());
        });
    }

    /**
     * Handles player join events to process queued votes.
     * When a player joins, any queued votes from when they were offline are processed.
     *
     * @param event the PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Check if this player has any queued votes
        if (plugin.getQueuedVotes().getQueuedVotes().containsKey(player.getUniqueId())) {
            Pair<String, Long> queuedVote = plugin.getQueuedVotes().getQueuedVote(player.getUniqueId());
            if (queuedVote == null) return;

            // Extract vote data and process it
            String serviceName = queuedVote.getLeft();
            String address = "";
            Long voteTime = queuedVote.getRight();
            plugin.getVPlayerManager().processVoteRequest(Utils.getOfflinePlayer(player.getName()), serviceName, address, voteTime);
        }
    }
}
