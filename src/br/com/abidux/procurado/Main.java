package br.com.abidux.procurado;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import br.com.abidux.procurado.commands.ProcuradoCommand;
import br.com.abidux.procurado.listeners.InventoryClick;
import br.com.abidux.procurado.listeners.PlayerDeath;
import br.com.abidux.procurado.managers.Messages;
import br.com.abidux.procurado.models.Procurado;
import br.com.abidux.procurado.utils.ProcuradoAPI;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	
	public static Economy economy;
	public static Main instance;
	
	@Override
	public void onEnable() {
		instance = this;
		if (!new File(getDataFolder(), "config.yml").exists()) saveDefaultConfig();
		loadConfig();
		load();
		setupEconomy();
		Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
		getCommand("procurado").setExecutor(new ProcuradoCommand());
	}
	
	@Override
	public void onDisable() {
		save();
	}
	
	@SuppressWarnings("unchecked")
	private void save() {
		JSONObject object = new JSONObject();
		for (Procurado procurado : Procurado.procurados.values()) {
			JSONObject obj = new JSONObject();
			obj.put("nome", procurado.getNome());
			obj.put("valor", procurado.getValor());
			object.put(procurado.getNome().toLowerCase(), obj);
		}
		File file = new File(getDataFolder(), "data.json");
		try {
			PrintWriter  writer = new PrintWriter(file);
			writer.print(object.toJSONString());
			writer.close();
		} catch (FileNotFoundException e) {
			Bukkit.getConsoleSender().sendMessage("§c[Procurado] Não foi possível salvar os jogadores.");
		}
	}
	
	private void load() {
		File file = new File(getDataFolder(), "data.json");
		if (file.exists()) {
			try {
				JSONObject object = (JSONObject) new JSONParser().parse(new FileReader(file));
				for (Object key : object.keySet()) {
					JSONObject obj = (JSONObject) object.get(key);
					String nome = (String) obj.get("nome");
					double valor = (double) obj.get("valor");
					Procurado.procurados.put(nome.toLowerCase(), new Procurado(nome, valor));
				}
			} catch (IOException | ParseException e) {
				Bukkit.getConsoleSender().sendMessage("§c[Procurado] Não foi possível carregar os jogadores.");
			}
		}
	}
	
	public void loadConfig() {
		long start = System.currentTimeMillis();
		Bukkit.getConsoleSender().sendMessage("§e[Procurado] Carregando a configuração.");
		ProcuradoAPI.MIN_VALUE = getConfig().getDouble("valor_minimo");
		ProcuradoAPI.USE_SUFFIX = getConfig().getBoolean("money_format.use_suffix");
		ProcuradoAPI.FORMAT = getConfig().getString("money_format.format");
		ProcuradoAPI.SUFFIXES = getConfig().getStringList("suffixes");
		ProcuradoAPI.CASAS_DECIMAIS = getConfig().getInt("money_format.casas_decimais");
		final String min_value = ProcuradoAPI.format(ProcuradoAPI.MIN_VALUE);
		for (String key : getConfig().getConfigurationSection("mensagens").getKeys(true)) {
			final String value = getConfig().getString("mensagens." + key).replace("&", "§").replace("@min_value", min_value);
			try {
				Field field = Messages.class.getDeclaredField(key.toUpperCase());
				field.set(null, value);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				Bukkit.getConsoleSender().sendMessage("§c[Procurado] Erro ao tentar carregar a mensagem \"" + key + "\".");
				continue;
			}
		}
		Bukkit.getConsoleSender().sendMessage("§e[Procurado] Configuração carregada em " + (System.currentTimeMillis() - start) + "ms");
	}
	
	private void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) economy = economyProvider.getProvider();
    }
	
	@SuppressWarnings("unchecked")
	public static List<Player> getOnlinePlayers() {
		try {
			Method field = Bukkit.class.getDeclaredMethod("getOnlinePlayers");
			field.setAccessible(true);
			return (List<Player>) field.invoke(null);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}