package Triangulations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import Jcg.geometry.Pair;
import POV.pt;

public class CornerBasedTriangulation implements Triangulation{
	public pt[] G;
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
			C[V[i]]=i+1;
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
				Pair<Integer> p = new Pair<>(V[3*i+(k+1)%3], V[3*i+k]);
				Pair<Integer> p1 = new Pair<>(V[3*i+k], V[3*i+(k+1)%3]);
				Pair<Integer> p0 = map.get(p);
				if (p0==null) map.put(p1, new Pair<>(3*i+k,-1));
				else {
					assert p0.getSecond()==-1;
					p0.setSecond(3*i+k);
					assert p0.getFirst()!=3*i+k;
				}
			}
		}
		for (int i=0;i<(nt);i++){
			for (int k=0;k<3;k++){
				Pair<Integer> p = new Pair<>(V[3*i+(k+1)%3], V[3*i+k]);
				Pair<Integer> p1 = new Pair<>(V[3*i+k], V[3*i+(k+1)%3]);
				Pair<Integer> p0 = map.get(p);
				if (p0==null) p0 = map.get(p1);
				
				if (V[p0.getFirst()]!=V[3*(p0.getSecond()/3)+(p0.getSecond()+1)%3]) throw new Error(""+i);
				S[p0.getFirst()]=3*(p0.getSecond()/3)+(p0.getSecond()+1)%3;
				if (V[S[p0.getFirst()]]!=V[p0.getFirst()]) throw new Error(""+i);

				if (V[p0.getSecond()]!=V[3*(p0.getFirst()/3)+(p0.getFirst()+1)%3]) throw new Error(""+i);
				S[p0.getSecond()]=3*(p0.getFirst()/3)+(p0.getFirst()+1)%3;
				if (V[S[p0.getSecond()]]!=V[p0.getSecond()]) throw new Error(""+i);
			}
		}
		for (int i=0;i<3*nt;i++)if (V[i]!=V[S[i]]) throw new Error(""+i+"  "+S[i]);
	}
	
	/**
	 * check S table correctness
	 */
	public void checkS(){
		Set<Integer> c = new HashSet<>();
		Set<Integer> v = new HashSet<>();
		for (int i=0;i<nv;i++)v.add(i);
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
		int f0=C[v]-1;
		Iterator<Integer> it = new Iterator<Integer>() {
			int face=f0;
			boolean first=true;
			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return (f0!=-1)&&(first||face!=f0);
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
		return 13*nt/2;
	}

}
