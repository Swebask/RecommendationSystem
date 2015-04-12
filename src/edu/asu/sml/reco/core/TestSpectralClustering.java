package edu.asu.sml.reco.core;

import java.util.Properties;

import edu.asu.sml.reco.ds.ClusterMembership;
import edu.ucla.sspace.clustering.Assignments;
import edu.ucla.sspace.clustering.CKVWSpectralClustering06;
import edu.ucla.sspace.matrix.Matrix;


public class TestSpectralClustering {

	public static ClusterMembership returnAssignmentsAfterSpectralClustering(Matrix matrix) {
		CKVWSpectralClustering06 spectralClusterer = new CKVWSpectralClustering06();
		
		Assignments assignments =spectralClusterer.cluster(matrix, new Properties());
		return new ClusterMembership(assignments.clusters());
	}
}
