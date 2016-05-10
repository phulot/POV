package cornerDS;

import java.util.Collection;

import POV.BorderFaceException;
import POV.pt;

public interface faceOperators {
	
	public int maxTetID();
	
	int getnv();
	
	void setNv(int nv);

	pt G(int f);
	
	int O(int f) throws BorderFaceException;
	
	Integer V(int f);
	
	public void save(String fn);
	
	boolean borderFace(int f);
	
	boolean borderCorner(int c);
	
	Collection<Integer> allTetIDS();
}
