package net.vulcandev.vulcanvoting.commands;

import net.vulcandev.vulcanvoting.VulcanVoting;
import net.vulcandev.vulcanvoting.guis.VoteGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for the /vote command.
 * Opens a GUI for players to view vote information and statistics.
 */
public class VoteCmd implements CommandExecutor {
    private final VulcanVoting plugin;

    /**
     * Constructs a new VoteCmd instance.
     *
     * @param plugin the VulcanVoting plugin instance
     */
    public VoteCmd(VulcanVoting plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes the vote command.
     * Opens the vote GUI if the sender is a player and no arguments are provided.
     *
     * @param sender the command sender
     * @param command the command being executed
     * @param label the alias used for the command
     * @param args the command arguments
     * @return true if the command was handled successfully, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                new VoteGUI(plugin, player).open();
                return true;
            }
        }
        return false;
    }
}
