package it.polito.tdp.ufo.model;

import java.time.Year;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.ufo.db.SightingsDAO;

public class Model {

	private SightingsDAO dao;
	private List<String> stati;
	private Graph<String, DefaultEdge> grafo;
	
	public Model() {
		dao = new SightingsDAO();
		
	}
	public List<AnnoCount> getAnni() {
		List<AnnoCount> anni = dao.getAnni();
		return anni;
	}
	
	public void creaGrafo(Year anno) {
		
		this.stati = this.dao.getStati(anno);
		this.grafo = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		
		//Aggiungo tutti i vertici
		Graphs.addAllVertices(grafo, stati);
		
		//Soluzione "semplice" --> doppio ciclo per l'arco
		for(String s1: this.grafo.vertexSet()) {
			for(String s2: this.grafo.vertexSet()) {
				if(!s1.equals(s2)) {
					if(this.dao.esisteArco(s1,s2, anno))
						grafo.addEdge(s1, s2);
				}
			}
		}
		
		System.out.println("Grafo creato! ");
		System.out.println("# Vertici: "+grafo.vertexSet().size());
		System.out.println("# Archi: "+grafo.edgeSet().size());

		
		
	}
	public int getNVertici() {
		
		return grafo.vertexSet().size();
	}
	public int getNArchi() {
		
		return grafo.edgeSet().size();
	}
	public List<String> getStati() {
		return stati;
	}
	
	public List<String> getSuccessori(String stato){
		return Graphs.successorListOf(this.grafo, stato);
	}
	
	public List<String> getPredecessori(String stato) {
		return Graphs.predecessorListOf(this.grafo, stato);
	}
	
	public List<String> getRaggiungibili() {
		List<String> raggiungibili = new LinkedList<String>();
		DepthFirstIterator<String, DefaultEdge> it = new DepthFirstIterator<String, DefaultEdge>(grafo);
		it.next();
		
		while(it.hasNext()) {
			raggiungibili.add(it.next());
		}
		
		return raggiungibili;
	}
}
