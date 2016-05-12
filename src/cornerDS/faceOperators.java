package cornerDS;

import java.util.Iterator;

import POV.BorderFaceException;
import POV.pt;

public interface faceOperators extends Iterable<Integer> {
	
	public int maxTetID();
	
	/**
	 * number of vertices
	 * @return
	 */
	int getnv();
	
	/**
	 * set number of vertices
	 * @param nv
	 */
	void setNv(int nv);

	/**
	 * get a geometric point 
	 * @param v : point id
	 * @return
	 */
	pt G(int v);
	
	/**
	 * get opposite face id
	 * @param f : face id
	 * @return face id
	 * @throws BorderFaceException
	 */
	int O(int f) throws BorderFaceException;
	
	/**
	 * get the vertex id of the vertex opposite of face f
	 * @param f : face id
	 * @return vertex id
	 */
	Integer V(int f);
	
	default int[] Vertices(int t){
		return new int[]{V(4*t),V(4*t+1),V(4*t+2),V(4*t+3)};
	}
	
	public void save(String fn);
	/**
	 * test if a face is on the border
	 * @param f : face id
	 * @return
	 */
	boolean borderFace(int f);
	
	/**
	 * test if a corner is on the border
	 * @param c : corner id
	 * @return
	 */
	boolean borderCorner(int c);
	
	/**
	 * iterate over all tetrahedrons
	 */
	Iterator<Integer> iterator();
	
}
