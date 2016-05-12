package oppositeVertex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import POV.*;
import Jcg.geometry.Pair;
import Jcg.geometry.Point_3;
import Jcg.triangulations2D.TriangulationDSFace_2;
import Jcg.triangulations2D.TriangulationDSVertex_2;
import Jcg.triangulations2D.TriangulationDS_2;

public class OppositeVertexBuilder {
	
	public static POV toPOV(OppositeVertex op){
		POV p = new POV();
		p.nv=op.border.sizeOfVertices();
		p.G = new pt[p.nv];
		JcgTriangulation t = (JcgTriangulation) op.border;
		for (int i=0;i<op.border.sizeOfVertices();i++){
			Point_3 pt = t.border.vertices.get(i).getPoint();
			p.G[i] = new pt();
			p.G[i].x= (float)(double)pt.x;
			p.G[i].y= (float)(double)pt.y;
			p.G[i].z= (float)(double)pt.z;
		}
		op.buildTetIds();
//		Set<Integer> IDS = op.allTetIDS();
//		System.out.println(IDS.size());
//		Integer[] IDStab = IDS.toArray(new Integer[0]);
		HashMap<Integer, Integer> map = new HashMap<Integer,Integer>();
		int kk=0;
		for (int i:op){
			map.put(i, kk++);
		}
		p.nt = kk-1;
		p.V = new Integer[4*p.nt];
		p.O = new int[4*p.nt];
		for (Integer id:op){
			for (int k=0;k<4;k++){
				p.V[4*map.get(id)+k]=op.Vertex(id, k);
				Integer o;
				try {
					o=op.opposite(4*id+k);
						p.O[4*map.get(id)+k]=4*map.get(o/4)+(o%4);
				} catch (BorderFaceException e) {
					p.O[4*map.get(id)+k]=4*map.get(id)+k;
				}
			}
		}
		
		return p;
	}
	
	public static OppositeVertex loadFromPOV(POV p){
		int g = p.computegenus();
		int nf= 2*p.nv+4*(g-1);
		OppositeVertex op = new OppositeVertex();
		op.interiorEdges = new HashMap<Integer,Set<Integer>>(p.nv);
		op.oppositeVertex = new int[nf];
//		ArrayList<int[]> neigh = new ArrayList<>();
		int[] V = new int[3*nf];
		int l = 0;
		p.createOtable();
		for (int i=0;i<p.nt;i++){
			for (int k=0;k<4;k++){
				try {
					p.O(4*i+k);
				} catch (BorderFaceException e) {
					if (k%2==0){
						V[3*l] = p.V[4*i+((k+1)%4)];
						V[3*l+1] = p.V[4*i+((k+2)%4)];
						V[3*l+2] = p.V[4*i+((k+3)%4)];
						op.oppositeVertex[l]=p.V[4*i+k];
						l++;
					}
					else{
						V[3*l] = p.V[4*i+((k+1)%4)];
						V[3*l+2] = p.V[4*i+((k+2)%4)];
						V[3*l+1] = p.V[4*i+((k+3)%4)];
						op.oppositeVertex[l]=p.V[4*i+k];
						l++;
					}
				}
			}
		}
		System.out.println(nf==l);
		op.border = new CornerBasedTriangulation(p.G, V, l, p.nv);
//		op.border = new JcgTriangulation( TriangulationDS_2(points, neigh.toArray(new int[0][0])));
		for (int i=0;i<12*p.nt;i++){
			Set<Integer> set = p.edgeNeighbors(i);
			boolean b =true;
			for (Integer t:set){
				if (t==-1){ b=false;break;}
				else if (b){
					for (int o = 0; o < 4; o++) {
						try {
							p.O(4 * t + o);
						} catch (BorderFaceException e) {
							b=false;break;
						}
					}
				}
			}
			if (b){
				Set<Integer> s0 = op.interiorEdges.get(p.v(i));
				if (s0==null) s0=new HashSet<>();
				s0.add(p.v(p.n(i)));
				op.interiorEdges.put(p.v(i), s0);
				s0 = op.interiorEdges.get(p.v(p.n(i)));
				if (s0==null) s0=new HashSet<>();
				s0.add(p.v(i));
				op.interiorEdges.put(p.v(p.n(i)), s0);
			}
		}
		op.buildOppositeFaces();
//		op.buildTetIds();
		System.out.println("OppositeVertex done");
		return op;
	}
	
	/**
	 * read a String File (not online algorithms -> bufferReader?
	 * @param name : path
	 * @return the file as a String Array, one entry per line
	 */
	static String[] loadStrings(String name) {
		ArrayList<String> l = new ArrayList<String>();
		try {
			BufferedReader fr = new BufferedReader(new FileReader(new File(name)));
			String line = fr.readLine();
			while (line != null) {
				l.add(line);
				line = fr.readLine();
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return l.toArray(new String[0]);
	}
	/**
	 * save the file as name.pov
	 * @param name
	 * @param inppov : write each entry as a new line
	 */
	static void saveStrings(String name, String[] inppov) {
		try {
			FileWriter fr = new FileWriter(new File(name + ".pov"));
			for (int i = 0; i < inppov.length; i++) {
				fr.write(inppov[i] + "\n");
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
