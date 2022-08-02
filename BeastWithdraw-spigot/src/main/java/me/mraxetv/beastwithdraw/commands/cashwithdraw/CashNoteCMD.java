package me.mraxetv.beastwithdraw.commands.cashwithdraw;

import me.mraxetv.beastwithdraw.BeastWithdrawPlugin;
import me.mraxetv.beastwithdraw.commands.CommandModule;
import me.mraxetv.beastwithdraw.utils.Utils;
import me.mraxetv.beastwithdraw.utils.XpManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CashNoteCMD extends CommandModule implements CommandExecutor {

    private BeastWithdrawPlugin pl;
    String message;
    List<String> messagel;

    public CashNoteCMD(BeastWithdrawPlugin plugin) {
        super(plugin,"BeastWithdraw.CashNote.Withdraw",1,2);
        pl = plugin;

        try {
            pl.getAliases().setAliases("bWithdraw", pl.getWithdrawManager().getCashNoteConfig().getStringList("CashNote.Aliases"));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            Utils.sendMessage(sender,"%prefix% Console can't use this command! /BeastWithdraw help");
            return true;
        }
        run(sender,args);
        return true;
    }


    @Override
    public void run(CommandSender sender, String[] args) {

        Player p = (Player) sender;

        //Check if has permission!
        if (!sender.hasPermission(permission)) {
            pl.getUtils().noPermission(p);
            return;
        }

        if(!hasEnoughArgs(args)) {
            String s = pl.getMessages().getString("Withdraws.CashNote.Cash");
            s = s.replaceAll("%balance%", "" + pl.getUtils().formatDouble(pl.getEcon().getBalance(p)));
            s = ChatColor.translateAlternateColorCodes('&', s);
            pl.getUtils().sendMessage(p, s);
            return;
        }
        double takenCash;

        int amount = 1;

        if (args.length == 2) {
            if (!Utils.isInt(args[1])) {
                String s = pl.getMessages().getString("Withdraws.NoNumber");
                s = s.replaceAll("%prefix%", Utils.getPrefix());
                s = s.replaceAll("%value%", args[1]);
                pl.getUtils().sendMessage(sender, s);
                return;
            }
            amount = Integer.parseInt(args[1]);
        }


        if ((args.length >= 1)) {
            //Withdraw all
            if (args[0].equalsIgnoreCase("all")) {
                if (!sender.hasPermission(permission+".All")) {
                    pl.getUtils().noPermission(p);
                    return;
                }
                takenCash = new Double(Utils.df2.format(pl.getEcon().getBalance(p)));
            }
            //Regular double check
            else if (!pl.getUtils().isDouble(args[0])) {
                String s = pl.getMessages().getString("Withdraws.NoNumber");
                s = s.replaceAll("%value%", args[0]);
                pl.getUtils().sendMessage(sender, s);
                return;
            } else {
                takenCash = Double.parseDouble(Utils.df2.format(Double.parseDouble(args[0])));
            }

            //Drop to floor if there is no empty slot
            if (!pl.getConfig().getBoolean("Settings.WithdrawDropFloor")) {
                if (p.getInventory().firstEmpty() == -1) {
                    message = pl.getMessages().getString("Withdraws.FullInventory");
                    pl.getUtils().sendMessage(p, message);
                    return;
                }
            }
            //Check if cash is bigger then 0
            if ((takenCash <= 0)) {
                message = pl.getMessages().getString("Withdraws.CashNote.Min");
                message = message.replaceAll("%mincash%", "" + 1);
                message = ChatColor.translateAlternateColorCodes('&', message);
                pl.getUtils().sendMessage(p, message);
                return;
            }
            double minCash = 0;
            //Limit min and max amount of xp which can be withdrawn
            if (!sender.hasPermission("BeastWithdraw.CashNote.ByPass.WithdrawLimit")) {
                minCash = pl.getWithdrawManager().getCashNoteConfig().getDouble("CashNote.Min");
                if (pl.getWithdrawManager().getCashNoteConfig().getBoolean("CashNote.PermNotes.Enabled")) {
                    for (String s : pl.getWithdrawManager().getCashNoteConfig().getConfigurationSection("CashNote.PermNotes").getKeys(false)) {
                        if (sender.hasPermission("BeastWithdraw.CashNote.PermNotes." + s)) {
                            minCash = pl.getWithdrawManager().getCashNoteConfig().getDouble("CashNote.PermNotes." + s + ".Min");
                        }
                    }

                }
                if ((takenCash < minCash)) {
                    message = pl.getMessages().getString("Withdraws.CashNote.Min");
                    message = message.replaceAll("%mincash%", pl.getUtils().formatDouble(pl.getConfig().getDouble("CashNote.Min")));
                    pl.getUtils().sendMessage(p, message);
                    return;
                }


                double maxCash = pl.getWithdrawManager().getCashNoteConfig().getDouble("CashNote.Max");
                if (pl.getWithdrawManager().getCashNoteConfig().getBoolean("CashNote.PermNotes.Enabled")) {
                    for (String s : pl.getWithdrawManager().getCashNoteConfig().getConfigurationSection("CashNote.PermNotes").getKeys(false)) {
                        if (sender.hasPermission("BeastWithdraw.CashNote.PermNotes." + s)) {
                            maxCash = pl.getWithdrawManager().getCashNoteConfig().getDouble("CashNote.PermNotes." + s + ".Max");
                        }
                    }
                }

                if ((takenCash > maxCash)) {
                    message = pl.getMessages().getString("Withdraws.CashNote.Max");
                    message = message.replaceAll("%maxcash%", pl.getUtils().formatDouble(maxCash));
                    pl.getUtils().sendMessage(p, message);
                    return;
                }
            }
            double balance = pl.getEcon().getBalance(p);

            if ((balance < takenCash * amount)) {
                message = pl.getMessages().getString("Withdraws.CashNote.NotEnough");
                message = message.replaceAll("%balance%", ""+pl.getUtils().formatDouble(balance));
                message = message.replaceAll("%takencash%", ""+pl.getUtils().formatDouble(takenCash*amount));;
                pl.getUtils().sendMessage(p, message);
                return;
            }

            //Charge Fee
            if (!p.isPermissionSet("BeastWithdraw.CashNote.ByPass.Fee")) {
                if (pl.getWithdrawManager().getCashNoteConfig().getBoolean("CashNote.Charges.Fee.Enabled")) {

                    double bal = pl.getEcon().getBalance(p);

                    //Money Fee
                    double moneyFee = pl.getWithdrawManager().getCashNoteConfig().getDouble("CashNote.Charges.Fee.Cost");
                    //lower the amount for fee in case 'all' argument is used!!
                    if(args[0].equalsIgnoreCase("all")) takenCash = takenCash-moneyFee;

                    if (bal < takenCash*amount +moneyFee*amount) {
                        String s = pl.getMessages().getString("Withdraws.CashNote.Fee.NotEnough");
                        s = s.replaceAll("%fee%", "" + pl.getUtils().formatDouble(moneyFee*amount));
                        pl.getUtils().sendMessage(p, s);
                        return;
                    }
                    pl.getEcon().withdrawPlayer(p, moneyFee*amount);
                    String s = pl.getMessages().getString("Withdraws.CashNote.Fee.TakenFee");
                    s = s.replaceAll("%fee%", "" + pl.getUtils().formatDouble(moneyFee*amount));
                    pl.getUtils().sendMessage(p, s);
                }
            }
            double tax = 0;
            //Charge Tax
            if (!p.isPermissionSet("BeastWithdraw.CashNote.ByPass.Tax")) {
                if (pl.getWithdrawManager().getCashNoteConfig().getBoolean("CashNote.Charges.Tax.Enabled")) {
                    double percentage = pl.getWithdrawManager().getCashNoteConfig().getDouble("CashNote.Charges.Tax.Percentage");
                    if (percentage > 100.0) percentage = 100.0;
                    tax = (takenCash * (percentage / 100));
                    String s = pl.getMessages().getString("Withdraws.CashNote.Tax.TakenTax");
                    s = s.replaceAll("%tax%", "" + pl.getUtils().formatDouble(tax * amount));
                    pl.getUtils().sendMessage(p, s);
                }
            }
            pl.getEcon().withdrawPlayer(p, takenCash*amount);


            String s = pl.getMessages().getString("Withdraws.CashNote.Withdraw");
            s = s.replaceAll("%takencash%", "" + pl.getUtils().formatDouble(takenCash*amount));
            s = s.replaceAll("%balance%", "" + Utils.formatDouble(pl.getEcon().getBalance(p)));
            Utils.sendMessage(p, s);

            takenCash = takenCash - tax;


            ItemStack cashNote = pl.getItemManger().getCashNote(p.getName(), takenCash, amount, true);
            if (p.getInventory().firstEmpty() != -1) {
                p.getInventory().addItem(cashNote);
            } else {
                p.getWorld().dropItem(p.getLocation(), cashNote);
            }

            if (pl.getWithdrawManager().getCashNoteConfig().getBoolean("CashNote.Sounds.Withdraw.Enabled")) {
                try {
                    String sound = pl.getWithdrawManager().getCashNoteConfig().getString("CashNote.Sounds.Withdraw.Sound");
                    p.playSound(p.getLocation(), Sound.valueOf(sound), 1f, 1f);

                } catch (Exception e) {
                    Bukkit.getServer().getConsoleSender().sendMessage(pl.getUtils().getPrefix() + "§cBroken sound in CashNote Withdraw section!");
                }
            }
        }
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
