package me.mraxetv.beastwithdraw;

import me.mraxetv.beastwithdraw.filemanager.FolderYaml;
import org.bukkit.configuration.file.FileConfiguration;

public class WithdrawManager {

    private BeastWithdrawPlugin pl;
    private FolderYaml xpBottleFile;
    private FolderYaml cashNoteFile;

    public WithdrawManager(BeastWithdrawPlugin pl) {
        this.pl = pl;
        xpBottleFile = new FolderYaml(pl,"Withdraws","XpBottle.yml");
        cashNoteFile = new FolderYaml(pl,"Withdraws","CashNote.yml");
    }

    public FileConfiguration getXpBottleConfig(){
        return xpBottleFile.getConfig();
    }
    public  FileConfiguration getCashNoteConfig(){
        return  cashNoteFile.getConfig();
    }


}
