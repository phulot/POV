package oppositeVertex;

import POV.*;

public class Main {

	public static void main(String[] args) {
		POV p = povBuilder.loadpov("data/sav");
		OppositeVertex op = OppositeVertexBuilder.loadFromPOV(p);
		int id = op.buildInteriorID(0, 414, 859, 1903);
		System.out.println("id " + id);
		System.out.println(op.Vertex(id, 0));
		System.out.println(op.Vertex(id, 1));
		System.out.println(op.Vertex(id, 2));
		System.out.println(op.Vertex(id, 3));
		System.out.println("Neighbor err nbr "+op.checkNeighbors());
		System.out.println(op.storageCost()+"     "+p.storageCost());
//		System.out.println(op.oppositeFaces.size());
		System.out.println("opposite Vertex Array Size " + op.border.sizeOfFaces());
		System.out.println("interior edges size : "+op.hashMapSize(op.interiorEdges));
		System.out.println("opposite face size : "+op.hashMapSize(op.oppositeFaces));
		System.out.println(op.oppositeFaces.size());
		System.out.println("border size "+op.border.storageCost());
//		for (Integer s : op.oppositeFaces.keySet()){
//			System.out.println(op.oppositeFaces.get(s));
//		}
	}
}
