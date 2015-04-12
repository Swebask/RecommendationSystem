package edu.asu.sml.reco.core;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import net.sf.javaml.clustering.mcl.MarkovClustering;
import net.sf.javaml.clustering.mcl.SparseMatrix;
import net.sf.javaml.clustering.mcl.SparseVector;


public class TestMarkovClustering {

	private double pGamma  = 2.0; // inflation parameter 2-3
	private double loopGain = 2.0;
	private double maxResidual = 0.5;
	private double maxZero = 0.05; // below this similarity level everything is pruned
	
	/**
	 * Assumes very small weights are already assigned a zero value i.e. here
	 * the graph is not fully connected.
	 * @param graph
	 * @return
	 */
	public List<Set<Integer>> returnClusteredMatrix(double[][] graph) {
		MarkovClustering mcl = new MarkovClustering();
		SparseMatrix sparseGraph = new SparseMatrix(graph);
		
		SparseMatrix clusteredGraph =  mcl.run(sparseGraph, maxResidual, pGamma, loopGain, maxZero);
		
		int[] size = clusteredGraph.getSize();
		List<Set<Integer>> clusters = new ArrayList<Set<Integer>>(size[0]);
		
		for(int col=0; col < size[1]; col++) {
			SparseVector sparseVector = clusteredGraph.getColum(col);
			for(int row=0; row <size[0]; row++) {
				Double val = sparseVector.get(row);
				if(val != null) {
					Set<Integer> cluster = clusters.get(row);
					if(cluster == null) {
						cluster = new HashSet<Integer>();
						clusters.set(row, cluster);
					}
					cluster.add(row);
				}
			}
		}
		return clusters;
	}
}
