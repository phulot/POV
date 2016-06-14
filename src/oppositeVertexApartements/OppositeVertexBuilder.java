package oppositeVertexApartements;

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
import NonManifoldTriangulation.*;
import Triangulations.pt;
import Triangulations.vec;
import oppositeVertexApartements.OppositeVertex.Face;
import oppositeVertexApartements.OppositeVertex.Tet;

public class OppositeVertexBuilder {
	
	public static POV toPOV(OppositeVertex op){
		POV p = new POV();
		p.nv=op.border.sizeOfVertices();
		p.G = new pt[p.nv];
		Vlist t = (Vlist) op.border;
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
		boolean[] m = Utils.computeHsurfaceT2(p);
		OppositeVertex op = borderAndTipsHamSurf(p,m);
		for (int i=0;i<12*p.nt;i++){
			Set<Integer> set = p.edgeNeighbors(i);
			boolean b =true;
			for (Integer t:set){
				if (t==-1){ b=false;break;}
				else if (b){
					for (int o = 0; o < 4; o++) {
						try {
							if (m[p.O(4 * t + o)/4]!=m[t]){
								b=false;
								break;
							}
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

	static OppositeVertex borderAndTipsHamSurf(POV p, boolean[] m) {
		int g = p.computegenus();
		int nf= 2*p.nv+4*(g-1);
		OppositeVertex op = new OppositeVertex();
		op.interiorEdges = new HashMap<Integer,Set<Integer>>(p.nv);
		op.maxTet=p.nt;
		ArrayList<Integer> T1 = new ArrayList<>();
		ArrayList<Integer> T2 = new ArrayList<>();
		ArrayList<Integer> T3 = new ArrayList<>();
		ArrayList<Integer> Of1 = new ArrayList<>();
		ArrayList<Integer> Ob1 = new ArrayList<>();
		ArrayList<Integer> Of2 = new ArrayList<>();
		ArrayList<Integer> Ob2 = new ArrayList<>();
		ArrayList<Integer> Of3 = new ArrayList<>();
		ArrayList<Integer> Ob3 = new ArrayList<>();
		int l = 0;
		for (int i=0;i<p.nt;i++){
			for (int o=0;o<4;o++){
				int fo = p.O[4*i+o];
				if (fo==4*i+o){
					if (m[i]) {
						T1.add(p.V[4*i+(o+1)%4]);
						T1.add(p.V[4*i+(o+2)%4]);
						T1.add(p.V[4*i+(o+3)%4]);
						Of1.add(p.V[4*i+o]);
						Ob1.add(-1);
					}
					else {
						T2.add(p.V[4*i+(o+1)%4]);
						T2.add(p.V[4*i+(o+2)%4]);
						T2.add(p.V[4*i+(o+3)%4]);
						Of2.add(-1);
						Ob2.add(p.V[4*i+o]);
					}
				}
				if (m[i]&&!m[fo/4]){
					T3.add(p.V[4*i+(o+1)%4]);
					T3.add(p.V[4*i+(o+2)%4]);
					T3.add(p.V[4*i+(o+3)%4]);
					Of3.add(p.V[4*i+o]);
					Ob3.add(p.V[fo]);
				}
			}
		}
		Of3.addAll(Of2);Of3.addAll(Of1);
		Ob3.addAll(Ob2);Ob3.addAll(Ob1);
		T3.addAll(T2);T3.addAll(T1);
		Integer[] V= T3.toArray(new Integer[0]);
		op.oppositeVertexfront=Of3.toArray(new Integer[0]);
		op.oppositeVertexback=Ob3.toArray(new Integer[0]);
		System.out.println("size "+op.oppositeVertexback.length);
		op.maxfaces=(op.oppositeVertexback.length/1000+1)*1000;
		System.out.println(T1);
		System.out.println(Of1);
		System.out.println(Ob1);
//		op.border = new CornerBasedTriangulation(p.G, V, l, p.nv);
		Vlist vl = new Vlist(p.G, V);
		vl.checkTriangulation();
		op.border=vl;
		return op;
	}
	
	static OppositeVertex borderAndTips(POV p) {
		int g = p.computegenus();
		int nf= 2*p.nv+4*(g-1);
		OppositeVertex op = new OppositeVertex();
		op.interiorEdges = new HashMap<Integer,Set<Integer>>(p.nv);
		op.maxfaces=(nf/1000+1)*1000;
		op.maxTet=p.nt;
		ArrayList<Integer> T1 = new ArrayList<>();
		ArrayList<Integer> Of = new ArrayList<>();
		ArrayList<Integer> Ob = new ArrayList<>();
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
				T1.add(p.V[i1]);
				T1.add(p.V[i2]);
				T1.add(p.V[i3]);
//				if (OppositeVertex.isFront(p.V[i1], p.V[i2], p.V[i3], p.V[b1], p.G, Vlist.E)){
//				if (Vlist.clockWise(p.G[p.V[i1]], p.G[p.V[i2]], p.G[p.V[i3]],  p.G[p.V[b1]])){
					Of.add(p.V[b1]);
					Ob.add(-1);
//				} else {
//					Ob.add(p.V[b1]);
//					Of.add(-1);
//				}
			}
			if (border == 2) {
				T1.add(p.V[b1]);
				T1.add(p.V[i1]);
				T1.add(p.V[i2]);
//				if (OppositeVertex.isFront(p.V[b1], p.V[i1], p.V[i2], p.V[b2], p.G, Vlist.E)) {
//				if (Vlist.clockWise(p.G[p.V[b1]], p.G[p.V[i1]], p.G[p.V[i2]],  p.G[p.V[b2]])){
					Of.add(p.V[b2]);
					Ob.add(-1);
//				} else {
//					Ob.add(p.V[b2]);
//					Of.add(-1);
//				}

				T1.add(p.V[b2]);
				T1.add(p.V[i2]);
				T1.add(p.V[i1]);
//				if (OppositeVertex.isFront(p.V[b2], p.V[i2], p.V[i1], p.V[b1], p.G, Vlist.E)) {
//				if (Vlist.clockWise(p.G[p.V[b2]], p.G[p.V[i2]], p.G[p.V[i1]],  p.G[p.V[b1]])){
					Of.add(p.V[b1]);
					Ob.add(-1);
//				} else {
//					Ob.add(p.V[b1]);
//					Of.add(-1);
//				}
			}
			if (border == 3) {
				T1.add(p.V[i1]);
				T1.add(p.V[b1]);
				T1.add(p.V[b2]);
//				if (OppositeVertex.isFront(p.V[i1], p.V[b1], p.V[b2], p.V[b3], p.G, Vlist.E)) {
//				if (Vlist.clockWise(p.G[p.V[i1]], p.G[p.V[b1]], p.G[p.V[b2]], p.G[p.V[b3]])){
					Of.add(p.V[b3]);
					Ob.add(-1);
//				} else {
//					Ob.add(p.V[b3]);
//					Of.add(-1);
//				}

				T1.add(p.V[i1]);
				T1.add(p.V[b2]);
				T1.add(p.V[b3]);
//				if (OppositeVertex.isFront(p.V[i1], p.V[b2], p.V[b3], p.V[b1], p.G, Vlist.E)) {
//				if (Vlist.clockWise(p.G[p.V[i1]], p.G[p.V[b2]], p.G[p.V[b3]], p.G[p.V[b1]])){
					Of.add(p.V[b1]);
					Ob.add(-1);
//				} else {
//					Ob.add(p.V[b1]);
//					Of.add(-1);
//				}

				T1.add(p.V[i1]);
				T1.add(p.V[b3]);
				T1.add(p.V[b1]);
//				if (OppositeVertex.isFront(p.V[i1], p.V[b3], p.V[b1], p.V[b2], p.G, Vlist.E)) {
//				if (Vlist.clockWise(p.G[p.V[i1]], p.G[p.V[b3]], p.G[p.V[b1]],  p.G[p.V[b2]])){
					Of.add(p.V[b2]);
					Ob.add(-1);
//				} else {
//					Ob.add(p.V[b2]);
//					Of.add(-1);
//				}
			}
		}
		Integer[] V= T1.toArray(new Integer[0]);
		op.oppositeVertexfront=Of.toArray(new Integer[0]);
		op.oppositeVertexback=Ob.toArray(new Integer[0]);
		System.out.println(T1);
		System.out.println(Of);
		System.out.println(Ob);
//		op.border = new CornerBasedTriangulation(p.G, V, l, p.nv);
		Vlist vl = new Vlist(p.G, V);
		vl.checkTriangulation();
		op.border=vl;
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
