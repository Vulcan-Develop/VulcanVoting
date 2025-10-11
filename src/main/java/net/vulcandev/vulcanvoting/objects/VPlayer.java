package net.vulcandev.vulcanvoting.objects;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a voting player with their vote statistics and preferences.
 */
@Getter
@Setter
public class VPlayer {
    /**
     * The total number of votes this player has accumulated.
     */
    private int totalVotes = 0;

    /**
     * Whether vote broadcast messages are toggled on for this player.
     * When true, the player sees messages when other players vote.
     */
    private boolean voteMsgToggled = false;

    /**
     * Increments the player's total vote count by one.
     */
    public void addTotalVote() {
        this.totalVotes += 1;
    }
}
