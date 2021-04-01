package br.com.abidux.procurado.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import br.com.abidux.procurado.Main;
import br.com.abidux.procurado.managers.Messages;
import br.com.abidux.procurado.models.Procurado;
import br.com.abidux.procurado.utils.ProcuradoAPI;
import br.com.abidux.procurado.utils.ProcuradoKilledEvent;

public class PlayerDeath implements Listener {
	
	@EventHandler
	void death(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			final Player entity = event.getEntity();
			final Player killer = entity.getKiller();
			if (Procurado.procurados.containsKey(entity.getName().toLowerCase())) {
				final Procurado procurado = Procurado.procurados.get(entity.getName().toLowerCase());
				Procurado.procurados.remove(entity.getName().toLowerCase());
				Main.economy.depositPlayer(killer, procurado.getValor());
				ProcuradoKilledEvent procuradoKilledEvent = new ProcuradoKilledEvent(killer, entity, procurado);
				Bukkit.getPluginManager().callEvent(procuradoKilledEvent);
				final String message = Messages.PROCURADO_MORTO.replace("@procurado", entity.getName()).replace("@killer", killer.getName()).replace("@valor", ProcuradoAPI.format(procurado.getValor()));
				for (Player p : Main.getOnlinePlayers()) {
					p.sendMessage(message);
				}
			}
		}
	}
	
}