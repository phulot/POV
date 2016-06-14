package oppositeVertexApartements;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import Jcg.geometry.Pair;
import NonManifoldTriangulation.Vlist;
import POV.BorderFaceException;
import POV.POV;
import POV.POVBuilder;
import oppositeVertexApartements.OppositeVertex.Tet;

public class oppositeTest {

	@Test
	public void test() {
		POV p = POVBuilder.createRandomCubeMesh(1000, 1);
//		POV p = POVBuilder.loadpov("data/gear");
		OppositeVertex op = OppositeVertexBuilder.borderAndTips(p);
		testCorrect(op);
		test1(op);
		op = OppositeVertexBuilder.loadFromPOV(p);
		testToInt(op);
//		System.out.println(op.isATet(0));
		test3(op);
//		traversal(op);
	}

	/**
	 * test if tipvertices are coherent with neighbor op
	 * @param op
	 */
	void test1(OppositeVertex op){
		for (int f=0;f<op.oppositeVertexback.length;f++){
//			System.out.println(op.border.FaceToSting(f)+" "+op.oppositeVertexfront[f]+" "+op.oppositeVertexback[f]);
			if (op.oppositeVertexfront[f]!=-1){
				assertTrue(op.tipVertex(0, f, true)!=-1);
				assertTrue(op.tipVertex(1, f, true)!=-1);
				assertTrue(op.tipVertex(2, f, true)!=-1);
			}
			if (op.oppositeVertexback[f]!=-1){
				assertTrue(op.tipVertex(0, f, false)!=-1);
				assertTrue(op.tipVertex(1, f, false)!=-1);
				assertTrue(op.tipVertex(2, f, false)!=-1);
			}
		}
	}
	/**
	 * tests if toInt(fromInt())==id
	 * @param op
	 */
	void testToInt(OppositeVertex op){
		for (Integer i:op){
			Tet t=op.new Tet().fromInt(i);
			for(int j=0;j<4;j++){
				assertTrue(op.new Face(t,j).equals(op.new Face().fromInt(op.new Face(t,j).toInt())));
			}
			assertTrue(t.toInt()==i);
		}
	}
	/**
	 * tests the correctness of the border (no boundary)
	 * @param op
	 */
	void testCorrect(OppositeVertex op){
		for (int i=0;i<6;i++){
			System.out.println(Vlist.nc(i)+"  "+i);
		}
		HashSet<Pair<Integer>> set= new HashSet<>();
		for (int i=0;i<op.border.sizeOfFaces();i++){
			for (int j=0;j<3;j++){
				int a=op.border.vw(3*i+j);
				int b=op.border.vw(3*i+(j+2)%3);
				Pair<Integer> p = new Pair<Integer>(Math.min(a, b),Math.max(a,b));
				if (set.contains(p))set.remove(p);
				else set.add(p);
			}
		}
		System.out.println(set);
		assertTrue(set.isEmpty());
		set= new HashSet<>();
		for (int i=0;i<op.border.sizeOfFaces();i++){
			for (int j=0;j<3;j++){
				int a=op.border.vw(Vlist.w(6*i+3+j));
				int b=op.border.vw(Vlist.w(6*i+3+(j+1)%3));
				Pair<Integer> p = new Pair<Integer>(Math.min(a, b),Math.max(a,b));
				if (set.contains(p))set.remove(p);
				else set.add(p);
				a=op.border.vw(Vlist.w(6*i+j));
				b=op.border.vw(Vlist.w(6*i+(j+1)%3));
				p = new Pair<Integer>(Math.min(a, b),Math.max(a,b));
				if (set.contains(p))set.remove(p);
				else set.add(p);
			}
		}
		System.out.println(set);
		assertTrue(set.isEmpty());
		System.out.println(op.border.L.length);
		int v=op.border.L[Vlist.w(120)];
		System.out.println(Vlist.w(96));
		while(v>=0) v=op.border.L[v];
		System.out.println(-v-1);
		v=op.border.H[-v-1];
		while(v>=0) {System.out.println(v);v=op.border.L[v];}
		for (int i=0;i<op.border.sizeOfFaces();i++)
			for (int j=0;j<6;j++)
				op.border.neighbor(j%3, i, (j/3)%2==0);
	}
	/**
	 * terversal on tets then on integers
	 * @param op
	 */
	void test3(OppositeVertex op){
		HashSet<Tet> set = new HashSet<>();
		HashSet<Tet> s = new HashSet<>();
		set.add(op.new Tet().fromInt(op.iterator().next()));
		while (!set.isEmpty()){
			HashSet<Tet> temp = new HashSet<>();
			for (Tet i : set){
//				System.out.println("build "+i);
				for (int o=0;o<4;o++){
					Tet t;
					try {
						t=op.opposite(op.new Face(i,o)).t;
						if (!s.contains(t)){
							assert t.equals(op.new Tet().fromInt(t.toInt()));
							s.add(t);
							temp.add(t);
						}
						
					} catch (BorderFaceException e) {
					}
				}
			}
			set=temp;
		}
		HashSet<Integer> seti = new HashSet<>();
		HashSet<Integer> si = new HashSet<>();
		seti.add(op.iterator().next());
		while (!seti.isEmpty()){
			HashSet<Integer> tempi = new HashSet<>();
			for (Integer i : seti){
				System.out.println("build "+i);
				for (int o=0;o<4;o++){
					Integer t;
					try {
						if (i>=0)
							t=op.opposite(op.new Face(op.new Tet().fromInt(i),o)).t.toInt();
						else {
//							System.out.println(i+"  "+(4*i-o));
							t=op.opposite(op.new Face().fromInt(4*i-o)).t.toInt();
						}
						if (!si.contains(t)){
							assert t.equals(op.new Tet().fromInt(t).toInt());
							si.add(t);
							tempi.add(t);
						}
						
					} catch (BorderFaceException e) {
					}
				}
			}
			seti=tempi;
		}
	}
	
	public Set<Integer> traversal(OppositeVertex DS){
		HashSet<Integer> set = new HashSet<>();
		HashSet<Integer> s = new HashSet<>();
		set.add(DS.iterator().next());
		while (!set.isEmpty()){
			HashSet<Integer> temp = new HashSet<>();
			for (Integer i : set){
				System.out.println(i);
				for (int o=0;o<4;o++){
					Integer t;
					try {
						if (i>0)
							t=DS.O(4*i+o)/4;
						else 
							t=DS.O(4*i-o)/4;
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
