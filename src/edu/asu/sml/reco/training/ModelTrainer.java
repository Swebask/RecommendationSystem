package edu.asu.sml.reco.training;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import edu.asu.sml.reco.core.ItemSet;
import edu.asu.sml.reco.core.TestSpectralClustering;
import edu.asu.sml.reco.core.UserProfileCreator;
import edu.asu.sml.reco.core.UserSet;
import edu.asu.sml.reco.core.UserSimilarityUtil;
import edu.asu.sml.reco.ds.ClusterMembership;
import edu.asu.sml.reco.ds.FeatureNameTable;
import edu.asu.sml.reco.ds.FeatureSet;
import edu.asu.sml.reco.ds.User;
import edu.asu.sml.reco.ds.UserIDLookupTable;
import edu.ucla.sspace.matrix.Matrix;
import edu.ucla.sspace.matrix.SparseOnDiskMatrix;
import edu.ucla.sspace.vector.DoubleVector;
import edu.ucla.sspace.vector.SparseHashDoubleVector;

/**
 * This class handles parsing the training data, storing UsersXFeatures matrix and
 * user-cluster members matrix
 * @author somak
 *
 */
public class ModelTrainer {

	/**
	 * The trainingInputFile is parsed to create individual user profiles. The user
	 * profiles are saved in the userOutputFile. Then clustering is performed in the
	 * user data and cluster membership is stored in the clustersOutputFile
	 * 
	 * @param trainingInputFileName
	 * @param userOutputFile
	 * @param clustersOutputFileName
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void trainModel(String trainingInputFileName, String userOutputFileName, 
			String clustersOutputFileName) throws FileNotFoundException, IOException {
		UserSet newUserSet = new UserSet();
		ItemSet itemSet = new ItemSet();
		
		UserProfileCreator userProfiles = new UserProfileCreator(newUserSet, itemSet);
		
		userProfiles.parseFileAndCreateUserProfiles(trainingInputFileName);
		
		Matrix matrix = createUserUserMatrix(newUserSet);
		
		ClusterMembership clusterMembers =  TestSpectralClustering.
				returnAssignmentsAfterSpectralClustering(matrix);
		
		clusterMembers.serializeToFile(clustersOutputFileName);
		
		saveUserProfilesToFile(newUserSet, userOutputFileName);
		
	}

	private static void saveUserProfilesToFile(UserSet newUserSet,
			String userOutputFileName) {
		newUserSet.serializeToFile(userOutputFileName);
	}

	private static Matrix createUserUserMatrix(UserSet newUserSet) {
		Matrix matrix = new SparseOnDiskMatrix(UserIDLookupTable.getSize(), UserIDLookupTable.getSize());
		
		Set<String> userIDs = newUserSet.getUserIDIterator();
		
		for(String userID: userIDs) {
			int row = UserIDLookupTable.lookUp(userID);
			User user_i = newUserSet.getLinkedUserProfile(userID);
			for(String userID2:userIDs) {
				int col = UserIDLookupTable.lookUp(userID2);
				User user_j = newUserSet.getLinkedUserProfile(userID2);
				if(col <= row)
					continue;
				double similarity = UserSimilarityUtil.getUserUserSimilarity(user_i, user_j);
				matrix.set(row, col, similarity);
			}
			
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
