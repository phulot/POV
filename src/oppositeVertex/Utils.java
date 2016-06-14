package oppositeVertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import POV.BorderFaceException;
import POV.POV;
import Triangulations.vec;
import Triangulations.Triangulation;
import oppositeVertex.OppositeVertex.Tet;
import Triangulations.pt;

public class Utils {
	public static double Side(vec n,vec t){
		return vec.dot(t,n);
	}
	public static vec normal(int v0,int v1, int v2, Triangulation t){
		vec AB = vec.V(t.G(v0),t.G(v1));
		vec AC = vec.V(t.G(v0),t.G(v2));
		vec n = vec.N(AB, AC);
		return n;
	}
	/**
	 * remove the vertices on one side of the plane (AiBiCi)
	 * the side is defined by op and vi : if op ==true removes the vertices that are on the opposite side of the plane relatively to vi
	 * create a copy
	 * @param s : set 
	 * @param vi : vertex id
	 * @param Ai : vertex id
	 * @param Bi : vertex id
	 * @param Ci : vertex id
	 * @param op : which side to remove
	 * @return a new set with only the vertices on the right side
	 */
	public static Set<Integer> removeSide(int sideReference, int a,Set<Integer> s,vec n, boolean removeOppositeSide, Triangulation t){
		Set<Integer> ss = new HashSet<Integer>();
		pt A=t.G(a);
		double b = Side(n,vec.V(t.G(sideReference),A));
		for (Integer i:s){
			double d=Utils.Side(n, vec.V(t.G(i),A))*b;
			if (d==0)ss.add(i);
			if ((d>0&&removeOppositeSide)||(d<0&&!removeOppositeSide))
				ss.add(i);
		}
		return ss;
	}
	
	
	public static boolean testVertexNeighbors(OppositeVertex op){
		int k=0;
		for (int v=0;v<op.getnv();v++){
			Set<Integer> s = op.VertexNeighbor(v);
			for (Integer u:s){
				if (!op.VertexNeighbor(u).contains(v)){
					k++;
				}
			}
		}
		System.out.println(k);
		return k==0;
	}
	
	public static boolean testVertexNeighbors(OppositeVertex op, POV p){
		for (int i=0;i<4*p.nt;i++){
			boolean b=false;
			int c= 12*(i/4)+p.rel[i%4];
			Set<Integer> s = p.vertexNeighbors(c);
			Set<Integer> s1 = new HashSet<>();
			for (Integer t:s){
				if (t==-1) b=true;
				if (t!=-1) 
				for (int k=0;k<4;k++){
					s1.add(p.v(12*t+p.rel[k]));
				}
			}
			if(!b) System.err.println("interior vertex");
			s1.remove(p.v(c));
			int v = p.v(c);
			Set<Integer> s0 = op.VertexNeighbor(v);
			if (s0.size()!=s1.size()){
				System.out.println(v);
				System.out.println("s0 : "+s0);
				System.out.println("s1 : "+s1);
				s1.removeAll(s0);
				System.out.println(op.VertexNeighbor(s1.iterator().next()).contains(v));;
			}
			else {s0.removeAll(s1);
				if (s0.size()!=0)
					System.out.println(s0);
			}
		}
		return false;
	}

	public static Boolean[] removeOpFromPov(OppositeVertex op,POV p){
		ArrayList<Boolean> res = new ArrayList<>();
		Set<Integer> s = new HashSet<>();
		for (int i=0;i<p.nt;i++){
			for (int k=0;k<4;k++){
				try {
					p.O(4*i+k);
				} catch (BorderFaceException e) {
					s.add(i);
				}
			}
		}
		Integer[] tab = s.toArray(new Integer[0]);
		Arrays.sort(tab);
		for (int l=(tab.length-1);l>=0;l--){
			res.add(false);
			p.removeTetrahedron(tab[l]);
		}
		s = new HashSet<>();
		for (Tet t : op.tetids){
			boolean b=false;
			for (int i=0;i<p.nt;i++){
				int k=0;
				for (int k0=0;k0<4;k0++){
					for (int k1=0;k1<4;k1++){
						if (t.Vertex(k1)==p.V[4*i+k0])k++;
					}
				}
				if (k==4) {p.removeTetrahedron(i);b=true;break;}
			}
			res.add(!b);
			if (!b) System.out.println(t);
		}
//		tab = s.toArray(new Integer[0]);
//		Arrays.sort(tab);
//		for (int k=tab.length-1;k>=0;k--){
//			p.removeTetrahedron(tab[k]);
//		}
		System.out.println("done");
		return res.toArray(new Boolean[0]);
	}
}
