package subsurface;
import java.util.TreeSet;

import POV.*;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

public class subSurface {
	/**
	 * pov mesh
	 */
	private POV pov;
	/** starting corner for operations */
	public int startingcorner;
	/** mark a subset of faces */
	public boolean[] marked;
	/** mark a subset of tetrahedrons */
	boolean[] mm;
	/** number of marked faces of a vertex related to marked[] */
	int[] vertexmarked;
	/** mark some tetradrons to show */
	boolean[] show;
	/** mark vertices to show them */
	boolean[] showv;
	double[] tetraType;
	double perf;
	String tree;
	public SurfaceCreation s;
	String name;

	public POV getPov() {return pov;}
	/**
	 * create a subsurface instance from the display interface
	 * @param p : display interface
	 */
	subSurface(String name) {
		this.name=name;
		pov = new POV();
		startingcorner=0;
		resetArrays();
		tetraType = new double[19];
	}
	public subSurface(POV p,String name) {
		this.name=name;
		pov = p;
		startingcorner=0;
		resetArrays();
		tetraType = new double[19];
	}
	private void resetArrays() {
		s = new CreateSurfaceFILO(this,startingcorner,Integer.MAX_VALUE, name);
		marked = new boolean[pov.maxnf];
		mm = new boolean[pov.maxnt];
		vertexmarked = new int[pov.maxnv];
		show = new boolean[pov.maxnt];
		showv = new boolean[pov.maxnv];
	}
	/**
	 * create a subsurface instance from an existing one
	 * copy operation 
	 * @param p display interface
	 * @param s subsurface to be copied
	 */
	subSurface(subSurface s) {
		startingcorner=0;
		pov=s.pov;
		resetArrays();
		tetraType = new double[19];
	}
	
	/**
	 * return the number of marked tetradron 
	 * @param tets collection of tet ids
	 * @return number of marked tets
	 */
	int markedNumber(Collection<Integer> tets){
		int k=0;
		for (Integer t : tets){
			if (marked[4*t])
				k++;
		}
		return k;
	}
	/**
	 * mark a face
	 * @param f : face id
	 */
	void markFace(int f) {
		if (!marked[f]) {
			vertexmarked[pov.v(3*f)]++;
			vertexmarked[pov.v(3*f+1)]++;
			vertexmarked[pov.v(3*f+2)]++;
		}
		marked[f] = true;
	}
	/**
	 * unmark a face
	 * @param f : face id
	 */
	void unmarkFace(int f) {
		if (marked[f]) {
			vertexmarked[pov.v(3*f)]--;
			vertexmarked[pov.v(3*f+1)]--;
			vertexmarked[pov.v(3*f+2)]--;
		}
		marked[f] = false;
	}
	/**
	 * mark a tetrahedron
	 * @param t : tet id
	 */
	void markTetrahedron(int t) {
		markFace(4 * t);
		markFace(4 * t + 1);
		markFace(4 * t + 2);
		markFace(4 * t + 3);
	}
	/**
	 * unmark a tetrahedron
	 * @param t : tet id
	 */
	void unmarkTetrahedron(int t) {
		unmarkFace(4 * t);
		unmarkFace(4 * t + 1);
		unmarkFace(4 * t + 2);
		unmarkFace(4 * t + 3);
	}

	/**
	 * compute the number of unmarked tetradra around c's vertex
	 * the border count for one
	 * @param c : corner id
	 * @return number of unmarked neighbors
	 */
	int unmarkedVertexNeighborsNbr(int c, boolean mar) {
		int k = 0;
		if (mar) {
			for (Integer t : pov.vertexNeighbors(c)) {
				if (t == -1)
					k++;
				else if (!marked[4*t])
					k++;
			}
			return k;
		} else {
			for (Integer t : pov.vertexNeighbors(c)) {
				if (t == -1)
					k++;
				else if (!mm[t])
					k++;
			}
			return k;
		}
	}
	/**
	 * return if an edge is marked (marked[]), tetradra test, not face 
	 * @param c : corner id that define the edge (v(c),v(n(c)))
	 * @return
	 */
	boolean edgeIsMarked(int c) {
		for (Integer t : pov.edgeNeighbors(c))
			if (t!=-1)
				if (marked[4*t]||marked[4*t+1]||marked[4*t+2]||marked[4*t+3])
					return true;
		return false;
	}
	
	/**
	 * compute the number of marked neighbors of an edge
	 * @param c : corner id of the edge
	 * @return number of marked neighbors 
	 */
	int EdgeNbrMarkedNeighbors(int c){
		int k=0;
		for (Integer t : pov.edgeNeighbors(c))
			if (t!=-1&&marked[4*t])
				k++;
		return k;
	}
	/**
	 * return if an edge is interior i.e. if all his neighbors are marked
	 * @param c : corner id
	 * @return
	 */
	boolean edgeIsInterior(int c) {
		for (Integer t : pov.edgeNeighbors(c)){
			if (t!=-1){
				if (!marked[4*t]||!marked[4*t+1]||!marked[4*t+2]||!marked[4*t+3])
					return false;
				for (int i =0;i<4;i++){
					if (pov.V[4*t+i]!=pov.v(c)&&pov.V[4*t+i]!=pov.v(pov.n(c)))
						try {
							pov.O(4*t+i);
						} catch (BorderFaceException e) {
							return false;
						}
				}
			}
			else return false;
		}
		return true;
	}
	/**
	 * return if an edge is marked in m 
	 * @param c : corner id that define the edge (v(c),v(n(c)))
	 * @param m : array of length nt 
	 * @return
	 */
	boolean edgeIsMarked(int c, boolean[] m) {
		for (Integer t : pov.edgeNeighbors(c))
			if (t!=-1&&m[t])
				return true;
		return false;
	}
	/**
	 * return if a vertex is marked in m 
	 * @param c : corner id
	 * @param m : array of length nt
	 * @return
	 */
	boolean vertexIsMarked(int c, boolean[] m) {
		TreeSet<Integer> l = pov.vertexNeighbors(c);
		for (Integer t : l){
			if (t!=-1)
				if (m[t]){
					return true;
				}
			}
		return false;
	}
	/**
	 * return the nbr of marked tetrahedra around t (vertex neighborhood included) 
	 * @param t : tet id
	 * @return
	 */
	int isIsolated(int t) {
		int k = 0;
		for (int i = 0; i < 4; i++) {
			TreeSet<Integer> l = pov.vertexNeighbors(pov.V[4 * t + i]);
			for (Integer tet : l)
				if (tet!=-1)
					if (marked[4 * tet])
						k++;
		}
		return k;
	}

	/**
	 * display non marked points and their neighbors
	 */
	void displayirregularPoints() {
		int[] rel= new int[]{4,2,1,0};
		for (int v = 0; v < pov.nt; v++) {
			for (int j = 0; j < 4; j++) {
				if (vertexmarked[v] == 0) {
					int c = 4 * v + rel[j];
					for (Integer i : pov.vertexNeighbors(c)) {
						if (i < pov.nt && i >= 0) {
							showv[pov.V[4 * i]] = true;
							showv[pov.V[4 * i + 1]] = true;
							showv[pov.V[4 * i + 2]] = true;
							showv[pov.V[4 * i + 3]] = true;
							for (Integer t : pov.vertexNeighbors(12 * i))
								if (t != -1)
									show[t] = true;
							for (Integer t : pov.vertexNeighbors(12 * i + 1))
								if (t != -1)
									show[t] = true;
							for (Integer t : pov.vertexNeighbors(12 * i + 2))
								if (t != -1)
									show[t] = true;
							for (Integer t : pov.vertexNeighbors(12 * i + 3))
								if (t != -1)
									show[t] = true;
						}
					}
					// F=new pt(G[V[f]]);
					return;
				}
			}
		}
	}
	/**
	 * create a subsurface
	 * @param it : maximum iteration number
	 */
	public void createOneSurface(int it) {
		//int c= (int)(Math.random()*(4*nt));
		s = new CreateSurfaceFILO(this,startingcorner,it,name);
		System.out.println("creating the subsurface");
		System.out.println("mesh genus "+pov.computegenus());
		s.Create();
		divideMesh d = new divideMesh(this);
		for (POV pp : d.apartments){
			System.out.println("apartment genus "+pp.computegenus());
		}
		pov = d.apartments.get(0);
//		for (POV p : d.apartments){
//			p.peal();
//		}
//		s.p=d.apartments.get(1);
		perf=s.getPerf();
		System.out.println(perf);
		//s = new CreateSurfaceFromShell(this, 0);
	}
	
	public void temporaryTest(){
		double k=0;
		double notk=0;
		for (int i=0;i<12*pov.nt;i++){
			Set<Integer> set = pov.edgeNeighbors(i);
			boolean b =true;
			for (Integer t:set){
				if (t==-1) b=false;
				else if (b){
					for (int o = 0; o < 4; o++) {
						try {
							pov.O(4 * t + o);
						} catch (BorderFaceException e) {
							b=false;
						}
					}
				}
			}
			if (b) k+=1/(double)set.size();
			else if (set.contains(-1)) notk+=1/(double)(set.size()-1);
			else notk+=1/(double)(set.size());
		}
		System.out.println(k);
		System.out.println(notk);
//		HashMap<Integer,Integer> snakes = new HashMap<>();
//		LinkedList<Integer> l = new LinkedList<>();
//		for (int i=0;i<pov.nt;i++){
//			snakes.put(i, i);
//			l.add(i);
//		}
//		l.addLast(-1);
//		boolean mod=true;
//		boolean mod2=true;
//		while(!l.isEmpty()&&mod){
////			mod =false;
//			int head= l.pollFirst();
//			if (head==-1){mod=mod2;mod2=false;l.addLast(-1);}
//			else{
//				l.addLast(head);
//				Integer queue = snakes.get(head);
//				if (queue==null)l.remove((Integer)head);
//				else{
//					for (int o=0;o<4;o++){
//						try {
//							Integer v;
//							if ((v=snakes.get(pov.tetraFromFace(pov.O(4*head+o))))!=null){
//								snakes.put(queue,snakes.get(v));
//								snakes.put(snakes.get(v),queue);
//								l.addLast(snakes.get(v));
//								l.addLast(queue);
//								snakes.remove(v);
//								l.remove((Integer)v);
//								snakes.remove(head);o=10;
//								l.remove((Integer)head);
//								mod2 =true;
//							}
//						} catch (BorderFaceException e) {
//						}
//					}
//				}
//			}
//		}
//		System.out.println(snakes.size());
//	}
//		double k=0,sum=0,max=0;
//		int[] occurence= new int[20];
//		for (int i=0;i<12*pov.nt;i++){
//			Set<Integer> set = pov.edgeNeighbors(i);
//			if (set.contains(-1)){
////				System.out.println(set+" "+set.size());
//				max=Math.max(max, set.size());
//				occurence[set.size()-2]++;
//				k+=1/(double)(set.size()-1);sum++;
//			}
//		}
//		System.out.println("average number of neighbours on border "+(sum/(double)k));
//		for (int i=0;i<max;i++)System.out.println(occurence[i]);
	}
	/**
	 * create nbiter subsurface and take the best one (perf meaning)
	 * @param it : max iteration per surface
	 * @param nbiter : number of subsurfaces created with different starting points(random)
	 */
	public void createSurface(int it, int nbiter){
		double perf=0;
		for (int i=0;i<nbiter;i++){
			subSurface sub= new subSurface(this);
			sub.startingcorner = (int)(Math.random()*(4*pov.nt));
			sub.createOneSurface(it);
			if (sub.perf>perf){
				perf=sub.perf;
				this.marked=sub.marked;
				this.mm=sub.mm;
				this.vertexmarked=sub.vertexmarked;
				this.show=sub.show;
				this.showv=sub.showv;
				this.tetraType=sub.tetraType;
				this.perf=sub.perf;
				this.tree=sub.tree;
				this.s=sub.s;
			}
		}
		s.CreateRepresentation();
	}
	/**
	 * unmark all interior faces
	 */
	public void emptySurface(){
		for (int i=0;i<pov.nf;i++){
			try {
				if (marked[i]&&marked[pov.O(i)]){
					unmarkFace(i);
					unmarkFace(pov.O(i));
				}
			} catch (BorderFaceException e) {
			}
		}
	}
	/**
	 * 
	 * @param typeID : tet topology id
	 * @return related String representation 
	 */
	String getStringType(int typeID) {
		if (typeID == 0)
			return "E";
		if (typeID == 1)
			return "V";
		if (typeID == 2)
			return "A";
		if (typeID == 3)
			return "B";
		if (typeID == 4)
			return "C";
		if (typeID == 5)
			return "AB";
		if (typeID == 6)
			return "AC";
		if (typeID == 7)
			return "BC";
		if (typeID == 8)
			return "ABC";
		if (typeID == 9)
			return "a";
		if (typeID == 10)
			return "b";
		if (typeID == 11)
			return "c";
		if (typeID == 12)
			return "ab";
		if (typeID == 13)
			return "ac";
		if (typeID == 14)
			return "bc";
		if (typeID == 15)
			return "abc";
		if (typeID == 16)
			return "Aa";
		if (typeID == 17)
			return "Bb";
		return "Cc";
	}
	/**
	 * compute the topology stats of a tree
	 * @param t : tet tree
	 */
	void TreeStat(Tree t) {
		tetraType=new double[19];
		tree="";
		LinkedList<Tree> l = new LinkedList<Tree>();
		l.add(t);
		while (!l.isEmpty()) {
			Tree tt = l.poll();
			if (tt._1 != null)
				l.add(tt._1);
			if (tt._2 != null)
				l.add(tt._2);
			if (tt._3 != null)
				l.add(tt._3);
			tt.toStat();
		}
	}
	
//	void apartementTree(Tree t, boolean b) {
//		if (!pov.borderCorner(t.cell.cor)) {
//			try {
//				t.cell.A = mm[pov.tetraFromCorner(pov.o(pov.s(t.cell.cor)))];
//			} catch (BorderCornerException e) {
//				t.cell.A=false;
//			}
//			try {
//				t.cell.B = mm[pov.tetraFromCorner(pov.o(pov.s(pov.n(t.cell.cor))))];
//			} catch (BorderCornerException e) {
//				t.cell.B=false;
//			}
//			try {
//				t.cell.C = mm[pov.tetraFromCorner(pov.o(pov.s(pov.n(pov.n(t.cell.cor)))))];
//			} catch (BorderCornerException e) {
//				t.cell.C=false;
//			}
//			t.cell.a = t.cell.C || t.cell.B || edgeIsMarked(pov.n(pov.n(pov.s(pov.n(t.cell.cor)))), mm);// c.n.s.n.n
//			t.cell.b = t.cell.C || t.cell.A || edgeIsMarked(pov.n(pov.s(t.cell.cor)), mm);
//			t.cell.c = t.cell.A || t.cell.B || edgeIsMarked(pov.s(pov.s(t.cell.cor)), mm);
//			t.cell.v = t.cell.A || t.cell.B || t.cell.C || t.cell.a || t.cell.b || t.cell.c ;//|| vertexIsMarked(pov.n(pov.n(pov.s(t.cor))), mm);
//			mm[pov.tetraFromCorner(t.cell.cor)] = true;
//		}
//	}
	/**
	 * check if the surface is manifold
	 * can be optimized with a way to go from vertex to a corner
	 * @return
	 */
	public boolean checkSurface() {
		boolean b = true;
		int vertexfail = 0;
		int edgefail = 0;
		for (int v = 0; v < 12*pov.nt; v++) {
			if (!checkContinuity(pov.vertexNeighbors(v))) {
				b = false;
				vertexmarked[pov.v(v)] = -1;
				vertexfail++;
			}
		}
		//TODO non optimal many times the same operation
		for (int c = 0; c < 12 * pov.nt; c++) {
			if (!checkContinuity(pov.edgeNeighbors(c))) {
				b = false;
				edgefail++;
				vertexmarked[pov.v(c)] = -1;
			}
			;
		}
		HashSet<Integer> l = checkVertices();
		b = b && l.isEmpty();
		System.out.println("edgfails : " + edgefail);
		System.out.println("vertexfail : " + vertexfail);
		return b;
	}
	/**
	 * return the set of internal vertices corner ids 
	 * @return the set of internal vertices ids
	 */
	private HashSet<Integer> checkVertices() {//return the set of internal vertices
		HashSet<Integer> ll = new HashSet<Integer>();
		int[] rel= new int[]{4,2,1,0};
		for (int v = 0; v < pov.nv; v++) {
			for (int j=0;j<4;j++){
				TreeSet<Integer> l = pov.vertexNeighbors(4*pov.nt+rel[j]);
				boolean b = true;
				for (Integer k : l) {
					if (k==-1) b=false;
					else b &= marked[4 * k];
				}
				if (b)
					ll.add(v);
			}
		}
		return ll;
	}
	/**
	 * check if the collection is face connected 
	 * @param col : tet ids collection
	 * @return is face connected?
	 */
	boolean checkContinuity(Collection<Integer> col) {
		ArrayList<Integer> l = new ArrayList<Integer>();//unmarked tets
		for (Integer i : col) {
			if (i!=-1)
			if (!marked[4 * i]) {
				l.add(i);
			}
			if (i==-1) l.add(i);
		}
		if (!l.isEmpty())
			col.removeAll(l);//marked tets
		if (col.isEmpty() || col.size() == 1)
			return true;
		boolean mod = isFaceConnected(col);
		if (l.isEmpty())
			return mod;
		return mod&&isFaceConnected(l);
	}
	/**
	 * return if the collection is face connected
	 * @param col : collection of tet ids
	 * @return is col face connected?
	 */
	private boolean isFaceConnected(Collection<Integer> col) {
		TreeSet<Integer> l0 = new TreeSet<Integer>();
		if (col.isEmpty())return true;
		l0.add(col.iterator().next());
		ArrayList<Integer> l;
		boolean mod=true;
		while (!col.isEmpty() && mod) {
			l = new ArrayList<Integer>();
			mod = false;
			for (Integer i : col) {
				if (i!=-1){
					try {
						if (l0.contains(pov.tetraFromFace(pov.O(4 * i)))) {
							mod = true;
							l0.add(i);
							l.add(i);
						} else if (l0.contains(pov.tetraFromFace(pov.O(4 * i + 1)))) {
							mod = true;
							l0.add(i);
							l.add(i);
						} else if (l0.contains(pov.tetraFromFace(pov.O(4 * i + 2)))) {
							mod = true;
							l0.add(i);
							l.add(i);
						} else if (l0.contains(pov.tetraFromFace(pov.O(4 * i + 3)))) {
							mod = true;
							l0.add(i);
							l.add(i);
						}
					} catch (BorderFaceException e) {
//						if (l0.contains(-1)) l0.add(i);
//						else if (l0.contains(i)) l0.add(-1);
					}
				}
			}
			if (!l.isEmpty())
				col.removeAll(l);
		}
		return col.isEmpty();
	}
	
}// end of subSurface class

class Cell {
	int cor;
	Boolean A, B, C, a, b, c, v=false;
	
	Cell(int cor,subSurface s){
		this.cor=cor;
		POV p= s.getPov();
		if (!p.borderCorner(cor)) {
			try {
				A = s.mm[p.tetraFromCorner(p.o(p.s(cor)))];
			} catch (BorderCornerException e) {
				A=null;
			}
			try {
				B = s.mm[p.tetraFromCorner(p.o(p.s(p.n(cor))))];
			} catch (BorderCornerException e) {
				B=null;
			}
			try {
				C = s.mm[p.tetraFromCorner(p.o(p.s(p.n(p.n(cor)))))];
			} catch (BorderCornerException e) {
				C=null;
			}
			a = (C!=null&&C) || (B!=null&&B) || s.edgeIsMarked(p.n(p.n(p.s(p.n(cor)))), s.mm);// c.n.s.n.n
			b = (C!=null&&C) || (A!=null&&A) || s.edgeIsMarked(p.n(p.s(cor)), s.mm);
			c = (A!=null&&A) || (B!=null&&B) || s.edgeIsMarked(p.s(p.s(cor)), s.mm);
			v = (A!=null&&A) || (B!=null&&B) || (C!=null&&C) || a || b || c || s.vertexIsMarked(p.n(p.n(p.s(cor))), s.mm);
		}
		else{
			System.err.println("border");
		}
	}
	
	void refresh(subSurface s){
		POV p= s.getPov();
		if (!p.borderCorner(cor)) {
			try {
				A = s.mm[p.tetraFromCorner(p.o(p.s(cor)))];
			} catch (BorderCornerException e) {
				A=null;
			}
			try {
				B = s.mm[p.tetraFromCorner(p.o(p.s(p.n(cor))))];
			} catch (BorderCornerException e) {
				B=null;
			}
			try {
				C = s.mm[p.tetraFromCorner(p.o(p.s(p.n(p.n(cor)))))];
			} catch (BorderCornerException e) {
				C=null;
			}
			a = (C!=null&&C) || (B!=null&&B) || s.edgeIsMarked(p.n(p.n(p.s(p.n(cor)))), s.mm);// c.n.s.n.n
			b = (C!=null&&C) || (A!=null&&A) || s.edgeIsMarked(p.n(p.s(cor)), s.mm);
			c = (A!=null&&A) || (B!=null&&B) || s.edgeIsMarked(p.s(p.s(cor)), s.mm);
			v = (A!=null&&A) || (B!=null&&B) || (C!=null&&C) || a || b || c || s.vertexIsMarked(p.n(p.n(p.s(cor))), s.mm);
		}
		else{
			System.err.println("border");
		}
	}
	
	public String toString(){
		String s = "";
		if (A==null||A)
			s += "A";
		if (B==null||B)
			s += "B";
		if (C==null||C)
			s += "C";
		if (!(B==null||B) && !(C==null||C) && a)
			s += "a";
		if (!(A==null||A) && !(C==null||C) && b)
			s += "b";
		if (!(B==null||B) && !(A==null||A) && c)
			s += "c";
		if (!(A==null||A) && !(B==null||B) && !(C==null||C) && !a && !b && !c && v)
			s = "V";
		if (!v)
			s = "E";
		return s;
	}
	public int GetType() {
		String ss = toString();
		if (ss.equals("E"))
			return 0;
		if (ss.equals("V"))
			return 1;
		if (ss.equals("A"))
			return 2;
		if (ss.equals("B"))
			return 3;
		if (ss.equals("C"))
			return 4;
		if (ss.equals("AB"))
			return 5;
		if (ss.equals("BC"))
			return 6;
		if (ss.equals("AC"))
			return 7;
		if (ss.equals("ABC"))
			return 8;
		if (ss.equals("a"))
			return 9;
		if (ss.equals("b"))
			return 10;
		if (ss.equals("c"))
			return 11;
		if (ss.equals("ab"))
			return 12;
		if (ss.equals("bc"))
			return 13;
		if (ss.equals("ac"))
			return 14;
		if (ss.equals("abc"))
			return 15;
		if (ss.equals("Aa"))
			return 16;
		if (ss.equals("Bb"))
			return 17;
		if (ss.equals("Cc"))
			return 18;
		return -1;
	}
	
	public String borderToString(){
		String s = "";
		if (A!=null&&A)
			s += "A";
		if (B!=null&&B)
			s += "B";
		if (C!=null&&C)
			s += "C";
		if (!(B!=null&&B) && !(C!=null&&C) && a)
			s += "a";
		if (!(A!=null&&A) && !(C!=null&&C) && b)
			s += "b";
		if (!(B!=null&&B) && !(A!=null&&A) && c)
			s += "c";
		if (!(A!=null&&A) && !(B!=null&&B) && !(C!=null&&C) && !a && !b && !c && v)
			s = "V";
		if (!v)
			s = "E";
		return s;
	}
}



class ApartmentTree extends Tree {
	ApartmentTree father,_1,_2,_3;
	static int[] faceNumber(){ return new int[]{2,2,0,0,0,-2,-2,-2,-4,2,2,2,2,2,2,0,0,0,0};}
	ApartmentTree(int cc, Tree t, subSurface sur) {
		super(cc, t, sur);
		// TODO Auto-generated constructor stub
	}

	@Override
	ApartmentTree next() {
		int cor=cell.cor;
		try {
			if (_1 == null && cell.A!=null&&!cell.A && !s.mm[s.getPov().tetraFromCorner(s.getPov().o(s.getPov().s(cor)))] && !s.marked[s.getPov().faceFromCorner(s.getPov().o(s.getPov().s(cor)))]
					&& !s.getPov().borderCorner(s.getPov().o(s.getPov().s(cor)))) {
				_1 = new ApartmentTree(s.getPov().o(s.getPov().s(cor)), this,s);
				return _1;
			}
		} catch (BorderCornerException e) {
		}
		try {
			if (_2 == null && cell.B!=null&&!cell.B && !s.mm[s.getPov().tetraFromCorner(s.getPov().o(s.getPov().s(s.getPov().n(cor))))] && !s.marked[s.getPov().faceFromCorner(s.getPov().o(s.getPov().s(s.getPov().n(cor))))]
					&& !s.getPov().borderCorner(s.getPov().o(s.getPov().s(s.getPov().n(cor))))) {
				_2 = new ApartmentTree(s.getPov().o(s.getPov().s(s.getPov().n(cor))), this,s);
				return _2;
			}
		} catch (BorderCornerException e) {
		}
		try {
			if (_3 == null && cell.C!=null&&!cell.C && !s.mm[s.getPov().tetraFromCorner(s.getPov().o(s.getPov().s(s.getPov().n(s.getPov().n(cor)))))] && !s.marked[s.getPov().faceFromCorner(s.getPov().o(s.getPov().s(s.getPov().n(s.getPov().n(cor)))))]
					&& !s.getPov().borderCorner(s.getPov().o(s.getPov().s(s.getPov().n(s.getPov().n(cor)))))) {
				_3 = new ApartmentTree(s.getPov().o(s.getPov().s(s.getPov().n(s.getPov().n(cor)))), this,s);
				return _3;
			}
		} catch (BorderCornerException e) {
		}
		if (father == null)
			return null;
		return father.next();
	}
	
	
}
