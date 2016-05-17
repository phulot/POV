package oppositeVertex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import POV.BorderFaceException;
import POV.POV;
import POV.vec;
import oppositeVertex.OppositeVertex.Tet;
import POV.pt;

public class Utils {
	/**
	 * gives on which side of the plane ABC is the points O
	 * @param A : point of the plane
 	 * @param B : point of the plane
	 * @param C : point of the plane
	 * @param O : point to test
	 * @return >0 if one side, <0 if other side, 0 if on the plane
	 */
	public static double Side(Point_3 A,Point_3 B,Point_3 C,Point_3 O){
		Vector_3 AB = (Vector_3) A.minus(B);
		Vector_3 AC = (Vector_3) A.minus(C);
		Vector_3 n = AB.crossProduct(AC);
		return (double)A.minus(O).innerProduct(n);
	}
	public static double Side(pt A,pt B,pt C,pt O){
		vec AB = vec.V(A,B);
		vec AC = vec.V(A,C);
		vec n = vec.N(AB, AC);
		return vec.dot(vec.V(A,O),n);
	}
	
	/**
	 * test if the two points O and V are on different sides of the plane ABC
	 * @param A : point of the plane
	 * @param B : point of the plane
	 * @param C : point of the plane
	 * @param O : point to test
	 * @param V : point to test
	 * @return 
	 */
	public static double oppositeSide(Point_3 A,Point_3 B,Point_3 C,Point_3 O,Point_3 V){
		return (Side(A,B,C,O)*Side(A, B, C, V));
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
		}
		return false;
	}

	public static double oppositeSide(pt A, pt B, pt C, pt O, pt V) {
		return (Side(A,B,C,O)*Side(A, B, C, V));
	}
	
	public static void removeOpFromPov(OppositeVertex op,POV p){
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
			p.removeTetrahedron(tab[l]);
		}
		s = new HashSet<>();
		for (Tet t : op.tetids){
			for (int i=0;i<p.nt;i++){
				int k=0;
				for (int k0=0;k0<4;k0++){
					for (int k1=0;k1<4;k1++){
						if (t.Vertex(k1)==p.V[4*i+k0])k++;
					}
				}
				if (k==4) s.add(i);
			}
		}
		tab = s.toArray(new Integer[0]);
		Arrays.sort(tab);
		for (int k=tab.length-1;k>=0;k--){
			p.removeTetrahedron(tab[k]);
		}
		System.out.println("done");
	}
}
