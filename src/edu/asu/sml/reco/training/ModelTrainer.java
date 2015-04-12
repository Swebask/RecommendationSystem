package edu.asu.sml.reco.training;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import edu.asu.sml.reco.core.ItemSet;
import edu.asu.sml.reco.core.TestSpectralClustering;
import edu.asu.sml.reco.core.UserProfileCreator;
import edu.asu.sml.reco.core.UserSet;
import edu.asu.sml.reco.ds.ClusterMembership;
import edu.asu.sml.reco.ds.FeatureNameTable;
import edu.asu.sml.reco.ds.FeatureSet;
import edu.asu.sml.reco.ds.User;
import edu.asu.sml.reco.ds.UserIDLookupTable;
import edu.ucla.sspace.matrix.Matrix;
import edu.ucla.sspace.matrix.SparseOnDiskMatrix;
import edu.ucla.sspace.vector.DoubleVector;
import edu.ucla.sspace.vector.SparseHashDoubleVector;

public class ModelTrainer {

	public static void trainModel(String trainingInputFileName, File userOutputFile, 
			String clustersOutputFileName) throws FileNotFoundException, IOException {
		UserSet newUserSet = new UserSet();
		ItemSet itemSet = new ItemSet();
		
		UserProfileCreator userProfiles = new UserProfileCreator(newUserSet, itemSet);
		
		userProfiles.parseFileAndCreateUserProfiles(trainingInputFileName);
		
		Matrix matrix = createUserMatrix(newUserSet);
		
		ClusterMembership clusterMembers =  TestSpectralClustering.
				returnAssignmentsAfterSpectralClustering(matrix);
		
		clusterMembers.serializeToFile(clustersOutputFileName);
		
		
	}

	private static Matrix createUserMatrix(UserSet newUserSet) {
		Matrix matrix = new SparseOnDiskMatrix(UserIDLookupTable.getSize(), FeatureNameTable.getSize());
		
		Set<String> userIDs = newUserSet.getUserIDIterator();
		for(String userID: userIDs) {
			int row = UserIDLookupTable.lookUp(userID);
			User user = newUserSet.getLinkedUserProfile(userID);
			
			DoubleVector featureValues = getDoubleVector(user.getSetOfFeatures());
			
			matrix.setRow(row, featureValues);
		}
		return matrix;
	}

	private static DoubleVector getDoubleVector(FeatureSet setOfFeatures) {
		Set<Entry<Integer, Double>> featureValues = setOfFeatures.getFeatureValueSet();
		SparseHashDoubleVector doubleVector = new SparseHashDoubleVector(FeatureNameTable.getSize());
		for(Entry<Integer, Double> entry: featureValues) {
			doubleVector.add(entry.getKey(), entry.getValue());
		}
		return doubleVector;
	}
}
