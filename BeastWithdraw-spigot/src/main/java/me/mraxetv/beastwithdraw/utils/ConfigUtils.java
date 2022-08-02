package me.mraxetv.beastwithdraw.utils;

import me.mraxetv.beastwithdraw.BeastWithdrawPlugin;

public final class ConfigUtils {


    private BeastWithdrawPlugin pl;

    public static String AMOUNT;


    public ConfigUtils(BeastWithdrawPlugin pl) {
        this.pl = pl;
        AMOUNT = pl.getMessages().getString("PlaceHolders.AmountFormat");
    }




}
