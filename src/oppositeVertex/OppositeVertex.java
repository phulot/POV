package oppositeVertex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import POV.BorderFaceException;
import POV.pt;
import cornerDS.faceOperators;

public class OppositeVertex implements faceOperators, Iterable<Integer>{
	Triangulation border;
	HashMap<Integer,Set<Integer>> interiorEdges;
	int[] oppositeVertex;
	HashMap<Integer,Set<Integer>> oppositeFaces;
	Integer [] tetids;
	int maxDegree = 50;
	int maxfaces = 10000;
	
	
	public int storageCost(){
		System.out.println(hashMapSize(interiorEdges));
//		return 2*border.sizeOfFaces()+oppositeVertex.length+interiorEdges.size();
//		return border.storageCost()+oppositeVertex.length+interiorEdges.size();
		return hashMapSize(interiorEdges)+border.sizeOfFaces()+tetids.length+border.storageCost();
	}
	
	public void buildOppositeFaces(){
		oppositeFaces = new HashMap<>();
		for (int i=0;i<border.sizeOfFaces();i++){
			boolean b=true;
//			for (Integer face : border.incidentFaces(oppositeVertex[i])){
//				if (oppositeVertex[face]==border.neighbor(0, i)||oppositeVertex[face]==border.neighbor(1, i)||oppositeVertex[face]==border.neighbor(2, i))
//					{b=false;break;}
//			}
			if (b){
				Set<Integer> s = oppositeFaces.get(oppositeVertex[i]);
				if (s==null){s=new HashSet<Integer>();oppositeFaces.put(oppositeVertex[i], s);}
				s.add(border.getVertexID(i, 0));
				s.add(border.getVertexID(i, 1));
				s.add(border.getVertexID(i, 2));
				
//				Set<Integer> s = oppositeFaces.get(oppositeVertex[i]);
//				if (s==null) {s=new HashSet<Integer>();oppositeFaces.put(oppositeVertex[i], s);}
//				s.add(i);
			}
		}
	}
	
	public int hashMapSize(HashMap<Integer,Set<Integer>> map){
		int k=0;
		for (Integer e:map.keySet())
			k+=map.get(e).size();
		return k;
	}
	/**
	 * return the four vertices of a tetrahedron
	 * @param tetid : tet id
	 * @return vertex ids Array
	 */
	public int[] Vertices(int tetid){
		int[] res = new int[4];
		if (tetid <maxfaces){
			res[0] = oppositeVertex[tetid];
			res[1] = border.getVertexID(tetid,2);
			res[2] = border.getVertexID(tetid,1);
			res[3] = border.getVertexID(tetid,0);
		}
		else {
			int t = tetid-maxfaces;
			res[0]=(int) (t/Math.pow(maxDegree, 3));
//			System.out.println(t);
			Set<Integer> set = VertexNeighbor(t);
			System.out.println(set);
			Integer[] s = set.toArray(new Integer[0]);
			for (int i=0;i<3;i++){
				res[3-i]=t%s.length;
				t=t/s.length;
			}
			for (int i=1;i<4;i++){
				res[i]=s[res[i]];
			}
		}
		return res;
	}
	
	/**
	 * Vertex operation 
	 * @param tetid : tet id
	 * @param relativeVertex : relative vertex id 
	 * @return vertex id
	 */
	public int Vertex(int tetid, int relativeVertex){
		if (tetid <maxfaces){
			if (relativeVertex==0) return oppositeVertex[tetid];
			else return border.getVertexID(tetid,3-relativeVertex);
		}
		else{
			int t = tetid-maxfaces;
			int v = t/(int)(Math.pow(maxDegree,3));
			if (relativeVertex==0) return v;
			Set<Integer> set = VertexNeighbor(v);
			System.out.println(set);
			Integer[] s = set.toArray(new Integer[0]);
			t=t%(int)(Math.pow(s.length,3d));
			if (relativeVertex==1) return s[(t/(int)(Math.pow(s.length,2)))];
			t=t%(int)(Math.pow(s.length,2));
			if (relativeVertex==2) return s[(t/s.length)];
			return s[t%s.length];
		}
	}
	
	/**
	 * return all faces which tip vertex is the same as face's
	 * @param face
	 * @return
	 */
//	public Set<Integer> sameVertex(int face){
//		Set<Integer> s = new HashSet<>();
//		int v = getOppositeVertex(face);
//		Set<Integer> active = new HashSet<>();
//		active.add(face);
//		while (!active.isEmpty()){
//			Set<Integer> temp = new HashSet<>();
//			for (Integer f:active){
//				for (int i=0;i<3;i++)
//					if (getOppositeVertex(border.neighbor(i,f))==v)
//						if (!s.contains(border.neighbor(i,f))){
//							temp.add(border.neighbor(i,f));
//							s.add(border.neighbor(i,f));
//						}
//			}
//			active=temp;
//		}
//		return s;
//	}
	
	/**
	 * return all vertex ids of v's neighbors
	 * @param v : vertex id
	 * @return set of vertex ids
	 */
	public Set<Integer> VertexNeighbor(int v){
		Set<Integer> res = new HashSet<>();
		for (Integer f:border.incidentFaces(v)){
			res.add(border.getVertexID(f,0));
			res.add(border.getVertexID(f,1));
			res.add(border.getVertexID(f,2));
			res.add(oppositeVertex[f]);
			for (Integer face : border.incidentFaces(oppositeVertex[f]))
				if (oppositeVertex[face]==v){
					res.add(border.getVertexID(face,0));
					res.add(border.getVertexID(face,1));
					res.add(border.getVertexID(face,2));
				}
		}
		if (oppositeFaces.get(v)!=null)
			res.addAll(oppositeFaces.get(v));
//			for (int f:oppositeFaces.get(v)){
//				res.add(border.getVertexID(f,0));
//				res.add(border.getVertexID(f,1));
//				res.add(border.getVertexID(f,2));
//			}
		if (interiorEdges.get(v)!=null)
			res.addAll(interiorEdges.get(v));
		res.remove((Integer)v);
		return res;
	}
	public Set<Integer> VertexNeighbor(int tetid, int relver){
		int v;
		if (tetid <maxfaces){
			if (relver==0) v=oppositeVertex[tetid];
			else v=border.getVertexID(tetid,3-relver);
		}
		else{
			int t = tetid-maxfaces;
			int v0 = t/(int)(Math.pow(maxDegree,3));
			if (relver==0) v=v0;
			else{
				Set<Integer> set = VertexNeighbor(v0);
				System.out.println(set);
				Integer[] s = set.toArray(new Integer[0]);
				t=t%(int)(Math.pow(s.length,3d));
				if (relver==1) v= s[(t/(int)(Math.pow(s.length,2)))];
				else {
					t=t%(int)(Math.pow(s.length,2));
					if (relver==2) v= s[(t/s.length)];
					else v= s[t%s.length];
				}
			}
		}
		Set<Integer> res = new HashSet<>();
		for (Integer f:border.incidentFaces(v)){
			res.add(border.getVertexID(f,0));
			res.add(border.getVertexID(f,1));
			res.add(border.getVertexID(f,2));
			res.add(oppositeVertex[f]);
			for (Integer face : border.incidentFaces(oppositeVertex[f]))
				if (oppositeVertex[face]==v){
					res.add(border.getVertexID(face,0));
					res.add(border.getVertexID(face,1));
					res.add(border.getVertexID(face,2));
				}
		}
		if (oppositeFaces.get(v)!=null)
			res.addAll(oppositeFaces.get(v));
//			for (int f:oppositeFaces.get(v)){
//				res.add(border.getVertexID(f,0));
//				res.add(border.getVertexID(f,1));
//				res.add(border.getVertexID(f,2));
//			}
		if (interiorEdges.get(v)!=null)
			res.addAll(interiorEdges.get(v));
		res.remove((Integer)v);
		return res;
	}
	
//	public Iterable<Integer> VertexNeighbor(int v){
//		Iterator<Integer> it = new Iterator<Integer>() {
//			int i=0;
//			int f;
//			Iterator<Integer> it1=border.incidentFaces(v).iterator();
//			Iterator<Integer> it2=null;
////			Iterator<Integer> it3 = interiorEdges.get(v).iterator();
////			Iterator<Integer> it4 = oppositeFaces.get(v).iterator();
//			int whichit;
//			@Override
//			public boolean hasNext() {
//				if (oppositeFaces.get(v)!=null){
//					return whichit<4||it1.hasNext();
//				}
//				if (interiorEdges.get(v)!=null){
//					return whichit<3||it1.hasNext()||i<3;
//				}
//				if (it2!=null){
//					return it1.hasNext()||it2.hasNext()||i<3;
//				}
//				return it1.hasNext();
//			}
//
//			@Override
//			public Integer next() {
//				if (i<3&&whichit==1)
//					return border.getVertexID(f,i++);
//				if (i==3&&whichit==1){
//					i=0;
//					whichit=2;
//					it2=border.incidentFaces(oppositeVertex[f]).iterator();
//					f=it2.next();
//					return border.getVertexID(f,i++);
//				}
//				if (i<3&&whichit==2)
//					return border.getVertexID(f,i++);
//				if (i==3&&whichit==2){
//					i=0;
//					if (it2.hasNext()){
//						f=it2.next();
//						return border.getVertexID(f,i++);
//					}
//					if (it1.hasNext()){
//						f=it1.next();
//						return border.getVertexID(f,i++);
//					}
//					if (interiorEdges.get(v)!=null){
//						it1=interiorEdges.get(v).iterator();
//						whichit=3;
//						return it1.next();
//					}
//					if (oppositeFaces.get(v)!=null){
//						it1=oppositeFaces.get(v).iterator();
//						whichit=4;
//						f=it1.next();
//						return border.getVertexID(f, i);
//					}
//				}
//				if (whichit==3){
//					if (it1.hasNext())return it1.next();
//					if (oppositeFaces.get(v)!=null){
//						it1=oppositeFaces.get(v).iterator();
//						whichit=4;
//						f=it1.next();
//						return border.getVertexID(f, i++);
//					}
//				}
//				if (whichit==4&&i<3){
//					return border.getVertexID(f,i++);
//				}
//				if (whichit==4&&i==3){
//					f=it1.next();
//					i=0;
//					return border.getVertexID(f,i++);
//				}
//				return null;
//			}
//		};
//		return new Iterable<Integer>() {
//			@Override
//			public Iterator<Integer> iterator() {
//				return it;
//			}
//		};
//	}
	
	public boolean isNeighbor(int v,int u){
		if (u==v)return false;
		if (interiorEdges.get(u)!=null&&interiorEdges.get(u).contains(v)) return true;
		if (oppositeFaces.get(u)!=null&&oppositeFaces.get(u).contains(v)) return true;
		for (Integer f:border.incidentFaces(u)){
			if (border.getVertexID(f,0)==v)return true;
			if (border.getVertexID(f,1)==v)return true;
			if (border.getVertexID(f,2)==v)return true;
			if (oppositeVertex[f]==v) return true;
		}
		for (Integer f:border.incidentFaces(u)){
			for (Integer face : border.incidentFaces(oppositeVertex[f]))
				if (oppositeVertex[face]==v){
					if (border.getVertexID(face,0)==v)return true;
					if (border.getVertexID(face,1)==v)return true;
					if (border.getVertexID(face,2)==v)return true;
				}
		}
//		if (oppositeFaces.get(u)!=null)
//			for (int f:oppositeFaces.get(u)){
//				if (border.getVertexID(f,0)==v)return true;
//				if (border.getVertexID(f,1)==v)return true;
//				if (border.getVertexID(f,2)==v)return true;
//			}
		return false;
	}
	
	public String NeighborType(int v,int u){
		if (u==v)return "not Neighbor";
		if (interiorEdges.get(u)!=null&&interiorEdges.get(u).contains(v)) return "interior edge";
		if (oppositeFaces.get(u)!=null&&oppositeFaces.get(u).contains(v)) return "opposite face";
		for (Integer f:border.incidentFaces(u)){
			if (border.getVertexID(f,0)==v)return "incident face";
			if (border.getVertexID(f,1)==v)return "incident face";
			if (border.getVertexID(f,2)==v)return "incident face";
			if (oppositeVertex[f]==v) return "opposite vertex";
		}
		for (Integer f:border.incidentFaces(u)){
			for (Integer face : border.incidentFaces(oppositeVertex[f]))
				if (oppositeVertex[face]==v){
					if (border.getVertexID(face,0)==v)return "ring";
					if (border.getVertexID(face,1)==v)return "ring";
					if (border.getVertexID(face,2)==v)return "ring";
				}
		}
//		if (oppositeFaces.get(u)!=null)
//			for (int f:oppositeFaces.get(u)){
//				if (border.getVertexID(f,0)==v)return true;
//				if (border.getVertexID(f,1)==v)return true;
//				if (border.getVertexID(f,2)==v)return true;
//			}
		return "not Neighbor";
	}
	
	public int checkNeighbors(){
		int err =0;
		for (int i=0;i<border.sizeOfVertices();i++){
			for (int j=0;j<border.sizeOfVertices();j++){
				if (isNeighbor(i,j)&&!isNeighbor(j, i)){
					err++;
					System.out.println(NeighborType(i,j));
				}
			}
		}
		return err;
	}
	/**
	 * build the id of an external tet form one of his external faces
	 * @param f : external face
	 * @return tet id
	 */
	private int buildBorderID(Integer f){
		int ID =f;
		for (int i=0;i<3;i++){
			if (oppositeVertex[border.neighbor(i,f)]==border.getVertexID(f, i))
				ID=Math.min(ID,border.neighbor(i,f));
		}
		return ID;
	}
	
	private boolean isABorderId(int f){
		int ID =f;
		for (int i=0;i<3;i++){
			if (oppositeVertex[border.neighbor(i,f)]==border.getVertexID(f, i))
				ID=Math.min(ID,border.neighbor(i,f));
		}
		return ID==f;
	}
	/**
	 * create the id of an interior tet from his incident vertices
	 * vertices already have to be oriented
	 * @param v0 : vertex id
	 * @param v1 : vertex id
	 * @param v2 : vertex id
	 * @param v3 : vertex id
	 * @return tet id
	 */
	public int buildInteriorID(int v0,int v1,int v2,int v3){
		if (v0==v1||v0==v2||v0==v3||v1==v2||v1==v3||v2==v3) throw new Error("degenerated tet");
		if (v0<v1&&v0<v2&&v0<v3){
			if (v1<v2&&v1<v3){
				int id;
				int k=0;
				id=(int)Math.pow(maxDegree,3)*v0;
				Integer[] verNeigh = VertexNeighbor(v0).toArray(new Integer[0]);
				for (int j=0;j<verNeigh.length;j++){
					if (verNeigh[j]==v1){k++;
						id+=(int)Math.pow(verNeigh.length,2)*j;
						System.out.println(j);
					}
					if (verNeigh[j]==v2){k++;
					System.out.println(j);
						id+=verNeigh.length*j;
					}
					if (verNeigh[j]==v3){k++;
					System.out.println(j);
						id+=j;
					}
				}
				id+=maxfaces;
				if (id<0) throw new Error();
				if (k!=3) throw new Error("vertexNeighbor Problem "+k);
				return id;
			}
			else return buildInteriorID(v0, v2, v3, v1);
		}
		else if(v1<v0&&v1<v2&&v1<v3){
			return buildInteriorID(v1, v0, v3, v2);
		}
		else if(v2<v0&&v2<v1&&v2<v3){
			return buildInteriorID(v2,v3,v0,v1);
		}
		else {
			return buildInteriorID(v3,v2,v1,v0);
		}
	}
	
	/**
	 * opposite operation on faces
	 * @param f : face id
	 * @return face id
	 */
	public Integer opposite(int f) throws BorderFaceException {
		int tetid = f/4;
		int i=f%4;
		Set<Integer> s = new HashSet<>();
		//TODO optimize this operation
		for (Integer ver : VertexNeighbor(tetid, (i + 1) % 4))
			if (isNeighbor(ver, Vertex(tetid, (i + 2) % 4)))
				if (isNeighbor(ver, Vertex(tetid, (i + 3) % 4)))
					if (ver!=i)
						s.add(ver);
//		Set<Integer> s = VertexNeighbor(Vertex(tetid, (i + 1) % 4));
//		s.retainAll(VertexNeighbor(Vertex(tetid, (i + 2) % 4)));
//		s.retainAll(VertexNeighbor(Vertex(tetid, (i + 3) % 4)));
//		s.remove((Integer)Vertex(tetid, i));
		s = border.removeSide(Vertex(tetid, i), s, Vertex(tetid, (i + 1) % 4), Vertex(tetid, (i + 2) % 4), Vertex(tetid, (i + 3) % 4), false);
		if (s.size()==0){
			throw new BorderFaceException(f+maxTetID());
		}
		if (s.size()==1){
			int o = oppositeFace(tetid, s.iterator().next(),i);
			return o;
		}
		else {
			for (Integer v:s){
				int k=0;
				Set<Integer> s0=border.removeSide(Vertex(tetid, (i+1)%4), s, Vertex(tetid, (i+2)%4), Vertex(tetid, (i+3)%4), v, true);
				Set<Integer> s1=border.removeSide(Vertex(tetid, (i+2)%4), s0, Vertex(tetid, (i+1)%4), Vertex(tetid, (i+3)%4), v, true);
				s1.remove((Integer)v);
				if (s1.isEmpty())k++;
				if (s1.size()==1){
					return oppositeFace(tetid, s1.iterator().next(), i);
				}
				s0=border.removeSide(Vertex(tetid, (i+2)%4), s, Vertex(tetid, (i+1)%4), Vertex(tetid, (i+3)%4), v, true);
				s1=border.removeSide(Vertex(tetid, (i+3)%4), s0, Vertex(tetid, (i+1)%4), Vertex(tetid, (i+2)%4), v, true);
				s1.remove((Integer)v);
				if (s1.isEmpty())k++;
				if (s1.size()==1){
					return oppositeFace(tetid, s1.iterator().next(), i);
				}
				s0=border.removeSide(Vertex(tetid, (i+1)%4), s, Vertex(tetid, (i+2)%4), Vertex(tetid, (i+3)%4), v, true);
				s1=border.removeSide(Vertex(tetid, (i+3)%4), s0, Vertex(tetid, (i+1)%4), Vertex(tetid, (i+2)%4), v, true);
				s1.remove((Integer)v);
				if (s1.isEmpty())k++;
				if (s1.size()==1){
					return oppositeFace(tetid, s1.iterator().next(), i);
				}
				if (k==3){
					return oppositeFace(tetid, v, i);
				}
			}
			throw new Error("opposite Error"+s);
		}
	}
	


	private int oppositeFace(int tetid, Integer v,int o) {
		int id=-1;
		int[] vert = Vertices(tetid);
		Iterable<Integer> col = border.incidentFaces(v);
		for (Integer f:col){
			int k=0;
			for (int i=0;i<3;i++){
				if (border.getVertexID(f, i)==vert[(o+1)%4]||border.getVertexID(f, i)==vert[(o+2)%4]||border.getVertexID(f, i)==vert[(o+3)%4])k++;
			}
			if (k==2){
				id = buildBorderID(f);
				break;
			}
		}
		if (id==-1)
			id = buildInteriorID(v, vert[(o+1)%4], vert[(o+3)%4], vert[(o+2)%4]);//TODO verifier orientation
		int[] vs = Vertices(id);
		for (int i=0;i<4;i++){
			if (v==vs[i])
				return 4*id+i;
		}
		return 4*id;
	}
	
	public void buildTetIds(){
		HashSet<Integer> set = new HashSet<>();
		HashSet<Integer> s = new HashSet<>();
		
		ArrayList<Integer> res = new ArrayList<>();
		set.add(0);
		while (!set.isEmpty()){
			HashSet<Integer> temp = new HashSet<>();
			for (Integer i : set){
				for (int o=0;o<4;o++){
					Integer t;
					try {
						t=opposite(4*i+o)/4;
						if (!s.contains(t)){
							s.add(t);
							if (t>maxfaces)
								res.add(t);
							temp.add(t);
						}
						
					} catch (BorderFaceException e) {
					}
				}
			}
			set=temp;
		}
		tetids = res.toArray(new Integer[0]);
	}
	
//	class IdIterator implements Ite
	
	@Override
	public int maxTetID() {
		return getnv()*maxDegree*maxDegree*maxDegree;
	}
	@Override
	public int getnv() {
		return border.sizeOfVertices();
	}
	@Override
	public void setNv(int nv) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public pt G(int f) {
		return border.G(f);
	}
	@Override
	public int O(int f) throws BorderFaceException {
		return opposite(f);
	}
	@Override
	public Integer V(int v) {
		return Vertex(v/4, v%4);
	}
	@Override
	public void save(String fn) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean borderFace(int f) {
		return f>=4*maxTetID();
	}
	@Override
	public boolean borderCorner(int c) {
		return c>=12*maxTetID();
	}

	@Override
	public Iterator<Integer> iterator() {
		Iterator<Integer> it = new Iterator<Integer>() {
			int t=0;
			@Override
			public boolean hasNext() {
				return t<maxfaces+tetids.length;
			}

			@Override
			public Integer next() {
				if (t<border.sizeOfFaces())
					while (!isABorderId(t))t++;
				if (t==border.sizeOfFaces()) t=maxfaces;
				if (t>=maxfaces){
					return tetids[t++-maxfaces];
				}
				return t++;
			}
			
		};
		return it;
	}
}