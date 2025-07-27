package net.vulcandev.vulcanvoting.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.vulcandev.vulcanvoting.VulcanVoting;
import net.vulcandev.vulcanvoting.objects.VPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class VotingPlaceHolders extends PlaceholderExpansion {
    private final VulcanVoting plugin;

    public VotingPlaceHolders(VulcanVoting plugin) { this.plugin = plugin; }

    @Override
    public boolean persist() { return true; }

    @Override
    public boolean canRegister() { return true; }

    @Override
    public @NotNull String getIdentifier() { return "vulcanvoting"; }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        UUID uuid = player.getUniqueId();

        if(identifier.equals("totalvotes")) {
            VPlayer vplayer = plugin.getVPlayerManager().getVPlayer(uuid);
            return String.format("%,d", vplayer.getTotalVotes());
        }

        if(identifier.equals("voteparty_current")) {
            return String.format("%,d", plugin.getVotePartyManager().getCurrentAmount());
        }

        if(identifier.equals("voteparty_amount")) {
            return String.format("%,d", plugin.getVotePartyManager().getVotepartyAmount());
        }

        return "Invalid Placeholder";
    }
}
