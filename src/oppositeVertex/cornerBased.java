package oppositeVertex;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Jcg.geometry.Pair;
import POV.pt;

public class cornerBased implements Triangulation{
	pt[] G;
	int[] C;
	int[] V;
	int[] S;
	int nv,nt;
	
	public cornerBased(pt[] pts,int[] c,int nt,int nv){
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
	
	public void createS(){
		S=new int[V.length];
		HashMap<Pair<Integer>, Pair<Integer>> map = new HashMap<>(6*nv);
		for (int i=0;i<(nt);i++){
			for (int k=0;k<3;k++){
				Pair<Integer> p = new Pair<>(Math.min(V[3*i+(k)%3], V[3*i+(k+1)%3]),Math.max(V[3*i+(k)%3], V[3*i+(k+1)%3]));
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
				
//				if (V[p0.getFirst()]!=V[3*(p0.getSecond()/3)+(p0.getSecond()+1)%3]) throw new Error(""+i);
				S[p0.getFirst()]=3*(p0.getSecond()/3)+(p0.getSecond()+1)%3;
//				if (V[S[p0.getFirst()]]!=V[p0.getFirst()]) throw new Error(""+i);

//				if (V[p0.getSecond()]!=V[3*(p0.getFirst()/3)+(p0.getFirst()+1)%3]) throw new Error(""+i);
				S[p0.getSecond()]=3*(p0.getFirst()/3)+(p0.getFirst()+1)%3;
//				if (V[S[p0.getSecond()]]!=V[p0.getSecond()]) throw new Error(""+i);
			}
		}
//		for (int i=0;i<3*nt;i++)System.out.println(S[i]+"   "+V[i]);.
//		System.out.println(map.size()+"   "+nt);
		for (int i=0;i<3*nt;i++)if (V[i]!=V[S[i]]) throw new Error(""+i);
	}
	
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
	
	@Override
	public Collection<Integer> incidentFaces(int v) {
		Set<Integer> s= new HashSet<>();
//		for (int i=0;i<3*nt;i++){
//			if (C[i]==v)
//				s.add(i/3);
//		}
		int d=2;
		int f=C[v];
//		if (C[f]!=v)f++;
//		if (C[f]!=v)f++;
		while (d!=0){
			int l=s.size();
			s.add(f/3);
			f=S[f];
			d=s.size()-l;
		}
//		System.out.println(s);
		if (s.size()<3) throw new Error("invalid triangulation");
		return s;
	}

	@Override
	public int sizeOfVertices() {
		return nv;
	}

	@Override
	public int sizeOfFaces() {
		return nt;
	}

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

}
