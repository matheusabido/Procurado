package br.com.abidux.procurado.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.abidux.procurado.models.Procurado;

public class ProcuradoAPI {
	
	public static List<String> SUFFIXES;
	public static boolean USE_SUFFIX;
	public static int CASAS_DECIMAIS;
	public static String FORMAT;
	public static double MIN_VALUE;
	
	public static double getQuantiaByString(String quantia) {
		try {
			return Double.parseDouble(quantia);
		} catch (NumberFormatException e) {}
		try {
			return Double.parseDouble(quantia.replace(",", "."));
		} catch (NumberFormatException e) {
			throw new NumberFormatException();
		}
	}
	
	private static DecimalFormat formatter, smallFormatter;
	public static String format(double amount) {
		if (formatter == null) {
			formatter = new DecimalFormat(USE_SUFFIX ? "0,000" : FORMAT);
			String format = "0";
			if (USE_SUFFIX) {
				if (CASAS_DECIMAIS > 0) {
					format += ".";
					for (int i = 0; i < CASAS_DECIMAIS; i++) {
						format += "0";
					}
				}
			} else {
				int index = FORMAT.indexOf(".");
				if (index != -1) format += FORMAT.substring(index);
			}
			smallFormatter = new DecimalFormat(format);
		}
		if (amount < 1000) return smallFormatter.format(amount);
		if (USE_SUFFIX) {
			final String raw = formatter.format(amount);
			final String[] comma = raw.split(",");
			final String[] s = comma.length > 1 ? raw.split(",") : raw.split("\\.");
			int index = s.length - 2;
			if (index < 0) index = 0;
			return (CASAS_DECIMAIS > 0 ? s[0] + "." + s[1].substring(0, CASAS_DECIMAIS) : s[0]) + SUFFIXES.get(index);
		} else return formatter.format(amount);
	}
	
	public static ArrayList<Procurado> getProcurados() {
		ArrayList<Procurado> procurados = new ArrayList<>();
		for (Procurado p : Procurado.procurados.values()) procurados.add(p);
		return procurados;
	}
	
	public static Procurado[] getTop(int top) {
		Procurado[] result = new Procurado[top];
		Collection<Procurado> procurados = getProcurados();
		for (int i = 0; i < top; i++) {
			double money = -1;
			Procurado procurado = null;
			for (Procurado c : procurados) {
				if (c.getValor() > money) {
					money = c.getValor();
					procurado = c;
				}
			}
			procurados.remove(procurado);
			result[i] = procurado;
		}
		return result;
	}
	
}