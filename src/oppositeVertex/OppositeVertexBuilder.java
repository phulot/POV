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
import oppositeVertex.OppositeVertex.Face;
import oppositeVertex.OppositeVertex.Tet;
import Triangulations.*;

public class OppositeVertexBuilder {
	
	public static POV toPOV(OppositeVertex op){
		POV p = new POV();
		p.nv=op.border.sizeOfVertices();
		p.G = new pt[p.nv];
		CornerBasedTriangulation t = (CornerBasedTriangulation) op.border;
		p.G = t.G;
		op.buildTetIds();
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
				Tet tet = op.new Tet().fromInt(id);
				p.V[4*map.get(id)+k]=tet.Vertex(k);
				Face o;
				try {
					o=op.opposite(op.new Face(tet,k));
						p.O[4*map.get(id)+k]=4*map.get(o.t.toInt())+(o.relf);
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
		op.maxfaces=(nf/1000+1)*1000;
		op.maxTet=p.nt;
		ArrayList<Integer> T1 = new ArrayList<>();
		ArrayList<Integer> T2 = new ArrayList<>();
		ArrayList<Integer> T3 = new ArrayList<>();
		ArrayList<Integer> O1 = new ArrayList<>();
		int l = 0;
		for (int i=0;i<p.nt;i++){
			int border = 0;
			int b1=-1,b2=-1,b3=-1;//border indices
			int i1=-1,i2=-1,i3=-1;//interior indices
			for (int k=0;k<4;k++){
				if (p.O[4*i+k]!=4*i+k){
					if (i1==-1)i1=4*i+k;else if (i2==-1)i2=4*i+k;else i3=4*i+k;
				}
				else{
					border++;if (b1==-1)b1=4*i+k;else if (b2==-1)b2=4*i+k;else b3=4*i+k;
				}
			}
			if (border==1){
//				if (b1%2==0){
					T1.add(p.V[i1]);
					T1.add(p.V[i2]);
					T1.add(p.V[i3]);
					O1.add(p.V[b1]);
//				}
//				else{
//					T1.add(p.V[4*i+((b1+1)%4)]);
//					T1.add(p.V[4*i+((b1+3)%4)]);
//					T1.add(p.V[4*i+((b1+2)%4)]);
//					O1.add(p.V[4*i+b1]);
//				}
			}
			if (border==2){
				T2.add(p.V[b1]);
				T2.add(p.V[i1]);
				T2.add(p.V[i2]);
				
				T2.add(p.V[b2]);
				T2.add(p.V[i2]);
				T2.add(p.V[i1]);
			}
			if (border==3){
				T3.add(p.V[i1]);
				T3.add(p.V[b1]);
				T3.add(p.V[b2]);
				
				T3.add(p.V[i1]);
				T3.add(p.V[b2]);
				T3.add(p.V[b3]);
				
				T3.add(p.V[i1]);
				T3.add(p.V[b3]);
				T3.add(p.V[b1]);
			}
		}
		op.nbrT1=T1.size()/3;
		op.nbrT2=T2.size()/3;
		op.nbrT3=T3.size()/3;
		T1.addAll(T2);
		T1.addAll(T3);
		Integer[] V= T1.toArray(new Integer[0]);
		op.oppositeVertex1=O1.toArray(new Integer[0]);
		System.out.println("no interior vertices "+(nf==l));
//		op.border = new CornerBasedTriangulation(p.G, V, l, p.nv);
		Vlist vl = new Vlist(p.G, V);
		vl.checkTriangulation();
		op.border=vl;
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
		System.out.println("oppositeFaces done");
		op.buildTetIds();
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
