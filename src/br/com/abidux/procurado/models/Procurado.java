package br.com.abidux.procurado.models;

import java.util.HashMap;

public class Procurado {
	
	public static HashMap<String, Procurado> procurados = new HashMap<>();
	
	private String nome;
	private double valor;
	public Procurado(String nome, double valor) {
		this.nome = nome;
		this.valor = valor;
	}
	
	public String getNome() {
		return nome;
	}
	public double getValor() {
		return valor;
	}
	
	public void setValor(double valor) {
		this.valor = valor;
	}
	
	public void remove() {
		if (procurados.containsKey(nome.toLowerCase())) {
			procurados.remove(nome.toLowerCase());
		}
	}
	
}