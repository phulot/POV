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
		Set<Integer> IDS = op.allTetIDS();
		System.out.println(IDS.size());
		Integer[] IDStab = IDS.toArray(new Integer[0]);
		HashMap<Integer, Integer> map = new HashMap<Integer,Integer>();
		for (int i=0;i<IDStab.length;i++){
			map.put(IDStab[i], i);
		}
		p.nt = IDS.size();
		p.V = new Integer[4*p.nt];
		p.O = new int[4*p.nt];
		for (Integer id:IDS){
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
		OppositeVertex op = new OppositeVertex();
		op.interiorEdges = new HashMap<Integer,Set<Integer>>();
		op.oppositeVertex = new int[4*p.nv];
//		ArrayList<int[]> neigh = new ArrayList<>();
		int[] c = new int[3*p.nt];
		int l = 0;
		for (int i=0;i<p.nt;i++){
			for (int k=0;k<4;k++){
				try {
					p.O(4*i+k);
				} catch (BorderFaceException e) {
					if (k%2==0){
						c[3*l] = p.V[4*i+((k+1)%4)];
						c[3*l+1] = p.V[4*i+((k+2)%4)];
						c[3*l+2] = p.V[4*i+((k+3)%4)];
						op.oppositeVertex[l]=p.V[4*i+k];
						l++;
					}
					else{
						c[3*l] = p.V[4*i+((k+1)%4)];
						c[3*l+2] = p.V[4*i+((k+2)%4)];
						c[3*l+1] = p.V[4*i+((k+3)%4)];
						op.oppositeVertex[l]=p.V[4*i+k];
						l++;
					}
				}
			}
		}
		op.border = new cornerBased(p.G, c,l , p.nv);
//		op.border = new JcgTriangulation( TriangulationDS_2(points, neigh.toArray(new int[0][0])));
		op.buildOppositeFaces();
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
	
	 public static TriangulationDS_2<Point_3> TriangulationDS_2(Point_3[] points, int[][] neighbors) {
	    	System.out.print("Jcg - Creating triangulation DS... ");
			long startTime=System.nanoTime(), endTime; // for evaluating time performances
	    	TriangulationDS_2 t = new TriangulationDS_2<>();
			
	    	if(points==null) 
	    		throw new Error("error: null points");
	    	else if(neighbors==null || points[0]==null) 
	    		throw new Error("error: null vertices or null faces"); 	
	    	
	    	t.faces=new ArrayList<TriangulationDSFace_2<Point_3>>();
	    	t.vertices=new ArrayList<TriangulationDSVertex_2<Point_3>>();    
	    	
	    	for(int i=0;i<points.length;i++) {
	    		if(points[i]==null) throw new Error("null vertex error");
	    		TriangulationDSVertex_2<Point_3> v=new TriangulationDSVertex_2<Point_3>(points[i]);
	    		t.vertices.add(v);
	    	}
	    	
	    	// first pair (the key) represents the edges of the triangulation
	    	// second pair (the associated value) represents the two neighboring faces (sharing an edge)
	    	HashMap<Pair<Integer>, Pair<Integer>> edges=new HashMap<Pair<Integer>, Pair<Integer>>(t.vertices.size()*4, (float)0.75);
	    	    	
	    	for(int i=0;i<neighbors.length;i++) {
	    		int i0=neighbors[i][0], i1=neighbors[i][1], i2=neighbors[i][2];
	    		t.createFace((TriangulationDSVertex_2<Point_3>)t.vertices.get(i0), (TriangulationDSVertex_2<Point_3>)t.vertices.get(i1), (TriangulationDSVertex_2<Point_3>)t.vertices.get(i2), null, null, null);
	    		
	    		for(int j=0;j<3;j++) {
	    			int index1=neighbors[i][(j+1)%3];
	    			int index2=neighbors[i][(j+2)%3];
	    			Pair<Integer> edge;
	    			if(index1<=index2) edge=new Pair<Integer>(index1, index2);
	    			else edge=new Pair<Integer>(index2, index1);
	    			
	    			if(edges.containsKey(edge)==false) {
	    				Pair<Integer> face;
	    				if(index1<=index2) face=new Pair<Integer>(i, -1);
	    				else face=new Pair<Integer>(-1, i);
	    				edges.put(edge, face);
	    			}
	    			else {
	    				Pair<Integer> face=edges.get(edge);
	    				if(face.getFirst()==-1) face.setFirst(i);
	    				else face.setSecond(i);
	    			}
	    		}
	    	}
	    	
	    	//System.out.println("setting neighboring faces");
	    	for(int i=0;i<neighbors.length;i++) {
	    		TriangulationDSFace_2<Point_3> currentFace=(TriangulationDSFace_2<Point_3>) t.faces.get(i);
	    		for(int j=0;j<3;j++) {    		
	    			
	    			// setting vertex adjacent face
	    			TriangulationDSVertex_2<Point_3> v=(TriangulationDSVertex_2<Point_3>) t.vertices.get(neighbors[i][j]);
	    			v.setFace((TriangulationDSFace_2<Point_3>) t.faces.get(i));
	    			
	    			int index1=neighbors[i][(j+1)%3];
	    			int index2=neighbors[i][(j+2)%3];
	    			Pair<Integer> edge;
	    			if(index1<=index2) edge=new Pair<Integer>(index1, index2);
	    			else edge=new Pair<Integer>(index2, index1);
	    			
	    			if(edges.containsKey(edge)==false) 
	    				throw new Error("error: edge not found");
	    			else {
	    				Pair<Integer> face=edges.get(edge);
	    				if(face.getFirst()==-1 && face.getSecond()==-1)
	    					throw new Error("error: wrong adjacent faces");
	    				if(face.getFirst()==-1 || face.getSecond()==-1); // boundary edge
	    				else {
	    					int neighborFace=face.getSecond();
	    					if (neighborFace==i) neighborFace=face.getFirst();
	    					if(neighborFace<0 || neighborFace>=t.faces.size())
	    						throw new Error("error neighbor face index");
	    					currentFace.setNeighbor(j, (TriangulationDSFace_2<Point_3>) t.faces.get(neighborFace));
	    				}
	    			}
	    		}
	    	}
	    	endTime=System.nanoTime(); 
	    	double duration=(double)(endTime-startTime)/(double)1000000000.;
	    	System.out.println("done (time: "+duration+" seconds)");
			return t;
	    }
}
