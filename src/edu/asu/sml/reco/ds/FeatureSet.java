package edu.asu.sml.reco.ds;

import java.util.Map.Entry;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import net.sf.javaml.clustering.mcl.SparseVector;

/**
 * Defines a set of feature values corresponding to the features in {@link sml.ds.FeatureNameTable}.
 * As discussed earlier, we could divide the features again in general set and specific set
 * of features.
 *   User similarity calculation might gain from differential treatment of such features
 * @author somak
 *
 */
public class FeatureSet {

	@Getter (AccessLevel.PUBLIC) private SparseVector featureValues;
	
	public FeatureSet(){
		featureValues = new SparseVector();
	}
	
	public void setFeature(String featureName, double value) {
		int index = FeatureNameTable.lookUp(featureName);
		featureValues.add(index, value);
	}
	
	public double getFeatureValue(int index) {
		return featureValues.get(index);
	}
	
	public double getFeatureValue(String featureName) {
		int index = FeatureNameTable.lookUp(featureName);
		return featureValues.get(index);
	}
	
	public Set<Entry<Integer, Double>> getFeatureValueSet() {
		return featureValues.entrySet();
	}
}
