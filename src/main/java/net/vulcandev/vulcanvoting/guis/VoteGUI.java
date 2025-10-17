package net.vulcandev.vulcanvoting.guis;

import me.plugin.libs.YamlDocument;
import net.vulcandev.vulcanvoting.VulcanVoting;
import net.vulcandev.vulcanvoting.objects.VPlayer;
import net.xantharddev.vulcanlib.libs.Colour;
import net.xantharddev.vulcanlib.libs.GUI;
import net.xantharddev.vulcanlib.libs.SimpleItem;
import net.xantharddev.vulcanlib.libs.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.Map;

/**
 * GUI for displaying voting information and options to players.
 * Allows players to view vote links, statistics, vote party progress, and toggle vote messages.
 */
public class VoteGUI extends GUI<Integer> {
    // Slot index constants for GUI items
    private static final int CLOSE_INDEX = -1;
    private static final int VOTE_INFO_INDEX = -2;
    private static final int VOTE_STATS_INDEX = -3;
    private static final int VOTE_PARTY_INDEX = -4;
    private static final int VOTE_PARTY_TOGGLE = -5;

    private final YamlDocument config;
    private final VulcanVoting plugin;

    public VoteGUI(VulcanVoting plugin, Player user) {
        super(user, VulcanVoting.get().getConf().getInt("voteGUI.main.rows"));
        this.plugin = plugin;
        this.config = plugin.getConf();
        build();
    }

    /**
     * Gets the name/title of the GUI from the configuration.
     *
     * @return the colored GUI title
     */
    @Override
    protected String getName() {
        return Colour.colour(config.getString("voteGUI.main.title"));
    }

    /**
     * Parses placeholder strings for the GUI.
     *
     * @param toParse the string to parse
     * @param index the item index
     * @return the parsed string
     */
    @Override
    protected String parse(String toParse, Integer index) {
        return toParse;
    }

    /**
     * Handles click events on GUI items.
     *
     * @param index the index of the clicked item
     * @param clickType the type of click performed
     */
    @Override
    protected void onClick(Integer index, ClickType clickType) {
        switch (index) {
            case CLOSE_INDEX:
                // Close the GUI
                user.closeInventory();
                break;
            case VOTE_INFO_INDEX:
                // Send vote links to the player
                user.closeInventory();
                for (String msg : config.getStringList("vote-links")) {
                    user.sendMessage(Colour.colour(msg));
                }
                break;
            case VOTE_PARTY_TOGGLE:
                // Toggle vote message notifications
                user.closeInventory();
                VPlayer vPlayer = plugin.getVPlayerManager().getVPlayer(user.getUniqueId());
                if (vPlayer.isVoteMsgToggled()) {
                    user.sendMessage(Colour.colour("&cVote Msgs are now disabled."));
                    vPlayer.setVoteMsgToggled(false);
                } else {
                    user.sendMessage(Colour.colour("&aVote Msgs are now enabled."));
                    vPlayer.setVoteMsgToggled(true);
                }
                break;
        }
    }

    /**
     * Creates the slot mapping for GUI items.
     *
     * @return map of slot positions to item indices
     */
    @Override
    protected Map<Integer, Integer> createSlotMap() {
        HashMap<Integer, Integer> slotMap = new HashMap<>();

        Utils.addIndexToMap(slotMap, config, "voteGUI.close.slot", CLOSE_INDEX, size);
        Utils.addIndexToMap(slotMap, config, "voteGUI.info.slot", VOTE_INFO_INDEX, size);
        Utils.addIndexToMap(slotMap, config, "voteGUI.stats.slot", VOTE_STATS_INDEX, size);
        Utils.addIndexToMap(slotMap, config, "voteGUI.voteparty.slot", VOTE_PARTY_INDEX, size);
        Utils.addIndexToMap(slotMap, config, "voteGUI.toggle.slot", VOTE_PARTY_TOGGLE, size);
        return slotMap;
    }

    /**
     * Gets the item for a specific index in the GUI.
     *
     * @param index the item index
     * @return the SimpleItem to display
     */
    @Override
    protected SimpleItem getItem(Integer index) {
        switch (index) {
            case CLOSE_INDEX:
                return Utils.createSimpleItemFromConfig(user, config, "voteGUI.close");
            case VOTE_INFO_INDEX:
                return Utils.createSimpleItemFromConfig(user, config, "voteGUI.info");
            case VOTE_STATS_INDEX:
                return Utils.createSimpleItemFromConfig(user, config, "voteGUI.stats");
            case VOTE_PARTY_INDEX:
                return Utils.createSimpleItemFromConfig(user, config, "voteGUI.voteparty");
            case VOTE_PARTY_TOGGLE:
                return Utils.createSimpleItemFromConfig(user, config, "voteGUI.toggle");
        }
        return SimpleItem.builder().build();
    }

    /**
     * Creates filler/decoration items for the GUI.
     *
     * @return map of slot positions to filler items
     */
    @Override
    protected Map<Integer, SimpleItem> createDummyItems() {
        return Utils.createDummyItems(user, config, "voteGUI.main.fillers");
    }
}
