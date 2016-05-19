package Triangulations;

import POV.pt;

public interface Triangulation {
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
	
	/**
	 * return the geometry point of vertex v
	 * @param v : vertex id
	 * @return geometric point
	 */
	public pt G(int v);
	
	public int storageCost();
	
}
