package Triangulations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import Jcg.geometry.Pair;

public class Vlist implements Triangulation {

	pt[] G;
	int[] H;
	int[] L;
	
	public Vlist(pt[] g,Integer[] C) {
		G=g;
		H=new int[g.length];
		L=new int[C.length];
		for (int i=0;i<g.length;i++){
			H[i]=-1*i-1;
		}
		for (int i=0;i<C.length;i++){
			L[i]=H[C[i]];
			H[C[i]]=i;
		}
//		reorderCycles();
	}
	
	@Override
	public Iterable<Integer> incidentFaces(int vertexid) {
		return new Iterable<Integer>() {
			
			@Override
			public Iterator<Integer> iterator() {
				
				return new Iterator<Integer>() {
					int t=H[vertexid];
					@Override
					public boolean hasNext() {
						return t>=0;
					}

					@Override
					public Integer next() {
						int temp=t;
						t=L[t];
						return temp/3;
					}
					
				};
			}
		};
	}

	@Override
	public int sizeOfVertices() {
		return G.length;
	}

	@Override
	public int sizeOfFaces() {
		return L.length/3;
	}

	@Override
	public int getVertexID(int faceID, int relativeVertexID) {
		int t = L[3*faceID+relativeVertexID];
		while(t>=0)t=L[t];
		return -1*t-1;
	}
	public int vertex(int w) {
		int t = L[w];
		while(t>=0)t=L[t];
		return -1*t-1;
	}

	@Override
	public int neighbor(int i, int faceID) {
		int ww = 3*faceID+i;
		int u = getVertexID(faceID, i);
		int v = getVertexID(faceID, (i+1)%3);
//		int z = getVertexID(faceID, (i+2)%3);
//		double maxAngle = 2*Math.PI;
		int x=ww;
		int w = H[u];
		while (w>=0){
			if (w!=ww){
				if (getVertexID(w/3, (w+1)%3)==v||getVertexID(w/3, (w+2)%3)==v){
					x=w;
//					int p;
//					if (getVertexID(w/3, (w+1)%3)==v)p=getVertexID(w/3, (w+2)%3); else p= getVertexID(w/3, (w+1)%3);
//					double planeAngle = angle(G[z],G[u],G[v],G[p]);
//					if (planeAngle<maxAngle){maxAngle=planeAngle;x=w;}
				}
			}
			w=L[w];
		}
		if (x==ww) throw new Error();
		return x/3;
	}
	
	double angle(pt A, pt B, pt C, pt D) {
		vec U= vec.N(vec.V(B,A),vec.V(B,C)).normalize(); 
		vec V= vec.N(vec.V(B,D),vec.V(B,C)).normalize();
		return Math.acos(vec.d(U,V)); //A.innerProduct(B) performs dot product A•B
	}

	@Override
	public pt G(int i) {
		return G[i];
	}

	@Override
	public int storageCost() {
		return H.length+L.length;
	}

	public void checkTriangulation(){
		int err=0;
		HashMap<Pair<Integer>,Integer> map = new HashMap<>();
		for (int i=0;i<L.length/3;i++){
			if (getVertexID(i, 0)==getVertexID(i, 1)||getVertexID(i, 0)==getVertexID(i, 2)||getVertexID(i, 1)==getVertexID(i, 2))
				err++;
			int u=getVertexID(i, 0),v=getVertexID(i, 1);
			Pair<Integer> p= new Pair<>(Math.min(u, v),Math.max(u, v));
			if (map.get(p)!=null)map.remove(p);else map.put(p, i);
			v=getVertexID(i, 2);
			p= new Pair<>(Math.min(u, v),Math.max(u, v));
			if (map.get(p)!=null)map.remove(p);else map.put(p, i);
			u=getVertexID(i, 1);
			p= new Pair<>(Math.min(u, v),Math.max(u, v));
			if (map.get(p)!=null)map.remove(p);else map.put(p, i);
		}
		if (err!=0)
			System.out.println("degenerated triangles "+err);
		System.out.println(map.size());
	}
	
//	private void reorderCycles(){
//		pt p = new pt();
//		pt.P();
//		for (int i=0;i<H.length;i++){
//			Set<Integer> s = new HashSet<Integer>();
//			int t = H[i];
//			while(t>=0){
//				s.add(t);
//				t=L[t];
//			}
//			int v=t;
//			t=H[i];
//			System.out.println(s+"   "+v);
//			for (Integer w: s){
//				int temp=neighbor(w, s);
//				if (temp!=H[i])
//					L[w]=temp;
//				else L[w]=v;
//			}
//		}
//		System.out.println("reodered");
//	}
//	public int neighbor(int ww,Set<Integer> set) {
//		int v= getVertexID(ww/3, (ww+1)%3);
////		int z = getVertexID(faceID, (i+2)%3);
////		double maxAngle = 2*Math.PI;
//		int x=ww;
//		for (Integer w:set){
//			if (w!=ww){
//				if (getVertexID(w/3, (w+1)%3)==v||getVertexID(w/3, (w+2)%3)==v){
//					x=w;
////					int p;
////					if (getVertexID(w/3, (w+1)%3)==v)p=getVertexID(w/3, (w+2)%3); else p= getVertexID(w/3, (w+1)%3);
////					double planeAngle = angle(G[z],G[u],G[v],G[p]);
////					if (planeAngle<maxAngle){maxAngle=planeAngle;x=w;}
//				}
//			}
//			
//		}
//		if (x==ww) throw new Error(""+ww);
//		return x/3;
//	}
}
