package Tests;

import static org.junit.Assert.*;

import org.junit.Test;


import POV.BorderFaceException;
import POV.POV;
import POV.povBuilder;
import Triangulations.Vlist;
import Triangulations.Triangulation;

public class TriangulationTest {

	@Test
	public void test() {
		POV p = povBuilder.loadpov("data/pts4");
		int g = p.computegenus();
		int nf= 2*p.nv+4*(g-1);
		int[] V = new int[3*nf];
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
		Triangulation t = new Vlist(p.G, V);
		test1(t);
		test2(t);
//		fail("Not yet implemented");
	}

	private void test1(Triangulation t) {
		int k=0;
		assert(t.getVertexID(11, 2)==0);
		for (int v = 0;v<t.sizeOfVertices();v++)
			for (int i : t.incidentFaces(v)){
				k++;
				boolean b=false;
				String s="";
				for (int j=0;j<3;j++){
					if (t.getVertexID(i, j)==v)b=true;
					s+="   "+t.getVertexID(i, j)+"/"+(3*i+j);
				}
				assertTrue(""+v+s, b);
			}
		assert (k==3*t.sizeOfFaces());
	}
	
	private void test2(Triangulation t) {
		for (int f =0;f<t.sizeOfFaces();f++){
			for (int i=0;i<3;i++){
				boolean b;
				try {
					t.neighbor(i, f);
					b=true;
				}
				catch (Error e){
					b=false;
				}
				assert b;
			}
		}
	}
	
}
