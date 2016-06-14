package Triangulations;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import Jcg.geometry.Point_3;
import Jcg.triangulations2D.TriangulationDSFace_2;
import Jcg.triangulations2D.TriangulationDSVertex_2;
import Jcg.triangulations2D.TriangulationDS_2;

class JcgTriangulation implements Triangulation{
	TriangulationDS_2<Point_3> border;
	
	
	public JcgTriangulation(TriangulationDS_2<Point_3> border) {
		this.border = border;
		int err = 0;
		int k=0;
		for (TriangulationDSFace_2<Point_3> face : border.faces){
			face.index=k;k++;
			for (int i=0;i<3;i++){
				if (face.neighbor(i)==face) err++;
			}
		}
		if (err!=0)
			throw new Error("invalid triangulation "+err);
		k=0;
		for (TriangulationDSVertex_2<Point_3> ver : border.vertices){
			ver.index=k;k++;
		}
	}

	@Override
	public Collection<Integer> incidentFaces(int vertexid) {
		Set<Integer> s= new HashSet<>();
		TriangulationDSVertex_2<Point_3> v = border.vertices.get(vertexid);
		TriangulationDSFace_2<Point_3> f = v.getFace();
		int d=2;
		while (d!=0){
			int l=s.size();
			int i = f.index(v);
			s.add(f.index);
			TriangulationDSFace_2<Point_3> temp = f.neighbor((i+1)%3);
			if (s.contains(temp)) f= f.neighbor((i+2)%3);
			else f=temp;
			d=s.size()-l;
		}
		System.out.println(s);
		if (s.size()<3) throw new Error("invalid triangulation");
		return s;
	}

	@Override
	public int sizeOfVertices() {
		return border.sizeOfVertices();
	}


	@Override
	public int getVertexID(int faceID, int relativeVertexID) {
		// TODO Auto-generated method stub
		return border.faces.get(faceID).vertex(relativeVertexID).index;
	}


	@Override
	public int neighbor(int i,int f) {
		return border.faces.get(f).neighbor(i).index;
	}


	@Override
	public pt G(int i) {
		Point_3 pp = border.vertices.get(i).getPoint();
		return new pt((float)(double)pp.x,(float)(double)pp.y,(float)(double)pp.z);
	}

	@Override
	public int sizeOfFaces() {
		return border.sizeOfFaces();
	}


	@Override
	public int storageCost() {
		// TODO Auto-generated method stub
		return 13*border.sizeOfVertices()-24;
	}
	
	
}
