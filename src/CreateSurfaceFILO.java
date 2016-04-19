import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class CreateSurfaceFILO extends SurfaceCreation {
	FILOTree surfaceTree;
	int it;
	int nbrleaves;
	CreateSurfaceFILO(subSurface sur, int c,int iter) {
		super(sur, c);
		it=iter;
	}
/*
	int isAddable(int t) {
		if (p.borderCorner(12 * t))
			return 0;
		if (s.marked[4 * t])
			return 0;
		int k = 0;
		int f1 = 0;
		int f2 = 0;
		if (s.marked[p.O(4 * t)]) {
			k++;
			f2 = f1;
			f1 = 4 * t;
		}
		if (s.marked[p.O(4 * t + 1)]) {
			k++;
			f2 = f1;
			f1 = 4 * t + 1;
		}
		if (s.marked[p.O(4 * t + 2)]) {
			k++;
			f2 = f1;
			f1 = 4 * t + 2;
		}
		if (s.marked[p.O(4 * t + 3)]) {
			k++;
			f2 = f1;
			f1 = 4 * t + 3;
		}
		if (k == 0)
			return -1;
		if (k == 4) {
			int nbr = Math.min(s.unmarkedNeighborsNbr(12 * t), s.unmarkedNeighborsNbr(12 * t + 1));
			nbr = Math.min(nbr, s.unmarkedNeighborsNbr(12 * t + 2));
			nbr = Math.min(nbr, s.unmarkedNeighborsNbr(12 * t + 11));
			if (nbr > 1)
				return 1;
			return 0;
		}
		if (k == 3) {
			int nbr = Math.min(s.unmarkedNeighborsNbr(12 * t), s.unmarkedNeighborsNbr(12 * t + 1));
			nbr = Math.min(nbr, s.unmarkedNeighborsNbr(12 * t + 2));
			nbr = Math.min(nbr, s.unmarkedNeighborsNbr(12 * t + 11));
			if (nbr > 1)
				return 1;
			return 0;
		} // return 0;
		if (k == 2) {
			int c = p.cornerOftetra(f1, f2);
			Set<Integer> l = p.edgeNeighbors(c);
			for (Integer tt : l) {
				if (s.marked[4 * tt])
					return 0;
			}
			return 1;
		}
		if (k == 1) {
			if (s.marked[p.O(4 * t)] && s.vertexmarked[p.V[4 * t]] == 0)
				return 1;
			if (s.marked[p.O(4 * t + 1)] && s.vertexmarked[p.V[4 * t + 1]] == 0)
				return 1;
			if (s.marked[p.O(4 * t + 2)] && s.vertexmarked[p.V[4 * t + 2]] == 0)
				return 1;
			if (s.marked[p.O(4 * t + 3)] && s.vertexmarked[p.V[4 * t + 3]] == 0)
				return 1;
			return 0;
		}
		return 0;
	}*/

	@Override
	public void Create() {
		s.mm=new boolean[p.maxnt];
		s.markTetrahedron(p.tetraFromCorner(startingCorner));
		DiggingSurfaceTree t = new DiggingSurfaceTree(startingCorner,null,true,s);
		nbrleaves=0;
		int iter = 0;
		while ((t=t.next())!=null&&iter<it){iter++;}
		//p.display.F=p.G[p.v(t.cor)];
		//p.currentCorner=t.cor;
		for (int i=0;i<p.nt;i++){
			if (s.mm[i])
				s.markTetrahedron(i);
		}
		int k = 0; 
		for (int i = 0; i < p.nv; i++) {
			if (s.vertexmarked[i] != 0)
				k++;
		}
		System.out.println("perf : "+k / (double) p.nv);
		System.out.println("surface faces : "+2*(k-2));
		System.out.println("theorical average valence : "+(6*(k-2))/(double)k);
		k=0;
		for (int i = 0; i < p.nt; i++) {
			if (s.marked[4*i])
				k++;
		}
		System.out.println("marked tetrahedron : " +k);
//		k = 0;
//		int kk=0;
//		int n=0;
//		double[] stat=new double[20];
//		for (int i = 0; i < 12*p.nt; i++) {
//			n++;
//			if (!s.edgeIsInterior(i)) {
//				int l = s.EdgeNbrMarkedNeighbors(i);
//				stat[l]+=1d/p.edgeNeighbors(i).size();
//				k = Math.max(k, l);
//				kk += l;
//				if (l >16) {
//					s.show[i / 12] = true;
//				}
//			}
//		}
//		System.out.println("max neighbors number : "+k);
//		System.out.println("average neighbors number : "+kk/(n*1d));
//		for (int i=0;i<20;i++)System.out.println(stat[i]/2);
//		for (int i = 0; i < p.nt; i++) {
//			if (s.isIsolated(i) == 0)
//				k++;
//		}
//		System.out.println("isolated vertices : " +k);
		
		exceptionsNbr();
	}
	
	public int exceptionsNbr(){
		int nbInterior=0;
		int nbexcep=0;
		s.emptySurface();
		int k=0;
		int kk=0;
		int n=0;
		int n2=0;
		double[] stat=new double[40];
		for (int i=0;i<p.nv;i++){
			stat[s.vertexmarked[i]]++;
			if (s.vertexmarked[i]!=0)n++;
			if (s.vertexmarked[i]>10)s.showv[i]=true;
			k=Math.max(k,s.vertexmarked[i]);
			kk+=s.vertexmarked[i];
			n2+= s.vertexmarked[i]*s.vertexmarked[i];
		}
		for (int i=0;i<40;i++)System.out.println(stat[i]);
//		for (int i = 0; i < p.nf; i++) {
//			if (s.marked[i]){
//				if (s.vertexmarked[p.v(3*i)]>10)
//					s.show[p.tetraFromFace(i)]=true;
//				if (s.vertexmarked[p.v(3*i+1)]>10)
//					s.show[p.tetraFromFace(i)]=true;
//				if (s.vertexmarked[p.v(3*i+2)]>10)
//					s.show[p.tetraFromFace(i)]=true;
//			}
//		}
		System.out.println("maximum valence : "+k);
		System.out.println("average valence : "+kk/(double)n);
		System.out.println("variance valence : "+Math.sqrt((kk*kk-n2))/(double)n);
		for (int i=0;i<p.nt;i++){
			if (s.mm[i]){
				if(!s.marked[4*i]&&!s.marked[4*i+1]&&!s.marked[4*i+2]&&!s.marked[4*i+3]){
					nbInterior++;
					if (s.edgeIsMarked(12*i,s.mm)&&!s.edgeIsMarked(12*i+6,s.mm))
						nbexcep++;
					if (s.edgeIsMarked(12*i+1,s.mm)&&!s.edgeIsMarked(12*i+3,s.mm))
						nbexcep++;
					if (s.edgeIsMarked(12*i+2,s.mm)&&!s.edgeIsMarked(12*i+4,s.mm))
						nbexcep++;
					if (s.edgeIsMarked(12*i+3,s.mm)&&!s.edgeIsMarked(12*i+1,s.mm))
						nbexcep++;
					if (s.edgeIsMarked(12*i+4,s.mm)&&!s.edgeIsMarked(12*i+2,s.mm))
						nbexcep++;
					if (s.edgeIsMarked(12*i+6,s.mm)&&!s.edgeIsMarked(12*i+0,s.mm))
						nbexcep++;
				}
			}
		}
		System.out.println("interior tetrahedra number : "+nbInterior);
		System.out.println("exception number : "+nbexcep);
		return nbexcep;
	}
	@Override
	public void CreateRepresentation(){
		s.mm=new boolean[p.maxnt];
		s.show=new boolean[p.maxnt];
		surfaceTree = new FILOTree(startingCorner, null, true, s);
		s.tree+=surfaceTree.toString();
		s.tetraType[surfaceTree.GetType()]++;
		s.show[p.tetraFromCorner(startingCorner)]=true;
		p.currentCorner=startingCorner;
		FILOTree tr = surfaceTree;
		int iter=0;
		while ((tr=tr.next())!=null&&iter<it){
			iter++;
			Integer cor = tr.cor;
			s.tetraType[tr.GetType()]++;
			s.tree+=","+tr.toString();
			s.mm[p.tetraFromCorner(cor)]=true;
		}
		s.mm[p.tetraFromCorner(startingCorner)]=true;

		
	}

//	
//	public void decostruct(){
//		int nbr = 0;
//		int nbr2 = 0;
//		boolean mod =true;
//		while(mod){
//			mod=false;
//			for (int i = 0; i < p.nt; i++) {
//				if (s.marked[4 * i]) {
//					int k = 0;
//					for (int j = 0; j < 4; j++)
//						if (s.marked[p.O[4 * i + j]])
//							k++;
//					if (k == 1) {
//						nbr++;
//						mod = true;
//						s.unmarkTetrahedron(i);
//					}
//				}
////				for (int c = 0; c < 12 * p.nt; c++) {
////					int k = s.markedNumber(p.edgeNeighbours(c));
////					if (k == 3) {
////						k = s.markedNumber(p.vertexNeighbours(c));
////						if (k==3){
////							nbr2++;
////							mod=true;
////							for (Integer t : p.edgeNeighbours(c)){
////								s.unmarkTetrahedron(t);
////							}
////						}
////					}
////				}
//			}
//		}
//		System.out.println("nbr erasable tetrahedrons : "+nbr);
//		System.out.println("nbr wrong erasable tetrahedrons : "+nbr2/3);
//	}
	
	@Override
	void saveTestStatSurface(FileWriter fw) throws IOException {
		fw.write("S is forced to be manifold all the time\n");
		fw.write("S initialized with a border face\n");
		fw.write("single test\n");
	}

	@Override
	void saveTestStatAppartement(FileWriter fw) {
		
	}
	
}
class DiggingSurfaceTree extends Tree{
	DiggingSurfaceTree _1,_2,_3,father;
	static int[] faceNumber(){return new int[]{2,2,0,0,0,-2,-2,-2,-4,2,2,2,2,2,2,0,0,0,0};}
	DiggingSurfaceTree(int cc, DiggingSurfaceTree t, boolean in, subSurface sur) {
		super(cc, t, in, sur);
		father = t;
		s.mm[p.tetraFromCorner(cor)]=true;
	}
	static boolean isManifold(Tree t){
		String ss=t.borderToString();
		boolean res= ss.equals("E");
		res|=ss.equals("A")||ss.equals("B")||ss.equals("C");
		res|=(ss.equals("AB")&&(t.s.unmarkedNeighborsNbr(t.cor)>1));
		res|=(ss.equals("AC")&&(t.s.unmarkedNeighborsNbr(t.p.n(t.p.n(t.cor)))>1));
		res|=(ss.equals("BC")&&(t.s.unmarkedNeighborsNbr(t.p.n(t.cor))>1));
		res|=(t.s.equals("ABC")&&(t.s.unmarkedNeighborsNbr(t.cor)>1)&&(t.s.unmarkedNeighborsNbr(t.p.n(t.p.n(t.cor)))>1)&&(t.s.unmarkedNeighborsNbr(t.p.n(t.cor))>1));
//		System.out.println(res+" "+ss);
		return res;
	}
	@Override
	DiggingSurfaceTree next() {
		try {
			int c=p.o(p.s(cor));
			if (_1 == null && !A && !s.mm[p.tetraFromCorner(c)] && !s.marked[p.faceFromCorner(c)]) {
				_1 = new DiggingSurfaceTree(c, this, in,s);
				if (!isManifold(_1)){
					s.mm[p.tetraFromCorner(_1.cor)]=false;
					_1=null;
				}
				else return _1;
			}
		} catch (BorderCornerException e) {
		}
		try {
			int c=p.o(p.s(p.n(cor)));
			if (_2 == null && !B && !s.mm[p.tetraFromCorner(c)] && !s.marked[p.faceFromCorner(c)]){
				_2 = new DiggingSurfaceTree(c, this, in,s);
				if (!isManifold(_2)){
					s.mm[p.tetraFromCorner(_2.cor)]=false;
					_2=null;
				}
				else return _2;
			}
		} catch (BorderCornerException e) {
		}
		try {
			int c= p.o(p.s(p.n(p.n(cor))));
			if (_3 == null && !C && !s.mm[p.tetraFromCorner(c)] && !s.marked[p.faceFromCorner(c)]){
				_3 = new DiggingSurfaceTree(c, this, in,s);
				if (!isManifold(_3)){
					s.mm[p.tetraFromCorner(_3.cor)]=false;
					_3=null;
				}
				else return _3;
			}
		} catch (BorderCornerException e) {
		}
//		if (_1==null&&_2==null&&_3==null) 
			
		if (father == null)
			return null;
		return father.next();
	}
}

class mainTree extends Tree{
	mainTree _1,_2,_3,father;
	static int[] faceNumber(){return new int[]{-2,-2,0,0,0,2,2,2,3,-2,-2,-2,-1,-1,-1,0,1,1,1};}
	mainTree(int cc, mainTree t, boolean in, subSurface sur) {
		super(cc, t, in, sur);
		father=t;
		s.show[p.tetraFromCorner(cc)]=true;
		addBorder();
	}
	
	@Override
	mainTree next() {
		boolean b1=false,b2=false,b3=false;
		String ss = this.toString();
		if (ss.equals("E")){b1=true;}
		else if (ss.equals("V")){b1=true;}
		else if (ss.equals("A")){b2=true;}
		else if (ss.equals("B")){b1=true;}
		else if (ss.equals("C")){b1=true;}
		else if (ss.equals("a")){b1=true;}
		else if (ss.equals("b")){b1=true;}
		else if (ss.equals("c")){b1=true;}
		else if (ss.equals("bc")){b1=true;b2=true;}
		else if (ss.equals("ac")){b1=true;b2=true;}
		else if (ss.equals("ab")){b1=true;b3=true;}
		else if (ss.equals("abc")){b1=true;b2=true;b3=true;}
		else if (ss.equals("Aa")){b2=true;b3=true;}
		else if (ss.equals("Bb")){b1=true;b3=true;}
		else if (ss.equals("Cc")){b1=true;b2=true;}
		else if (ss.equals("AB")){b3=true;}
		else if (ss.equals("AC")){b2=true;}
		else if (ss.equals("BC")){b1=true;}
		else if (ss.equals("ABC")){}
		else System.out.println(ss);
		try {if (b1&&_1==null){_1 = new mainTree(p.o(p.s(cor)), this, in,s);return _1;}
		} catch (BorderCornerException e) {System.err.println("borderCorner");e.printStackTrace();}
		try {if (b2&&_2==null){_2 = new mainTree(p.o(p.s(p.n(cor))), this, in,s);return _2;}
		} catch (BorderCornerException e) {System.err.println("borderCorner");e.printStackTrace();}
		try {if (b3&&_3==null){_3 = new mainTree(p.o(p.s(p.n(p.n(cor)))), this, in,s);return _3;}
		} catch (BorderCornerException e) {System.err.println("borderCorner");e.printStackTrace();}
		if (father == null)return null; return father.next();
	}
	
	public void addBorder(){
		boolean considerBorder = true;
		
		try {
			int cc= p.o(p.s(cor));
			A=s.mm[p.tetraFromCorner(cc)]||(considerBorder&&!s.marked[p.faceFromCorner(cc)]);
		} catch (BorderCornerException e) {
			A=true;
		}
		try {
			int cc = p.o(p.s(p.n(cor)));
			B=s.mm[p.tetraFromCorner(cc)]||(considerBorder&&!s.marked[p.faceFromCorner(cc)]);
		} catch (BorderCornerException e) {
			B=true;
		}
		try {
			int cc=p.o(p.s(p.n(p.n(cor))));
			C=s.mm[p.tetraFromCorner(cc)]||(considerBorder&&!s.marked[p.faceFromCorner(cc)]);
		} catch (BorderCornerException e) {
			C=true;
		}
		a=s.edgeIsMarked(p.n(p.n(p.s(p.n(cor)))),s.mm)||(considerBorder&&!s.edgeIsInterior(p.n(p.n(p.s(p.n(cor))))));
		b=s.edgeIsMarked(p.n(p.s(cor)),s.mm)||(considerBorder&&!s.edgeIsInterior(p.n(p.s(cor))));
		c=s.edgeIsMarked(p.s(p.s(cor)),s.mm)||(considerBorder&&!s.edgeIsInterior(p.s(p.s(cor))));
		v=A||B||C||a||b||c||s.vertexIsMarked(p.n(p.n(p.s(cor))), s.mm)||considerBorder;
	}
	
}
