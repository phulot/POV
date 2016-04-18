import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class CreateSurfaceFromShell extends SurfaceCreation {
	Tree aptree;
	CreateSurfaceFromShell(subSurface sur, int c) {
		super(sur, c);
	}

	@Override
	public void Create() {
		for (int i=0;i<p.nt;i++)
			s.mm[i]=true;
		int c = startingCorner;
		s.unmarkTetrahedron(p.tetraFromCorner(c));
		surfaceTree = new ShellSurfaceTree(c,null,true,s);
		Tree t = surfaceTree;
		while ((t=t.next())!=null){
			Integer cor = t.cor;//prior.poll();
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

	ShellSurfaceTree(int cc, Tree t, boolean in, subSurface sur) {
		super(cc, t, in, sur);
	}

	private static boolean isEreasable(Tree t){
		t.s.unmarkTetrahedron(t.p.tetraFromCorner(t.cor));
		if (!t.s.checkContinuity(t.p.vertexNeighbors(t.cor))) {
			t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.p.vertexNeighbors(t.p.n(t.cor)))) {
			t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.p.vertexNeighbors(t.p.n(t.p.n(t.cor))))) {
			t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.p.vertexNeighbors(t.p.n(t.p.n(t.p.s(t.cor)))))) {
			t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.p.edgeNeighbors(t.cor))) {
			t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.p.edgeNeighbors(t.p.n(t.cor)))) {
			t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.p.edgeNeighbors(t.p.n(t.p.n(t.cor))))) {
			t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
			return false;
		}
		if (!t.s.checkContinuity(t.p.edgeNeighbors(t.p.s(t.p.s(t.cor))))) {
			t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
			return false;
		}
		if (t.s.vertexmarked[t.p.v(t.cor)]==0){
			t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
			return false;
		}
		if (t.s.vertexmarked[t.p.v(t.p.n(t.cor))]==0){
			t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
			return false;
		}
		if (t.s.vertexmarked[t.p.v(t.p.n(t.p.n(t.cor)))]==0){
			t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
			return false;
		}
		if (t.s.vertexmarked[t.p.v(t.p.n(t.p.n(t.p.s(t.cor))))]==0){
			t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
			return false;
		}
		t.s.markTetrahedron(t.p.tetraFromCorner(t.cor));
		return true;
	}
	
	@Override
	Tree next() {
		try {
			if (_1 == null && !A && !s.mm[p.tetraFromCorner(p.o(p.s(cor)))] && !s.marked[p.faceFromCorner(p.o(p.s(cor)))]
					&& !p.borderCorner(p.o(p.s(cor)))) {
				_1 = new ShellSurfaceTree(p.o(p.s(cor)), this, in,s);
				if (!isEreasable(_1))_1=null;
				else {
					s.unmarkTetrahedron(p.tetraFromCorner(cor));
					return _1;
				}
			}
		} catch (BorderCornerException e) {
		}
		try {
			if (_2 == null && !B && !s.mm[p.tetraFromCorner(p.o(p.s(p.n(cor))))] && !s.marked[p.faceFromCorner(p.o(p.s(p.n(cor))))]
					&& !p.borderCorner(p.o(p.s(p.n(cor))))) {
				_2 = new ShellSurfaceTree(p.o(p.s(p.n(cor))), this, in,s);
				if (!isEreasable(_2))_2=null;
				else {
					s.unmarkTetrahedron(p.tetraFromCorner(cor));
					return _2;
				}
			}
		} catch (BorderCornerException e) {
		}
		try {
			if (_3 == null && !C && !s.mm[p.tetraFromCorner(p.o(p.s(p.n(p.n(cor)))))] && !s.marked[p.faceFromCorner(p.o(p.s(p.n(p.n(cor)))))]
					&& !p.borderCorner(p.o(p.s(p.n(p.n(cor)))))) {
				_3 = new ShellSurfaceTree(p.o(p.s(p.n(p.n(cor)))), this, in,s);
				if (!isEreasable(_3))_3=null;
				else {
					s.unmarkTetrahedron(p.tetraFromCorner(cor));
					return _3;
				}
			}
		} catch (BorderCornerException e) {
		}
		if (father == null)
			return null;
		return father.next();
	}

	@Override
	int[] faceNumber() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
