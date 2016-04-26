package subsurface;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import POV.BorderCornerException;

public class CreateSurfaceFromShell extends SurfaceCreation {
	Tree aptree;
	CreateSurfaceFromShell(subSurface sur, int c,String name) {
		super(sur, c,name);
	}

	@Override
	public void Create() {
		for (int i=0;i<p.nt;i++)
			s.mm[i]=true;
		int c = startingCorner;
		s.unmarkTetrahedron(p.tetraFromCorner(c));
		surfaceTree = new ShellSurfaceTree(c,null,s);
		Tree t = surfaceTree;
		while ((t=t.next())!=null){
			Integer cor = t.cell.cor;//prior.poll();
			if (!s.marked[p.faceFromCorner(cor)]){
				s.tetraType[t.GetType()]++;
				s.tree+=","+t.toString();
			}
			s.mm[p.tetraFromCorner(cor)]=true;
		}
		aptree = surfaceTree;
		for (int i=0;i<p.nt;i++){
			if (s.mm[i])
				s.markTetrahedron(i);
		}
		int k = 0;
		for (int i = 0; i < p.nv; i++) {
			if (s.vertexmarked[i] != 0)
				k++;
		}
		System.out.println(k / (double) p.nv);
		k = 0;
		for (int i = 0; i < p.nt; i++) {
			if (s.isIsolated(i) == 0)
				k++;
		}
	}
	
	@Override
	void saveTestStatSurface(FileWriter fw) throws IOException {
		//fw.write("S is forced to be manifold all the time\n");
		fw.write("S initialized with the shell\n");
		fw.write("single test\n");
	}

	@Override
	void saveTestStatAppartement(FileWriter fw) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	Tree createApartementTree(int c, boolean b) {
		return aptree;
	}

	@Override
	public void CreateRepresentation() {
		// TODO Auto-generated method stub
		
	}

}

class ShellSurfaceTree extends Tree{

	ShellSurfaceTree(int cc, Tree t, subSurface sur) {
		super(cc, t, sur);
	}

	private static boolean isEreasable(Tree t){
		t.s.unmarkTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
		if (!t.s.checkContinuity(t.s.getPov().vertexNeighbors(t.cell.cor))) {
			t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.s.getPov().vertexNeighbors(t.s.getPov().n(t.cell.cor)))) {
			t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.s.getPov().vertexNeighbors(t.s.getPov().n(t.s.getPov().n(t.cell.cor))))) {
			t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.s.getPov().vertexNeighbors(t.s.getPov().n(t.s.getPov().n(t.s.getPov().s(t.cell.cor)))))) {
			t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.s.getPov().edgeNeighbors(t.cell.cor))) {
			t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.s.getPov().edgeNeighbors(t.s.getPov().n(t.cell.cor)))) {
			t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.s.getPov().edgeNeighbors(t.s.getPov().n(t.s.getPov().n(t.cell.cor))))) {
			t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.s.getPov().edgeNeighbors(t.s.getPov().s(t.s.getPov().s(t.cell.cor))))) {
			t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
			return false;
		}
		if (t.s.vertexmarked[t.s.getPov().v(t.cell.cor)]==0){
			t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
			return false;
		}
		if (t.s.vertexmarked[t.s.getPov().v(t.s.getPov().n(t.cell.cor))]==0){
			t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
			return false;
		}
		if (t.s.vertexmarked[t.s.getPov().v(t.s.getPov().n(t.s.getPov().n(t.cell.cor)))]==0){
			t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
			return false;
		}
		if (t.s.vertexmarked[t.s.getPov().v(t.s.getPov().n(t.s.getPov().n(t.s.getPov().s(t.cell.cor))))]==0){
			t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
			return false;
		}
		t.s.markTetrahedron(t.s.getPov().tetraFromCorner(t.cell.cor));
		return true;
	}
	
	@Override
	Tree next() {
		try {
			if (_1 == null && !cell.A && !s.mm[s.getPov().tetraFromCorner(s.getPov().o(s.getPov().s(cell.cor)))] && !s.marked[s.getPov().faceFromCorner(s.getPov().o(s.getPov().s(cell.cor)))]
					&& !s.getPov().borderCorner(s.getPov().o(s.getPov().s(cell.cor)))) {
				_1 = new ShellSurfaceTree(s.getPov().o(s.getPov().s(cell.cor)), this,s);
				if (!isEreasable(_1))_1=null;
				else {
					s.unmarkTetrahedron(s.getPov().tetraFromCorner(cell.cor));
					return _1;
				}
			}
		} catch (BorderCornerException e) {
		}
		try {
			if (_2 == null && !cell.B && !s.mm[s.getPov().tetraFromCorner(s.getPov().o(s.getPov().s(s.getPov().n(cell.cor))))] && !s.marked[s.getPov().faceFromCorner(s.getPov().o(s.getPov().s(s.getPov().n(cell.cor))))]
					&& !s.getPov().borderCorner(s.getPov().o(s.getPov().s(s.getPov().n(cell.cor))))) {
				_2 = new ShellSurfaceTree(s.getPov().o(s.getPov().s(s.getPov().n(cell.cor))), this,s);
				if (!isEreasable(_2))_2=null;
				else {
					s.unmarkTetrahedron(s.getPov().tetraFromCorner(cell.cor));
					return _2;
				}
			}
		} catch (BorderCornerException e) {
		}
		try {
			if (_3 == null && !cell.C && !s.mm[s.getPov().tetraFromCorner(s.getPov().o(s.getPov().s(s.getPov().n(s.getPov().n(cell.cor)))))] && !s.marked[s.getPov().faceFromCorner(s.getPov().o(s.getPov().s(s.getPov().n(s.getPov().n(cell.cor)))))]
					&& !s.getPov().borderCorner(s.getPov().o(s.getPov().s(s.getPov().n(s.getPov().n(cell.cor)))))) {
				_3 = new ShellSurfaceTree(s.getPov().o(s.getPov().s(s.getPov().n(s.getPov().n(cell.cor)))), this,s);
				if (!isEreasable(_3))_3=null;
				else {
					s.unmarkTetrahedron(s.getPov().tetraFromCorner(cell.cor));
					return _3;
				}
			}
		} catch (BorderCornerException e) {
		}
		if (father == null)
			return null;
		return father.next();
	}

	
}
