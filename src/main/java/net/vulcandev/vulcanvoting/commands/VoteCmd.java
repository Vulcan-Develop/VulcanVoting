package net.vulcandev.vulcanvoting.commands;

import net.vulcandev.vulcanvoting.VulcanVoting;
import net.vulcandev.vulcanvoting.guis.VoteGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCmd implements CommandExecutor {
    private final VulcanVoting plugin;

    public VoteCmd(VulcanVoting plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                new VoteGUI(plugin, player).open();
                return true;
            }
        }
        return false;
    }
}
