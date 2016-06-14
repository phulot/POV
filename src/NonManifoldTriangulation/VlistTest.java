package NonManifoldTriangulation;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import Jcg.geometry.Pair;
import POV.BorderFaceException;
import POV.POV;
import POV.POVBuilder;
import Triangulations.pt;

public class VlistTest {

	@Test
	public void test() {
		POV p = POVBuilder.loadpov("data/gear");
		int g = p.computegenus();
		int nf= 2*p.nv+4*(g-1);
		Integer[] V = new Integer[3*nf];
		int l = 0;
		for (int i=0;i<p.nt;i++){
			for (int k=0;k<4;k++){
				try {
					p.O(4*i+k);
				} catch (BorderFaceException e) {
					if (k%2==0){
						V[3*l] = p.V[4*i+((k+1)%4)];
						V[3*l+1] = p.V[4*i+((k+2)%4)];
						V[3*l+2] = p.V[4*i+((k+3)%4)];
						l++;
					}
					else{
						V[3*l] = p.V[4*i+((k+1)%4)];
						V[3*l+2] = p.V[4*i+((k+2)%4)];
						V[3*l+1] = p.V[4*i+((k+3)%4)];
						l++;
					}
				}
			}
		}
		System.out.println("no interior vertices "+(nf==l));
		Vlist t = new Vlist(p.G, V);
		testCorrect(V);
		testConstructor(p.G, V);
		testgetCorner(t);
//		testNeighbor(t);
	}
	
	void testNeighbor(Vlist t){
		boolean[] tab = new boolean[t.L.length/3];
		for (int i=0;i<tab.length;i++){
			tab[i]=t.clockWise(i,Vlist.E);
			System.out.print(tab[i]+",");
		}
		System.out.println();
		for (int i=0;i<tab.length;i++){
			System.out.println(t.vw(3*i)+", "+t.vw(3*i+1)+", "+t.vw(3*i+2));
		}
		for (int c=0;c<2*t.L.length;c++){
			int c1 = t.neighbor(c);
			boolean b=(tab[Vlist.tc(c)]^tab[Vlist.tc(c1)])==(Vlist.isfVl(c)^Vlist.isfVl(c1));
			System.out.println(t.vw(Vlist.w(c))+"  "+t.vw(Vlist.w(Vlist.nc(c)))+"  "+t.vw(Vlist.w(Vlist.pc(c))));
			System.out.println(t.vw(Vlist.w(c1))+"  "+t.vw(Vlist.w(Vlist.nc(c1)))+"  "+t.vw(Vlist.w(Vlist.pc(c1))));
			System.out.println(c+"  " + c1+"  "+b+ " tab "+tab[Vlist.tc(c)]+" "+tab[Vlist.tc(c1)]+" f "+Vlist.isfVl(c)+" " +Vlist.isfVl(c1));
			assertTrue((tab[Vlist.tc(c)]^tab[Vlist.tc(c1)])==(Vlist.isfVl(c)^Vlist.isfVl(c1)));
		}
	}
	
	void testgetCorner(Vlist t){
		for (int i=0;i<t.L.length;i++){
			assertTrue(Vlist.isfVl(t.getCorner(i, true)));
			assertFalse(Vlist.isfVl(t.getCorner(i, false)));
		}
	}
	
	void testConstructor(pt[] G, Integer[] V){
		Vlist t = new Vlist(G,V);
		for (int i = 0; i < V.length; i++){
			assert(t.vw(i)==V[i]);
		}
	}
	
	void testCorrect(Integer[] V){
		HashSet<Pair<Integer>> set= new HashSet<>();
		for (int i=0;i<V.length/3;i++){
			for (int j=0;j<3;j++){
				int a=V[3*i+j];
				int b=V[3*i+(j+1)%3];
				Pair<Integer> p = new Pair<Integer>(Math.min(a, b),Math.max(a,b));
				if (set.contains(p))set.remove(p);
				else set.add(p);
			}
		}
		System.out.println(set);
		assertTrue(set.isEmpty());
	}
}
