import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;

import Jcg.geometry.Point_3;
import Jcg.triangulations3D.Delaunay_3;
import Jcg.triangulations3D.TriangulationDSCell_3;
import Jcg.triangulations3D.TriangulationDSVertex_3;
/**
 * 
 * @author Pierre Hulot
 * @author jarek Rossignac
 * representation of a tetrahedral mesh by the array of his vertices, 
 * the array of the opposite vertex of each face (same tetrahedron)
 * and the Array of the opposite face of each face(other tetrahedron) 
 */
public class pov {
	/**
	 * display interface
	 */
	POVjava display;
	private final int[] swing = new int[] { 8, 5, 9, 0, 7, 10, 2, 11, 3, 6, 1, 4 };
	private final int[] ver = new int[] { 3, 2, 1, 3, 0, 2, 1, 0, 3, 1, 2, 0 };
	/**
	 *  max number of vertices
	 */
	int maxnv = 10000; 
	/**
	 *  number of vertices currently used in P
	 */
	int nv = 0; 
	/**
	 *  picked vertex index,
	 */
	int pv = 0; 
	/**
	 *  insertion vertex index
	 */
	int iv = 0; 
	/**
	 *  current corner index
	 */
	int currentCorner = 0; 
	/**
	 *  max number of tets
	 */
	int maxnt = 30000; 
	/**
	 *  max number of faces (including external)
	 */
	int maxnf = maxnt * 5;
	/**
	 * tetrahedron number
	 */
	int nt = 0;
	/**
	 * face number
	 */
	int nf = 0;
	/**
	 *  geometry table (vertices)
	 */
	pt[] G = new pt[maxnv]; 
	/**
	 *  ID of visible vertex of face
	 */
	int[] V = new int[maxnf]; 
	/**
	 * ID of opposite face
	 */
	int[] O = new int[maxnf];
	/**
	create an instance of POV representation
	@param display interface that extends PApplet 
	*/
	pov(POVjava p) {
		display = p;
	}

	/**
		draw a corner in blue
		@param corner id
	 */
	void drawCorner(int c) {
		if (borderCorner(c)) {
			try {
				drawCorner(o(c));
			} catch (BorderCornerException e) {
				e.printStackTrace();
			}
		} else {
			pt id = G[v(c)];
			pt cor = new pt(id);
			pt ver = new pt(id);
			vec v1 = vec.V(id, G[v(n(c))]);
			ver.add(1.f / 3f, v1);
			vec v2 = vec.V(id, G[v(n(n(c)))]);
			cor.add(vec.V(1.f / 3f, vec.V(v1, v2)));

			display.fill(display.blue, 300);
			display.noStroke();
			display.show(G[v(c)], ver, cor);
			ver = new pt(id);
			ver.add(1f / 3, v2);
			display.show(G[v(c)], ver, cor);
		}
	}

	/**
		draw the selected corner in blue
		used to navigate into the surface
	 */
	void drawSelectedCorner() {
		drawCorner(currentCorner);
	}

	/**
	 	next operation on corner
	 	@param corner id
	 	@return next corner
	 */
	int n(int c) {
		int r = c + 1;
		if (r % 3 == 0)
			r = r - 3;
		return r;
	}

	/**
	 	next operation on curent vertex
	 */
	void n() {
		currentCorner = n(currentCorner);
	}
	/*
	/**
	 * face which opposite vertex is v
	 * V[opppositeFaceFromVertex(v)]=v
	 * @param v : vertex id
	 * @return face id
	 */
	/*
	int oppositeFaceFromVertex(int v){
		if (v>3)
			return (v-3)*4;
		else return v;
	}*/
	/*
	/**
	 * opposite operation of v
	 * v(cornerFromVertex(v))=v
	 * @param v : vertex id
	 * @return corner id
	 */
	/*
	int cornerFromVertex(int v){
		if (v>3)return (v-3)*12+4;
		if (v==0)return 4;
		if (v==1)return 2;
		if (v==2)return 1;
		return 0;
	}*/
	
	/**
	 	face index of the corner
	 	@param corner id
	 	@return face id
	 */
	int faceFromCorner(int c) {
		return c / 3;
	}

	/**
	 * relative corner in Face
	 * @param c : corner id
	 * @return relative corner id in the face (between 0 and 2)
	 */
	int relativeCornerInFace(int c) {
		return c % 3;
	}

	/**
	 * tetrahedron index of the face
	 * @param f : face id
	 * @return tetrahedron id
	 */
	int tetraFromFace(int f) {
		return f / 4;
	}

	/**
	 * retlative face in tetrahadron
	 * @param f : face id
	 * @return tetrahedron id
	 */
	int relativeFaceInTetra(int f) {
		return f % 4;
	}

	/**
	 * tetrahedron index of the corner
	 * @param c : corner id
	 * @return tetrahedron id
	 */
	int tetraFromCorner(int c) {
		return c / 12;
	}

	/**
	 * relative corner in tetrahedron
	 * @param c : corner id
	 * @return relative corner id (between 0 and 11)
	 */
	int relativeCornerInTetra(int c) {
		return c % 12;
	}

	/**
	 * vertex index of the corner
	 * @param c : corner id
	 * @return vertex id
	 */
	int v(int c) {
		if (borderCorner(c))
			try {
				return v(o(c));
			} catch (BorderCornerException e) {
				return -1;
			}
		int t = tetraFromCorner(c);
		int rc = relativeCornerInTetra(c);
		return V[4 * t + ver[rc]];
	}

	/**
	 *  opposite operation on corner, throw an exception if the returned corner is outside the mesh
	 *  the exception has a o field that allow access to the outside corner
	 * @param c : corner id
	 * @return opposite corner id
	 * @throws BorderCornerException is the returned corner is outside the mesh
	 */
	int o(int c) throws BorderCornerException {
		// print(c+" ");
		if (borderCorner(c)) {
			int f = faceFromCorner(c);
			int o = O[f];
			if (c % 3 == 0)
				return 3 * o;
			if (c % 3 == 1)
				return 3 * o + 2;
			return 3 * o + 1;
		} else {
			int v = v(c);
			int f = faceFromCorner(c);
			int o = 3 * O[f];
			if (borderCorner(o)) {
				if (c % 3 == 0)
					throw new BorderCornerException(o);
				if (c % 3 == 1)
					throw new BorderCornerException(o+2);
				throw new BorderCornerException(o+1);
			}
			if (v == v(o))
				return o;
			o++;
			if (v == v(o))
				return o;
			o++;
			return o;
		}
	}
	/**
	 * opposite operation on current corner
	 */
	void o() {
		try {
			currentCorner = o(currentCorner);
		} catch (BorderCornerException e) {
			currentCorner = e.o;
		}
	}

	/**
	 *  swing operation
	 * @param c : corner id
	 * @return swing corner id
	 */
	int s(int c) {
		if (!borderCorner(c)) {
			int rc = relativeCornerInTetra(c);
			return 12 * tetraFromCorner(c) + swing[rc];
		} else {
			int v;
			try {
				v = v(n(o(c)));
			} catch (BorderCornerException e) {
				v = v(n(n(c)));
			}
			boolean b = true;
			int temp;
			try {
				temp = o(c);
			} catch (BorderCornerException e) {
				temp = e.o;
			}
			while (b) {
				try {
					temp = o(rot(v, temp));
				} catch (BorderCornerException e) {
					temp = e.o;
				}
				b = !borderCorner(temp);
			}
			return temp;
		}
	}
	/**
	 * swing operation on current corner
	 */
	void s() {
		currentCorner = s(currentCorner);
	}

//	/**
//	 * return the swing of a corner on the boundary
//	 * @param c : outside corner id
//	 * @return swing of c
//	 */
//	private int borderNeighbour(int c) {
//		int v = v(n(c));
//		int q = s(s(c));
//		while (borderCorner(q)) {
//			int temp;
//			try {
//				temp = n(o(q));
//			} catch (BorderCornerException e) {
//				temp = e.o;
//			}
//			if (!(v(n(temp)) == v || v(n(n(temp))) == v))
//				temp = s(temp);
//			q = temp;
//		}
//		return q;
//	}

	/**
	 * return the swing of c that has the vertex v in his corresponding face
	 * @param v : vertex id of next(c)
	 * @param c : corner id
	 * @return corner id 
	 */
	int rot(int v, int c) {
		if (v(n(s(c))) == v || v(n(n(s(c)))) == v) {
			return s(c);
		} else
			return s(s(c));
	}

	/**
	 * test if a corner is on the border (outside the Mesh)
	 * @param c : corner id
	 * @return true -> outside the mesh
	 */
	boolean borderCorner(int c) {
		return (c / 12 >= nt);
	}

	/**
	 *  return a corner witch vertex is opposite of face f1 or f2 and witch his
	 *	next is the other vertex opposite of f1 or f2
	 *	identifiable as an edge
	 * @param f1 : face id 
	 * @param f2 : face id
	 * @return corner id
	 */
	int cornerOftetra(int f1, int f2) {
		int f = 4 * tetraFromFace(f1);
		if (f == f1 || f == f2)
			f++;
		if (f == f1 || f == f2)
			f++;
		if (f == f1 || f == f2)
			f++;
		if (v(3 * f) == V[f1] || v(3 * f) == V[f2])
			if (v(3 * f + 1) == V[f1] || v(3 * f + 1) == V[f2])
				return 3 * f;
			else
				return 3 * f + 2;
		else
			return 3 * f + 1;
	}

	/**
	 * return all tetrahedra containing v(c) and v(n(c))
	 * @param c : corner id 
	 * @return List of tetrahedra ids containing the edge (v(c),v(n(c)))
	 */
	ArrayList<Integer> edgeNeighbors(int c){
		return  edgeNeighboursbis(c,true);
	}
	
	private ArrayList<Integer> edgeNeighboursbis(int c,boolean way) {
		ArrayList<Integer> l = new ArrayList<Integer>();
		boolean b = true;
		boolean first = true;
		int v = v(n(c));
		int s = c;
		while (b) {
			if (!borderCorner(s)) {
				int t = tetraFromCorner(s);
				b = t!=tetraFromCorner(c)||first;
				if (b)
					l.add(t);
				s = s(s);
				if (v(n(s)) == v || v(n(n(s))) == v)
					try {
						s = o(s);
					} catch (BorderCornerException e) {
						b=false;
					}
				else
					try {
						s = o(s(s));
					} catch (BorderCornerException e) {
						b=false;
					}
			} else{
				b = false;
				l.add(-1);
			}
			first=false;
		}
		if (way)
			l.addAll(edgeNeighboursbis(s(n(c)),false));
		return l;
	}

	/**
	 *  return all tetrahedra containing v(c)
	 * @param c : corner id
	 * @return tetrahedra ids list
	 */
	TreeSet<Integer> vertexNeighbors(int c) {
		if (borderCorner(c))
			try {
				c = o(c);
			} catch (BorderCornerException e2) {
				return null;
			}
		ArrayList<Integer> l = new ArrayList<Integer>();
		TreeSet<Integer> t = new TreeSet<Integer>();
		l.add(c);
		t.add(tetraFromCorner(c));
		while (!l.isEmpty()) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for (Integer cor : l) {
				if (!borderCorner(cor)) {
					int c0;
					try {
						c0 = o(cor);
						if (!t.contains(tetraFromCorner(c0))) {
							t.add(tetraFromCorner(c0));
							temp.add(c0);
						}
					} catch (BorderCornerException e1) {
						if (!t.contains(-1)) {
							t.add(-1);
						}
					}
					int c1;
					try {
						c1 = o(s(cor));
						if (!t.contains(tetraFromCorner(c1))) {
							t.add(tetraFromCorner(c1));
							temp.add(c1);
						}
					} catch (BorderCornerException e) {
						if (!t.contains(-1)) {
							t.add(-1);
						}
					}
					int c2;
					try {
						c2 = o(s(s(cor)));
						if (!t.contains(tetraFromCorner(c2))) {
							t.add(tetraFromCorner(c2));
							temp.add(c2);
						}
					} catch (BorderCornerException e) {
						if (!t.contains(-1)) {
							t.add(-1);
						}
					}
				}
			}
			l = temp;
		}
		return t;
	}

	/**
	 *  edge contraction operation
	 *  erase the edge (v(c),v(n(c)))
	 *  do not erase completely the tetraedron (the tetraedron space is still
	 *  occupied)
	 * @param c : corner id 
	 */
	void edgeContraction(int c) {
		if (borderCorner(c)) {
			try {
				edgeContraction(o(c));
			} catch (BorderCornerException e) {
				e.printStackTrace();
			}
		} else {
			TreeSet<Integer> n1 = vertexNeighbors(c);
			System.out.print("edgeContration");
			int v1 = v(c);
			int v2 = v(n(c));
			ArrayList<Integer> l = edgeNeighbors(c);
			for (Integer t : l) {
				int f0;
				int f1;
				if (V[4 * t] == v1 || V[4 * t] == v2)
					f0 = 4 * t;
				else if (V[4 * t + 1] == v1 || V[4 * t + 1] == v2)
					f0 = 4 * t + 1;
				else if (V[4 * t + 2] == v1 || V[4 * t + 2] == v2)
					f0 = 4 * t + 2;
				else
					f0 = 4 * t + 3;
				if (V[f0 + 1] == v1 || V[f0 + 1] == v2)
					f1 = f0 + 1;
				else if (V[f0 + 2] == v1 || V[f0 + 2] == v2)
					f1 = f0 + 2;
				else
					f1 = f0 + 3;
				int o0 = O[f0];
				int o1 = O[f1];
				O[o0] = o1;
				O[o1] = o0;
				V[4 * t] = -1;
				V[4 * t + 1] = -1;
				V[4 * t + 2] = -1;
				V[4 * t + 3] = -1;
			}
			G[v2] = pt.P(G[v1], G[v2]);
			for (Integer k : n1) {
				if (k!=-1)
				for (int i = 0; i < 4; i++)
					if (V[4 * k + i] == v1)
						V[4 * k + i] = v2;
			}
		}
	}

	void edgeContraction() {
		edgeContraction(currentCorner);
		for (int i = 0; i < nt; i++) {
			if (V[4 * i] != -1) {
				currentCorner = 12 * i;
				break;
			}
		}
	}
	
	/**
	 * clean the array (V and O) from the cancelled tetrahedra 
	 * O(nf) operation 
	 */
//	void cleanSurface() {
//		System.out.print("cleaning...");
//		TreeSet<Integer> toClean = new TreeSet<Integer>();
//		for (int i = 0; i < 4 * nt; i++)
//			if (V[i] == -1)
//				toClean.add(i);
//		System.out.println(1);
//		int n = toClean.size() / 4;
//		for (int i = 4 * nt; i < nf; i++)
//			if (V[O[i]] == -1)
//				toClean.add(i);
//		System.out.println(2);
//		for (int i = 0; i < nf; i++) {
//			int k = toClean.headSet(i).size();
//			V[i - k] = V[i];
//			O[i - k] = O[i] - toClean.headSet(O[i]).size();
//		}
//		System.out.println(3);
//		nt -= n;
//		nf -= toClean.size();
//		System.err.println("done");
//	}
	void cleanSurface() {
		System.out.print("cleaning...");
		int k=0;
		for (int i = 0; i < 4 * nt; i++)
			if (V[i] == -1)
				k++;
		int n = k / 4;
		k=0;
		for (int i=0;i<4*nt;i++){
			if (V[i]==-1)k++;
			else {
				V[i - k] = V[i];
			}
		}
		nt -= (n+1);
		for (int i=0;i<4*nt;i++){
			if (V[i]==-1){
				throw new Error("\ncleaning fail "+i);
			}
		}
		createOtable();
		orientMesh();
		System.out.println("done");
	}
	
	int rank(int k, Integer[] a,int low, int high){
		if (high-low<=1) return low;
		if (k>a[(high+low)/2]) return rank(k,a,(high+low)/2,high);
		if (k<a[(high+low)/2]) return rank(k,a,low,(high+low)/2);
		if (k==a[(high+low)/2]) return (high+low)/2;
		return 0;
	}
	/**
	 * number of common vertices between two tetrahedra
	 * @param t1 : tetrahedron id
	 * @param t2 : tetrahedron id
	 * @return number of common vertices
	 */
	int CommonVertices(int t1, int t2) {
		if (t1>nt||t2>nt) System.out.println("border");
		if (t1==t2) new Error("equal");
		int k = 0;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++){
				if (V[4 * t1 + i] == V[4 * t2 + j])
					k++;
//				if (i!=j&&V[4 * t2 + i] == V[4 * t2 + j])
//					new Error("degenerated tetrahedron");
//				if (i!=j&&V[4 * t1 + i] == V[4 * t1 + j])
//					new Error("degenerated tetrahedron");
			}
		return k;
	}

	/**
	 * initialize G, to use to build by hand a tetrahedrization
	 */
	void declare() {
		for (int i = 0; i < maxnv; i++)
			G[i] = pt.P();
	} // init all point objects
	/*
	 * pov initiManual() { nv=5; nt=3; nf=18; // internal V[0]=0; O[0]=7;
	 * V[1]=1; O[1]=13; V[2]=2; O[2]=12; V[3]=3; O[3]=11; V[4]=1; O[4]=15;
	 * V[5]=2; O[5]=14; V[6]=3; O[6]=9; V[7]=4; O[7]=0; V[8]=1; O[8]=16; V[9]=0;
	 * O[9]=6; V[10]=2; O[10]=17; V[11]=4; O[11]=3; // external V[12]=5;
	 * O[12]=2; V[13]=5; O[13]=1; V[14]=5; O[14]=5; V[15]=5; O[15]=4; V[16]=5;
	 * O[16]=8; V[17]=5; O[17]=10; return this;} // makes a single tet mesh
	 */

	/**
	 * display the tetrahedrization
	 */
	void showWall() {
		for (int t = 0; t < nt; t++) {
			if (V[4 * t] != -1) {
				display.show(G[V[4 * t + 1]], G[V[4 * t + 2]], G[V[4 * t + 3]]);
				display.show(G[V[4 * t]], G[V[4 * t + 2]], G[V[4 * t + 3]]);
				display.show(G[V[4 * t]], G[V[4 * t + 1]], G[V[4 * t + 3]]);
				display.show(G[V[4 * t]], G[V[4 * t + 1]], G[V[4 * t + 2]]);
			}
		}
	}

	/**
	 *  resets P so that we can start adding points
	 * @return
	 */
	pov empty() {
		nv = 0;
		pv = 0;
		return this;
	} 

	/**
	 * adds a point at the end
	 * @param P
	 * @return
	 */
	pov addPt(pt P) {
		G[nv].setTo(P);
		pv = nv;
		nv++;
		return this;
	} 

	pov addPt(float x, float y) {
		G[nv].x = x;
		G[nv].y = y;
		pv = nv;
		nv++;
		return this;
	}

	pov copyFrom(pov Q) {
		empty();
		nv = Q.nv;
		for (int v = 0; v < nv; v++)
			G[v] = pt.P(Q.G[v]);
		return this;
	}

	int idOfVertexWithClosestScreenProjectionTo(pt M) { // for picking a vertex
		// with the mouse
		display.pp = 0;
		for (int i = 1; i < nv; i++)
			if (POVjava.d(M, display.ToScreen(G[i])) <= POVjava.d(M, display.ToScreen(G[display.pp])))
				display.pp = i;
		return display.pp;
	}

	pt closestProjectionOf(pt M) { // for picking inserting O. Returns
		// projection but also CHANGES iv !!!!
		pt C = pt.P(G[0]);
		float d = POVjava.d(M, C);
		for (int i = 1; i < nv; i++)
			if (POVjava.d(M, G[i]) <= d) {
				iv = i;
				C = pt.P(G[i]);
				d = POVjava.d(M, C);
			}
		for (int i = nv - 1, j = 0; j < nv; i = j++) {
			pt A = G[i], B = G[j];
			if (POVjava.projectsBetween(M, A, B) && POVjava.disToLine(M, A, B) < d) {
				d = POVjava.disToLine(M, A, B);
				iv = i;
				C = POVjava.projectionOnLine(M, A, B);
			}
		}
		return C;
	}

	pov insertPt(pt P) { // inserts new vertex after vertex with ID iv
		for (int v = nv - 1; v > iv; v--)
			G[v + 1].setTo(G[v]);
		iv++;
		G[iv].setTo(P);
		nv++; // increments vertex count
		return this;
	}

	pov insertClosestProjection(pt M) {
		pt P = closestProjectionOf(M); // also sets iv
		insertPt(P);
		return this;
	}

	pov deletePicked() {
		for (int i = pv; i < nv; i++)
			G[i].setTo(G[i + 1]);
		pv = Math.max(0, pv - 1);
		nv--;
		return this;
	}

	pov setPt(pt P, int i) {
		G[i].setTo(P);
		return this;
	}

	pov showPicked() {
		display.show(G[pv], 13);
		return this;
	}

	/**
	 * draw vertices
	 * @param r : radius
	 * @return
	 */
	pov drawBalls(float r) {
		for (int v = 0; v < nv; v++)
			display.show(G[v], r);
		return this;
	}

	pov showPicked(float r) {
		display.show(G[pv], r);
		return this;
	}

	pov drawClosedCurve(float r) {
		for (int v = 0; v < nv - 1; v++)
			display.stub(G[v], vec.V(G[v], G[v + 1]), r, r / 2);
		display.stub(G[nv - 1], vec.V(G[nv - 1], G[0]), r, r / 2);
		return this;
	}

	pov setPickedTo(int pp) {
		pv = pp;
		System.out.println("picked" + pv);
		return this;
	}

	pov movePicked(vec V) {
		G[pv].add(V);
		return this;
	} // moves selected point (index p) by amount mouse moved recently

	pov moveAll(vec V) {
		for (int i = 0; i < nv; i++)
			G[i].add(V);
		return this;
	};

	pt Picked() {
		return G[pv];
	}

	/**
	 * create a random mesh by a delaunay3D and deleting some tetrahedrons 
	 * @param nbv : number of vertices
	 * @param prob : probability of keeping each tetrahedron
	 */
	void createRandomMesh(int nbv, float prob) {
		Delaunay_3 del = new Delaunay_3();
		int N = nbv;
		nv = nbv;
		int spacesize = 500;
		for (int i = 0; i < N; i++) {
			Point_3 p = new Point_3((int) (Math.random() * spacesize / 5), (int) (Math.random() * spacesize / 5),
					(int) (Math.random() * spacesize));
			del.insert(p);

		}
		Collection<TriangulationDSCell_3<Point_3>> cells = del.finiteCells();
		ArrayList<TriangulationDSVertex_3<Point_3>> vertex = new ArrayList<TriangulationDSVertex_3<Point_3>>(
				del.finiteVertices());
		int k = 0;
		for (TriangulationDSVertex_3<Point_3> v : vertex) {
			G[k] = new pt(Float.valueOf("" + v.getPoint().x), Float.valueOf("" + v.getPoint().y),
					Float.valueOf("" + v.getPoint().z));
			k++;
		}
		int nbt = 0;
		for (TriangulationDSCell_3<Point_3> t : cells) {
			if (Math.random() < prob) {
				for (int i = 0; i < 4; i++)
					V[4 * nbt + i] = vertex.indexOf(t.vertex(i));
				nbt++;
			}
		}
		nt = nbt;
//		reorderTetrahedrons();
		createOtable();
		orientMesh();
	}

	void savepov(String fn) {
		// cleanSurface();
		String[] inppov = new String[nv + nf + 3];
		int s = 0;
		inppov[s++] = "" + (nv);
		inppov[s++] = "" + (nf);
		inppov[s++] = "" + (nt);
		for (int i = 0; i < nv; i++) {
			inppov[s++] = (G[i].x) + "," + (G[i].y) + "," + (G[i].z);
		}
		for (int k = 0; k < nf; k++) {
			inppov[s++] = (V[k]) + "," + (O[k]);
		}
		saveStrings(fn, inppov);
	};

	/**
	 * to load a orientated mesh
	 * @param fn
	 */
	static pov loadpov(String fn, POVjava p) {
		pov pov = new pov(p);
		System.out.println("loading: " + fn);
		String[] ss = pov.loadStrings(fn+".pov");
		int s = 0;
		pov.nv = Integer.valueOf(ss[s++]);
		System.out.println("nv=" + pov.nv);
		pov.nf = Integer.valueOf(ss[s++]);
		System.out.println("nf=" + pov.nf);
		pov.nt = Integer.valueOf(ss[s++]);
		System.out.println("nt=" + pov.nt);
		pov.maxnt=pov.nt;
		pov.maxnv=pov.nv;
		pov.maxnf=6*pov.nt;
		pov.G = new pt[pov.maxnv]; 
		pov.declare();
		pov.V = new int[pov.maxnf];
		pov.O = new int[pov.maxnf];
		for (int k = 0; k < pov.nv; k++) {
			int i = k + s;
			String[] xy = ss[i].split(",");
			pov.G[k].setTo(Float.valueOf(xy[0]), Float.valueOf(xy[1]), Float.valueOf(xy[2]));
		}
		for (int k = 0; k < pov.nf; k++) {
			int i = k + s + pov.nv;
			String[] VO = ss[i].split(",");
			pov.V[k] = Integer.valueOf(VO[0]);
			pov.O[k] = Integer.valueOf(VO[1]);
		}
		pov.pv = 0;
//		pov.reorderTetrahedrons();
		pov.createOtable();
		pov.orientMesh();
		ArrayList<Integer> l = pov.testIsManifold();
		while (!l.isEmpty()){
			pov.toManifold(l);
			l = pov.testIsManifold();
		}
		if (pov.checkMesh())
			pov.savepov(fn);
		return pov;
	};

	/**
	 * to orient a mesh
	 * don't touch the first face of each tetrahedron
	 */
	void orientMesh() {
		for (int i = 0; i < nt; i++) {
			if (POVjava.m(G[V[4 * i]], G[V[4 * i + 1]], G[V[4 * i + 2]], G[V[4 * i + 3]]) < 0)
				changeorientation(4 * i);
		}
	}

	private void changeorientation(int face) {
		int f1 = face + 1;
		if (f1 % 4 == 0)
			f1 = f1 - 4;
		int f2 = f1 + 1;
		if (f2 % 4 == 0)
			f2 = f2 - 4;
		int v = V[f1];
		int o = O[f1];
		O[O[f1]] = f2;
		O[O[f2]] = f1;
		V[f1] = V[f2];
		O[f1] = O[f2];
		V[f2] = v;
		O[f2] = o;
	}

	/**
	 * to load a PV mesh (create the O table)
	 * @param fn : file path
	 */
	static pov loadPV(String fn, POVjava p) {
		pov pov = new pov(p);
		System.out.println("loading: " + fn);
		String[] ss = pov.loadStrings(fn);
		int s = 0;
		pov.nv = Integer.valueOf(ss[s++]);
		System.out.println("nv=" +pov. nv);
		pov.nf = Integer.valueOf(ss[s++]);
		System.out.println("nf=" + pov.nf);
		pov.nt = Integer.valueOf(ss[s++]);
		System.out.println("nt=" + pov.nt);
		pov.maxnt=pov.nt;
		pov.maxnv=pov.nv;
		pov.maxnf=6*pov.nt;
		pov.G = new pt[pov.maxnv]; 
		pov.declare();
		pov.V = new int[pov.maxnf];
		pov.O = new int[pov.maxnf];
		for (int k = 0; k < pov.nv; k++) {
			int i = k + s;
			String[] xy = (ss[i].split(","));
			pov.G[k].setTo(Float.valueOf(xy[0]), Float.valueOf(xy[1]), Float.valueOf(xy[2]));
		}
		for (int k = 0; k < 4 * pov.nt; k++) {
			int i = k + s + pov.nv;
			pov.V[k] = Integer.valueOf(ss[i].split(",")[0]);
		}
		pov.pv = 0;
//		pov.reorderTetrahedrons();
		pov.createOtable();
		pov.orientMesh();
		ArrayList<Integer> l = pov.testIsManifold();
		while (!l.isEmpty()){
			pov.toManifold(l);
			l = pov.testIsManifold();
		}
		if (pov.checkMesh())
			pov.savepov(fn);
		System.out.println("done");
		return pov;
	}
	
	void cancelDoubleTets(){
		System.out.print("creating O table...");
		class Quad {
			int[] t=new int[4];

			Quad(int v0, int v1, int v2,int v3) {
				if (v0==v1||v0==v2||v1==v2||v0==v3||v1==v3||v2==v3) new Error("degenerated face");
				t[0]=v0;t[1]=v1;t[2]=v2;t[3]=v3;
				Arrays.sort(t);
			}

			public boolean equals(Object obj) {
				if (obj instanceof Quad) {
					Quad o = (Quad) obj;
					for (int i=0;i<4;i++)
						if (o.t[i]!=t[i]) return false;
					return true;
				}
				return false;
			}

			public int hashCode() {
				int k=0;
				for (int i=0;i<4;i++)
					k+=t[i]*i*i*i;
				return k;
			}
		}

		HashMap<Quad, Integer> htable = new HashMap<Quad, Integer>();
		for (int i = 0; i < nt; i++) {
			Quad tr = new Quad(V[4 * i], V[4 * i+1], V[4 * i + 2], V[4 * i + 3]);
			Integer g = htable.get(tr);
			if (g != null && g >= 0) {
				removeTetrahedron(i);
			} else {
				htable.put(tr, i);
			}
		}
		cleanSurface();
	}
	
	/**
	 *  Create O from V
	 *  linear time algorithm
	 */
	void createOtable() {
		System.out.print("creating O table...");
		class Triplet {
			int x, y, z;

			Triplet(int v0, int v1, int v2) {
				if (v0==v1||v0==v2||v1==v2) new Error("degenerated face");
				x = Math.min(v0, Math.min(v1, v2));
				z = Math.max(v0, Math.max(v1, v2));
				y = v0 + v1 + v2 - x - z;
			}

			public boolean equals(Object obj) {
				if (obj instanceof Triplet) {
					Triplet o = (Triplet) obj;
					return (o.x == x && o.y == y && o.z == z);
				}
				return false;
			}

			public int hashCode() {
				return x + y*y + z*z*z;
			}
		}

		HashMap<Triplet, Integer> htable = new HashMap<Triplet, Integer>();
		for (int i = 0; i < nt; i++) {
			for (int k = 0; k < 4; k++) {
				int f = 4 * i + k;
				Triplet tr = new Triplet(V[4 * i + ((k + 1) % 4)], V[4 * i + ((k + 2) % 4)], V[4 * i + ((k + 3) % 4)]);
				Integer g = htable.get(tr);
				if (g != null && g >= 0) {
					O[f] = g;
					O[g] = f;
//					int l;
//					if ((l= CommonVertices(tetraFromFace(f),tetraFromFace(g)))!=3)System.err.println(f+" "+l);
					htable.remove(tr);
				} else {
					htable.put(tr, f);
				}
			}
		}
		int k = 4 * nt;
		for (Entry<Triplet, Integer> t : htable.entrySet()) {
			int i = t.getValue();
			O[i] = k;
			O[k] = i;
			V[k] = -1;
			k++;
		}
		nf = k;
		System.out.println("done");
	}
	/**
	 * load a .ele file (need a .node file with the same name)
	 * save the new mesh as file.pov
	 * @param fn : file Path
	 * @param mult : multiplication factor
	 */
	static pov loadele(String fn, float mult, POVjava p) {
		pov pov = new pov(p);
		System.out.println("loading: " + fn);
		String[] ss = pov.loadStrings(fn + ".node");
		pov.nv = Integer.valueOf(ss[0].split(" ")[0]);
		System.out.println("nv=" + pov.nv);
		pov.maxnv=pov.nv;
		pov.G = new pt[pov.maxnv]; 
		pov.declare();
		int off = Integer.valueOf((" " + ss[1]).split(" +")[1]);
		for (int k = 0; k < pov.nv; k++) {
			String[] sss = (" " + ss[k + 1]).split(" +");
			pov.G[Integer.valueOf(sss[1]) - off].setTo(mult * Float.valueOf(sss[2]), mult * Float.valueOf(sss[3]),
					mult * Float.valueOf(sss[4]));
		}
		ss = pov.loadStrings(fn + ".ele");
		pov.nt = Integer.valueOf(ss[0].split(" ")[0]);
		pov.maxnt=pov.nt;
		pov.maxnf=6*pov.nt+50;
		pov.nf=6*pov.nt;
		pov.V = new int[pov.maxnf];
		pov.O = new int[pov.maxnf];
		System.out.println("nt=" + pov.nt);
		for (int k = 0; k < pov.nt; k++) {
			String[] sss = (" " + ss[k + 1]).split(" +");
			for (int i = 0; i < 4; i++) {
				pov.V[4 * k + i] = Integer.valueOf(sss[i + 2]) - off;
			}
		}
		pov.pv = 0;
//		pov.reorderTetrahedrons();
		pov.createOtable();
		pov.orientMesh();
		ArrayList<Integer> l = pov.testIsManifold();
		while (!l.isEmpty()){
			pov.toManifold(l);
			l = pov.testIsManifold();
		}
		if (pov.checkMesh())
			pov.savepov(fn);
		System.out.println("done");
		return pov;
	}
	/**
	 * load a .sma file
	 * @param fn : file Path
	 * @param mult : multiplication factor
	 */
	static pov loadsma(String fn, float mult, POVjava p) {
		pov pov = new pov(p);
		System.out.println("loading: " + fn);
		String[] ss = pov.loadStrings(fn + ".sma");
		pov.nv = Integer.valueOf(ss[0].split(" ")[2]);
		pov.nt = Integer.valueOf(ss[1].split(" ")[2]);
		System.out.println("nv=" + pov.nv);
		System.out.println("nt=" + pov.nt);
		pov.maxnt=pov.nt;
		pov.maxnv=pov.nv;
		pov.maxnf=6*pov.nt+50;
		pov.nf=4*pov.nv;
		pov.G = new pt[pov.maxnv]; 
		pov.declare();
		pov.V = new int[pov.maxnf];
		pov.O = new int[pov.maxnf];
		int currt = 0;
		int currv = 0;
		System.out.print("loading...");
		for (int k = 4; k < ss.length; k++) {
			String[] sss = (ss[k]).split(" +");
			if (sss[0].equals("v")) {
				pov.G[currv].setTo(mult * Float.valueOf(sss[1]), mult * Float.valueOf(sss[2]),
						mult * Float.valueOf(sss[3]));
				currv++;
			}
			if (sss[0].equals("c")) {
				int i = Integer.valueOf(sss[1]) - 1;
				if (i > 0)
					pov.V[4 * currt] = i - 1;
				else {
					pov.V[4 * currt] = currv + i + 1;
				}
				i = Integer.valueOf(sss[2]) - 1;
				if (i > 0)
					pov.V[4 * currt + 1] = i - 1;
				else {
					pov.V[4 * currt + 1] = currv + i + 1;
				}
				i = Integer.valueOf(sss[3]) - 1;
				if (i > 0)
					pov.V[4 * currt + 2] = i - 1;
				else {
					pov.V[4 * currt + 2] = currv + i + 1;
				}
				i = Integer.valueOf(sss[4]) - 1;
				if (i > 0)
					pov.V[4 * currt + 3] = i - 1;
				else {
					pov.V[4 * currt + 3] = currv + i + 1;
				}
				currt++;
			}
		}
		System.out.println("done");
		pov.pv = 0;
		pov.cancelDoubleTets();
//		pov.reorderTetrahedrons();
		pov.createOtable();
		pov.orientMesh();
		ArrayList<Integer> l = pov.testIsManifold();
		while (!l.isEmpty()){
			pov.toManifold(l);
			l = pov.testIsManifold();
		}
		if (pov.checkMesh())
			pov.savepov(fn);
		System.out.println("done");
		return pov;
	}
	//read a String File (not online algorithms -> bufferReader?
	String[] loadStrings(String name) {
		ArrayList<String> l = new ArrayList<String>();
		try {
			BufferedReader fr = new BufferedReader(new FileReader(new File(name)));
			String line = fr.readLine();
			while (line != null) {
				l.add(line);
				line = fr.readLine();
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return l.toArray(new String[0]);
	}
	/**
	 * save the file as name.pov
	 * @param name
	 * @param inppov : write each entry as a new line
	 */
	void saveStrings(String name, String[] inppov) {
		try {
			FileWriter fr = new FileWriter(new File(name + ".pov"));
			for (int i = 0; i < inppov.length; i++) {
				fr.write(inppov[i] + "\n");
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * check the combinatorial correctness of the Mesh
	 * print the number of incorrectness
	 * @return
	 */
	boolean checkMesh() {
		int nbr=0;
//		nbr = checkPVarray(nbr);
//		if (nbr!=0)
//			System.out.println("vertex errs : "+nbr);
		nbr = checkOtable(nbr);
		if (nbr!=0)
			System.out.println("errs : "+nbr);
		boolean b = testIsManifold().isEmpty();
		System.out.println("is Manifold : "+b);
		return nbr==0&&b;
	}
	
	/**
	 * erase unmanifold tetrahedra
	 */
	private void toManifold(ArrayList<Integer> l) {
		System.out.println("is Manifold : "+l.isEmpty());
		for (Integer t:l){
			removeTetrahedron(t);
		}
		cleanSurface();
	}

	/*private int checkPVarray(int nbr) {
		for (int v=0;v<nv;v++){
			 if(V[oppositeFaceFromVertex(v)]!=v)nbr++;
			 if(v(cornerFromVertex(v))!=v)nbr++;
		}
		return nbr;
	}*/

	private int checkOtable(int nbr) {
		int k;
		for (int i = 0; i < nt; i++) {
			if (!borderCorner(3*O[4 * i])) {
				k = CommonVertices(i, tetraFromFace(O[4 * i]));
				if (k != 3){
					nbr++;
					System.out.println("err "+k);
				}
			}
			if (!borderCorner(3*O[4 * i+1])) {
				k = CommonVertices(i, tetraFromFace(O[4 * i + 1]));
				if (k != 3){
					nbr++;
				System.out.println("err "+k);}
			}
			if (!borderCorner(3*O[4 * i+2])) {
				k = CommonVertices(i, tetraFromFace(O[4 * i + 2]));
				if (k != 3){
					nbr++;
				System.out.println("err "+k);}
			}
			if (!borderCorner(3*O[4 * i+3])) {
				k = CommonVertices(i, tetraFromFace(O[4 * i + 3]));
				if (k != 3){
					nbr++;
				System.out.println("err "+k);}
			}
		}
		return nbr;
	}
	/**
	 * test is the mesh vertices are manifold
	 * @return a list of tetrahedra of which one vertex is not manifold
	 */
	private ArrayList<Integer> testIsManifold(){
		int[] rel= new int[]{4,2,1,0};
		boolean[] vertex = new boolean[nv];
		boolean[] tet = new boolean[4*nt];
		ArrayList<Integer> l = new ArrayList<Integer>();
		for (int t=0;t<nt;t++){
			boolean b= true;
			for (int i=0;i<4;i++){
				if (!tet[4*t+i]){
					if (vertex[V[4*t+i]]){
						l.add(t);b=false;
					}
				}
			}
			if(b)
			for (int i=0;i<4;i++){
				if (!tet[4*t+i]){
					for (Integer tt : vertexNeighbors(12*t+rel[i])){
						if (tt!=-1){
							int k=0;
							if (V[4*tt+k]==V[4*t+i])tet[4*tt+k]=true;
							else{
								k++;
								if (V[4*tt+k]==V[4*t+i])tet[4*tt+k]=true;
								else{
									k++;
									if (V[4*tt+k]==V[4*t+i])tet[4*tt+k]=true;
									else{
										k++;
										if (V[4*tt+k]==V[4*t+i])tet[4*tt+k]=true;
									}
								}
							}
						}
					}
					vertex[V[4*t+i]]=true;
				}
			}
		}
		System.out.println(l.size());
		return l;
	}
	/*
	/**
	 * reorder tets and vertices such that the nv-3 first tets introduce one new vertex, their fist one
	 * needs Otable to be recompute
	 */
	/*
	void reorderTetrahedrons(){
		pt ver[] = new pt[nv];
		int faces[] = new int[maxnf];
		ver[0]=G[V[0]];
		ver[1]=G[V[1]];
		ver[2]=G[V[2]];
		ver[3]=G[V[3]];
		faces[0]=0;
		faces[1]=1;
		faces[2]=2;
		faces[3]=3;
		int k=4;
		LinkedList<Integer> l = new LinkedList<Integer>();
		HashMap<pt,Integer> set = new HashMap<pt,Integer>();
		for (int i=0;i<4;i++)set.put(G[V[i]],i);
		for (int i= 1;i<nt;i++)l.add(i);
		while (k<nv&&!l.isEmpty()){
			LinkedList<Integer> temp = new LinkedList<>();
			for (Integer t:l){
				int v=0;
				int n=0;
				for (int i=0;i<4;i++){
					if (set.get(G[V[4*t+i]])!=null)
						n++;
					else v=4*t+i;
				}
				if (n==3){
					ver[k]=G[V[v]];
					set.put(G[V[v]],k);
					faces[4*(k-3)]=k;
					int f=v+1;
					if (f%4==0)f=f-4;
					faces[4*(k-3)+1]=set.get(G[V[f]]);
					f++;
					if (f%4==0)f=f-4;
					faces[4*(k-3)+2]=set.get(G[V[f]]);
					f++;
					if (f%4==0)f=f-4;
					faces[4*(k-3)+3]=set.get(G[V[f]]);
					k++;
				}
				else temp.add(t);
			}
			l=temp;
		}
		for (Integer t:l){
			for (int j=0;j<4;j++)faces[4*(k-3)+j]=set.get(G[V[4*t+j]]);
			k++;
		}
		G=ver;
		V=faces;
	}*/
	/**
	 * remove a tetrahedron from the mesh
	 * needs a cleaning(cleanSurface) to avoid negative array indexes
	 * @param t : tet id
	 */
	public void removeTetrahedron(int t){
		for (int i=0;i<4;i++){
			V[4*t+i]=-1;
			if (O[4*t+i]>=0)
			if (!borderCorner(3*O[4*t+i])){
				O[O[4*t+i]]=nf;
				nf++;
			}
			O[4*t+i]=-1;
		}
	}
}
