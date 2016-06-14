package NonManifoldTriangulation;

import java.util.HashMap;
import java.util.Iterator;

import Jcg.geometry.Pair;
import Triangulations.pt;
import Triangulations.vec;

public class Vlist{

	public pt[] G;
	public int[] H;
	public int[] L;
//	public static pt E=new pt(1000,0,0);
	
	public Vlist(pt[] g, Integer[] C) {
		G = g;
		H = new int[g.length];
		L = new int[C.length];
		for (int i = 0; i < g.length; i++) {
			H[i] = -1 * i - 1;
		}
		for (int i = 0; i < C.length; i++) {
			L[i] = H[C[i]];
			H[C[i]] = i;
		}
		for (int i = 0; i < C.length; i++){
			assert(vw(i)==C[i]);
		}
		// reorderCycles();
	}

	//TODO to return only one side of incident faces
	//TODO function to check if a vertex is on a junction edge. 
	
	/**
	 * returns an iterable containing all the wedge incident to the vertex vertexid
	 * @param vertexid : vertex id
	 * @return wedge ids
	 */
	public Iterable<Integer> incidentCorner(int vertexid) {
		return new Iterable<Integer>() {

			@Override
			public Iterator<Integer> iterator() {

				return new Iterator<Integer>() {
					int t = H[vertexid];

					@Override
					public boolean hasNext() {
						return t >= 0;
					}

					@Override
					public Integer next() {
						int temp = t;
						t = L[t];
						return temp;
					}

				};
			}
		};
	}

	
	public int sizeOfVertices() {
		return G.length;
	}

	
	public int sizeOfFaces() {
		return L.length / 3;
	}

	/**
	 * return the vertex associted with a wedge
	 * @param wedge
	 * @return vertex id
	 */
	
	/**
	 * return the vertex associted with the wedge corresponding to the face/rel association
	 * @param faceID: face id
	 * @param relativeVertexID: 0, 1 or 2, the relative vertex of the face, numbered as wedges
	 * @return vertex id
	 */
	public int vface(int faceID,int relativeVertexID) {
		return vw(3*faceID+relativeVertexID);
	}


	private double angle(pt A, pt B, pt C, pt D) {
		vec U = vec.N(vec.V(B, A), vec.V(B, C)).normalize();
		vec V = vec.N(vec.V(B, D), vec.V(B, C)).normalize();
		if (vec.dot(vec.V(B, C),vec.N(U, V))>0)
			return 1-vec.d(U, V);
		else return 3+vec.d(U, V);
	}

	
	public pt G(int i) {
		return G[i];
	}

	
	public int storageCost() {
		return H.length + L.length;
	}

	public void checkTriangulation() {
		int err = 0;
		HashMap<Pair<Integer>, Integer> map = new HashMap<>();
		for (int i = 0; i < L.length / 3; i++) {
			if (vface(i, 0) == vface(i, 1) || vface(i, 0) == vface(i, 2)
					|| vface(i, 1) == vface(i, 2))
				err++;
			int u = vface(i, 0), v = vface(i, 1);
			Pair<Integer> p = new Pair<>(Math.min(u, v), Math.max(u, v));
			if (map.get(p) != null)
				map.remove(p);
			else
				map.put(p, i);
			v = vface(i, 2);
			p = new Pair<>(Math.min(u, v), Math.max(u, v));
			if (map.get(p) != null)
				map.remove(p);
			else
				map.put(p, i);
			u = vface(i, 1);
			p = new Pair<>(Math.min(u, v), Math.max(u, v));
			if (map.get(p) != null)
				map.remove(p);
			else
				map.put(p, i);
		}
		if (err != 0)
			System.out.println("degenerated triangles " + err);
		System.out.println(map.size());
	}
/**
 * corner -> side (0 or 1)
 * @param c
 * @return
 */
	public static boolean isfVl(int c){return (c/3) % 2==0;}
	/**
	 * corner -> wedge
	 * @param c
	 * @return
	 */
	public static int w(int c){if (isfVl(c)) return 3*(c/6)+c%3; else return 3*(c/6)+2-c%3;}
	/**
	 * corner -> triangle
	 * @param c
	 * @return
	 */
	public static int tc(int c){return (c/6);}
	/**
	 * wedge -> triangle
	 * @param w
	 * @return
	 */
	public static int tw(int w){return (w/3);}
	/**
	 * corner -> corner , next operation
	 * @param c
	 * @return
	 */
	public static int nc(int c){return 3*(c/3)+(c+1)%3;}
	/**
	 * wedge->wedge, next operation
	 * @param w
	 * @return
	 */
	public static int nw(int w){return 3*(w/3)+(w+1) % 3;}
	/**
	 * corner -> corner , prev operation
	 * @param c
	 * @return
	 */
	public static int pc(int c){return 3*(c/3)+(c+2) % 3;}
	/**
	 * wedge->wedge, prev operation
	 * @param w
	 * @return
	 */
	public static int pw(int w){return 3*(w/3)+(w+2) % 3;}
	/**
	 * corner -> corner, corner of the same triangle, same vertex, other side
	 * @param c
	 * @return
	 */
	public static int x(int c){if(isfVl(c)) return c + 5- 2*(c % 3); else return c -2*(c % 3)-1; }
	/**
	 * wedge->vertex id, vertex operation
	 * @param w
	 * @return
	 */
	public int vw(int w){
		int v=L[w];
		while(v>=0) v=L[v];
		return v*-1-1;
	}
	
	/**
	 * swing operation for triangle mesh
	 * @param rel: 0,1 or 2 
	 * @param faceID
	 * @param isFront: side of the face 
	 * @return return a corner id
	 */
	public int neighbor(int rel, int faceID, boolean isFront) {
		assert rel<3;
		int corner = getCorner(3*faceID+rel,isFront);
		return neighbor(corner);
	}
	/**
	 * 
	 * need to check this function !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * corner -> corner, neighbor operation
	 * @param c
	 * @return
	 */
	int neighbor(int c) {
		int ww=w(c), u=vw(ww), v=vw(w(pc(c))), z=vw(w(nc(c)));
		double maxAngle = 2*Math.PI,planeAngle; int x=-1;
		int w=H[u];
		while(w>=0){
			if(w!=ww){
				int p;
				//TODO test if nw if enough
				if(vw(nw(w))==v || vw(pw(w))==v ){
					if(vw(nw(w))==v) p=vw(pw(w)); else p=vw(nw(w));
					planeAngle= angle(G[z], G[u], G[v], G[p]);
//					if (w==60)
//					System.out.println("planeAngle "+planeAngle);
					if(planeAngle <maxAngle) {maxAngle = planeAngle; x=w;}
				}
			}
			w=L[w];
		}
		if(x==-1) throw new Error(c+"   "+vw(w(c))+"  "+vw(w(nc(c)))+"  "+vw(w(pc(c)))); // there is no other triangle sharing edge (u,v) with c.t, return the corner on the other side of c.w
//		System.out.println(clockWise(tc(c), E)+"  "+clockWise(tw(x), E));
		
		return getCorner(x,isfVl(c));
//		if (maxAngle>=Math.PI){
//			return getCorner(x, !clockWise(tc(c), G[z]));
//		}
//		return getCorner(x,clockWise(tc(x), G[z]));
		
//		if (clockWise(tc(c), E)==clockWise(tw(x), E))
//			return getCorner(x, isfVl(c));
//		return getCorner(x, !isfVl(c));
//		if(clockWise(G[v(x)], G[v(nw(x))], G[v(pw(x))],E)) // function cw(E, A, B, C) check if vertex A, B, C appear clockwise from viewpoint E
//		double alpha= angle(G[z], G[u], G[v], G[pp]);
//		double beta = angle(G[z], G[u], G[v], E);
//		if (isfVl(c))
//			if (alpha>beta&&alpha<beta+Math.PI)
//				return getCorner(x,true);
//			else return getCorner(x, false);
//		else if (alpha+2*Math.PI-beta<Math.PI&&alpha+2*Math.PI-beta>0)
//			return getCorner(x, true);
//		return getCorner(x, false);
//		if (angle(G[z], G[u], G[v], G[pp])<angle(G[z], G[u], G[v], E))
//			return getCorner(x,!isfVl(c));
//		else return getCorner(x,isfVl(c));
	}
	
	int getCorner(int w, boolean isFrontface) {
		if (isFrontface) return (w / 3 * 6 + w % 3);
		else return (w / 3 * 6 + 5 - (w % 3));
	}

	public static boolean clockWise(pt A, pt B, pt C,pt E) {
		vec U = vec.V(E, A), V = vec.V(E, B), W = vec.V(E, C);
		return vec.dot(U, vec.N(V, W)) >= 0;
	}
	public boolean clockWise(int face,pt E) {
		return clockWise(G[vw(3*face)], G[vw(3*face+1)], G[vw(3*face+2)], E);
	}

	public String FaceToSting(int f){
		return "("+vw(3*f)+", "+vw(3*f+1)+", "+vw(3*f+2)+")";
	}
}
