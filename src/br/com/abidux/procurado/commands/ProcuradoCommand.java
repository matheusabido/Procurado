package br.com.abidux.procurado.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import br.com.abidux.procurado.Main;
import br.com.abidux.procurado.managers.Messages;
import br.com.abidux.procurado.models.Procurado;
import br.com.abidux.procurado.utils.PlayerProcuradoEvent;
import br.com.abidux.procurado.utils.ProcuradoAPI;

public class ProcuradoCommand implements CommandExecutor {
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("procurado")) {
			if (sender instanceof Player) {
				final Player player = (Player) sender;
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase(player.getName())) {
						player.sendMessage(Messages.NAO_PODE_PROCURAR_A_SI_MESMO);
						return true;
					}
					final Player target = Bukkit.getPlayer(args[0]);
					if (target == null) {
						player.sendMessage(Messages.JOGADOR_OFFLINE);
						return true;
					}
					double quantia;
					try {
						quantia = ProcuradoAPI.getQuantiaByString(args[1]);
					} catch (NumberFormatException ex) {
						player.sendMessage(Messages.NUMERO_INVALIDO);
						return true;
					}
					if (quantia <= 0) {
						player.sendMessage(Messages.NUMERO_MENOR_OU_IGUAL_A_ZERO);
						return true;
					}
					if (quantia < ProcuradoAPI.MIN_VALUE) {
						player.sendMessage(Messages.QUANTIA_INSUFICIENTE);
						return true;
					}
					if (Main.economy.getBalance(player) < quantia) {
						player.sendMessage(Messages.SALDO_INSUFICIENTE);
						return true;
					}
					Main.economy.withdrawPlayer(player, quantia);
					Procurado procurado = Procurado.procurados.getOrDefault(target.getName().toLowerCase(), new Procurado(target.getName(), 0));
					procurado.setValor(procurado.getValor() + quantia);
					Procurado.procurados.put(target.getName().toLowerCase(), procurado);
					PlayerProcuradoEvent event = new PlayerProcuradoEvent(player, target, procurado);
					Bukkit.getPluginManager().callEvent(event);
					if (!event.isCancelled()) {
						final String message = Messages.PLAYER_SENDO_PROCURADO.replace("@sender", player.getName()).replace("@procurado", target.getName()).replace("@valor", ProcuradoAPI.format(quantia));
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.sendMessage(message);
						}
					}
				} else if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("procurado.admin")) {
					Main.instance.reloadConfig();
					Main.instance.loadConfig();
					player.sendMessage("§aPlugin recarregado.");
					return true;
				} else {
					final Procurado[] top = ProcuradoAPI.getTop(5);
					player.openInventory(getInventory(top));
				}
			} else sender.sendMessage(Messages.NAO_E_UM_JOGADOR);
		}
		return false;
	}
	
	private static final int[] slots = {11, 12, 13, 14, 15};
	private static Inventory getInventory(Procurado[] top) {
		Inventory inventory = Bukkit.createInventory(null, 3*9, "§cLista de Procurados");
		for (int i = 0; i < top.length; i++) {
			inventory.setItem(slots[i], getHead(top[i]));
		}
		return inventory;
	}
	
	private static ItemStack getHead(Procurado procurado) {
		Material material = Material.getMaterial("PLAYER_HEAD");
		ItemStack item = material == null ? new ItemStack(Material.SKULL_ITEM, 1, (short) 3) : new ItemStack(Material.getMaterial("PLAYER_HEAD"));
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		if (procurado != null) {
			meta.setOwner(procurado.getNome());
			meta.setDisplayName("§c" + procurado.getNome());
			meta.setLore(Arrays.asList(Messages.PROCURADO_VALOR.replace("@valor", ProcuradoAPI.format(procurado.getValor()))));
		} else meta.setDisplayName("§cNenhum");
		item.setItemMeta(meta);
		return item;
	}
	
}