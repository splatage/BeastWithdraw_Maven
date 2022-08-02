package me.mraxetv.beastwithdraw;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "beastwithdraw-sponge",
        name = "BeastWithdraw",
        version = "2.9-SNAPSHOT",
        description = "Exp and Money withdrow plugin!",
        url = "",
        authors = {
                "MrAxeTv"
        },
        dependencies = {
                @Dependency(id = "Vault")
        }
)
public class BeastWithdrawSponge {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {


    }
}
