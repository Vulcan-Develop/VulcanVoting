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

public class QueuedVotes {
    private final VulcanVoting plugin;
    private final File queuedVoteFile;
    private final Map<UUID, Pair<String, Long>> queuedVotes = new HashMap<>();

    public QueuedVotes(VulcanVoting plugin) {
        this.plugin = plugin;
        this.queuedVoteFile = new File(plugin.getDataFolder(), "data/queuedvotes.json");

        loadQueuedVote();
    }

    public Map<UUID, Pair<String, Long>> getQueuedVotes() {
        return Collections.unmodifiableMap(queuedVotes);
    }

    public Pair<String, Long> getQueuedVote(UUID uuid) {
        return queuedVotes.get(uuid);
    }

    public void addQueuedVote(UUID uuid, String serviceName) {
        queuedVotes.put(uuid, Pair.of(serviceName.toLowerCase(), System.currentTimeMillis()));
    }

    public void saveQueuedVotes(boolean async) {
        DataUtils.saveToJson(queuedVoteFile, queuedVotes, async);
    }

    private void loadQueuedVote() {
        Type type = new TypeToken<Map<UUID, Pair<String, Long>>>() {}.getType();
        Map<UUID, Pair<String, Long>> queuedVotesMap = DataUtils.loadFromJson(queuedVoteFile, type, HashMap::new);
        queuedVotes.clear();
        queuedVotes.putAll(queuedVotesMap);
    }

}
