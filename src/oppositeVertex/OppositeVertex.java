package oppositeVertex;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import POV.BorderFaceException;
import POV.pt;
import POV.vec;
import Triangulations.Triangulation;
import cornerDS.faceOperators;

public class OppositeVertex implements faceOperators, Iterable<Integer>{
	Triangulation border;
	HashMap<Integer,Set<Integer>> interiorEdges;
	int[] oppositeVertex;
	HashMap<Integer,Set<Integer>> oppositeFaces;
	Tet [] tetids;
	int maxTet;
	int maxfaces;
	
	public OppositeVertex(){};
	public OppositeVertex(Triangulation border, HashMap<Integer, Set<Integer>> interiorEdges, int[] oppositeVertex,
			int maxTet) {
		super();
		this.border = border;
		this.interiorEdges = interiorEdges;
		this.oppositeVertex = oppositeVertex;
		this.maxTet = maxTet;
	}

	public int storageCost(){
//		System.out.println(hashMapSize(interiorEdges));
//		return 2*border.sizeOfFaces()+oppositeVertex.length+interiorEdges.size();
//		return border.storageCost()+oppositeVertex.length+interiorEdges.size();
		return hashMapSize(interiorEdges)+hashMapSize(oppositeFaces)+border.sizeOfFaces()+tetids.length+border.storageCost();
	}
	
	public void buildOppositeFaces(){
		oppositeFaces = new HashMap<>();
		for (int i=0;i<border.sizeOfFaces();i++){
			boolean b=true;
			for (Integer face : border.incidentFaces(oppositeVertex[i])){
				for (int k=0;k<3;k++){
					if (oppositeVertex[face]==border.getVertexID(i, k))
						b=false;
				}
			}
			if (b){
				Set<Integer> s = oppositeFaces.get(oppositeVertex[i]);
				if (s==null){s=new HashSet<Integer>();oppositeFaces.put(oppositeVertex[i], s);}
//				s.add(border.getVertexID(i, 0));
//				s.add(border.getVertexID(i, 1));
//				s.add(border.getVertexID(i, 2));
				s.add(i);
			}
		}
	}
	
	public int hashMapSize(HashMap<Integer,Set<Integer>> map){
		int k=0;
		for (Integer e:map.keySet())
			k+=map.get(e).size()+1;
		return k;
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
//		System.out.println("neighbor");
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
//			res.addAll(oppositeFaces.get(v));
			for (int f:oppositeFaces.get(v)){
				res.add(border.getVertexID(f,0));
				res.add(border.getVertexID(f,1));
				res.add(border.getVertexID(f,2));
			}
		if (interiorEdges.get(v)!=null)
			res.addAll(interiorEdges.get(v));
		res.remove((Integer)v);
		return res;
	}
	
	public boolean isNeighbor(int v,int u){
		if (u==v)return false;
		if (interiorEdges.get(u)!=null&&interiorEdges.get(u).contains(v)) return true;
//		if (oppositeFaces.get(u)!=null&&oppositeFaces.get(u).contains(v)) return true;
		for (Integer f:border.incidentFaces(u)){
			if (border.getVertexID(f,0)==v)return true;
			if (border.getVertexID(f,1)==v)return true;
			if (border.getVertexID(f,2)==v)return true;
			if (oppositeVertex[f]==v) return true;
		}
		for (Integer f:border.incidentFaces(u)){
			for (Integer face : border.incidentFaces(oppositeVertex[f]))
				if (oppositeVertex[face]==u){
					if (border.getVertexID(face,0)==v)return true;
					if (border.getVertexID(face,1)==v)return true;
					if (border.getVertexID(face,2)==v)return true;
				}
		}
		if (oppositeFaces.get(u)!=null)
			for (int f:oppositeFaces.get(u)){
				if (border.getVertexID(f,0)==v)return true;
				if (border.getVertexID(f,1)==v)return true;
				if (border.getVertexID(f,2)==v)return true;
			}
		return false;
	}
	
	public String NeighborType(int v,int u){
		if (u==v)return "not Neighbor";
		if (interiorEdges.get(u)!=null&&interiorEdges.get(u).contains(v)) return "interior edge";
//		if (oppositeFaces.get(u)!=null&&oppositeFaces.get(u).contains(v)) return "opposite face";
		for (Integer f:border.incidentFaces(u)){
			if (border.getVertexID(f,0)==v)return "incident face";
			if (border.getVertexID(f,1)==v)return "incident face";
			if (border.getVertexID(f,2)==v)return "incident face";
			if (oppositeVertex[f]==v) return "opposite vertex";
		}
		for (Integer f:border.incidentFaces(u)){
			for (Integer face : border.incidentFaces(oppositeVertex[f]))
				if (oppositeVertex[face]==u){
					if (border.getVertexID(face,0)==v)return "ring";
					if (border.getVertexID(face,1)==v)return "ring";
					if (border.getVertexID(face,2)==v)return "ring";
				}
		}
		if (oppositeFaces.get(u)!=null)
			for (int f:oppositeFaces.get(u)){
				if (border.getVertexID(f,0)==v)return "opposite face";
				if (border.getVertexID(f,1)==v)return "opposite face";
				if (border.getVertexID(f,2)==v)return "opposite face";
			}
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
	private Tet buildBorderTet(Integer f){
		int ID =f;
		for (int i=0;i<3;i++)
			for (int j=0;j<3;j++)
				if (oppositeVertex[border.neighbor(i,f)]==border.getVertexID(f, j))
					ID=Math.max(ID,border.neighbor(i,f));
		return new Tet(ID);
	}
	
	private boolean isABorderTet(int f){
		int ID =f;
		for (int i=0;i<3;i++)
			for (int k=0;k<3;k++)
				if (oppositeVertex[border.neighbor(i,f)]==border.getVertexID(f, k))
					ID=Math.max(ID,border.neighbor(i,f));
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
	public Tet buildInteriorID(int v0,int v1,int v2,int v3){
		assert (v0!=v1&&v0!=v2&&v0!=v3&&v1!=v2&&v1!=v3&&v2!=v3);
		return new Tet(v0,v1,v2,v3);
	}
	public Tet buildInteriorID(int[] tab){
		assert (tab[0]!=tab[1]&&tab[0]!=tab[2]&&tab[0]!=tab[3]&&tab[1]!=tab[2]&&tab[1]!=tab[3]&&tab[2]!=tab[3]);
		return new Tet(tab);
	}
	
	/**
	 * opposite operation on faces
	 * @param f : face id
	 * @return face id
	 */
	public Face opposite(Face f) throws BorderFaceException {
		Tet tet = f.t;
		int i=f.relf;
		if (!tet.interior){
			if (i==0){
				throw new BorderFaceException(f.toInt()+4*maxTetID());
			}
			for (int k=0;k<3;k++)
//				for (int i0=0;i0<3;i0++)
					if (oppositeVertex[border.neighbor(k,tet.t[0])]==tet.Vertex(i)){
						if (tetids!=null){
							throw new BorderFaceException(f.toInt()+4*maxTetID());
						}
						else {
							throw new BorderFaceException(4*maxTetID());
						}
					}
		}
		Set<Integer> s = commonNeighbors(tet, i);
		
		if (s.size()==0){
			System.out.println("false border " + oppositeVertex[border.neighbor(0,tet.t[0])]+"  "+ oppositeVertex[border.neighbor(1,tet.t[0])]+"  "+ oppositeVertex[border.neighbor(2,tet.t[0])]);
			Set<Integer> set = VertexNeighbor(tet.Vertex((i + 1) % 4));
			set.retainAll(VertexNeighbor(tet.Vertex((i + 2) % 4)));
			set.retainAll(VertexNeighbor(tet.Vertex((i + 3) % 4)));
			System.out.println(set+"   "+tet.Vertex(i)+"  "+tet);
//			pt A=border.G(tet.Vertex((i+1)%4));
//			vec n = Utils.normal(tet.Vertex((i+1)%4), tet.Vertex((i+2)%4), tet.Vertex((i+3)%4), border);
//			for (Integer l:set){
////				double d =Utils.Side(n, vec.V(border.G(l),A))*Utils.Side(n,vec.V(border.G(tet.Vertex(i)),A));
//				System.out.println(VertexNeighbor(l));
//			}
//			throw new BorderFaceException(4*maxTetID());
			throw new Error(""+f.t.interior+"  "+VertexNeighbor(tet.Vertex((i+1)%4)));
		}
		if (s.size()==1){
			Face o = oppositeFace(tet, s.iterator().next(),i);
//			System.out.println(o+"  "+o.t);
			return o;
		}
		else {
//			throw new BorderFaceException(4*maxTetID());
//			System.out.println(s);
			while (s.size()>1){
				int v= s.iterator().next();
				s.remove(v);
				vec n1 = Utils.normal(v, tet.Vertex((i+2)%4), tet.Vertex((i+3)%4), border);
				vec n2 = Utils.normal(tet.Vertex((i+1)%4), v, tet.Vertex((i+3)%4), border);
				vec n3 = Utils.normal(tet.Vertex((i+1)%4), tet.Vertex((i+2)%4), v, border);
				Set<Integer> s1 = Utils.removeSide(tet.Vertex((i+1)%4), tet.Vertex((i+2)%4), s, n1, true, border);
				Set<Integer> s2 = Utils.removeSide(tet.Vertex((i+2)%4), tet.Vertex((i+1)%4), s, n2, true, border);
				Set<Integer> s3 = Utils.removeSide(tet.Vertex((i+3)%4), tet.Vertex((i+1)%4), s, n3, true, border);
				int k=0;
				Set<Integer> ss = Utils.removeSide(tet.Vertex((i+1)%4), tet.Vertex((i+2)%4), s, n1, true, border);
				ss.retainAll(s2);
				if (ss.isEmpty())k++;
				ss = s1;
				ss.retainAll(s3);
				if (ss.isEmpty())k++;
				ss = s2;
				ss.retainAll(s3);
				if (ss.isEmpty())k++;
				if (k==3){
					Face o = oppositeFace(tet, v, i);
//					System.out.println(o);
					return o;
				}
			}
			Face o= oppositeFace(tet, s.iterator().next(), i);
//			System.out.println(o);
			return o;
//			throw new Error("opposite Error"+s);
		}
	}
	private Set<Integer> commonNeighbors(Tet tet, int i) {
		Set<Integer> ss = VertexNeighbor(tet.Vertex((i + 1) % 4));
		vec n = Utils.normal(tet.Vertex((i+1)%4), tet.Vertex((i+2)%4), tet.Vertex((i+3)%4), border);
		ss = Utils.removeSide(tet.Vertex(i), tet.Vertex((i + 1) % 4), ss, n, false, border);
		Set<Integer> s = new HashSet<>();
		//TODO optimize this operation
		for (Integer ver : ss)
			if (ver!=tet.Vertex(i))
				if (isNeighbor(ver, tet.Vertex((i + 2) % 4)))
					if (isNeighbor(ver, tet.Vertex((i + 3) % 4)))
						s.add(ver);
		return s;
	}
//	private Set<Integer> commonNeighbors(Tet tet, int i) {
//		Set<Integer> s = VertexNeighbor(tet.Vertex((i + 1) % 4));
//		s.remove(tet.Vertex(i));
//		s.retainAll(VertexNeighbor(tet.Vertex((i + 2) % 4)));
//		s.retainAll(VertexNeighbor(tet.Vertex((i + 3) % 4)));
//		//TODO optimize this operation
//	vec n = Utils.normal(tet.Vertex((i+1)%4), tet.Vertex((i+2)%4), tet.Vertex((i+3)%4), border);
//	s = Utils.removeSide(tet.Vertex(i), tet.Vertex((i + 1) % 4), s, n, false, border);
//		return s;
//	}

	private Face oppositeFace(Tet tet, Integer v,int o) {
		Tet id=null;
		int[] vert = tet.Vertices();
		Iterable<Integer> col = border.incidentFaces(v);
		for (Integer f:col){
			int k=0;
			for (int i=0;i<3;i++){
				for (int j=0;j<4;j++)
					if (j!=o)
						if (border.getVertexID(f, i)==vert[j])
							k++;
			}
			if (k>=2){
				id = buildBorderTet(f);
				break;
			}
		}
		if (id==null){
			int[] idvert = new int[]{v,vert[(o+1)%4],vert[(o+2)%4],vert[(o+3)%4]};
			Arrays.sort(idvert);
			if (vec.m(vec.V(border.G(idvert[0]),border.G(idvert[1])),vec.V(border.G(idvert[0]),border.G(idvert[2])),vec.V(border.G(idvert[0]),border.G(idvert[3])))>0)
				id = buildInteriorID(idvert);//TODO verifier orientation
			else id = buildInteriorID(idvert[0],idvert[1],idvert[3],idvert[2]);
		}
		int[] vs = id.Vertices();
		for (int i=0;i<4;i++){
			if (v==vs[i])
				return new Face(id,i);
		}
		throw new Error();
//		return new Face(id,0);
	}
	
	public void buildTetIds(){
		System.out.println("OppositeVertex.buildTetIds()");
		HashSet<Tet> set = new HashSet<>();
		HashSet<Tet> s = new HashSet<>();
		HashSet<Tet> res = new HashSet<>();
		set.add(new Tet(0));
		while (!set.isEmpty()){
			HashSet<Tet> temp = new HashSet<>();
			for (Tet i : set){
				for (int o=0;o<4;o++){
					Tet t;
					try {
						t=opposite(new Face(i,o)).t;
						if (!s.contains(t)){
							s.add(t);
							if (t.interior)
								res.add(t);
							temp.add(t);
						}
						
					} catch (BorderFaceException e) {
					}
				}
			}
			set=temp;
		}
		tetids = res.toArray(new Tet[0]);
		Arrays.sort(tetids,new Comparator<Tet>() {
			@Override
			public int compare(Tet o1, Tet o2) {
				return -o2.hashCode()+o1.hashCode();//ordre croissant
			}
		});
	}
	
	
	@Override
	public int maxTetID() {
		if (tetids!=null)
			return maxfaces+tetids.length;
		return 2*maxTet+maxfaces;
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
//		System.out.println(f+"  "+4*maxTetID()+"  "+maxfaces+"  "+tetids.length);
		if (f>=4*maxTetID()) return f-4*maxTetID();
		return opposite(new Face().fromInt(f)).toInt();
	}
	@Override
	public Integer V(int v) {
		return new Tet().fromInt(v/4).Vertex(v%4);
	}
	@Override
	public int[] Vertices(int t) {
		return new Tet().fromInt(t).Vertices();
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
		return new Iterator<Integer>() {
			int t=0;
			@Override
			public boolean hasNext() {
				if (tetids.length==0) return t<border.sizeOfFaces();
				return t<maxfaces+tetids.length;
			}

			@Override
			public Integer next() {
				if (t<border.sizeOfFaces())
					while (t<border.sizeOfFaces()&&!isABorderTet(t))t++;
				if (t>=border.sizeOfFaces()&&t<maxfaces) t=maxfaces;
				if (t>=maxfaces){
					return t++;
				}
				return t++;
			}
			
		};
	}
	class Tet {
		Boolean interior;
		int[] t=new int[4];
		Tet(){};
		Tet(int f){
			interior=false;
			t[0]=f;
		}
		Tet(int v0,int v1,int v2,int v3){
			interior=true;
			t[0]=v0;t[1]=v1;t[2]=v2;t[3]=v3;
		}
		Tet(int[]tab){
			interior=true;
			t=tab;
		}
		/**
		 * return the four vertices of a tetrahedron
		 * @param tetid : tet id
		 * @return vertex ids Array
		 */
		int[] Vertices(){
			int[] res = new int[4];
			if (interior==null){
				res[0] = -1;
				res[1] = border.getVertexID(t[0],0);
				res[2] = border.getVertexID(t[0],1);
				res[3] = border.getVertexID(t[0],2);
				return res;
			}
			if (!interior) {
				res[0] = oppositeVertex[t[0]];
				res[1] = border.getVertexID(t[0],2);
				res[2] = border.getVertexID(t[0],1);
				res[3] = border.getVertexID(t[0],0);
				return res;
			}else{
				return t;
			}
		}
		/**
		 * Vertex operation 
		 * @param tetid : tet id
		 * @param relativeVertex : relative vertex id 
		 * @return vertex id
		 */
		int Vertex(int i){
//			if (interior==null){
//				switch (i) {
//				case 0:
//					return -1;
//				case 1:
//					return border.getVertexID(t[0],0);
//				case 2:
//					return border.getVertexID(t[0],1);
//				case 3:
//					return border.getVertexID(t[0],2);
//				default:
//					return -1;
//				}
//			}
//			else
			if (!interior) {
				switch (i) {
				case 0:
					return oppositeVertex[t[0]];
				case 1:
					return border.getVertexID(t[0],2);
				case 2:
					return border.getVertexID(t[0],1);
				case 3:
					return border.getVertexID(t[0],0);
				default:
					return -1;
				}
			}else{
				return t[i];
			}
		}
		int toInt(){
			if (interior==null) return t[0]+maxTetID();
			if (!interior) return t[0];
			return maxfaces+rank();
		}
//		int tempid(int v0, int v1, int v2, int v3){
//			if (v0==v1||v0==v2||v0==v3||v1==v2||v1==v3||v2==v3) throw new Error("degenerated tet");
//			if (v0<v1&&v0<v2&&v0<v3){
//				if (v1<v2&&v1<v3){
//					if (v2<v3){
//						return v0+maxfaces/2*(v1+maxfaces/2*(v2+maxfaces/2*v3));
////						int id;
////						int k=0;
////						id=(int)Math.pow(maxDegree,3)*v0;
////						Integer[] verNeigh = VertexNeighbor(v0).toArray(new Integer[0]);
////						for (int j=0;j<verNeigh.length;j++){
////							if (verNeigh[j]==v1){k++;
////							id+=(int)Math.pow(verNeigh.length,2)*j;
//////						System.out.println(j);
////							}
////							if (verNeigh[j]==v2){k++;
//////						System.out.println(j);
////							id+=verNeigh.length*j;
////							}
////							if (verNeigh[j]==v3){k++;
//////						System.out.println(j);
////							id+=j;
////							}
////						}
////						id+=maxfaces;
////						if (id<0) throw new Error();
////						if (k!=3) throw new Error("vertexNeighbor Problem "+k+"  "+v0+","+v1+","+v2+","+v3);
////						return id;
//					}
//					else return tempid(v0, v1, v3, v2);
//				}
//				else return tempid(v0, v2, v3, v1);
//			}
//			else if(v1<v0&&v1<v2&&v1<v3){
//				return tempid(v1, v0, v3, v2);
//			}
//			else if(v2<v0&&v2<v1&&v2<v3){
//				return tempid(v2,v3,v0,v1);
//			}
//			else {
//				return tempid(v3,v2,v1,v0);
//			}
//		}
		Tet fromInt(int tetid){
			if (tetid > maxTetID()){
				interior=null;
				t[0]=tetid-maxTetID();
			}
			else if (tetid < maxfaces){
				if (tetid>border.sizeOfFaces()) throw new Error(""+tetid+"  "+border.sizeOfFaces());
				interior=false;
				t=buildBorderTet(tetid).t;
			}
			else{
				this.interior=true;
				this.t = tetids[tetid-maxfaces].t;
//				int[] res = new int[4];
//				int tet = tetid-maxfaces;
//				res[0]=(int) (tet/Math.pow(maxDegree, 3));
//				Set<Integer> set = VertexNeighbor(res[0]);
////				System.out.println(set);
//				Integer[] s = set.toArray(new Integer[0]);
//				for (int i=0;i<3;i++){
//					res[3-i]=tet%s.length;
//					tet=tet/s.length;
//				}
//				for (int i=1;i<4;i++){
//					res[i]=s[res[i]];
//				}
//				this.t=res;
			}
			return this;
		}
		public String toString(){
			int[] vs = Vertices();
			return "["+vs[0]+","+vs[1]+","+vs[2]+","+vs[3]+"]";
		}
		@Override
		public boolean equals(Object o){
			Tet tet = (Tet) o;
//			if (interior!=tet.interior)return false;
//			if (!interior) return this.t[0]==tet.t[0];
			int k=0;
			for (int i=0;i<4;i++)
				for (int j=0;j<4;j++)
					if (Vertex(i)==tet.Vertex(j))k++;
			return k==4;//tempid(this.t[0], this.t[1], this.t[2], this.t[3])==tempid(tet.t[0], tet.t[1], tet.t[2], tet.t[3]);
		}
		@Override
		public int hashCode(){
			if (interior)
				return t[0]+t[1]+t[2]+t[3];
			return t[0];
		}
		private int rank(int min,int max){
			if (max-min<2)return min;
			if (tetids[min].hashCode()==tetids[max-1].hashCode())
				for (int i=min;i<max;i++){
					if (tetids[i].equals(this)) return i;
				}
			if (tetids[(min+max)/2].hashCode()<this.hashCode())return rank((min+max)/2,max);
			if (tetids[(min+max)/2].hashCode()>this.hashCode()) return rank(min,(min+max)/2);
			int k= (min+max)/2;
			while (tetids[k].hashCode()==this.hashCode()){
				if (tetids[k].equals(this)) return k;
				k++;
			}
			k= (min+max)/2-1;
			while (tetids[k].hashCode()==this.hashCode()){
				if (tetids[k].equals(this)) return k;
				k--;
			}
			return -1;
		}
		public int rank(){
			if (interior==null||!interior)return -1;
			return rank(0,tetids.length);
		}
//		public boolean eq(Tet tet){
//			int[] t1 = t.clone();
//			int[] t2 = tet.t.clone();
//			Arrays.sort(t1);
//			Arrays.sort(t2);
//			for (int i=0;i<4;i++){
//				if (t1[i]!=t2[i])return false;
//			}
//			return true;
//		}
		//O(ln(tetid.size))
	}
	class Face{
		Tet t;
		int relf;
		public Face() {}
		public Face(Tet t, int relf) {
			this.t = t;
			this.relf = relf;
		}
		int toInt(){
			return 4*t.toInt()+relf;
		}
		Face fromInt(int faceid){
			if (faceid>4*maxTetID()){
				t = new Tet();t.fromInt(faceid/4);
				relf=0;
				return this;
			}
			t = new Tet();t.fromInt(faceid/4);
			relf= faceid%4;
			return this;
		}
		public String toString(){
			int[] vs = t.Vertices();
//			return relf+"";
			return "["+vs[(relf+1)%4]+","+vs[(relf+2+(1+relf)%2)%4]+","+vs[(relf+3-(1+relf)%2)%4]+"]";
		}
	}
}