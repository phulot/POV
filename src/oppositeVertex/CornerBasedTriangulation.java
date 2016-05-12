package oppositeVertex;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import Jcg.geometry.Pair;
import POV.pt;

public class CornerBasedTriangulation implements Triangulation{
	pt[] G;
	int[] C;
	int[] V;
	int[] S;
	int nv,nt;
	
	/**
	 * create the triangulation from a SVR representation
	 * @param pts : arrays of points
	 * @param c : array of incidence, 3 entries per triangle
	 * @param nt : number of triangles
	 * @param nv : number of vertices
	 */
	public CornerBasedTriangulation(pt[] pts,int[] c,int nt,int nv){
		V=c;
		G=pts;
		this.nv=nv;
		this.nt=nt;
		C=new int[nv];
		for (int i=0;i<3*nt;i++){
			C[V[i]]=i;
		}
		createS();
		checkS();
	}
	
	/**
	 * create the swing table
	 */
	public void createS(){
		S=new int[V.length];
		HashMap<Pair<Integer>, Pair<Integer>> map = new HashMap<>(6*nv);
		for (int i=0;i<(nt);i++){
			for (int k=0;k<3;k++){
				Pair<Integer> p = new Pair<>(Math.min(V[3*i+k], V[3*i+(k+1)%3]),Math.max(V[3*i+k], V[3*i+(k+1)%3]));
				Pair<Integer> p0 = map.get(p);
				if (p0==null) map.put(p, new Pair<>(3*i+k,-1));
				else {
					assert p0.getSecond()==-1;
					p0.setSecond(3*i+k);
					assert p0.getFirst()!=3*i+k;
				}
			}
		}
		for (int i=0;i<(nt);i++){
			for (int k=0;k<3;k++){
				Pair<Integer> p = new Pair<>(Math.min(V[3*i+(k)%3], V[3*i+(k+1)%3]),Math.max(V[3*i+(k)%3], V[3*i+(k+1)%3]));
				Pair<Integer> p0 = map.get(p);
				
				if (V[p0.getFirst()]!=V[3*(p0.getSecond()/3)+(p0.getSecond()+1)%3]) throw new Error(""+i);
				S[p0.getFirst()]=3*(p0.getSecond()/3)+(p0.getSecond()+1)%3;
				if (V[S[p0.getFirst()]]!=V[p0.getFirst()]) throw new Error(""+i);

				if (V[p0.getSecond()]!=V[3*(p0.getFirst()/3)+(p0.getFirst()+1)%3]) throw new Error(""+i);
				S[p0.getSecond()]=3*(p0.getFirst()/3)+(p0.getFirst()+1)%3;
				if (V[S[p0.getSecond()]]!=V[p0.getSecond()]) throw new Error(""+i);
			}
		}
//		for (int i=0;i<3*nt;i++)System.out.println(S[i]+"   "+V[i]);.
//		System.out.println(map.size()+"   "+nt);
		for (int i=0;i<3*nt;i++)if (V[i]!=V[S[i]]) throw new Error(""+i+"  "+S[i]);
	}
	
	/**
	 * check S table correctness
	 */
	public void checkS(){
		Set<Integer> c = new HashSet<>();
		Set<Integer> v = new HashSet<>();
		for (int i=0;i<3*nt;i++)c.add(i);
		for (int i=0;i<nv;i++)v.add(i);
		boolean b=true;
		while (!c.isEmpty()){
			int cor = c.iterator().next();
			int ver=V[cor];
			int cc=S[cor];
			c.remove(cor);
			while (cc!=cor){
				assert V[cc]==ver;
				c.remove(cc);
				cc=S[cc];
			}
			if (!v.contains(ver)) throw new Error();
			v.remove(ver);
		}
	}
	/**
	 * return face id of face containing a vertex (id)
	 */
	@Override
	public Iterable<Integer> incidentFaces(int v) {
		int f0=C[v];
		Iterator<Integer> it = new Iterator<Integer>() {
			int face=f0;
			boolean first=true;
			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return first||face!=f0;
			}
			@Override
			public Integer next() {
				int temp = face;
				first=false;
				face=S[face];
				return temp/3;
			}
		};
		return new Iterable<Integer>() {
			@Override
			public Iterator<Integer> iterator() {
				return it;
			}
		};
	}
	/**
	 * return the number of vertices
	 */
	@Override
	public int sizeOfVertices() {
		return nv;
	}
	/**
	 * return the number of triangles
	 */
	@Override
	public int sizeOfFaces() {
		return nt;
	}
	/**
	 * remove all vertices from the set s, on the ather side of the plane A,B,C
	 */
	@Override
	public Set<Integer> removeSide(int sideReference, Set<Integer> s, int Ai, int Bi, int Ci,
			boolean removeOppositeSide) {
		Set<Integer> ss = new HashSet<Integer>();
		pt A = G[Ai];
		pt B = G[Bi];
		pt C = G[Ci];
		pt O = G[sideReference];
		for (Integer i:s){
			pt V = G[i];
			double d=Utils.oppositeSide(A, B, C, O, V);
			if (d==0)ss.add(i);
			if ((d>0&&removeOppositeSide)||(d<0&&!removeOppositeSide))
				ss.add(i);
		}
		return ss;
	}

	@Override
	public int getVertexID(int faceID, int relativeVertexID) {
		return V[3*faceID+relativeVertexID];
	}

	@Override
	public int neighbor(int i, int faceID) {
		return S[3*faceID+i]/3;
	}

	@Override
	public pt G(int i) {
		return G[i];
	}

	@Override
	public int storageCost() {
		return 13*nv;
	}

}
