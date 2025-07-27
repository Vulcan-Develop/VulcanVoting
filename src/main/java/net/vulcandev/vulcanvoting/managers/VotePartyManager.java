package net.vulcandev.vulcanvoting.managers;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import net.vulcandev.vulcanvoting.VulcanVoting;
import net.vulcandev.vulcanvoting.objects.VPlayer;
import net.xantharddev.vulcanlib.libs.DataUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public class VotePartyManager {
    private final VulcanVoting plugin;
    @Getter
    private final int votepartyAmount;
    @Getter
    @Setter
    private Integer currentAmount;
    private final File currentAmountFile;
    private final String[] rewards;

    public VotePartyManager(VulcanVoting plugin) {
        this.plugin = plugin;
        this.votepartyAmount = plugin.getConf().getInt("vote-party.amount-needed");
        this.currentAmountFile = new File(plugin.getDataFolder(), "data/voteparty.json");
        this.rewards = plugin.getConf().getStringList("vote-party.rewards").toArray(new String[0]);
        loadCurrentAmount();
    }

    public void saveCurrentAmount(boolean async) {
        DataUtils.saveToJson(currentAmountFile, currentAmount, async);
    }

    private void loadCurrentAmount() {
        Type type = new TypeToken<Integer>() {}.getType();
        currentAmount = DataUtils.loadFromJson(currentAmountFile, type, () -> 0);
    }

    public void addVoteParty() {
        this.currentAmount += 1;
    }

    public void handleVoteParty() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Map<UUID, VPlayer> vPlayers = plugin.getVPlayerManager().getVPlayers();
            vPlayers.forEach((uuid, vPlayer) -> {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                if (!offlinePlayer.isOnline()) return;
                if (vPlayer.getTotalVotes() == 0) return;
                for (String cmd : rewards) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", offlinePlayer.getName()));
                }
            });
        });
    }
}
