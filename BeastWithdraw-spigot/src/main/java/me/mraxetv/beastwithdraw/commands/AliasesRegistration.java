package me.mraxetv.beastwithdraw.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import me.mraxetv.beastwithdraw.BeastWithdrawPlugin;

public class AliasesRegistration {
	private BeastWithdrawPlugin pl;



	public AliasesRegistration(BeastWithdrawPlugin plugin) {
		pl = plugin;

	}

	public void setAliases(String cmdName, List<String> aliases) throws NoSuchMethodException,
	SecurityException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, NoSuchFieldException {
		Method getCommandMap = pl.getServer().getClass().getMethod("getCommandMap");
		SimpleCommandMap cmdMap = (SimpleCommandMap) getCommandMap.invoke(pl.getServer());
		List<String> list = cmdMap.getCommand(cmdName).getAliases();
		for(String s :aliases) {
			list.add(s.toLowerCase());
		}

		Command cmd = cmdMap.getCommand(cmdName).setAliases(list);
		cmdMap.register(cmdName, cmd);
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ("&7[&4Beast&bWithdraw&7] &2/" + cmdName + " command aliases &e" + list + " &2are registered.")));
	}
}