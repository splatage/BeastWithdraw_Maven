package me.mraxetv.beastwithdraw.commands;
import me.mraxetv.beastwithdraw.BeastWithdrawPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandModule
{
    public String permission;
    public int minArgs;
    public int maxArgs;
    public BeastWithdrawPlugin pl;

    /**
     * @param permission - The label of the command.
     * @param minArgs - The minimum amount of arguments.
     * @param maxArgs - The maximum amount of arguments.
     */
    public CommandModule(BeastWithdrawPlugin pl, String permission, int minArgs, int maxArgs)
    {
        this.pl = pl;
        this.permission = permission;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    //This method will process the command.
    public abstract void run(CommandSender sender, String[] args);

    public boolean hasEnoughArgs(String[] args){
        return (args.length >= minArgs && args.length <= maxArgs);
    }

    public boolean isOnline(String name){
        return Bukkit.getPlayer(name) != null;

    }



    public abstract List<String> getTabComplete(final CommandSender sender, String[] args);

}
