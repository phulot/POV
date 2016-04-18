import java.util.TreeSet;
import java.util.concurrent.SynchronousQueue;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

class subSurface {
	/**
	 * pov mesh
	 */
	pov pov;
	/**
	 * diplay interface
	 */
	POVjava display;
	/**
	 * starting corner for operations 
	 */
	int startingcorner;
	/**
	 * mark a subset of faces
	 */
	boolean[] marked;
	/**
	 * mark a subset of tetrahedrons
	 */
	boolean[] mm;
	/**
	 * number of marked faces of a vertex 
	 * related to marked[]
	 */
	int[] vertexmarked;
	/**
	 * mark some tetradrons to show
	 */
	boolean[] show;
	/**
	 * mark vertices to show them
	 */
	boolean[] showv;
	double[] tetraType;
	double perf;
	String tree;
	SurfaceCreation s;

	/**
	 * create a subsurface instance from the display interface
	 * @param p : display interface
	 */
	subSurface(POVjava p) {
		pov = new pov(p);
		display=p;
		startingcorner=0;
		resetArrays();
		tetraType = new double[19];
	}
	subSurface(pov p) {
		pov = p;
		display=p.display;
		startingcorner=0;
		resetArrays();
		tetraType = new double[19];
	}
	private void resetArrays() {
		s = new CreateSurfaceFILO(this,startingcorner,Integer.MAX_VALUE);
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
	subSurface(POVjava p, subSurface s) {
		startingcorner=0;
		display=s.display;
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
	 * mark current corner's face
	 */
	void markCorner() {
		markFace(pov.faceFromCorner(pov.currentCorner));
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
	 * mark current corner's tet
	 */
	void markTetrahedron() {
		markTetrahedron(pov.tetraFromCorner(pov.currentCorner));
	}
	/**
	 * unmark current corner's tet
	 */
	void unmarkTetrahedron() {
		unmarkTetrahedron(pov.tetraFromCorner(pov.currentCorner));
	}
	/**
	 * mark all neighbors of current corner vertex
	 */
	void markNeighbours() {
		TreeSet<Integer> l = pov.vertexNeighbors(pov.currentCorner);
		for (Integer i : l)
			if (i!=-1)
				markTetrahedron(i);
	}
	/**
	 * unmark all neighbors of current corner vertex
	 */
	void unmarkNeighbours() {
		ArrayList<Integer> l = pov.edgeNeighbors(pov.currentCorner);
		for (Integer i : l)
			if (i!=-1)
				unmarkTetrahedron(i);
	}
	/**
	 * compute the number of unmarked tetradra around c's vertex
	 * @param c : corner id
	 * @return number of unmarked neighbors
	 */
	int unmarkedNeighborsNbr(int c) {
		int k = 0;
		for (Integer t : pov.vertexNeighbors(c)) {
			if (t==-1)k++;
			else if (!marked[4 * t])
				k++;
		}
		return k;
	}
	/**
	 * return if an edge is marked (marked[]), tetradra test, not face 
	 * @param c : corner id that define the edge (v(c),v(n(c)))
	 * @return
	 */
	boolean edgeIsMarked(int c) {
		for (Integer t : pov.edgeNeighbors(c))
			if (marked[4*t]||marked[4*t+1]||marked[4*t+2]||marked[4*t+3])
				return true;
		return false;
	}
	/**
	 * return if an edge is interior i.e. if all his neighbors are marked
	 * @param c : corner id
	 * @return
	 */
	boolean edgeIsInterior(int c) {
		for (Integer t : pov.edgeNeighbors(c)){
			if (!marked[4*t]||!marked[4*t+1]||!marked[4*t+2]||!marked[4*t+3])
				return false;
			for (int i =0;i<4;i++){
				if (pov.V[4*t+i]!=pov.v(c)&&pov.V[4*t+i]!=pov.v(pov.n(c)))
					if (pov.borderCorner(3*pov.O[4*t+i]))
						return false;
			}
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
			if (m[t])
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
	 * display marked walls (marked[])
	 */
	void showMarkedWall() {
		for (int t = 0; t < pov.nt; t++) {
			if (pov.V[4 * t] != -1&&!show[t]) {
				if (marked[4 * t])
					display.show(pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				if (marked[4 * t + 1])
					display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				if (marked[4 * t + 2])
					display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 3]]);
				if (marked[4 * t + 3])
					display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]]);
			}
		}
	}
	/**
	 * display show&marked walls (show[],marked[])
 	 */
	void showShowWall() {
		for (int t = 0; t < pov.nt; t++) {
			if (pov.V[4 * t] != -1&&show[t]) {
				if (marked[4 * t])
					display.show(pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				if (marked[4 * t + 1])
					display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				if (marked[4 * t + 2])
					display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 3]]);
				if (marked[4 * t + 3])
					display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]]);
			}
		}
	}
	/**
	 * display show walls (show[])
 	 */
	void showWallm() {
		for (int t = 0; t < pov.nt; t++) {
			if (pov.V[4 * t] != -1 && show[t]) {
				display.show(pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 3]]);
				display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]]);
			}
		}
	}
	/**
	 * display non marked points and their neighbors
	 */
	void displayirregularPoints() {
		for (int v = 0;v < pov.nv; v++) {
			if (vertexmarked[v] == 0) {
				int c = pov.cornerFromVertex(v);
				for (Integer i : pov.vertexNeighbors(c)) {
					if (i < pov.nt&&i>=0) {
						showv[pov.V[4 * i]] = true;
						showv[pov.V[4 * i + 1]] = true;
						showv[pov.V[4 * i + 2]] = true;
						showv[pov.V[4 * i + 3]] = true;
						for (Integer t : pov.vertexNeighbors(12 * i))
							if (t!=-1) show[t] = true;
						for (Integer t : pov.vertexNeighbors(12 * i + 1))
							if (t!=-1) show[t] = true;
						for (Integer t : pov.vertexNeighbors(12 * i + 2))
							if (t!=-1) show[t] = true;
						for (Integer t : pov.vertexNeighbors(12 * i + 3))
							if (t!=-1) show[t] = true;
					}
				}
				// F=new pt(G[V[f]]);
				return;
			}
		}
	}
	/**
	 * create a subsurface
	 * @param it : maximum iteration number
	 */
	void createOneSurface(int it) {
		//int c= (int)(Math.random()*(4*nt));
		s = new CreateSurfaceFILO(this,startingcorner,it);
		s.Create();
		perf=s.getPerf();
		//s = new CreateSurfaceFromShell(this, 0);
	}
	/**
	 * create nbiter subsurface and take the best one (perf meaning)
	 * @param it : max iteration per surface
	 * @param nbiter : number of subsurfaces created with different starting points(random)
	 */
	void createSurface(int it, int nbiter){
		double perf=0;
		for (int i=0;i<nbiter;i++){
			subSurface sub= new subSurface(display,this);
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
	 * display all vertices 
	 * green if showv[]
	 * red if vermarked[] ==-1
	 * blue else where
	 * @param r radius
	 * @return
	 */
	pov drawBalls(float r) {
		for (int v = 0; v < pov.nv; v++) {
			if (showv[v]) {
				display.fill(display.green, 100);
				display.show(pov.G[v], 3*r);
			} else if (vertexmarked[v] == -1) {
				display.fill(display.red, 100);
				display.show(pov.G[v], 3 * r);
			} else {
				display.fill(display.blue, 100);
				display.show(pov.G[v], r);
			}
		}
		return pov;
	}
	/**
	 * unmark all interior faces
	 */
	public void emptySurface(){
		for (int i=0;i<pov.nf;i++){
			if (marked[i]&&marked[pov.O[i]]){
				unmarkFace(i);
				unmarkFace(pov.O[i]);
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
	
	void apartementTree(Tree t, boolean b) {
		if (!pov.borderCorner(t.cor)) {
			try {
				t.A = mm[pov.tetraFromCorner(pov.o(pov.s(t.cor)))];
			} catch (BorderCornerException e) {
				t.A=false;
			}
			try {
				t.B = mm[pov.tetraFromCorner(pov.o(pov.s(pov.n(t.cor))))];
			} catch (BorderCornerException e) {
				t.B=false;
			}
			try {
				t.C = mm[pov.tetraFromCorner(pov.o(pov.s(pov.n(pov.n(t.cor)))))];
			} catch (BorderCornerException e) {
				t.C=false;
			}
			t.a = t.C || t.B || edgeIsMarked(pov.n(pov.n(pov.s(pov.n(t.cor)))), mm);// c.n.s.n.n
			t.b = t.C || t.A || edgeIsMarked(pov.n(pov.s(t.cor)), mm);
			t.c = t.A || t.B || edgeIsMarked(pov.s(pov.s(t.cor)), mm);
			t.v = t.A || t.B || t.C || t.a || t.b || t.c ;//|| vertexIsMarked(pov.n(pov.n(pov.s(t.cor))), mm);
			mm[pov.tetraFromCorner(t.cor)] = true;
		}
	}
	/**
	 * check if the surface is manifold
	 * @return
	 */
	boolean checkSurface() {
		boolean b = true;
		int vertexfail = 0;
		int edgefail = 0;
		for (int v = 0; v < pov.nt; v++) {
			if (!checkContinuity(pov.vertexNeighbors(pov.cornerFromVertex(v)))) {
				b = false;
				vertexmarked[v] = -1;
				vertexfail++;
			}
		}
		//TODO non optimal many times the same operation
		for (int c = 0; c < 12 * pov.nt; c++) {
			if (!checkContinuity(pov.edgeNeighbors(c))) {
				b = false;
				edgefail++;
				vertexmarked[pov.v(c)] = -1;
				display.show(pov.G[pov.v(c)], 5);
				display.fill(display.red, 100);
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
		for (int v = 0; v < pov.nv; v++) {
			for (int j=0;j<4;j++){
				TreeSet<Integer> l = pov.vertexNeighbors(pov.cornerFromVertex(v));
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
				if (i!=-1)
					if (l0.contains(pov.tetraFromFace(pov.O[4 * i]))) {
						mod = true;
						l0.add(i);
						l.add(i);
					} else if (l0.contains(pov.tetraFromFace(pov.O[4 * i + 1]))) {
						mod = true;
						l0.add(i);
						l.add(i);
					} else if (l0.contains(pov.tetraFromFace(pov.O[4 * i + 2]))) {
						mod = true;
						l0.add(i);
						l.add(i);
					} else if (l0.contains(pov.tetraFromFace(pov.O[4 * i + 3]))) {
						mod = true;
						l0.add(i);
						l.add(i);
					}
			}
			if (!l.isEmpty())
				col.removeAll(l);
		}
		return col.isEmpty();
	}
	
}// end of subSurface class

abstract class Tree {
	int cor;
	subSurface s;
	pov p;
	boolean in;
	Tree father;
	Tree _1 = null;
	Tree _2 = null;
	Tree _3 = null;
	Boolean A, B, C, a, b, c, v=false;
	Tree(){}
	Tree(int cc, Tree t, boolean in, subSurface sur) {
		s=sur;
		p=s.pov;
		if (p.borderCorner(cc))
			throw new Error("Illegal node");
		cor = cc;
		this.in = in;
		father = t;
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


	abstract Tree next();

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
	
	public String toStat(){
		String ss = toString();
		s.tree += "," + ss;
		s.tetraType[GetType()]++;
		return ss;
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
}

class ApartmentTree extends Tree {
	static int[] faceNumber(){ return new int[]{2,2,0,0,0,-2,-2,-2,-4,2,2,2,2,2,2,0,0,0,0};}
	ApartmentTree(int cc, Tree t, boolean in, subSurface sur) {
		super(cc, t, in, sur);
		// TODO Auto-generated constructor stub
	}

	@Override
	Tree next() {
		try {
			if (_1 == null && !A && !s.mm[p.tetraFromCorner(p.o(p.s(cor)))] && !s.marked[p.faceFromCorner(p.o(p.s(cor)))]
					&& !p.borderCorner(p.o(p.s(cor)))) {
				_1 = new ApartmentTree(p.o(p.s(cor)), this, in,s);
				return _1;
			}
		} catch (BorderCornerException e) {
		}
		try {
			if (_2 == null && !B && !s.mm[p.tetraFromCorner(p.o(p.s(p.n(cor))))] && !s.marked[p.faceFromCorner(p.o(p.s(p.n(cor))))]
					&& !p.borderCorner(p.o(p.s(p.n(cor))))) {
				_2 = new ApartmentTree(p.o(p.s(p.n(cor))), this, in,s);
				return _2;
			}
		} catch (BorderCornerException e) {
		}
		try {
			if (_3 == null && !C && !s.mm[p.tetraFromCorner(p.o(p.s(p.n(p.n(cor)))))] && !s.marked[p.faceFromCorner(p.o(p.s(p.n(p.n(cor)))))]
					&& !p.borderCorner(p.o(p.s(p.n(p.n(cor)))))) {
				_3 = new ApartmentTree(p.o(p.s(p.n(p.n(cor)))), this, in,s);
				return _3;
			}
		} catch (BorderCornerException e) {
		}
		if (father == null)
			return null;
		return father.next();
	}
	
	
}
