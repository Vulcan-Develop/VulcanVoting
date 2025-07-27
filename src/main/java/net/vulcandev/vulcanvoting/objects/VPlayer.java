package net.vulcandev.vulcanvoting.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VPlayer {
    private int totalVotes = 0;
    private boolean voteMsgToggled = false;

    public void addTotalVote() {
        this.totalVotes += 1;
    }
}
