package net.vulcandev.vulcanvoting.commands;

import net.vulcandev.vulcanvoting.VulcanVoting;
import net.vulcandev.vulcanvoting.objects.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FakeVoteCmd implements CommandExecutor {
    private final VulcanVoting plugin;

    public FakeVoteCmd(VulcanVoting plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("vulcanvoting.fakevote")) return false;
        Player player = Bukkit.getPlayer("OfficialGaming");
        VPlayer vPlayer = plugin.getVPlayerManager().getVPlayer(player.getUniqueId());
        vPlayer.setVoteMsgToggled(true);
        plugin.getVPlayerManager().processVoteRequest("OfficialGaming", false, true, "Testing");
        return true;
    }
}
