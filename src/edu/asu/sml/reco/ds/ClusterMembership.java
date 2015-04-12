package edu.asu.sml.reco.ds;

import java.io.*;
import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;

public class ClusterMembership implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@Getter (AccessLevel.PUBLIC) private List<Set<Integer>> clusterMembers;

	public ClusterMembership(List<Set<Integer>> clusterMembers) {
		super();
		this.clusterMembers = clusterMembers;
	}
	
	public void serializeToFile(String outputFileName) {
		try {
			OutputStream file = new FileOutputStream(outputFileName);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(clusterMembers);
			output.close();
			System.out.println("Cluster members exported to:" + outputFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static ClusterMembership deserializeFile(String inputFileName) {
		try {
			InputStream file = new FileInputStream(inputFileName);
		    InputStream buffer = new BufferedInputStream(file);
		    ObjectInput input = new ObjectInputStream (buffer);
		    ClusterMembership clusterMembers = (ClusterMembership) input.readObject();
		    
		    input.close();
		    return clusterMembers;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		return null;
	}
}
