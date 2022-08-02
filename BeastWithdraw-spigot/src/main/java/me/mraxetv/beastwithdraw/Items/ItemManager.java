package me.mraxetv.beastwithdraw.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;


import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import me.mraxetv.beastwithdraw.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.mraxetv.beastwithdraw.BeastWithdrawPlugin;
import me.mraxetv.beastwithdraw.utils.Version;


public class ItemManager {
    private BeastWithdrawPlugin pl;

    TreeMap<Integer, Integer> xpList;
    TreeMap<Integer, Integer> cashNote;

    private boolean xpAmountModelsB = false;
    private boolean cashAmountModelsB = false;

    public ItemManager(BeastWithdrawPlugin plugin) {
        pl = plugin;
        init();
    }

    public void init() {

        if (pl.getWithdrawManager().getXpBottleConfig().getBoolean("XpBottle.CustomModel.AmountModelData.Enabled")) {

            xpList = new TreeMap<>(Collections.reverseOrder());

            if (pl.getWithdrawManager().getXpBottleConfig().isSet("XpBottle.CustomModel.AmountModelData.Range")) {
                xpAmountModelsB = true;

                for (String s : pl.getWithdrawManager().getXpBottleConfig().getStringList("XpBottle.CustomModel.AmountModelData.Range")) {
                    String args[] = s.split("-");
                    if (!Utils.isInt(args[0])) continue;
                    String data[] = args[1].split(":");
                    if (!Utils.isInt(data[1])) continue;
                    xpList.put(Integer.parseInt(args[0]), Integer.parseInt(data[1]));
                }
            }

        }
        if (pl.getWithdrawManager().getCashNoteConfig().getBoolean("CashNote.CustomModel.AmountModelData.Enabled")) {
            cashNote = new TreeMap<>(Collections.reverseOrder());
            cashAmountModelsB = true;

            if (pl.getWithdrawManager().getCashNoteConfig().isSet("CashNote.CustomModel.AmountModelData.Range")) {

                for (String s : pl.getWithdrawManager().getCashNoteConfig().getStringList("CashNote.CustomModel.AmountModelData.Range")) {
                    String args[] = s.split("-");
                    if (!Utils.isInt(args[0])) continue;
                    String data[] = args[1].split(":");
                    if (!Utils.isInt(data[1])) continue;
                    cashNote.put(Integer.parseInt(args[0]), Integer.parseInt(data[1]));
                }
            }
        }

    }


    public ItemStack getXpb(String owner, int value, int amount, boolean signed) {

        ItemStack item = new ItemStack(MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_13_R1) ? Material.EXPERIENCE_BOTTLE : Material.valueOf("EXP_BOTTLE"), amount);
        if (pl.getWithdrawManager().getXpBottleConfig().isSet("XpBottle.Data")) {
            item.setDurability((short) pl.getWithdrawManager().getXpBottleConfig().getInt("XpBottle.Data"));
        }
        ItemMeta meta = item.getItemMeta();
        if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_14_R1)) {
            if (xpAmountModelsB) {
                for (int i : xpList.keySet())
                    if (value >= i) {
                        meta.setCustomModelData(xpList.get(i));
                        break;
                    }
            }else meta.setCustomModelData(pl.getWithdrawManager().getXpBottleConfig().getInt("XpBottle.CustomModel.Data"));
        }


        if (signed) {

            String n = pl.getWithdrawManager().getXpBottleConfig().getString("XpBottle.Player.Name");
            n = n.replaceAll("%player%", owner);
            n = n.replaceAll("%value%", "" + pl.getUtils().formatNumber(value));
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', n));
            ArrayList<String> lore = new ArrayList<String>();
            for (String s : pl.getWithdrawManager().getXpBottleConfig().getStringList("XpBottle.Player.Lore")) {
                s = ChatColor.translateAlternateColorCodes('&', s);
                s = s.replace("%player%", "" + owner);
                s = s.replace("%value%", "" + pl.getUtils().formatNumber(value));
                lore.add(s);
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            NBTItem tag = new NBTItem(item);
            tag.setInteger(pl.getWithdrawManager().getXpBottleConfig().getString("XpBottle.NBTLore"), value);
            tag.setBoolean("bCraft", true);

            item = tag.getItem();
            return item;
        }
        String n = pl.getWithdrawManager().getXpBottleConfig().getString("XpBottle.Server.Name");
        n = n.replaceAll("%value%", "" + pl.getUtils().formatNumber(value));
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', n));
        ArrayList<String> lore = new ArrayList<String>();
        for (String s : pl.getWithdrawManager().getXpBottleConfig().getStringList("XpBottle.Server.Lore")) {
            s = ChatColor.translateAlternateColorCodes('&', s);
            s = s.replace("%value%", "" + pl.getUtils().formatNumber(value));
            lore.add(s);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        NBTItem tag = new NBTItem(item);
        tag.setInteger(pl.getWithdrawManager().getXpBottleConfig().getString("XpBottle.NBTLore"), value);
        tag.setBoolean("bCraft", true);
        item = tag.getItem();
        return item;
    }

    public ItemStack getCashNote(String owner, double value, int amount, boolean signed) {

        ItemStack item = new ItemStack(Material.getMaterial(pl.getWithdrawManager().getCashNoteConfig().getString("CashNote.Item")), amount);
        if (pl.getWithdrawManager().getCashNoteConfig().isSet("CashNote.Data")) {
            item.setDurability((short) pl.getWithdrawManager().getCashNoteConfig().getInt("CashNote.Data"));
        }
        ItemMeta meta = item.getItemMeta();

        if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_14_R1)) {
            if (cashAmountModelsB) {
                for (int i : cashNote.keySet())
                    if (value >= i) {
                        meta.setCustomModelData(cashNote.get(i));
                        break;
                    }
            }else meta.setCustomModelData(pl.getWithdrawManager().getCashNoteConfig().getInt("CashNote.CustomModel.Data"));
        }
        if (signed) {

            if (pl.getWithdrawManager().getCashNoteConfig().getBoolean("CashNote.Glow")) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            String n = pl.getWithdrawManager().getCashNoteConfig().getString("CashNote.Player.Name");
            n = n.replaceAll("%player%", owner);
            n = n.replaceAll("%value%", "" + pl.getUtils().formatDouble(value));
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', n));
            ArrayList<String> lore = new ArrayList<String>();
            for (String s : pl.getWithdrawManager().getCashNoteConfig().getStringList("CashNote.Player.Lore")) {
                s = ChatColor.translateAlternateColorCodes('&', s);
                s = s.replace("%player%", "" + owner);
                s = s.replace("%value%", "" + pl.getUtils().formatDouble(value));
                lore.add(s);
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            NBTItem tag = new NBTItem(item);
            tag.setDouble(pl.getWithdrawManager().getCashNoteConfig().getString("CashNote.NBTLore"), value);
            tag.setBoolean("bCraft", true);
            item = tag.getItem();
            return item;
        }
        if (pl.getWithdrawManager().getCashNoteConfig().getBoolean("CashNote.Glow")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        String n = pl.getWithdrawManager().getCashNoteConfig().getString("CashNote.Server.Name");
        n = n.replaceAll("%value%", "" + pl.getUtils().formatDouble(value));
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', n));
        ArrayList<String> lore = new ArrayList<String>();
        for (String s : pl.getWithdrawManager().getCashNoteConfig().getStringList("CashNote.Server.Lore")) {
            s = ChatColor.translateAlternateColorCodes('&', s);
            s = s.replace("%value%", "" + pl.getUtils().formatDouble(value));
            lore.add(s);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        NBTItem tag = new NBTItem(item);
        tag.setDouble(pl.getWithdrawManager().getCashNoteConfig().getString("CashNote.NBTLore"), value);
        tag.setBoolean("bCraft", true);
        item = tag.getItem();
        return item;
    }


}
