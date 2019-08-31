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
		this.dao = new SightingsDAO();
		
	}

	public List<AnnoCount> getAnni() {
		return this.dao.getAnni();
	}
	
	public void creaGrafo(Year anno) {
		this.grafo = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		stati = this.dao.getStati(anno);
		Graphs.addAllVertices(this.grafo, this.stati);
		
		for(String s1: this.grafo.vertexSet()) {
			for(String s2: this.grafo.vertexSet()) {
				if(!s1.equals(s2)) {
					if(this.dao.esisteArco(s1,s2, anno)) {
						this.grafo.addEdge(s1, s2);
					}
				}
			}
		}
		
		System.out.println("Grafo creato!");
		System.out.println("Numero vertici: "+this.grafo.vertexSet().size());
		System.out.println("Numero archi: "+this.grafo.edgeSet().size());
		
		

	}

	public int getNVertici() {
		return this.grafo.vertexSet().size();
	}

	public int getNArchi() {
		return this.grafo.edgeSet().size();
	}

	public List<String> getStati() {
		return this.stati;
	}
	
	public List<String> getSuccessori(String stato){
		return Graphs.successorListOf(this.grafo, stato);
	}
	
	public List<String> getPredecessori(String stato){
		return Graphs.predecessorListOf(this.grafo, stato);
	}
	
	public List<String> getRaggiungibili(String stato){
		List<String> raggiungibili = new LinkedList<>();
		DepthFirstIterator<String, DefaultEdge> dp = new DepthFirstIterator<String,DefaultEdge>(this.grafo, stato);
		
		dp.next();
		
		while (dp.hasNext()) {
			raggiungibili.add(dp.next());
		}
		
		return raggiungibili;
	}
}
