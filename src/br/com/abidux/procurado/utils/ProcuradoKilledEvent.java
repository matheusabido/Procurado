package br.com.abidux.procurado.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.abidux.procurado.models.Procurado;

public class ProcuradoKilledEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private final Procurado procurado;
	private final Player killer, entity;
	
	public ProcuradoKilledEvent(Player killer, Player entity, Procurado procurado) {
		this.procurado = procurado;
		this.killer = killer;
		this.entity = entity;
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
	
	public Player getKiller() {
		return killer;
	}
	
	public Player getEntity() {
		return entity;
	}
	
}