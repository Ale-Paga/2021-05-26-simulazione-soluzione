package it.polito.tdp.yelp.model;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	private SimpleDirectedWeightedGraph<Business, DefaultWeightedEdge> grafo;
	private YelpDao dao;
	//private Map<String, Business> idMap;
	private List<Business> vertici;
	
	public Model() {
		dao = new YelpDao();
		//idMap = new HashMap<String, Business>();
		
	}
	
	
	public void creaGrafo(String city, Year year) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		//dao.getBusinessByCityAndYear(city, year, idMap);
		vertici = dao.getBusinessByCityAndYear(city, year);
		
		
		//Graphs.addAllVertices(grafo, idMap.values());
		Graphs.addAllVertices(grafo, vertici);
		System.out.println("Num of vertex "+ grafo.vertexSet().size());
		
		for(Adiacenza a : dao.getAdiacenze(city, year, idMap)) {
			Graphs.addEdge(grafo, a.getB1(), a.getB2(), a.getPeso());
		}
		
		System.out.println("Num of edge "+ grafo.edgeSet().size());
	}
	
	public int getVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<String> getAllCity(){
		return dao.getAllCity();
	}
	
	public Business getMigliore() {
		if(this.grafo==null) {
			throw new RuntimeException("Grafo inesistente");
		}
		Business migliore = null;
		
		for(Business b: grafo.vertexSet()) {
			if( migliore ==null) {
				migliore = b;
			}
			if(grafo.degreeOf(migliore)<grafo.degreeOf(b)) {
				migliore =b;
			}
		}
		return migliore;
		
	}
	
}
