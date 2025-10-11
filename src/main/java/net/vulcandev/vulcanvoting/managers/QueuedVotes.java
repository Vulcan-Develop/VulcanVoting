package net.vulcandev.vulcanvoting.managers;

import com.google.gson.reflect.TypeToken;
import net.vulcandev.vulcanvoting.VulcanVoting;
import net.xantharddev.vulcanlib.libs.DataUtils;
import net.xantharddev.vulcanlib.libs.Pair;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages queued votes for offline players.
 * When a player votes while offline, their vote is queued and processed when they join.
 */
public class QueuedVotes {
    private final VulcanVoting plugin;
    private final File queuedVoteFile;
    private final Map<UUID, Pair<String, Long>> queuedVotes = new HashMap<>();

    /**
     * Constructs a new QueuedVotes manager and loads queued votes.
     *
     * @param plugin the VulcanVoting plugin instance
     */
    public QueuedVotes(VulcanVoting plugin) {
        this.plugin = plugin;
        this.queuedVoteFile = new File(plugin.getDataFolder(), "data/queuedvotes.json");
        loadQueuedVote();
    }

    /**
     * Gets an unmodifiable view of all queued votes.
     *
     * @return unmodifiable map of UUID to queued vote data (service name and timestamp)
     */
    public Map<UUID, Pair<String, Long>> getQueuedVotes() {
        return Collections.unmodifiableMap(queuedVotes);
    }

    /**
     * Gets the queued vote for a specific player.
     *
     * @param uuid the player's UUID
     * @return the queued vote data, or null if no vote is queued
     */
    public Pair<String, Long> getQueuedVote(UUID uuid) {
        return queuedVotes.get(uuid);
    }

    /**
     * Adds a vote to the queue for an offline player.
     *
     * @param uuid the player's UUID
     * @param serviceName the name of the voting service
     */
    public void addQueuedVote(UUID uuid, String serviceName) {
        queuedVotes.put(uuid, Pair.of(serviceName.toLowerCase(), System.currentTimeMillis()));
    }

    /**
     * Saves queued votes to disk.
     *
     * @param async whether to save asynchronously
     */
    public void saveQueuedVotes(boolean async) {
        DataUtils.saveToJson(queuedVoteFile, queuedVotes, async);
    }

    /**
     * Loads queued votes from disk.
     */
    private void loadQueuedVote() {
        Type type = new TypeToken<Map<UUID, Pair<String, Long>>>() {}.getType();
        Map<UUID, Pair<String, Long>> queuedVotesMap = DataUtils.loadFromJson(queuedVoteFile, type, HashMap::new);
        queuedVotes.clear();
        queuedVotes.putAll(queuedVotesMap);
    }
}
