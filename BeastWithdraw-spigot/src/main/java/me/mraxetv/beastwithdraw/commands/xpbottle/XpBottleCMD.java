package me.mraxetv.beastwithdraw.commands.xpbottle;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
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
import me.mraxetv.beastwithdraw.BeastWithdrawPlugin;

public class XpBottleCMD extends CommandModule implements CommandExecutor {

    private BeastWithdrawPlugin pl;
    String message;
    List<String> messagel;

    public XpBottleCMD(BeastWithdrawPlugin plugin) {
        super(plugin,"BeastWithdraw.XpBottle.Withdraw",1,2);
        pl = plugin;

        try {
            pl.getAliases().setAliases("XpBottle", pl.getWithdrawManager().getXpBottleConfig().getStringList("XpBottle.Aliases"));
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
            String s = pl.getMessages().getString("Withdraws.XpBottle.Xpb");
            s = s.replaceAll("%value%", "" + pl.getUtils().formatNumber(XpManager.getTotalExperience(p)));
            s = s.replaceAll("%xp%", "" + pl.getUtils().formatNumber(XpManager.getTotalExperience(p)));
            s = ChatColor.translateAlternateColorCodes('&', s);
            pl.getUtils().sendMessage(p, s);
            return;
        }
        int takenXp;

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
                takenXp = XpManager.getTotalExperience(p);
            }
            //Withdraw with levels
            else if(args[0].toLowerCase().endsWith("l") && Utils.isInt(args[0].toLowerCase().split("l")[0])){

                int lv = Integer.parseInt(args[0].toLowerCase().split("l")[0]);
                takenXp = XpManager.getExpToLevel(lv);
            }
            //Regular integer check
            else if (!pl.getUtils().isInt(args[0])) {
                String s = pl.getMessages().getString("Withdraws.NoNumber");
                s = s.replaceAll("%value%", args[0]);
                pl.getUtils().sendMessage(sender, s);
                return;
            } else {
                takenXp = Math.abs(Integer.parseInt(args[0]));
            }

            //Drop to floor if there is no empty slot
            if (!pl.getConfig().getBoolean("Settings.WithdrawDropFloor")) {
                if (p.getInventory().firstEmpty() == -1) {
                    message = pl.getMessages().getString("Withdraws.FullInventory");
                    pl.getUtils().sendMessage(p, message);
                    return;
                }
            }
            //Check if xp is bigger then 0
            if ((takenXp <= 0)) {
                message = pl.getMessages().getString("Withdraws.XpBottle.Min");
                message = message.replaceAll("%minxp%", "" + 1);
                message = ChatColor.translateAlternateColorCodes('&', message);
                pl.getUtils().sendMessage(p, message);
                return;
            }
            int minXp = 0;
            //Limit min and max amount of xp which can be withdrawn
            if (!sender.hasPermission("BeastWithdraw.XpBottle.ByPass.WithdrawLimit")) {
                minXp = pl.getWithdrawManager().getXpBottleConfig().getInt("XpBottle.Min");
                if (pl.getWithdrawManager().getXpBottleConfig().getBoolean("XpBottle.PermNotes.Enabled")) {
                    for (String s : pl.getWithdrawManager().getXpBottleConfig().getConfigurationSection("XpBottle.PermNotes").getKeys(false)) {
                        if (sender.hasPermission("BeastWithdraw.XpBottle.PermNotes." + s)) {
                            minXp = pl.getWithdrawManager().getXpBottleConfig().getInt("XpBottle.PermNotes." + s + ".Min");
                        }
                    }

                }
                if ((takenXp < minXp)) {
                    message = pl.getMessages().getString("Withdraws.XpBottle.Min");
                    message = message.replaceAll("%minxp%", pl.getUtils().formatNumber(minXp));
                    pl.getUtils().sendMessage(p, message);
                    return;
                }


                int maxXp = pl.getWithdrawManager().getXpBottleConfig().getInt("XpBottle.Max");
                if (pl.getWithdrawManager().getXpBottleConfig().getBoolean("XpBottle.PermNotes.Enabled")) {
                    for (String s : pl.getWithdrawManager().getXpBottleConfig().getConfigurationSection("XpBottle.PermNotes").getKeys(false)) {
                        if (sender.hasPermission("BeastWithdraw.XpBottle.PermNotes." + s)) {
                            maxXp = pl.getWithdrawManager().getXpBottleConfig().getInt("XpBottle.PermNotes." + s + ".Max");
                        }
                    }
                }

                if ((takenXp > maxXp)) {
                    message = pl.getMessages().getString("Withdraws.XpBottle.Max");
                    message = message.replaceAll("%maxxp%", pl.getUtils().formatNumber(maxXp));
                    pl.getUtils().sendMessage(p, message);
                    return;
                }
            }
            int xp = XpManager.getTotalExperience(p);

            if ((xp < takenXp * amount)) {
                message = pl.getMessages().getString("Withdraws.XpBottle.NotEnough");
                message = message.replaceAll("%xp%", "" + pl.getUtils().formatNumber(xp));
                message = message.replaceAll("%takenxp%", "" + pl.getUtils().formatNumber(takenXp));
                pl.getUtils().sendMessage(p, message);
                return;
            }

            //Charge Fee
            if (!p.isPermissionSet("BeastWithdraw.XpBottle.ByPass.Fee")) {
                if (pl.getWithdrawManager().getXpBottleConfig().getBoolean("XpBottle.Charges.Fee.Enabled")) {

                    double bal = pl.getEcon().getBalance(p);
                    //Money Fee
                    double moneyFee = pl.getWithdrawManager().getXpBottleConfig().getDouble("XpBottle.Charges.Fee.Cost");
                    if (bal < moneyFee*amount) {
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
            int tax = 0;
            //Charge Tax
            if (!p.isPermissionSet("BeastWithdraw.XpBottle.ByPass.Tax")) {
                if (pl.getWithdrawManager().getXpBottleConfig().getBoolean("XpBottle.Charges.Tax.Enabled")) {
                    double percentage = pl.getWithdrawManager().getXpBottleConfig().getDouble("XpBottle.Charges.Tax.Percentage");
                    if (percentage > 100.0) percentage = 100.0;
                    tax = (int) (takenXp * (percentage / 100));
                    String s = pl.getMessages().getString("Withdraws.XpBottle.Tax.TakenTax");
                    s = s.replaceAll("%tax%", "" + pl.getUtils().formatNumber(tax * amount));
                    pl.getUtils().sendMessage(p, s);
                }
            }
            XpManager.setTotalExperience(p, (xp - takenXp*amount));

            String s = pl.getMessages().getString("Withdraws.XpBottle.Withdraw");
            s = s.replaceAll("%takenxp%", "" + pl.getUtils().formatNumber(takenXp*amount));
            Utils.sendMessage(p, s);

            takenXp = takenXp - tax;


            ItemStack Xpb = pl.getItemManger().getXpb(p.getName(), takenXp, amount, true);
            if (p.getInventory().firstEmpty() != -1) {
                p.getInventory().addItem(Xpb);
            } else {
                p.getWorld().dropItem(p.getLocation(), Xpb);
            }

            if (pl.getWithdrawManager().getXpBottleConfig().getBoolean("XpBottle.Sounds.Withdraw.Enabled")) {
                try {
                    String sound = pl.getWithdrawManager().getXpBottleConfig().getString("XpBottle.Sounds.Withdraw.Sound");
                    p.playSound(p.getLocation(), Sound.valueOf(sound), 1f, 1f);

                } catch (Exception e) {
                    Bukkit.getServer().getConsoleSender().sendMessage(pl.getUtils().getPrefix() + "§cBroken sound in XpBottle Withdraw section!");
                }
            }
        }
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
