package br.com.abidux.procurado.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.abidux.procurado.models.Procurado;

public class PlayerProcuradoEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private final Procurado procurado;
	private final Player sender, target;
	
	public PlayerProcuradoEvent(Player sender, Player target, Procurado procurado) {
		this.procurado = procurado;
		this.sender = sender;
		this.target = target;
	}
	
	public Player getTarget() {
		return target;
	}
	
	public Player getSender() {
		return sender;
	}
	
	public Procurado getProcurado() {
		return procurado;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
}