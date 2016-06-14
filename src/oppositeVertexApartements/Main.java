package oppositeVertexApartements;

import java.util.Set;
import java.util.function.Predicate;

import POV.*;
import cornerDS.cornerBasedDS;


public class Main {

	public static void main(String[] args) {
		POV p = POVBuilder.createRandomMesh(10000, 1);
//		POV p = POVBuilder.loadPV("data/gear.pov");
//		Utils.computeHsurfaceT1(p);
//		Utils.computeHsurfaceT2(p);
//		double e = 0;
//		for (int t:p){
//			for (int i=0;i<12;i++){
//				Set<Integer> s = p.edgeNeighbors(i+12*t);
//				if (s.contains(-1)) e+=1/(s.size()-1d);
//				else e+=1/(double)s.size();
//			}
//		}
//		System.out.println("edge nbr "+e);
		OppositeVertex op = OppositeVertexBuilder.loadFromPOV(p);
		
		Utils.testVertexNeighbors(op,p);
		int k=0;
		for (Integer t : p){
			for (int i=0;i<4;i++)
				if (p.O[4*t+i]==4*t+i){
					k++;
					break;
				}
//				for (int j=i+1;j<4;j++){
//					if (!op.isNeighbor(p.V[4*t+i], p.V[4*t+j]))
//						k++;
//				}
		}
//		System.out.println("Neighbor err nbr "+k);
		System.out.println(op.storageCost()+"     "+p.storageCost());
//		System.out.println(op.oppositeFaces.size());
//		System.out.println("opposite Vertex Array Size " + op.oppositeVertexbackwall.length+ op.oppositeVertexbackwind.length+op.oppositeVertexfrontwall.length+op.oppositeVertexfrontwind.length);
		System.out.println("interior edges size : "+op.hashMapSize(op.interiorEdges));
		System.out.println("opposite face size : "+op.hashMapSize(op.oppositeFaces));
		System.out.println(op.oppositeFaces.size());
		System.out.println("tetids size : "+op.tetids.length);
		System.out.println("border size "+op.border.storageCost());
//		k=0;
		
//		for (Integer i : op){
//			if (i<op.maxfaces){
//				System.out.println(i);
//				k++;System.out.println(op.new Tet().fromInt(i));
//			}
//		}
		System.out.println("number of ext tets : "+k);
		k=0;
		for (Integer v =0;v<op.border.sizeOfVertices();v++){
			for (Integer l:op.border.incidentCorner(v))
				k++;
		}
		System.out.println(k/(double)op.border.sizeOfVertices());
		cornerBasedDS cor = new cornerBasedDS(op);
		long time =System.nanoTime();
		Set<Integer> s = cor.traversal();
		s.removeIf(new Predicate<Integer>() {

			@Override
			public boolean test(Integer t) {
				return false;// t<op.maxfaces;
			}
		});
		System.out.println(s.size());
		double t1 = (System.nanoTime()-time)/(double)1000000;
//		System.out.println("traversal time "+t1);
		cor = new cornerBasedDS(p);
		time =System.nanoTime();
		System.out.println(cor.traversal().size());
		double t2 = (System.nanoTime()-time)/(double)1000000;
//		System.out.println("traversal time "+t2);
		System.out.println("rapport "+(t1/t2));
		time =System.nanoTime();
		for (Integer i : op){
			op.Vertices(i);
		}
		System.out.println("op display time "+(System.nanoTime()-time)/(double)1000000);
		time =System.nanoTime();
		for (Integer i : p){
			p.Vertices(i);
		}
		System.out.println("pov display time "+(System.nanoTime()-time)/(double)1000000);
		k=0;
		System.out.println("regularity "+op.computeRegularity());
//		for (int i=0;i<op.tetids.length;i++)
//			for (int j=i+1;j<op.tetids.length;j++)
//				if (op.tetids[i].equals(op.tetids[j]))
//					k++;
//		System.out.println(k);
//		for (Integer s : op.oppositeFaces.keySet()){
//			System.out.println(op.oppositeFaces.get(s));
//		}
//		for (Integer i : op){
//			System.out.println(i+"   "+op.new Tet().fromInt(i));
//		}
	}
}
