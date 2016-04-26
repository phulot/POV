package subsurface;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import POV.BorderCornerException;

public class CreateSurfaceFIFO extends SurfaceCreation {
	FILOTree surfaceTree;
	int it;
	CreateSurfaceFIFO(subSurface sur, int c,int iter,String name) {
		super(sur, c, name);
		it=iter;
	}
	
	@Override
	public void Create() {
		s.markTetrahedron(p.tetraFromCorner(startingCorner));
		DiggingSurfaceTree t = new DiggingSurfaceTree(startingCorner,null,s);
		while ((t=t.next())!=null){
			Integer cor = t.cell.cor;
			s.mm[p.tetraFromCorner(cor)]=true;
		}
		for (int i=0;i<p.nt;i++){
			if (s.mm[i])
				s.markTetrahedron(i);
		}
	}

	public void CreateRepresentation(){
		s.mm=new boolean[p.maxnt];
		s.show=new boolean[p.maxnt];
		surfaceTree = new FILOTree(startingCorner, null, s);
//		p.currentCorner=startingCorner;
		FILOTree tr = surfaceTree;
		int iter=0;
		LinkedList<FILOTree> l = new LinkedList<FILOTree>();
		l.add(tr);
		while (!l.isEmpty()&&iter<it){
			tr=l.pollFirst();
			tr.addBorder();
			tr.createsons();
			if (tr._1!=null) l.addLast(tr._1);
			if (tr._2!=null) l.addLast(tr._2);
			if (tr._3!=null) l.addLast(tr._3);
			iter++;
			s.tetraType[tr.GetType()]++;
			s.tree+=","+tr.toString();
		}
		int k = 0; 
		for (int i = 0; i < p.nv; i++) {
			if (s.vertexmarked[i] != 0)
				k++;
		}
		System.out.println("perf : "+k / (double) p.nv);
		k=0;
		for (int i = 0; i < p.nt; i++) {
			if (s.marked[4*i])
				k++;
		}
		System.out.println("marked tetrahedron : " +k);
		k = 0;
		for (int i = 0; i < p.nt; i++) {
			if (s.isIsolated(i) == 0)
				k++;
		}
	}
	
	@Override
	void saveTestStatSurface(FileWriter fw) throws IOException {
		fw.write("S is forced to be manifold all the time\n");
		fw.write("S initialized with a border face\n");
		fw.write("single test\n");
	}

	@Override
	void saveTestStatAppartement(FileWriter fw) {
		// TODO Auto-generated method stub
		
	}
	
}
class FILOTree extends Tree{
	FILOTree _1,_2,_3,father;
	int[] faceNumber(){ return new int[]{0,0,1,1,1,2,2,2,3,0,0,0,0,0,0,0,1,1,1};}
	public FILOTree(int cc, FILOTree t, subSurface sur) {
		super(cc, t, sur);
		father=t;
		s.show[s.getPov().tetraFromCorner(cc)]=true;
		s.mm[s.getPov().tetraFromCorner(cc)]=true;
		addBorder();
	}
	
	void createsons(){
		try {
			if(!cell.A)
				_1=new FILOTree(s.getPov().o(s.getPov().s(cell.cor)), this, s);
			if(!cell.B)
				_2=new FILOTree(s.getPov().o(s.getPov().s(s.getPov().n(cell.cor))), this, s);
			if(!cell.C)
				_3=new FILOTree(s.getPov().o(s.getPov().s(s.getPov().n(s.getPov().n(cell.cor)))), this, s);
		} catch (BorderCornerException e) {
		}
	}
	
	@Override
	FILOTree next() {
		int cor = cell.cor;
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
		try {if (b1&&_1==null){_1 = new FILOTree(s.getPov().o(s.getPov().s(cor)), this,s);return _1;}
		} catch (BorderCornerException e) {System.err.println("borderCorner");e.printStackTrace();}
		try {if (b2&&_2==null){_2 = new FILOTree(s.getPov().o(s.getPov().s(s.getPov().n(cor))), this,s);return _2;}
		} catch (BorderCornerException e) {System.err.println("borderCorner");e.printStackTrace();}
		try {if (b3&&_3==null){_3 = new FILOTree(s.getPov().o(s.getPov().s(s.getPov().n(s.getPov().n(cor)))), this,s);return _3;}
		} catch (BorderCornerException e) {System.err.println("borderCorner");e.printStackTrace();}
		if (father == null)return null; return father.next();
	}
	
	public void addBorder(){
		boolean considerBorder = true;
		int cor = cell.cor;
		try {
			int cc= s.getPov().o(s.getPov().s(cor));
			cell.A=s.mm[s.getPov().tetraFromCorner(cc)]||(considerBorder&&!s.marked[s.getPov().faceFromCorner(cc)]);
		} catch (BorderCornerException e) {
			cell.A=true;
		}
		try {
			int cc = s.getPov().o(s.getPov().s(s.getPov().n(cor)));
			cell.B=s.mm[s.getPov().tetraFromCorner(cc)]||(considerBorder&&!s.marked[s.getPov().faceFromCorner(cc)]);
		} catch (BorderCornerException e) {
			cell.B=true;
		}
		try {
			int cc=s.getPov().o(s.getPov().s(s.getPov().n(s.getPov().n(cor))));
			cell.C=s.mm[s.getPov().tetraFromCorner(cc)]||(considerBorder&&!s.marked[s.getPov().faceFromCorner(cc)]);
		} catch (BorderCornerException e) {
			cell.C=true;
		}
		cell.a=s.edgeIsMarked(s.getPov().n(s.getPov().n(s.getPov().s(s.getPov().n(cor)))),s.mm)||(considerBorder&&!s.edgeIsInterior(s.getPov().n(s.getPov().n(s.getPov().s(s.getPov().n(cor))))));
		cell.b=s.edgeIsMarked(s.getPov().n(s.getPov().s(cor)),s.mm)||(considerBorder&&!s.edgeIsInterior(s.getPov().n(s.getPov().s(cor))));
		cell.c=s.edgeIsMarked(s.getPov().s(s.getPov().s(cor)),s.mm)||(considerBorder&&!s.edgeIsInterior(s.getPov().s(s.getPov().s(cor))));
		cell.v=cell.A||cell.B||cell.C||cell.a||cell.b||cell.c||s.vertexIsMarked(s.getPov().n(s.getPov().n(s.getPov().s(cor))), s.mm)||considerBorder;
	}
	
}
