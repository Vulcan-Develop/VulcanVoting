package net.vulcandev.vulcanvoting.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.vulcandev.vulcanvoting.VulcanVoting;
import net.vulcandev.vulcanvoting.objects.VPlayer;
import net.xantharddev.vulcanlib.Debugger;
import net.xantharddev.vulcanlib.libs.Pair;
import net.xantharddev.vulcanlib.libs.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class VotifierListener implements Listener {
    private final VulcanVoting plugin;
    private final long cooldownMillis = 24 * 60 * 60 * 1000;

    public VotifierListener(VulcanVoting plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVotifierEvent(VotifierEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Vote vote = event.getVote();
            OfflinePlayer offlinePlayer = Utils.getOfflinePlayer(vote.getUsername());

            if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
                System.out.println("Voting debug: offlinePlayer is null or has not played before.");
                return;
            }

            UUID uuid = offlinePlayer.getUniqueId();
            if(plugin.getVPlayerManager().isOnCooldown(uuid, vote.getServiceName(), cooldownMillis)) {
                System.out.println("Voting debug: ignored due to service cooldown for " + vote.getUsername() + " on " + vote.getServiceName());
                return;
            }

            boolean fakeVote = plugin.getVPlayerManager().isFakeVote(vote.getAddress().toLowerCase());

            VPlayer vPlayer = plugin.getVPlayerManager().getVPlayer(uuid);

            if (!fakeVote) {
                vPlayer.addTotalVote();
            }

            plugin.getVPlayerManager().processVoteRequest(vote.getUsername(), fakeVote, offlinePlayer.isOnline(), vote.getServiceName());
            plugin.getVPlayerManager().applyServiceCooldown(uuid, vote.getServiceName());

            System.out.println("Voting debug: " + vote.getUsername() + " voted for " + vote.getServiceName() + "at address " + vote.getAddress());
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(plugin.getQueuedVotes().getQueuedVotes().containsKey(player.getUniqueId())) {
            Pair<String, Long> queuedVote = plugin.getQueuedVotes().getQueuedVote(player.getUniqueId());
            if(queuedVote == null) return;
            String serviceName = queuedVote.getLeft();
            Long voteTime = queuedVote.getRight();
            plugin.getVPlayerManager().processVoteRequest(player.getName(), false, true, serviceName);
        }
    }
}
