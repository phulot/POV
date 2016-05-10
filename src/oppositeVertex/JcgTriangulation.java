package oppositeVertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Jcg.geometry.Pair;
import Jcg.geometry.Point_2;
import Jcg.geometry.Point_3;
import Jcg.triangulations2D.TriangulationDSFace_2;
import Jcg.triangulations2D.TriangulationDSVertex_2;
import Jcg.triangulations2D.TriangulationDS_2;

import POV.pt;

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


	/**
	 * remove the vertices on one side of the plane (AiBiCi)
	 * the side is defined by op and vi : if op ==true removes the vertices that are on the opposite side of the plane relatively to vi
	 * create a copy
	 * @param s : set 
	 * @param vi : vertex id
	 * @param Ai : vertex id
	 * @param Bi : vertex id
	 * @param Ci : vertex id
	 * @param op : which side to remove
	 * @return a new set with only the vertices on the right side
	 */
	public Set<Integer> removeSide(int vi,Set<Integer> s,int Ai,int Bi,int Ci, boolean op){
		Set<Integer> ss = new HashSet<Integer>();
		Point_3 A = border.vertices.get(Ai).getPoint();
		Point_3 B = border.vertices.get(Bi).getPoint();
		Point_3 C = border.vertices.get(Ci).getPoint();
		Point_3 O = border.vertices.get(vi).getPoint();
		for (Integer i:s){
			Point_3 V = border.vertices.get(i).getPoint();
			double d=Utils.oppositeSide(A, B, C, O, V);
			if (d==0)ss.add(i);
			if ((d>0&&op)||(d<0&&!op))
				ss.add(i);
		}
		return ss;
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
	
	
}
