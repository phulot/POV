package neighbourGraoh;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import cornerDS.faceOperators;

public class NeighbourGraph implements faceOperators {
	HashMap<Integer, Set<Integer>> neighborsVertices;
	int maxDegree = 40;
	int maxfaces = 10000;

	/**
	 * Vertex operation
	 * @param tetid : tet id
	 * @param relativeVertex : relative vertex id
	 * @return vertex id
	 */
	public int Vertex(int tetid, int relativeVertex) {
		if (tetid < maxfaces) {
			if (relativeVertex == 0)
				return oppositeVertex[tetid];
			else
				return border.getVertexID(tetid, 3 - relativeVertex);
		} else {
			int t = tetid - maxfaces;
			int v = t / (int) (Math.pow(maxDegree, 3));
			if (relativeVertex == 0)
				return v;
			Integer[] s = VertexNeighbor(v).toArray(new Integer[0]);
			Arrays.sort(s);
			t = t % (int) (Math.pow(maxDegree, 3d));
			if (relativeVertex == 1)
				return s[(t / (int) (Math.pow(maxDegree, 2)))];
			t = t % (int) (Math.pow(maxDegree, 2));
			if (relativeVertex == 2)
				return s[(t / maxDegree)];
			return s[t % maxDegree];
		}
	}

	/**
	 * return all faces which tip vertex is the same as face's
	 * 
	 * @param face
	 * @return
	 */
	public Set<Integer> sameVertex(int face) {
		Set<Integer> s = new HashSet<>();
		int v = getOppositeVertex(face);
		Set<Integer> active = new HashSet<>();
		active.add(face);
		while (!active.isEmpty()) {
			Set<Integer> temp = new HashSet<>();
			for (Integer f : active) {
				for (int i = 0; i < 3; i++)
					if (getOppositeVertex(border.neighbor(i, f)) == v)
						if (!s.contains(border.neighbor(i, f))) {
							temp.add(border.neighbor(i, f));
							s.add(border.neighbor(i, f));
						}
			}
			active = temp;
		}
		return s;
	}

	/**
	 * return all vertex ids of v's neighbors
	 * 
	 * @param v
	 *            : vertex id
	 * @return set of vertex ids
	 */
	// TODO verify correctness
	public Set<Integer> VertexNeighbor(int v) {
		return neighborsVertices.get(v);
	}

	/**
	 * build the id of an external tet form one of his external faces
	 * 
	 * @param f
	 *            : external face
	 * @return tet id
	 */
	private int buildBorderID(Integer f) {
		int ID = f;
		for (int i = 0; i < 3; i++) {
			if (getOppositeVertex(border.neighbor(i, f)) == border.getVertexID(f, i))
				ID = Math.min(ID, border.neighbor(i, f));
		}
		return ID;
	}

	/**
	 * create the id of an interior tet from his incident vertices vertices
	 * already have to be oriented
	 * 
	 * @param v0
	 *            : vertex id
	 * @param v1
	 *            : vertex id
	 * @param v2
	 *            : vertex id
	 * @param v3
	 *            : vertex id
	 * @return tet id
	 */
	private int buildInteriorID(int v0, int v1, int v2, int v3) {
		if (v0 == v1 || v0 == v2 || v0 == v3 || v1 == v2 || v1 == v3 || v2 == v3)
			throw new Error("degenerated tet");
		if (v0 < v1 && v0 < v2 && v0 < v3) {
			if (v1 < v2 && v1 < v3) {
				int id;
				int k = 0;
				id = (int) Math.pow(maxDegree, 3) * v0;
				Integer[] verNeigh = VertexNeighbor(v0).toArray(new Integer[0]);
				Arrays.sort(verNeigh);
				for (int j = 0; j < verNeigh.length; j++) {
					if (verNeigh[j] == v1) {
						k++;
						id += (int) Math.pow(maxDegree, 2) * j;
					}
					if (verNeigh[j] == v2) {
						k++;
						id += maxDegree * j;
					}
					if (verNeigh[j] == v3) {
						k++;
						id += j;
					}
				}
				id += maxfaces;
				if (k != 3)
					throw new Error("vertexNeighbor Problem");
				return id;
			} else
				return buildInteriorID(v0, v2, v3, v1);
		} else if (v1 < v0 && v1 < v2 && v1 < v3) {
			return buildInteriorID(v1, v0, v3, v2);
		} else if (v2 < v0 && v2 < v1 && v2 < v3) {
			return buildInteriorID(v2, v3, v0, v1);
		} else {
			return buildInteriorID(v3, v2, v1, v0);
		}
	}

	/**
	 * opposite operation on faces
	 * 
	 * @param f
	 *            : face id
	 * @return face id
	 */
	public Integer opposite(int f) throws BorderFaceException {
		int tetid = f / 4;
		int i = f % 4;
		Set<Integer> s;
		s = VertexNeighbor(Vertex(tetid, (i + 1) % 4));
		s.retainAll(VertexNeighbor(Vertex(tetid, (i + 2) % 4)));
		s.retainAll(VertexNeighbor(Vertex(tetid, (i + 3) % 4)));
		s.remove((Integer) Vertex(tetid, i));
		s = border.removeSide(Vertex(tetid, i), s, Vertex(tetid, (i + 1) % 4), Vertex(tetid, (i + 2) % 4),
				Vertex(tetid, (i + 3) % 4), false);
		// System.out.println(s.size());
		if (s.size() == 0) {
			throw new BorderFaceException(f + maxTetID());
		}
		if (s.size() == 1) {
			int o = oppositeFace(tetid, s.iterator().next(), i);
			return o;
		} else {
			// System.out.println("test "+tetid+" // "+Vertex(tetid, i)+"
			// "+Vertex(tetid, (i+1)%4)+" "+Vertex(tetid, (i+2)%4)+"
			// "+Vertex(tetid, (i+3)%4));
			for (Integer v : s) {
				int k = 0;
				Set<Integer> s0 = border.removeSide(Vertex(tetid, (i + 1) % 4), s, Vertex(tetid, (i + 2) % 4),
						Vertex(tetid, (i + 3) % 4), v, true);
				Set<Integer> s1 = border.removeSide(Vertex(tetid, (i + 2) % 4), s0, Vertex(tetid, (i + 1) % 4),
						Vertex(tetid, (i + 3) % 4), v, true);
				s1.remove((Integer) v);
				if (s1.isEmpty())
					k++;
				if (s1.size() == 1) {
					return oppositeFace(tetid, s1.iterator().next(), i);
				}
				s0 = border.removeSide(Vertex(tetid, (i + 2) % 4), s, Vertex(tetid, (i + 1) % 4),
						Vertex(tetid, (i + 3) % 4), v, true);
				s1 = border.removeSide(Vertex(tetid, (i + 3) % 4), s0, Vertex(tetid, (i + 1) % 4),
						Vertex(tetid, (i + 2) % 4), v, true);
				s1.remove((Integer) v);
				if (s1.isEmpty())
					k++;
				if (s1.size() == 1) {
					return oppositeFace(tetid, s1.iterator().next(), i);
				}
				s0 = border.removeSide(Vertex(tetid, (i + 1) % 4), s, Vertex(tetid, (i + 2) % 4),
						Vertex(tetid, (i + 3) % 4), v, true);
				s1 = border.removeSide(Vertex(tetid, (i + 3) % 4), s0, Vertex(tetid, (i + 1) % 4),
						Vertex(tetid, (i + 2) % 4), v, true);
				s1.remove((Integer) v);
				if (s1.isEmpty())
					k++;
				if (s1.size() == 1) {
					return oppositeFace(tetid, s1.iterator().next(), i);
				}
				if (k == 3) {
					return oppositeFace(tetid, v, i);
				}
			}
			throw new Error("opposite Error");
		}
	}

	private int oppositeFace(int tetid, Integer v, int o) {
		// Integer v=s.iterator().next();
		Set<Integer> s;
		s = VertexNeighbor(v);
		// System.out.println(s+ " "+Vertex(tetid, (o + 1) % 4)+ "
		// "+Vertex(tetid, (o + 2) % 4)+ " "+Vertex(tetid, (o + 3) % 4));
		// s.retainAll(VertexNeighbor(Vertex(tetid, (o + 2) % 4)));
		// s.retainAll(VertexNeighbor(Vertex(tetid, (o + 3) % 4)));
		if (!s.contains(Vertex(tetid, (o + 1) % 4)))
			throw new Error("Ne1");
		if (!s.contains(Vertex(tetid, (o + 2) % 4)))
			throw new Error("Ne2");
		if (!s.contains(Vertex(tetid, (o + 3) % 4)))
			throw new Error("Ne3");

		int id = -1;
		Collection<Integer> col = border.incidentFaces(v);
		for (Integer f : col) {
			int k = 0;
			for (int i = 0; i < 3; i++) {
				if (border.getVertexID(f, i) == Vertex(tetid, (o + 1) % 4)
						|| border.getVertexID(f, i) == Vertex(tetid, (o + 2) % 4)
						|| border.getVertexID(f, i) == Vertex(tetid, (o + 3) % 4))
					k++;
			}
			if (k == 2) {
				id = buildBorderID(f);
				break;
			}
		}
		if (id == -1)
			id = buildInteriorID(v, Vertex(tetid, (o + 1) % 4), Vertex(tetid, (o + 3) % 4), Vertex(tetid, (o + 2) % 4));// TODO
																														// verifier
																														// orientation
		for (int i = 0; i < 4; i++) {
			if (v == Vertex(id, i))
				return 4 * id + i;
		}
		return 4 * id;
	}

	/**
	 * Return the set of all tetrahedron ids
	 * 
	 * @return the set of all tetrahedron ids
	 */
	public Set<Integer> allTetIDS() {
		HashSet<Integer> set = new HashSet<>();
		HashSet<Integer> res = new HashSet<>();
		set.add(0);
		res.add(0);
		while (!set.isEmpty()) {
			HashSet<Integer> temp = new HashSet<>();
			for (Integer i : set) {
				for (int o = 0; o < 4; o++) {
					Integer t;
					try {
						t = opposite(4 * i + o);
						if (!res.contains(t / 4)) {
							res.add(t / 4);
							temp.add(t / 4);
						}

					} catch (BorderFaceException e) {
					}
				}
			}
			set = temp;
		}
		return res;
	}

	@Override
	public int maxTetID() {
		return getnv() * maxDegree * maxDegree * maxDegree;
	}

	@Override
	public int getnv() {
		return border.sizeOfVertices();
	}

	@Override
	public void setNv(int nv) {
		// TODO Auto-generated method stub

	}

	@Override
	public pt G(int f) {
		return border.G(f);
	}

	@Override
	public int O(int f) throws BorderFaceException {
		return opposite(f);
	}

	@Override
	public Integer V(int v) {
		return Vertex(v / 4, v % 4);
	}

	@Override
	public void save(String fn) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean borderFace(int f) {
		return f >= 4 * maxTetID();
	}

	@Override
	public boolean borderCorner(int c) {
		return c >= 12 * maxTetID();
	}
}
