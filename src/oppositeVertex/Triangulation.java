package oppositeVertex;

import java.util.Set;

import POV.pt;

interface Triangulation {
	/**
	 * return all incident faces of a vertex
	 * @param vertexid : vertex id
	 * @return collection of face ids
	 */
	public Iterable<Integer> incidentFaces(int vertexid);
	
	/**
	 * return the number of vertices
	 * @return
	 */
	public int sizeOfVertices();
	
	/**
	 * return the number of vertices
	 * @return
	 */
	public int sizeOfFaces();
	
	/**
	 * remove the vertices on one side of the plane (AiBiCi)
	 * the side is defined by op and vi : if op ==true removes the vertices that are on the opposite side of the plane relatively to vi
	 * create a copy
	 * @param s : set 
	 * @param vi : vertex id
	 * @param Ai : vertex id
	 * @param Bi : vertex id
	 * @param Ci : vertex id
	 * @param op : which side to remove
	 * @return a new set with only the vertices on the right side
	 */
	public Set<Integer> removeSide(int sideReference,Set<Integer> s,int Ai,int Bi,int Ci, boolean removeOppositeSide);
	
	/**
	 * gives a vertex ID form one of his neighboring faces and the label of the vertex in this face 
	 * @param faceID
	 * @param relativeVertexID
	 * @return vertex ID
	 */
	public int getVertexID(int faceID,int relativeVertexID);
	
	/**
	 * return the i-th neighbor of f 
	 * @param i : 0, 1 or 2
	 * @param faceID : face id
	 * @return face id
	 */
	public int neighbor(int i, int faceID);
	
	public pt G(int i);
	
	public int storageCost();
	
}
