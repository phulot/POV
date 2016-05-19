package POV;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import Jcg.geometry.Point_3;
import Jcg.triangulations3D.Delaunay_3;
import Jcg.triangulations3D.TriangulationDSCell_3;
import Jcg.triangulations3D.TriangulationDSVertex_3;
import Triangulations.pt;

public class POVBuilder {

	/**
	 * create a random mesh by a delaunay3D and deleting some tetrahedrons 
	 * @param nbv : number of vertices
	 * @param prob : probability of keeping each tetrahedron
	 * @return 
	 */
	public static POV createRandomMesh(int nbv, float prob) {
		POV pov = new POV();
		Delaunay_3 del = new Delaunay_3();
		int N = nbv;
		pov.nv = nbv;
		int spacesize = 500000;
		for (int i = 0; i < N; i++) {
			Point_3 p = new Point_3((int) (Math.random() * spacesize ), (int) (Math.random() * spacesize ),
					(int) (Math.random() * spacesize));
			del.insert(p);

		}
		Collection<TriangulationDSCell_3<Point_3>> cells = del.finiteCells();
		ArrayList<TriangulationDSVertex_3<Point_3>> vertex = new ArrayList<TriangulationDSVertex_3<Point_3>>(
				del.finiteVertices());
		int k = 0;
		pov.maxnt = cells.size();
		pov.maxnf = 4*cells.size();
		pov.V=new Integer[4*cells.size()];
		for (TriangulationDSVertex_3<Point_3> v : vertex) {
			pov.G[k] = new pt(Float.valueOf("" + v.getPoint().x)/1000, Float.valueOf("" + v.getPoint().y)/1000,
					Float.valueOf("" + v.getPoint().z)/1000);
			k++;
		}
		int nbt = 0;
		for (TriangulationDSCell_3<Point_3> t : cells) {
			if (Math.random() < prob) {
				for (int i = 0; i < 4; i++)
					pov.V[4 * nbt + i] = vertex.indexOf(t.vertex(i));
				nbt++;
			}
		}
		pov.nt = nbt;
		pov.nf = 4*pov.nt;
//		reorderTetrahedrons();
		pov.createOtable();
		pov.orientMesh();
		return pov;
	}
	
	/*
	 * pov initiManual() { nv=5; nt=3; nf=18; // internal V[0]=0; O[0]=7;
	 * V[1]=1; O[1]=13; V[2]=2; O[2]=12; V[3]=3; O[3]=11; V[4]=1; O[4]=15;
	 * V[5]=2; O[5]=14; V[6]=3; O[6]=9; V[7]=4; O[7]=0; V[8]=1; O[8]=16; V[9]=0;
	 * O[9]=6; V[10]=2; O[10]=17; V[11]=4; O[11]=3; // external V[12]=5;
	 * O[12]=2; V[13]=5; O[13]=1; V[14]=5; O[14]=5; V[15]=5; O[15]=4; V[16]=5;
	 * O[16]=8; V[17]=5; O[17]=10; return this;} // makes a single tet mesh
	 */

	
	public static POV loadpov(String fn){
		return loadpov(fn, 1f);
	}

	/**
	 * to load a pov mesh
	 * @param fn : path
	 * @param mult : multiplication factor
	 * @param p : display
	 * @return the pov mesh
	 */
	public static POV loadpov(String fn, float mult) {
		try {
			POV pov = new POV();
			System.out.println("loading: " + fn);
			String[] ss = loadStrings(fn+".pov");
			int s = 0;
			pov.nv = Integer.valueOf(ss[s++]);
			System.out.println("nv=" + pov.nv);
			pov.nt = Integer.valueOf(ss[s++]);
			System.out.println("nt=" + pov.nt);
			pov.maxnt=pov.nt;
			pov.nf=4*pov.nt;
			pov.maxnv=pov.nv;
			pov.maxnf=4*pov.nt+100;
			pov.G = new pt[pov.maxnv]; 
			pov.declare();
			pov.V = new Integer[pov.maxnf];
			int[] O = new int[pov.maxnf];
			for (int k = 0; k < pov.nv; k++) {
				int i = k + s;
				String[] xy = ss[i].split(",");
				pov.G[k].setTo(mult*Float.valueOf(xy[0]), mult*Float.valueOf(xy[1]), mult*Float.valueOf(xy[2]));
			}
			for (int k = 0; k < pov.nf; k++) {
				int i = k + s + pov.nv;
				String[] VO = ss[i].split(",");
				pov.V[k] = Integer.valueOf(VO[0]);
				O[k] = Integer.valueOf(VO[1]);
				if (O[k]>=pov.nf) O[k]=k;
			}
			pov.setO(O);
//		pov.reorderTetrahedrons();
//		pov.createOtable();
//		pov.orientMesh();
//		ArrayList<Integer> l = pov.testIsManifold();
//		while (!l.isEmpty()){
//			pov.toManifold(l);
//			l = pov.testIsManifold();
//		}
			pov.checkMesh();
			System.out.println("done");
			return pov;
		} catch (Exception e){
			e.printStackTrace();
			return loadoldpov(fn,mult);
		}
	};
	
	/**
	 * to load a PV mesh (create the O table)
	 * @param fn : file path
	 */
	public static POV loadPV(String fn) {
		POV pov = new POV();
		System.out.println("loading: " + fn);
		String[] ss = loadStrings(fn);
		int s = 0;
		pov.nv = Integer.valueOf(ss[s++]);
		System.out.println("nv=" +pov. nv);
//		pov.nf = Integer.valueOf(ss[s++]);
//		System.out.println("nf=" + pov.nf);
		pov.nt = Integer.valueOf(ss[s++]);
		System.out.println("nt=" + pov.nt);
		pov.nf=4*pov.nt;
		pov.maxnt=pov.nt;
		pov.maxnv=pov.nv;
		pov.maxnf=6*pov.nt;
		pov.G = new pt[pov.maxnv]; 
		pov.declare();
		pov.V = new Integer[pov.maxnf];
		for (int k = 0; k < pov.nv; k++) {
			int i = k + s;
			String[] xy = (ss[i].split(","));
			pov.G[k].setTo(Float.valueOf(xy[0]), Float.valueOf(xy[1]), Float.valueOf(xy[2]));
		}
		for (int k = 0; k < 4 * pov.nt; k++) {
			int i = k + s + pov.nv;
			pov.V[k] = Integer.valueOf(ss[i].split(",")[0]);
		}
//		pov.reorderTetrahedrons();
		pov.createOtable();
		pov.orientMesh();
//		Set<Integer> l = pov.testIsManifold();
//		while (!l.isEmpty()){
//			pov.toManifold(l);
//			l = pov.testIsManifold();
//		}
//		if (pov.checkMesh())
			pov.savepov(fn);
		System.out.println("done");
		return pov;
	}
	
	public static POV loadoldpov(String fn,float mult) {
		POV pov = new POV();
		System.out.println("loading: " + fn);
		String[] ss = loadStrings(fn+".pov");
		int s = 0;
		pov.nv = Integer.valueOf(ss[s++]);
		System.out.println("nv=" + pov.nv);
		pov.nf = Integer.valueOf(ss[s++]);
		System.out.println("nf=" + pov.nf);
		pov.nt = Integer.valueOf(ss[s++]);
		pov.nf=4*pov.nt;
		System.out.println("nt=" + pov.nt);
		pov.maxnt=pov.nt;
		pov.maxnv=pov.nv;
		pov.maxnf=4*pov.nt;
		pov.G = new pt[pov.maxnv]; 
		pov.declare();
		pov.V = new Integer[pov.maxnf];
		int[] O = new int[pov.maxnf];
		for (int k = 0; k < pov.nv; k++) {
			int i = k + s;
			String[] xy = ss[i].split(",");
			pov.G[k].setTo(Float.valueOf(xy[0]), Float.valueOf(xy[1]), Float.valueOf(xy[2]));
		}
		for (int k = 0; k < 4*pov.nt; k++) {
			int i = k + s + pov.nv;
			String[] VO = ss[i].split(",");
			pov.V[k] = Integer.valueOf(VO[0]);
			O[k] = Integer.valueOf(VO[1]);
			if (O[k]>=pov.nf) O[k]=k;
		}
		pov.setO(O);
//		pov.reorderTetrahedrons();
//		pov.createOtable();
//		pov.orientMesh();
//		Set<Integer> l = pov.testIsManifold();
//		while (!l.isEmpty()){
//			pov.toManifold(l);
//			l = pov.testIsManifold();
//		}
//		if (pov.checkMesh())
			pov.savepov(fn);
		System.out.println("done");
		return pov;
	};
	
	/**
	 * load a .ele file (need a .node file with the same name)
	 * save the new mesh as file.pov
	 * @param fn : file Path
	 * @param mult : multiplication factor
	 */
	public static POV loadele(String fn, float mult) {
		POV pov = new POV();
		System.out.println("loading: " + fn);
		String[] ss =loadStrings(fn + ".node");
		pov.nv = Integer.valueOf(ss[0].split(" ")[0]);
		System.out.println("nv=" + pov.nv);
		pov.maxnv=pov.nv;
		pov.G = new pt[pov.maxnv]; 
		pov.declare();
		int off = Integer.valueOf((" " + ss[1]).split(" +")[1]);
		for (int k = 0; k < pov.nv; k++) {
			String[] sss = (" " + ss[k + 1]).split(" +");
			pov.G[Integer.valueOf(sss[1]) - off].setTo(mult * Float.valueOf(sss[2]), mult * Float.valueOf(sss[3]),
					mult * Float.valueOf(sss[4]));
		}
		ss = loadStrings(fn + ".ele");
		pov.nt = Integer.valueOf(ss[0].split(" ")[0]);
		pov.maxnt=pov.nt;
		pov.maxnf=6*pov.nt+50;
		pov.nf=6*pov.nt;
		pov.V = new Integer[pov.maxnf];
		System.out.println("nt=" + pov.nt);
		for (int k = 0; k < pov.nt; k++) {
			String[] sss = (" " + ss[k + 1]).split(" +");
			for (int i = 0; i < 4; i++) {
				pov.V[4 * k + i] = Integer.valueOf(sss[i + 2]) - off;
			}
		}
//		pov.reorderTetrahedrons();
		pov.createOtable();
		pov.orientMesh();
		Set<Integer> l = pov.testIsManifold();
		while (!l.isEmpty()){
			pov.toManifold(l);
			l = pov.testIsManifold();
		}
		if (pov.checkMesh())
			pov.savepov(fn);
		System.out.println("done");
		return pov;
	}
	/**
	 * load a .sma file
	 * @param fn : file Path
	 * @param mult : multiplication factor
	 */
	public static POV loadsma(String fn, float mult) {
		POV pov = new POV();
		System.out.println("loading: " + fn);
		String[] ss = loadStrings(fn + ".sma");
		pov.nv = Integer.valueOf(ss[0].split(" ")[2]);
		pov.nt = Integer.valueOf(ss[1].split(" ")[2]);
		System.out.println("nv=" + pov.nv);
		System.out.println("nt=" + pov.nt);
		pov.maxnt=pov.nt;
		pov.maxnv=pov.nv;
		pov.maxnf=6*pov.nt+50;
		pov.nf=4*pov.nv;
		pov.G = new pt[pov.maxnv]; 
		pov.declare();
		pov.V = new Integer[pov.maxnf];
		int currt = 0;
		int currv = 0;
		System.out.print("loading...");
		for (int k = 4; k < ss.length; k++) {
			String[] sss = (ss[k]).split(" +");
			if (sss[0].equals("v")) {
				pov.G[currv].setTo(mult * Float.valueOf(sss[1]), mult * Float.valueOf(sss[2]),
						mult * Float.valueOf(sss[3]));
				currv++;
			}
			if (sss[0].equals("c")) {
				for (int j=0;j<4;j++){
					int i = Integer.valueOf(sss[j+1]);
					if (i > 0)
						pov.V[4 * currt+j] = i-1;
					else {
						pov.V[4 * currt+j] = currv + i;
					}
				}
				currt++;
			}
		}
		pov.cancelDoubleTets();
//		pov.reorderTetrahedrons();
		pov.createOtable();
		pov.orientMesh();
		Set<Integer> l = pov.testIsManifold();
		while (!l.isEmpty()){
			pov.toManifold(l);
			l = pov.testIsManifold();
		}
		if (pov.checkMesh())
			pov.savepov(fn);
		System.out.println("done");
		return pov;
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
