package cornerDS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import POV.BorderCornerException;
import POV.BorderFaceException;
import POV.pt;

public class cornerBasedDS {

	public final int[] rel = new int[] { 4, 2, 1, 0 };
	private final int[] swing = new int[] { 8, 5, 9, 0, 7, 10, 2, 11, 3, 6, 1, 4 };
	private final int[] ver = new int[] { 3, 2, 1, 3, 0, 2, 1, 0, 3, 1, 2, 0 };
	public faceOperators DS;

	public cornerBasedDS(faceOperators dS) {
		System.out.println(dS.getClass());
		DS = dS;
	}

	/**
	 * next operation on corner
	 * @param corner id
	 * @return next corner
	 */
	public int n(int c) {
		int r = c + 1;
		if (r % 3 == 0)
			r = r - 3;
		return r;
	}

	/**
	 * face index of the corner
	 * @param corner id
	 * @return face id
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
		if (DS.borderCorner(c))
			try {
				return v(o(c));
			} catch (BorderCornerException e) {
				return -1;
			}
		int t = tetraFromCorner(c);
		int rc = relativeCornerInTetra(c);
		return DS.V(4 * t + ver[rc]);
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
		if (DS.borderCorner(c)) {
			int f = faceFromCorner(c);
			int o = f-4*DS.maxTetID();
			if (c % 3 == 0)
				return 3 * o;
			if (c % 3 == 1)
				return 3 * o + 2;
			return 3 * o + 1;
		} else {
			int v = v(c);
			int f = faceFromCorner(c);
			try {
				int o = 3 * DS.O(f);
				if (v == v(o))
					return o;
				o++;
				if (v == v(o))
					return o;
				o++;
				return o;
			} catch (BorderFaceException e) {
				if (c % 3 == 0)
						throw new BorderCornerException(3*f+12*DS.maxTetID());
					if (c % 3 == 1)
						throw new BorderCornerException(3*f+12*DS.maxTetID()+2);
					throw new BorderCornerException(3*f+12*DS.maxTetID()+1);
			}
		}
	}

	/**
	 *  swing operation
	 * @param c : corner id
	 * @return swing corner id
	 */
	public int s(int c) {
		if (!DS.borderCorner(c)) {
			int rc = relativeCornerInTetra(c);
			return 12 * tetraFromCorner(c) + swing[rc];
		} else {
			int v = v(n(n(c)));
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
				b = !DS.borderCorner(temp);
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
	 *  return a corner witch vertex is opposite of face f1 or f2 and witch his
	 *	next is the other vertex opposite of f1 or f2
	 *	identifiable as an edge
	 * @param f1 : face id 
	 * @param f2 : face id
	 * @return corner id
	 */
	public int cornerOftetra(int f1, int f2) {
		if (DS.borderFace(f1)||DS.borderFace(f2)) throw new Error("border Faces");
		int f = 4 * tetraFromFace(f1);
		if (f == f1 || f == f2)
			f++;
		if (f == f1 || f == f2)
			f++;
//		if (f == f1 || f == f2)
//			f++;
		if (v(3 * f) == DS.V(f1) || v(3 * f) == DS.V(f2))
			if (v(3 * f + 1) == DS.V(f1) || v(3 * f + 1) == DS.V(f2))
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
			if (!DS.borderCorner(s)) {
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
		if (DS.borderCorner(c))
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
				if (!DS.borderCorner(cor)) {
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
	 * number of common vertices between two tetrahedra
	 * @param t1 : tetrahedron id
	 * @param t2 : tetrahedron id
	 * @return number of common vertices
	 */
	int CommonVertices(int t1, int t2) {
		if (t1>DS.maxTetID()||t2>DS.maxTetID()) System.out.println("border");
		if (t1==t2) new Error("equal");
		int k = 0;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++){
				if (DS.V(4 * t1 + i).equals(DS.V(4 * t2 + j)))
					k++;
			}
		return k;
	}
	public int oppositeFace(int vertex, int tet){
		if (DS.V(4*tet).equals(vertex))return 4*tet;
		if (DS.V(4*tet+1).equals(vertex))return 4*tet+1;
		if (DS.V(4*tet+2).equals(vertex))return 4*tet+2;
		if (DS.V(4*tet+3).equals(vertex))return 4*tet+3;
		throw new Error("vertex not in tetra");
	}
	/**
	 * to pick a corner
	 * @param M : point
	 * @return corner id
	 */
	public int idOfCornerClosestTo(pt M) {
		int temp=0;
		for (Integer i : DS)
			for (int k=0;k<4;k++)
				if (pt.d(M, DS.G(DS.V(4*i+k))) < pt.d(M, DS.G(DS.V(temp)))){
					temp = 4*i+k;
				}
		return 12*(temp/4)+rel[temp%4];
	}
	
	public Set<Integer> traversal(){
		HashSet<Integer> set = new HashSet<>();
		HashSet<Integer> s = new HashSet<>();
		set.add(0);
		while (!set.isEmpty()){
			HashSet<Integer> temp = new HashSet<>();
			for (Integer i : set){
				for (int o=0;o<4;o++){
					Integer t;
					try {
						t=tetraFromFace(DS.O(4*i+o));
						if (!s.contains(t)){
							s.add(t);
							temp.add(t);
						}
					} catch (BorderFaceException e) {
					}
				}
			}
			set=temp;
		}
		return s;
	}
}
