/**Review.java
 * 7:09:35 PM @author Arindam
 */
package edu.asu.sml.reco.reader;

import java.util.Map;

import edu.asu.sml.reco.ds.FeatureSet;

/**
 * @author Arindam
 *
 */
public class Review {
	private String 
		productId,
		title,
		price,
		userId,
		helpfulness,
		score,
		time,
		summary,
		text;
	private Map<String,String> featureValues;
	private FeatureSet featureVector = null;
	

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHelpfulness() {
		return helpfulness;
	}

	public void setHelpfulness(String helpfulness) {
		this.helpfulness = helpfulness;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Review() {
	}

	public Map<String, String> getFeatureValues() {
		return featureValues;
	}

	public void setFeatureValues(Map<String, String> featureValues) {
		this.featureValues = featureValues;
	}

	@Override
	public String toString() {
		return "Review [productId=" + productId + ", title=" + title
				+ ", price=" + price + ", userId=" + userId + ", helpfulness="
				+ helpfulness + ", score=" + score + ", time=" + time
				+ ", summary=" + summary + ", text=" + text
				+ ", featureValues=" + featureValues + "]";
	}

	public FeatureSet getFeatureVector() {
		return featureVector;
	}

	public void setFeatureVector(FeatureSet featureVector) {
		this.featureVector = featureVector;
	}
	
	
}
