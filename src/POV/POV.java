package POV;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import cornerDS.faceOperators;

import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author Pierre Hulot
 * @author jarek Rossignac
 * representation of a tetrahedral mesh by the array of his vertices, 
 * the array of the opposite vertex of each face (same tetrahedron)
 * and the Array of the opposite face of each face(other tetrahedron) 
 */
public class POV implements faceOperators{
	/**
	 * display interface
	 */
//	POVjava display;
	public final int[] rel= new int[]{4,2,1,0};
	private final int[] swing = new int[] { 8, 5, 9, 0, 7, 10, 2, 11, 3, 6, 1, 4 };
	private final int[] ver = new int[] { 3, 2, 1, 3, 0, 2, 1, 0, 3, 1, 2, 0 };
	/** max number of vertices */
	public int maxnv = 10000; 
	/** number of vertices currently used in P */
	public int nv = 0; 
	/** max number of tets */
	public int maxnt = 30000; 
	/** max number of faces (including external) */
	public int maxnf = maxnt * 4;
	/** tetrahedron number */
	public int nt = 0;
	/** face number */
	public int nf = 0;
	/** geometry table (vertices) */
	public pt[] G = new pt[maxnv]; 
	/** ID of visible vertex of face */
	public Integer[] V = new Integer[maxnf]; 
	/** ID of opposite face */
	public int[] O = new int[maxnf];
	/**
		create an instance of POV representation
		@param display interface that extends PApplet 
	*/
	public POV() {}
	
	public int storageCost(){
		return 8*nt;
	}
	
	void refreshIntegers(){
		maxnv=G.length;
		maxnf=V.length;
		maxnt=V.length/4;
		int k=0;
		for (int i=0;i<G.length;i++){
			if (G[i]!=null)k++;
		}
		nv=k;
		k=0;
		for (int i=0;i<G.length;i++){
			if (V[i]!=-1&&V[i]!=null)k++;
		}
		nt=k;
		nf=4*nt;
	}
	
	public POV(POV p) {
		this.maxnv = p.maxnv;
		this.nv = p.nv;
		this.maxnt = p.maxnt;
		this.maxnf = p.maxnf;
		this.nt = p.nt;
		this.nf = p.nf;
		G = p.G;
		V = p.V;
		O = p.O;
	}


	/**
	 	next operation on corner
	 	@param corner id
	 	@return next corner
	 */
	public int n(int c) {
		int r = c + 1;
		if (r % 3 == 0)
			r = r - 3;
		return r;
	}

	/**
	 	next operation on curent vertex
	 */
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
	public int faceFromCorner(int c) {
		return c / 3;
	}

	/**
	 * relative corner in Face
	 * @param c : corner id
	 * @return relative corner id in the face (between 0 and 2)
	 */
	public int relativeCornerInFace(int c) {
		return c % 3;
	}

	/**
	 * tetrahedron index of the face
	 * @param f : face id
	 * @return tetrahedron id
	 */
	public int tetraFromFace(int f) {
		return f / 4;
	}

	/**
	 * retlative face in tetrahadron
	 * @param f : face id
	 * @return tetrahedron id
	 */
	public int relativeFaceInTetra(int f) {
		return f % 4;
	}

	/**
	 * tetrahedron index of the corner
	 * @param c : corner id
	 * @return tetrahedron id
	 */
	public int tetraFromCorner(int c) {
		return c / 12;
	}

	/**
	 * relative corner in tetrahedron
	 * @param c : corner id
	 * @return relative corner id (between 0 and 11)
	 */
	public int relativeCornerInTetra(int c) {
		return c % 12;
	}

	/**
	 * vertex index of the corner
	 * @param c : corner id
	 * @return vertex id
	 */
	public int v(int c) {
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
	public int o(int c) throws BorderCornerException {
		// print(c+" ");
		if (borderCorner(c)) {
			int f = faceFromCorner(c);
			int o = f-4*nt;
			if (c % 3 == 0)
				return 3 * o;
			if (c % 3 == 1)
				return 3 * o + 2;
			return 3 * o + 1;
		} else {
			int v = v(c);
			int f = faceFromCorner(c);
			if (f==O[f]) {
				if (c % 3 == 0)
					throw new BorderCornerException(3*f+12*nt);
				if (c % 3 == 1)
					throw new BorderCornerException(3*f+12*nt+2);
				throw new BorderCornerException(3*f+12*nt+1);
			}
			int o = 3 * O[f];
			if (v == v(o))
				return o;
			o++;
			if (v == v(o))
				return o;
			o++;
			return o;
		}
	}


	public int O(int f) throws BorderFaceException{
		if (borderFace(f))
			return f-4*nt;
		if (O[f]==f)
			throw new BorderFaceException(f+4*nt);
		return O[f];
	}
	/**
	 *  swing operation
	 * @param c : corner id
	 * @return swing corner id
	 */
	public int s(int c) {
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
	public boolean borderCorner(int c) {
		return (c >= nt*12);
	}
	
	/**
	 * test if a face is on the border (outside the Mesh)
	 * @param f : face id
	 * @return true -> outside the mesh
	 */
	public boolean borderFace(int f) {
		return (f >= 4*nt);
	}

	/**
	 *  return a corner witch vertex is opposite of face f1 or f2 and witch his
	 *	next is the other vertex opposite of f1 or f2
	 *	identifiable as an edge
	 * @param f1 : face id 
	 * @param f2 : face id
	 * @return corner id
	 */
	public int cornerOftetra(int f1, int f2) {
		if (borderFace(f1)||borderFace(f2)) throw new Error("border Faces");
		int f = 4 * tetraFromFace(f1);
		if (f == f1 || f == f2)
			f++;
		if (f == f1 || f == f2)
			f++;
//		if (f == f1 || f == f2)
//			f++;
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
	public Set<Integer> edgeNeighbors(int c){
		return  edgeNeighboursbis(c,true);
	}
	
	private Set<Integer> edgeNeighboursbis(int c,boolean way) {
		Set<Integer> l = new HashSet<Integer>();
		boolean b = true;
		boolean first = true;
		int v = v(n(c));
		int s = c;
		while (b) {
			if (!borderCorner(s)) {
				int t = tetraFromCorner(s);
				b = !l.contains(t)||first;
				if (b)
					l.add(t);
				s = s(s);
				if (v(n(s)) == v || v(n(n(s))) == v)
					try {
						s = o(s);
					} catch (BorderCornerException e) {
						s=e.o;
//						l.add(-1);
//						b=false;
					}
				else
					try {
						s = o(s(s));
					} catch (BorderCornerException e) {
						s=e.o;
//						l.add(-1);
//						b=false;
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
	public TreeSet<Integer> vertexNeighbors(int c) {
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
			Set<Integer> l = edgeNeighbors(c);
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


	
	/*void cleanSurface() {
		System.out.print("cleaning...");
		TreeSet<Integer> toClean = new TreeSet<Integer>();
		for (int i = 0; i < 4 * nt; i++)
			if (V[i] == -1)
				toClean.add(i);
		System.out.println(1);
		int n = toClean.size() / 4;
		for (int i = 4 * nt; i < nf; i++)
			if (V[O[i]] == -1)
				toClean.add(i);
		System.out.println(2);
		for (int i = 0; i < nf; i++) {
			int k = toClean.headSet(i).size();
			V[i - k] = V[i];
			O[i - k] = O[i] - toClean.headSet(O[i]).size();
		}
		System.out.println(3);
		nt -= n;
		nf -= toClean.size();
		System.err.println("done");
	}
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
	*/
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
				if (V[4 * t1 + i].equals(V[4 * t2 + j]))
					k++;
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

	/**
	 *  resets P so that we can start adding points
	 * @return
	 */
	POV empty() {
		nv = 0;
		return this;
	} 

	/**
	 * adds a point at the end
	 * @param P
	 * @return
	 */
	POV addPt(pt P) {
		G[nv].setTo(P);
		nv++;
		return this;
	} 

	POV addPt(float x, float y) {
		G[nv].x = x;
		G[nv].y = y;
		nv++;
		return this;
	}

	POV copyFrom(POV Q) {
		empty();
		nv = Q.nv;
		for (int v = 0; v < nv; v++)
			G[v] = pt.P(Q.G[v]);
		return this;
	}

	public int oppositeFace(int vertex, int tet){
		if (V[4*tet].equals(vertex))return 4*tet;
		if (V[4*tet+1].equals(vertex))return 4*tet+1;
		if (V[4*tet+2].equals(vertex))return 4*tet+2;
		if (V[4*tet+3].equals(vertex))return 4*tet+3;
		throw new Error("vertex not in tetra");
	}
	
	/**
	 * to pick a corner
	 * @param M : point
	 * @return corner id
	 */
	public int idOfCornerWithClosestTo(pt M) {
		int temp=0;
		for (int i = 1; i < 4*nt; i++)
			if (pt.d(M, G[V[i]]) < pt.d(M, G[V[temp]]))
				temp = i;
		return 12*(temp/4)+rel[temp%4];
	}



	public POV moveAll(vec V) {
		for (int i = 0; i < nv; i++)
			G[i].add(V);
		return this;
	};

	/**
	 * to orient a mesh
	 * the method don't change the first face of each tetrahedron
	 */
	void orientMesh() {
		for (int i = 0; i < nt; i++) {
			if (pt.m(G[V[4 * i]], G[V[4 * i + 1]], G[V[4 * i + 2]], G[V[4 * i + 3]]) < 0)
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
		invertFaces(f1, f2);
	}

	public void savepov(String fn) {
		String[] inppov = new String[nv + 4*nt + 2];
		int s = 0;
		inppov[s++] = "" + (nv);
		inppov[s++] = "" + (nt);
		for (int i = 0; i < nv; i++) {
			inppov[s++] = (G[i].x) + "," + (G[i].y) + "," + (G[i].z);
		}
		for (int i = 0; i < 4*nt; i++) {
			inppov[s++] = (V[i]) + "," + (O[i]);
		}
		povBuilder.saveStrings(fn, inppov);
		System.out.println("saved");
	};
	
	
	/**
	 * cancel tets that are doubled in the mesh
	 */
	void cancelDoubleTets(){
		System.out.print("creating O table...");
		class Quad {
			int[] t=new int[4];

			Quad(int v0, int v1, int v2,int v3) {
				if (v0==v1||v0==v2||v1==v2||v0==v3||v1==v3||v2==v3) new Error("degenerated face");
				t[0]=v0;t[1]=v1;t[2]=v2;t[3]=v3;
				Arrays.sort(t);
			}
			
			@Override
			public boolean equals(Object obj) {
				if (obj instanceof Quad) {
					Quad o = (Quad) obj;
					for (int i=0;i<4;i++)
						if (o.t[i]!=t[i]) return false;
					return true;
				}
				return false;
			}

			@Override
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
	}
	
	/**
	 *  Create O from V
	 *  linear time algorithm
	 */
	public void createOtable() {
		O = new int[maxnf];
		System.out.print("creating O table...");
		class Triplet {
			int x, y, z;

			Triplet(int v0, int v1, int v2) {
				if (v0==v1||v0==v2||v1==v2) new Error("degenerated face");
				x = Math.min(v0, Math.min(v1, v2));
				z = Math.max(v0, Math.max(v1, v2));
				y = v0 + v1 + v2 - x - z;
			}

			@Override
			public boolean equals(Object obj) {
				if (obj instanceof Triplet) {
					Triplet o = (Triplet) obj;
					return (o.x == x && o.y == y && o.z == z);
				}
				return false;
			}

			@Override
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
					htable.remove(tr);
				} else {
					htable.put(tr, f);
				}
			}
		}
		int k = 4 * nt;
		for (Entry<Triplet, Integer> t : htable.entrySet()) {
			int i = t.getValue();
			O[i] = i;
		}
		nf = k;
		System.out.println("done");
	}



	/**
	 * check the combinatorial correctness of the Mesh
	 * print the number of incorrectness
	 * @return
	 */
	public boolean checkMesh() {
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
	void toManifold(Set<Integer> l) {
		System.out.println("is Manifold : "+l.isEmpty());
		for (Integer t:l){
			removeTetrahedron(t);
		}
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
			for (int j=0;j<4;j++)
				if (O[O[4*i+j]]!=4*i+j)nbr++;
			try {
				k = CommonVertices(i, tetraFromFace(O(4 * i)));
				if (k != 3){
					nbr++;
					System.out.println("err "+k);
				}
				k = CommonVertices(i, tetraFromFace(O(4 * i + 1)));
				if (k != 3){
					nbr++;
					System.out.println("err "+k);}
				k = CommonVertices(i, tetraFromFace(O(4 * i + 2)));
				if (k != 3){
					nbr++;
					System.out.println("err "+k);}
				k = CommonVertices(i, tetraFromFace(O(4 * i + 3)));
				if (k != 3){
					nbr++;
					System.out.println("err "+k);}
			} catch (BorderFaceException e) {
			}
		}
		return nbr;
	}
	/**
	 * test is the mesh vertices are manifold
	 * @return a list of tetrahedra of which one vertex is not manifold
	 */
	Set<Integer> testIsManifold(){
		boolean[] vertex = new boolean[nv];
		boolean[] tet = new boolean[4*nt];
		Set<Integer> l = new HashSet<Integer>();
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
					int v= V[4*t+i];
					for (Integer tt : vertexNeighbors(12*t+rel[i])){
						if (tt!=-1){
							int k=0;
							if (V[4*tt+k].equals(v))tet[4*tt+k]=true;
							else{
								k++;
								if (V[4*tt+k].equals(v))tet[4*tt+k]=true;
								else{
									k++;
									if (V[4*tt+k].equals(v))tet[4*tt+k]=true;
									else{
										k++;
										if (V[4*tt+k].equals(v))tet[4*tt+k]=true;
									}
								}
							}
						}
					}
					tet[4*t+i]=true;
					vertex[V[4*t+i]]=true;
				}
			}
		}
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
	 * @param t : tet id
	 */
	public void removeTetrahedron(int t){
		invertTets(t, nt-1);
		for (int i=0;i<4;i++){
			O[O[4*nt-4+i]]=O[4*nt-4+i];
			O[4*nt-4+i]=-1;
		}
		nt--;
	}
	/**
	 * invert two faces (to rearrange the mesh arrays)
	 * to use only for inverting two faces in the same tet
	 * @param f1 : face id
	 * @param f2 : face id
	 */
	private void invertFaces(int f1,int f2){
		if(f1>4*nt||f2>4*nt) throw new Error("Cannot invert border Faces");
		O[O[f1]]=f2;
		O[O[f2]]=f1;
		int temp = O[f1];
		O[f1]=O[f2];
		O[f2]=temp;
		temp=V[f1];
		V[f1]=V[f2];
		V[f2]=temp;
	}
//	private void invertFaces(int t1,int t2, int i){
//		if(t1>nt||t2>nt||i>3) throw new Error("Cannot invert border Faces");
//		int f1=4*t1+i,f2=4*t2+i;
//		O[O[f1]]=map(t1,t2,f1);
//		O[O[f2]]=map(t1,t2,f2);
//		int temp = map(t1,t2,O[f1]);
//		O[f1]=O[f2];
//		O[f2]=temp;
//		temp=V[f1];
//		V[f1]=V[f2];
//		V[f2]=temp;
//	}
	private int map(int t1,int t2, int i){
		if (tetraFromFace(i)!=t1&&tetraFromFace(i)!=t2) return i;
		if (tetraFromFace(i)==t1){
			return 4*(t2-t1)+i;
		}
		else {
			return 4*(t1-t2)+i;
		}
	}
	/**
	 * invert two tets (to rearrange the mesh arrays)
	 * @param t1 : tet id
	 * @param t2 : tet id
	 */
	public void invertTets(int t1, int t2){
		Set<Integer> S = new HashSet<>();
		for (int i=0;i<4;i++){
			S.add(O[4*t1+i]);
			S.add(4*t1+i);
			S.add(O[4*t2+i]);
			S.add(4*t2+i);
		}
		for (Integer i : S){
			O[i]=map(t1,t2,O[i]);
		}
		for (int i= 0;i<4;i++){
			invertTetsFaces(4*t1+i,4*t2+i);
		}
	}
	private void invertTetsFaces(int f1,int f2){
		if(f1>4*nt||f2>4*nt) throw new Error("Cannot invert border Faces");
		int temp = O[f1];
		O[f1]=O[f2];
		O[f2]=temp;
		temp=V[f1];
		V[f1]=V[f2];
		V[f2]=temp;
	}

	public void setO(int[] o) {
		O = o;
	}

	
	
	public int computegenus(){
		int v=nv;
		int r=nt;
		int extf=0;
		Set<Integer> edges = new HashSet<>();
		Set<Integer> vert = new HashSet<>();
		for (int i=0;i<nt;i++){
			for (int j=0;j<4;j++){
				for (int k=j+1;k<4;k++){
					if (V[4*i+j]<V[4*i+k])
						edges.add(2*nv*(V[4*i+j])+V[4*i+k]);
					else edges.add(2*nv*(V[4*i+k])+V[4*i+j]);
				}
				vert.add(V[4*i+j]);
				if (O[4*i+j]==4*i+j)
					extf++;
			}
		}
		v=vert.size();
		int e=edges.size();
		int t=2*nt+extf/2;
		return r+e+1-v-t;
	}

	@Override
	public int maxTetID() {
		return nt;
	}

	@Override
	public pt G(int f) {
		return G[f];
	}

	@Override
	public Integer V(int f) {
		return V[f];
	}

	@Override
	public void save(String fn) {
		savepov(fn);
	}

	@Override
	public Iterator<Integer> iterator() {
		Iterator<Integer> it = new Iterator<Integer>() {
			 int t=0;
			@Override
			public boolean hasNext() {
				return t<nt;
			}

			@Override
			public Integer next() {
				return t++;
			}
		};
		return it;
	}

	@Override
	public int getnv() {
		return nv;
	}

	@Override
	public void setNv(int nv) {
		this.nv=nv;
	}
	
}


