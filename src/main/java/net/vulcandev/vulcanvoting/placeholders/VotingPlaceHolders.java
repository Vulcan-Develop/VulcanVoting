package net.vulcandev.vulcanvoting.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.vulcandev.vulcanvoting.VulcanVoting;
import net.vulcandev.vulcanvoting.objects.VPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * PlaceholderAPI expansion for VulcanVoting.
 * Provides placeholders for vote statistics and vote party information.
 * <p>
 * Available placeholders:
 * - %vulcanvoting_totalvotes% - Player's total vote count
 * - %vulcanvoting_voteparty_current% - Current vote party progress
 * - %vulcanvoting_voteparty_amount% - Vote party goal amount
 */
public class VotingPlaceHolders extends PlaceholderExpansion {
    private final VulcanVoting plugin;

    /**
     * Constructs a new VotingPlaceHolders expansion.
     *
     * @param plugin the VulcanVoting plugin instance
     */
    public VotingPlaceHolders(VulcanVoting plugin) {
        this.plugin = plugin;
    }

    /**
     * Whether this expansion should persist through reloads.
     *
     * @return true to persist
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Whether this expansion can be registered.
     *
     * @return true if registration is allowed
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * Gets the identifier for this expansion.
     *
     * @return the identifier "vulcanvoting"
     */
    @Override
    public @NotNull String getIdentifier() {
        return "vulcanvoting";
    }

    /**
     * Gets the author(s) of this expansion.
     *
     * @return comma-separated list of authors
     */
    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    /**
     * Gets the version of this expansion.
     *
     * @return the plugin version
     */
    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /**
     * Handles placeholder requests.
     *
     * @param player the player requesting the placeholder
     * @param identifier the placeholder identifier
     * @return the placeholder value, or "Invalid Placeholder" if not found
     */
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        UUID uuid = player.getUniqueId();

        // %vulcanvoting_totalvotes% - Player's total votes
        if (identifier.equals("totalvotes")) {
            VPlayer vplayer = plugin.getVPlayerManager().getVPlayer(uuid);
            return String.format("%,d", vplayer.getTotalVotes());
        }

        // %vulcanvoting_voteparty_current% - Current vote party count
        if (identifier.equals("voteparty_current")) {
            return String.format("%,d", plugin.getVotePartyManager().getCurrentAmount());
        }

        // %vulcanvoting_voteparty_amount% - Vote party goal
        if (identifier.equals("voteparty_amount")) {
            return String.format("%,d", plugin.getVotePartyManager().getVotepartyAmount());
        }

        return "Invalid Placeholder";
    }
}
