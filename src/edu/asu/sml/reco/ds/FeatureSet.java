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
public class FeatureSet implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Getter (AccessLevel.PUBLIC) private SparseVector featureValues;
	@Getter (AccessLevel.PUBLIC) private SparseVector countOfUsers;

	public FeatureSet(){
		featureValues = new SparseVector();
		countOfUsers = new SparseVector();
	}

	public void setFeature(String featureName, double value) {
		int index = FeatureNameTable.lookUp(featureName);
		if(index == -1)
			return;
		featureValues.add(index, value);
		Double count = countOfUsers.get(index);
		if(count == null)
			countOfUsers.add(index, 1);
		else
			countOfUsers.add(index, count+1);
	}

	public double getFeatureValue(int index) {
		if(featureValues.containsKey(index))
			return featureValues.get(index);
		return 0.0;
	}

	public double getFeatureValue(String featureName) {
		int index = FeatureNameTable.lookUp(featureName);
		if(index == -1)
			return Double.MIN_VALUE;
		return getFeatureValue(index);
	}

	public Set<Entry<Integer, Double>> getFeatureValueSet() {
		return featureValues.entrySet();
	}

	public void aggregateNewSetOfFeatures(FeatureSet featuresForThisReview) {
		for(Entry<Integer,Double> entry: featuresForThisReview.featureValues.entrySet()) {
			Double oldValue = this.featureValues.get(entry.getKey());
			if(oldValue == null)
				this.featureValues.add(entry.getKey(), entry.getValue());
			else {
				double oldCount = countOfUsers.get(entry.getKey());
				double newValue = (oldValue * oldCount + entry.getValue()) / (oldCount+1);
				this.featureValues.add(entry.getKey(),newValue);
			}

		}
	}

	public void merge(FeatureSet featuresForThisReview,Double scaling) {
		if(featuresForThisReview ==null)
			return;
		for(Entry<Integer,Double> entry: featuresForThisReview.featureValues.entrySet()) {
			Double oldValue = this.featureValues.get(entry.getKey());
			if(oldValue==null){
				this.featureValues.add(entry.getKey(), scaling*entry.getValue());
			}else{
				this.featureValues.add(entry.getKey(), scaling*entry.getValue()+oldValue);
			}

		}
	}

	public void scale(Double scaling) {
		for(Entry<Integer,Double> entry: this.featureValues.entrySet()) {
			this.featureValues.add(entry.getKey(), scaling*entry.getValue());	
		}
	}
	public Set<Integer> getFeatureIndices() {
		return featureValues.keySet();
	}
}
