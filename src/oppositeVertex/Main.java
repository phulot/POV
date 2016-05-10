package oppositeVertex;

import POV.*;

public class Main {

	public static void main(String[] args) {
		POV p = povBuilder.loadpov("data/hinge");
		OppositeVertex op = OppositeVertexBuilder.loadFromPOV(p);
//		Utils.testVertexNeighbors(op,p);
		long temp = System.currentTimeMillis();
		System.out.println("err "+op.checkNeighbors());
		System.out.println((System.currentTimeMillis()-temp));
//		POV p = OppositeVertexBuilder.toPOV(op);
//		p.savepov("data/test");
		int s=0;
		
		for (int i=0;i<op.border.sizeOfVertices();i++){
			s+=op.border.incidentFaces(i).size();
			for (int f : op.border.incidentFaces(i)){
				if (!(op.border.getVertexID(f, 0)==i||op.border.getVertexID(f, 1)==i||op.border.getVertexID(f, 2)==i)){
					throw new Error();
				}
			}
		}
		System.out.println(s/(double)op.border.sizeOfVertices());
	}
}
