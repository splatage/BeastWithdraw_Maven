package me.mraxetv.beastwithdraw.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class CashRedeemEvent extends Event implements Cancellable {

    private final Player p;
    private boolean isCancelled;
    private final ItemStack item;
    private final boolean offHand;
    private double cash;

    public CashRedeemEvent(Player p, ItemStack item, double cash, boolean offHand) {
        this.p = p;
        this.item = item;
        this.offHand = offHand;
        this.cash = cash;
    }

    public Player getPlayer() { return p; }

    public ItemStack getItem() { return item; }

    public double getCash(){return cash;}

    public boolean inOffHand() { return offHand; }

    public boolean isCancelled() { return this.isCancelled; }

    public void setCancelled(boolean isCancelled) { this.isCancelled = isCancelled; }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }

}
