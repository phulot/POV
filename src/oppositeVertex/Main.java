package oppositeVertex;

import java.util.Set;

import POV.*;

public class Main {

	public static void main(String[] args) {
		POV p = povBuilder.loadele("data/tahol", 10f);
		double e = 0;
		for (int t:p){
			for (int i=0;i<12;i++){
				Set<Integer> s = p.edgeNeighbors(i+12*t);
				if (s.contains(-1)) e+=1/(s.size()-1d);
				else e+=1/(double)s.size();
			}
		}
		System.out.println("edge nbr "+e);
		OppositeVertex op = OppositeVertexBuilder.loadFromPOV(p);
//		System.out.println("Neighbor err nbr "+op.checkNeighbors());
		System.out.println(op.storageCost()+"     "+p.storageCost());
//		System.out.println(op.oppositeFaces.size());
		System.out.println("opposite Vertex Array Size " + op.border.sizeOfFaces());
		System.out.println("interior edges size : "+op.hashMapSize(op.interiorEdges));
		System.out.println("opposite face size : "+op.hashMapSize(op.oppositeFaces));
		System.out.println(op.oppositeFaces.size());
		System.out.println("tetids size : "+op.tetids.length);
		System.out.println("border size "+op.border.storageCost());
		int k=0;
		for (Integer i : op){
			if (i<op.maxfaces)k++;
		}
		System.out.println("number of ext tets : "+k);
//		for (Integer s : op.oppositeFaces.keySet()){
//			System.out.println(op.oppositeFaces.get(s));
//		}
//		for (Integer i : op){
//			System.out.println(i+"   "+op.new Tet().fromInt(i));
//		}
	}
}
