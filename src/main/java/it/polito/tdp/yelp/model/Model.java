package it.polito.tdp.yelp.model;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	private SimpleDirectedWeightedGraph<Business, DefaultWeightedEdge> grafo;
	private YelpDao dao;
	private Map<String, Business> idMap;
	private List<Business> vertici;
	
	//variabili per la ricorsione
	private List<Business>  percorsoBest;
	
	public Model() {
		dao = new YelpDao();
		//idMap = new HashMap<String, Business>();
		
	}
	
	
	public void creaGrafo(String city, Year year) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		//dao.getBusinessByCityAndYear(city, year, idMap);
		vertici = dao.getBusinessByCityAndYear(city, year);
		idMap = new HashMap<String, Business>();
		for(Business b: vertici) {
			this.idMap.put(b.getBusinessId(), b);
		}
		
		//Graphs.addAllVertices(grafo, idMap.values());
		Graphs.addAllVertices(grafo, vertici);
		
/////////IPOTESI 1: calcolare la media recensioni mentre leggo i business (non ho carico sul dao)
		/*for(Business b1: this.vertici) {
			for(Business b2: this.vertici) {
				if(b1.getMediaRecensioni < b2.getMediaRecensioni) {
					Graphs.addEdge(this.grafo, b1, b2, b2.getMediaRecensioni-b1.getMediaRecensioni);
				}else if(b1.getMediaRecensioni > b2.getMediaRecensioni) {
					
				}else if(b1.getMediaRecensioni == b2.getMediaRecensioni) {
					
				}
			}
		}*/
		
////////IPOTESI 2: non modifico l'oggetto businnes ma creo una mappa una mappa per ricordarmi le medie delle recensioni (ho carico sul dao)
		/*Map<Business, Double> mediaRecensioni = new HashMap<Business, Double>();
		//carica la mappa con il DAO
		for(Business b1: this.vertici) {
			for(Business b2: this.vertici) {
				if(mediaRecensioni.get(b1) < mediaRecensioni.get(b2)) {
					Graphs.addEdge(this.grafo, b1, b2, mediaRecensioni.get(b2)-mediaRecensioni.get(b1));
				}else if(mediaRecensioni.get(b1) > mediaRecensioni.get(b2)) {
					
				}else if(mediaRecensioni.get(b1) == mediaRecensioni.get(b2)) {
					
				}
			}
		}*/
////////IPOTESI 3: faccio calcolare gli archi al DB
		List<ArcoGrafo> archi = dao.calcolaArchi(city, year);
		
		for(ArcoGrafo a : archi) {
			Graphs.addEdge(grafo, 
					this.idMap.get(a.getBusiness_Id1()), 
					this.idMap.get(a.getBusiness_Id2()), 
					a.getPeso());
		}
		String.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(),
				this.grafo.edgeSet().size()) ;
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
		/*if(this.grafo==null) {
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
		}*/
		double max =0.0;
		Business result = null;
		for(Business b : this.grafo.vertexSet()) {
			double val =0.0;
			for(DefaultWeightedEdge e: grafo.incomingEdgesOf(b)) {
				val+=grafo.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge e: grafo.outgoingEdgesOf(b)) {
				val-=grafo.getEdgeWeight(e);
			}
			if(val>max) {
				max=val;
				result=b;
			}
		}
		return result;
		
		
	}
	
	public List<Business> percorsoMigliore(Business partenza, Business arrivo, double soglia){
		this.percorsoBest=null;
		List<Business> parziale = new ArrayList<Business>();
		parziale.add(partenza);
		cerca(parziale , 1, arrivo, soglia);
		//cercaLungo(parziale , 1, arrivo, soglia);
		return percorsoBest;
		
	}
	
	private void cerca(List<Business> parziale, int livello, Business arrivo, double soglia) {
		Business ultimo = parziale.get(parziale.size()-1);

		//caso terminale: ho trovato l'arrivo
		if(ultimo.equals(arrivo)) {
			if(this.percorsoBest==null) {
				this.percorsoBest = new ArrayList<Business>(parziale);
				return;
			}else if(parziale.size()<percorsoBest.size()) {
				this.percorsoBest= new ArrayList<Business>(parziale);
				return;
			}else {
				return;
			}
		}
		
		//generazione dei percorsi
		//cerca i successori di 'ultimo'
		for(DefaultWeightedEdge e: grafo.outgoingEdgesOf(ultimo)) {
			if(this.grafo.getEdgeWeight(e)>soglia) {
				//fai ricorsione
				//controlla anche che la destinazione non sia già in parziale
				Business prossimo = Graphs.getOppositeVertex(grafo, e, ultimo);
				
				if(!parziale.contains(prossimo)) {
					parziale.add(prossimo);
					cerca(parziale, livello+1, arrivo, soglia);
					parziale.remove(parziale.size()-1);
				}
				
				
			}
		}
	}
	
	private void cercaLungo(List<Business> parziale, int livello, Business arrivo, double soglia) {
		Business ultimo = parziale.get(parziale.size()-1);

		//caso terminale: ho trovato l'arrivo
		if(ultimo.equals(arrivo)) {
			if(this.percorsoBest==null) {
				this.percorsoBest = new ArrayList<Business>(parziale);
				return;
			}else if(parziale.size()>percorsoBest.size()) {
				this.percorsoBest= new ArrayList<Business>(parziale);
				return;
			}else {
				return;
			}
		}
		
		//generazione dei percorsi
		//cerca i successori di 'ultimo'
		for(DefaultWeightedEdge e: grafo.outgoingEdgesOf(ultimo)) {
			if(this.grafo.getEdgeWeight(e)>soglia) {
				//fai ricorsione
				//controlla anche che la destinazione non sia già in parziale
				Business prossimo = Graphs.getOppositeVertex(grafo, e, ultimo);
				
				if(!parziale.contains(prossimo)) {
					parziale.add(prossimo);
					cerca(parziale, livello+1, arrivo, soglia);
					parziale.remove(parziale.size()-1);
				}
				
				
			}
		}
	}
	
	public Set<Business> getVerticiB() {
		return this.grafo.vertexSet();
	}
	
}
