package subsurface;

import POV.BorderCornerException;
import subsurface.DiggingSurfaceTree;

public class DiggingSurfaceTree extends Tree{
	DiggingSurfaceTree _1,_2,_3,father;
	static int[] faceNumber(){return new int[]{2,2,0,0,0,-2,-2,-2,-4,2,2,2,2,2,2,0,0,0,0};}
	public DiggingSurfaceTree(int cc, DiggingSurfaceTree t, subSurface sur) {
		super(cc, t, sur);
		father = t;
		s.mm[s.getPov().tetraFromCorner(cc)]=true;
	}
	static boolean isManifold(Tree t){
		String ss=t.borderToString();
		boolean res= ss.equals("E");
		res|=ss.equals("A")||ss.equals("B")||ss.equals("C");
		res|=(ss.equals("AB")&&(t.s.unmarkedVertexNeighborsNbr(t.cell.cor,false)>1));
		res|=(ss.equals("AC")&&(t.s.unmarkedVertexNeighborsNbr(t.s.getPov().n(t.s.getPov().n(t.cell.cor)),false)>1));
		res|=(ss.equals("BC")&&(t.s.unmarkedVertexNeighborsNbr(t.s.getPov().n(t.cell.cor),false)>1));
		res|=(t.s.equals("ABC")&&(t.s.unmarkedVertexNeighborsNbr(t.cell.cor,false)>1)&&(t.s.unmarkedVertexNeighborsNbr(t.s.getPov().n(t.s.getPov().n(t.cell.cor)),false)>1)&&(t.s.unmarkedVertexNeighborsNbr(t.s.getPov().n(t.cell.cor),false)>1));
//		System.out.println(res+" "+ss);
		return res;
	}
	@Override
	public	DiggingSurfaceTree next() {
		try {
			int c=s.getPov().o(s.getPov().s(cell.cor));
			if (_1 == null && !cell.A && !s.mm[s.getPov().tetraFromCorner(c)] && !s.marked[s.getPov().faceFromCorner(c)]) {
				_1 = new DiggingSurfaceTree(c, this,s);
				if (!isManifold(_1)){
					s.mm[s.getPov().tetraFromCorner(_1.cell.cor)]=false;
					_1=null;
				}
				else return _1;
			}
		} catch (BorderCornerException e) {
		}
		try {
			int c=s.getPov().o(s.getPov().s(s.getPov().n(cell.cor)));
			if (_2 == null && !cell.B && !s.mm[s.getPov().tetraFromCorner(c)] && !s.marked[s.getPov().faceFromCorner(c)]){
				_2 = new DiggingSurfaceTree(c, this,s);
				if (!isManifold(_2)){
					s.mm[s.getPov().tetraFromCorner(_2.cell.cor)]=false;
					_2=null;
				}
				else return _2;
			}
		} catch (BorderCornerException e) {
		}
		try {
			int c= s.getPov().o(s.getPov().s(s.getPov().n(s.getPov().n(cell.cor))));
			if (_3 == null && !cell.C && !s.mm[s.getPov().tetraFromCorner(c)] && !s.marked[s.getPov().faceFromCorner(c)]){
				_3 = new DiggingSurfaceTree(c, this,s);
				if (!isManifold(_3)){
					s.mm[s.getPov().tetraFromCorner(_3.cell.cor)]=false;
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
