package br.com.abidux.procurado.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClick implements Listener {
	
	@EventHandler
	void click(InventoryClickEvent e) {
		if (e.getClickedInventory().getTitle().equals("§cLista de Procurados")) {
			e.setCancelled(true);
		}
	}
	
}