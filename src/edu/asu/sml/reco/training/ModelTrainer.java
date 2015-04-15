package edu.asu.sml.reco.training;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
			String itemSetOutputFileName, String clustersOutputFileName,
			String userSimilarityOutputFileName) throws FileNotFoundException, IOException {
		UserSet newUserSet = new UserSet();
		ItemSet itemSet = new ItemSet();
		
		UserProfileCreator userProfiles = new UserProfileCreator(newUserSet, itemSet);
		
		userProfiles.parseFileAndCreateUserProfiles(trainingInputFileName);
		
		saveUserProfilesToFile(newUserSet, userOutputFileName);
		
		saveItemProfilesToFile(itemSet, itemSetOutputFileName);
		
		Matrix matrix = createUserUserMatrix(newUserSet);
		
		saveUserSimilarityMatrix(matrix, userSimilarityOutputFileName);
		
		ClusterMembership clusterMembers =  TestSpectralClustering.
				returnAssignmentsAfterSpectralClustering(matrix);
		
		clusterMembers.serializeToFile(clustersOutputFileName);
		

	}

	private static void saveUserSimilarityMatrix(Matrix matrix,
			String userSimilarityOutputFileName) {
		try {
			OutputStream file = new FileOutputStream(userSimilarityOutputFileName);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			int rows = matrix.rows();
			output.writeInt(rows);
			for(int i=0; i < rows ; i++) {
				output.writeObject(matrix.getRowVector(i));
			}
			//output.writeObject(matrix);
			output.close();
			System.out.println("User set exported to:" + userSimilarityOutputFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private static void saveUserProfilesToFile(UserSet newUserSet,
			String userOutputFileName) {
		newUserSet.serializeToFile(userOutputFileName);
	}
	
	private static void saveItemProfilesToFile(ItemSet itemSet,
			String itemSetOutputFileName) {
		itemSet.serializeToFile(itemSetOutputFileName);
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
	
	public static void main(String[] args) {
		String trainingInputFileName = "./parsedReviewTraining.txt";
		String userOutputFileName = "userOutput.txt";
		String itemSetOutputFileName = "itemSetOutput.txt";
		String clustersOutputFileName = "clusterOutput.txt";
		String similarityMatrixOutputFileName = "similarityMatrix.matrix";
		
		try {
			FeatureNameTable.populateFeatureNames();
			UserIDLookupTable.populateFeatureNames();
			System.out.println("Intitalization done...");
			ModelTrainer.trainModel(trainingInputFileName, userOutputFileName, itemSetOutputFileName, 
					clustersOutputFileName, similarityMatrixOutputFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
