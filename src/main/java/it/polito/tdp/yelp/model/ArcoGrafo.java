package it.polito.tdp.yelp.model;

public class ArcoGrafo {
	//private Business b1;
	//private Business b2;
	//private int peso;
	private String business_Id1;
	private String business_Id2;
	private double peso;
	
	public ArcoGrafo(String business_Id1, String business_Id2, double peso) {
		super();
		this.business_Id1 = business_Id1;
		this.business_Id2 = business_Id2;
		this.peso = peso;
	}

	public String getBusiness_Id1() {
		return business_Id1;
	}

	public void setBusiness_Id1(String business_Id1) {
		this.business_Id1 = business_Id1;
	}

	public String getBusiness_Id2() {
		return business_Id2;
	}

	public void setBusiness_Id2(String business_Id2) {
		this.business_Id2 = business_Id2;
	}

	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}
	
	
	
	
	

}
