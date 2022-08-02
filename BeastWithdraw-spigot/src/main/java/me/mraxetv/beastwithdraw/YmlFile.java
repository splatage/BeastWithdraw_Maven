package me.mraxetv.beastwithdraw;

import me.mraxetv.beastwithdraw.filemanager.YamlCommentor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;


public class YmlFile {

	private File customConfigFile;
	private FileConfiguration customConfig;
	private JavaPlugin pl;
	private String name ;


	public YmlFile(JavaPlugin pl, String name) {
		this.pl= pl;
		this.name = name;
		saveDeafultConfig();
		configUpdate();
		addDefaults();

	}

	public FileConfiguration getConfig() {
		return this.customConfig;
	}


	public void addDefaults(){

		InputStreamReader defaultConfigStream = null;
		YamlConfiguration defaultConfig = null;
		try {
			defaultConfigStream = new InputStreamReader(pl.getResource(this.name), "UTF-8");

			if (defaultConfigStream != null) {
				defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
				//keys
				Set<String> keys = customConfig.getKeys(true);

				for(String s : defaultConfig.getKeys(true)){
					if(!keys.contains(s)){
						customConfig.setDefaults(defaultConfig);
						customConfig.options().copyDefaults(true);
						saveConfig();
						return;
					}
				}
			}
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	public void saveConfig() {
		YamlCommentor.saveCommented(customConfig,customConfigFile);
	}



	public void saveDeafultConfig() {
		customConfigFile = new File(pl.getDataFolder(), name);
		if (!customConfigFile.exists()) {
			customConfigFile.getParentFile().mkdirs();
			//Getting proper version of file 1.8 or 1.13+
            File temp;  

            	  pl.saveResource(name, false);
            	  customConfigFile = new File(pl.getDataFolder()+"/"+name);


		}
         //Loading File
		customConfig= new YamlConfiguration();
		try {
			customConfig.load(new InputStreamReader(new FileInputStream(customConfigFile), "UTF-8"));
			//customConfig.load(customConfigFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void configUpdate() {
		InputStreamReader newConfigStream = null;
		YamlConfiguration newConfig = null;
		try {
				newConfigStream = new InputStreamReader(pl.getResource(name), "UTF-8");

			if (newConfigStream != null) {
				newConfig = YamlConfiguration.loadConfiguration((Reader)newConfigStream);
			}
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		double configVersion = newConfig.getDouble("ConfigVersion");
		double oldVersion = customConfig.getDouble("ConfigVersion");


		if(customConfig.getDouble("ConfigVersion") < configVersion || !customConfig.isSet("ConfigVersion")) {
			File file;
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd-HH.mm.ss");
			Date date = new Date();
			//System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43

			file = new File(pl.getDataFolder(), name);
			boolean renameResult = file.renameTo(new File(pl.getDataFolder(), name.replaceAll(".yml"," ")+ dateFormat.format(date) + "_old.yml"));
			if(renameResult) {
				saveDeafultConfig();
				pl.getServer().getConsoleSender().sendMessage(
						ChatColor.translateAlternateColorCodes('&',"&7[&4Beast&bWithdraw&7] &cOld "+name+" ("+oldVersion+") has been replaced with new version " + configVersion));

			}else {
				pl.getServer().getConsoleSender().sendMessage(
						ChatColor.translateAlternateColorCodes('&',"&7[&4Beast&bWithdraw&7] &cServer has failed to replace old version of "+
						name + " please contact author MrAxeTv!"));

			}

		}
	}



}
