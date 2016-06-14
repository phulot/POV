package oppositeVertexApartements;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import POV.BorderFaceException;
import Triangulations.pt;
import Triangulations.vec;
import NonManifoldTriangulation.Vlist;
import cornerDS.faceOperators;

public class OppositeVertex implements faceOperators, Iterable<Integer>{
	Vlist border;
	HashMap<Integer,Set<Integer>> interiorEdges;
//	HashMap<Integer,Set<Integer>> interiorEdgesapt;
	Integer[] oppositeVertexfrontwall;
	Integer[] oppositeVertexbackwall;
	Integer[] oppositeVertexfrontwindow;
	Integer[] oppositeVertexbackwindow;
	HashMap<Integer,Set<Integer>> oppositeFaces;
//	HashMap<Integer,Set<Integer>> oppositeFaces;
	//TODO change tetid in int[][]
	Tet [] tetids;
	int maxTet;
	int maxfaces;
	int regularity;
	int walls;int mainWindows;
	
	public OppositeVertex(){};
//	public OppositeVertex(Vlist border, HashMap<Integer, Set<Integer>> interiorEdges, Integer[] oppositeVertexfront, Integer[] oppositeVertexback,
//			int maxTet) {
//		super();
//		this.border = border;
//		this.interiorEdges = interiorEdges;
//		this.oppositeVertexfront = oppositeVertexfront;
//		this.oppositeVertexback = oppositeVertexback;
//		this.maxTet = maxTet;
//		System.out.println("size"+oppositeVertexback.length);
//	}
	
	public double computeRegularity(){
		double wall=0;
		for (int i=0;i<tetids.length;i++){
			int k=0;
			for (int j=0;j<4;j++){
				try {
					if (opposite(new Face(tetids[i],j)).t.interior)
						k++;
				} catch (BorderFaceException e) {
				}
			}
			if (k==2) wall++;
			if (k==3) wall+=3;
			if (k==4) wall+=6;
		}
		return wall/(tetids.length*4);
	}
	public int storageCost(){
//		return 2*border.sizeOfFaces()+oppositeVertex.length+interiorEdges.size();
//		return border.storageCost()+oppositeVertex.length+interiorEdges.size();
		return hashMapSize(interiorEdges)+hashMapSize(oppositeFaces)+oppositeVertexfrontwall.length+oppositeVertexfrontwindow.length+oppositeVertexbackwall.length+oppositeVertexbackwindow.length+2*tetids.length+border.storageCost();
	}
	
	/**
	 * tip vertex of face, on isfront side
	 * @param face: face id
	 * @param isfront: side
	 * @return vertex id
	 */
	public int tipVertex(int face, boolean isfront){
		if (isfront){
			if (face<walls)
				return oppositeVertexfrontwall[face];
			else if (face<walls+mainWindows)return oppositeVertexfrontwindow[face-walls];
			return -1;
		}
		else {
			if (face<walls)
				return oppositeVertexbackwall[face];
			else if (face<walls+mainWindows)return -1;
			return oppositeVertexbackwindow[face-walls-mainWindows];
		}
	}
	/**
	 * tip vertex of the corner : of corner's face on the side of the corner.
	 * @param corner: corner id
	 * @return vertex id
	 */
	public int tipVertex(int k, int f, boolean front){
//		System.out.println(front);
//		assert tipVertex(f, front)!=-1;
		int i= tipVertex(border.neighbor(k,f,front));
//		assert i!=-1;
		return i;
	}
	
	public int tipVertex(int corner){
		return tipVertex(Vlist.tc(corner), Vlist.isfVl(corner));
	}
	
	public void buildOppositeFaces(){
		oppositeFaces = new HashMap<>();
		for (int i=0;i<border.sizeOfFaces();i++){
			boolean bt=true,bf=true;
			if (tipVertex(i,true)!=-1)
				for (Integer wface : border.incidentCorner(tipVertex(i,true))){
					for (int k=0;k<3;k++){
						if (tipVertex(wface/3,true)==border.vface(i, k)||tipVertex(wface/3,false)==border.vface(i, k))
							bt=false;
					}
				}
			else bt=false;
			if (tipVertex(i,false)!=-1)
				for (Integer wface : border.incidentCorner(tipVertex(i,false))){
					for (int k=0;k<3;k++){
						if (tipVertex(wface/3,true)==border.vface(i, k)||tipVertex(wface/3,false)==border.vface(i, k))
							bf=false;
					}
				}
			else bf=false;
			if (bt){
				int v = tipVertex(i,true);
				Set<Integer> s = oppositeFaces.get(v);
				if (s==null){s=new HashSet<Integer>();oppositeFaces.put(v, s);}
				s.add(i);
			}
			if (bf){
				int v = tipVertex(i,false);
				Set<Integer> s = oppositeFaces.get(v);
				if (s==null){s=new HashSet<Integer>();oppositeFaces.put(v, s);}
				s.add(i);
			}
		}
	}
	
	public int hashMapSize(HashMap<Integer,Set<Integer>> map){
		int k=0;
		for (Integer e:map.keySet())
			k+=map.get(e).size()+1;
		return 4/3*k;
	}

	/**
	 * return all vertex ids of v's neighbors
	 * @param v : vertex id
	 * @return set of vertex ids
	 */
	public Set<Integer> VertexNeighbor(int v){
		Set<Integer> res = new HashSet<>();
		for (Integer w:border.incidentCorner(v)){
			// add neighbors on the Vlist surface.
			res.add(border.vw(Vlist.nw(w)));
			res.add(border.vw(Vlist.pw(w)));//can be removed
			int ot = tipVertex(w/3,true);
			if (ot!=-1){
				//add front tip vertex
				res.add(ot);
				// add front tip vertex neighbors
				for (Integer wface : border.incidentCorner(ot))
					if (tipVertex(wface/3,true)==v||tipVertex(wface/3,false)==v){
						res.add(border.vw(Vlist.nw(wface)));
						res.add(border.vw(Vlist.pw(wface)));
					}
			}
			int of=tipVertex(w/3,false);
			if (of!=-1){
				//add back tip vertex
				res.add(of);
				//add its neighbors
				for (Integer wface : border.incidentCorner(of))
					if (tipVertex(wface/3,true)==v||tipVertex(wface/3,false)==v){
						res.add(border.vw(Vlist.nw(wface)));
						res.add(border.vw(Vlist.pw(wface)));
					}
			}

		}
		//add unreachable faces
		if (oppositeFaces.get(v)!=null)
//			res.addAll(oppositeFaces.get(v));
			for (int f:oppositeFaces.get(v)){
				res.add(border.vface(f,0));
				res.add(border.vface(f,1));
				res.add(border.vface(f,2));
			}
		//add pilars
		if (interiorEdges.get(v)!=null)
			res.addAll(interiorEdges.get(v));
		res.remove((Integer)v);
		if (res.contains(-1))throw new Error();
//		System.out.println(v+" "+res);
		return res;
	}
	
	public boolean isNeighbor(int v,int u){
		if (u==v)return false;
		if (interiorEdges.get(u)!=null&&interiorEdges.get(u).contains(v)) return true;
//		if (oppositeFaces.get(u)!=null&&oppositeFaces.get(u).contains(v)) return true;
		for (Integer w:border.incidentCorner(u)){
			if (border.vw(Vlist.nw(w))==v)return true;
			if (border.vw(Vlist.pw(w))==v)return true;
			if (tipVertex(w/3,true)==v) return true;
			if (tipVertex(w/3,false)==v) return true;
		}
		for (Integer w:border.incidentCorner(u)){
			int o;
			if ((o=tipVertex(w/3,true))!=-1)
				for (Integer wface : border.incidentCorner(o))
					if (tipVertex(wface/3,true)==u||tipVertex(wface/3,false)==u){
						if (border.vw(Vlist.nw(wface))==v)return true;
						if (border.vw(Vlist.pw(wface))==v)return true;
					}
			if ((o=tipVertex(w/3,false))!=-1)
				for (Integer wface : border.incidentCorner(o))
					if (tipVertex(wface/3,true)==u||tipVertex(wface/3,false)==u){
						if (border.vw(Vlist.nw(wface))==v)return true;
						if (border.vw(Vlist.pw(wface))==v)return true;
					}
		}
		if (oppositeFaces.get(u)!=null)
			for (int f:oppositeFaces.get(u)){
				if (border.vface(f,0)==v)return true;
				if (border.vface(f,1)==v)return true;
				if (border.vface(f,2)==v)return true;
			}
		return false;
	}
	
	public String NeighborType(int v,int u){
		if (u==v)return "not Neighbor";
		if (interiorEdges.get(u)!=null&&interiorEdges.get(u).contains(v)) return "interior edge";
//		if (oppositeFaces.get(u)!=null&&oppositeFaces.get(u).contains(v)) return "opposite face";
		for (Integer w:border.incidentCorner(u)){
			if (border.vw(Vlist.nw(w))==v)return "incident face";
			if (border.vw(Vlist.pw(w))==v)return "incident face";
			if (tipVertex(w/3,true)==v||tipVertex(w/3,false)==v) return "opposite vertex";
		}
		for (Integer w:border.incidentCorner(u)){
			for (Integer wface : border.incidentCorner(tipVertex(w/3,true)))
				if (tipVertex(wface/3,true)==u||tipVertex(wface/3,false)==u){
					if (border.vw(Vlist.nw(wface))==v)return "ring";
					if (border.vw(Vlist.pw(wface))==v)return "ring";
				}
			for (Integer wface : border.incidentCorner(tipVertex(w/3,false)))
				if (tipVertex(wface/3,true)==u||tipVertex(wface/3,false)==u){
					if (border.vw(Vlist.nw(wface))==v)return "ring";
					if (border.vw(Vlist.pw(wface))==v)return "ring";
				}
		}
		if (oppositeFaces.get(u)!=null)
			for (int f:oppositeFaces.get(u)){
				if (border.vface(f,0)==v)return "opposite face";
				if (border.vface(f,1)==v)return "opposite face";
				if (border.vface(f,2)==v)return "opposite face";
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
	//TODO more intellingent algorithm
	private Tet buildBorderTet(Integer f,boolean main){
		int ID =f;
		Tet temp = new Tet(f,main);
		for (int i=0;i<0;i++){
			Face tmp = new Face(temp,i+1);
			int ff= tmp.isWindow();
			if (ff>ID){
				ID=ff;
			}
//			int ff;
//			int bor = border.neighbor(i,f,isFront);
//			for (int j=0;j<3;j++){
//				if (tipVertex(bor)==border.vface(f, j)){
//					if ((ff=Vlist.t(bor))>ID){
//						ID=ff;front=Vlist.isfVl(bor)==0;
//					}
//				}
//			}
		}
		assert tipVertex(ID, main)!=-1;
		return new Tet(ID,main);
	}
	
	boolean isATet(int t){
		if (t>=0) {
			if (t<walls+mainWindows)
				return isABorderTet(t, true);
			else return false;
		}
		if (-t-1<walls||-t-1>walls+mainWindows)
			return isABorderTet(-t-1, false);
		return false;
	}
	
	private boolean isABorderTet(int f, boolean  main){
		int ID =f;
		Tet temp = new Tet(f,main);
		for (int i=0;i<3;i++){
			Face tmp = new Face(temp,i+1);
			int ff= tmp.isWindow();
			if (ff>ID){
				ID=ff;
			}
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
	public Tet buildInteriorTet(int v0,int v1,int v2,int v3, boolean main){
		assert (v0!=v1&&v0!=v2&&v0!=v3&&v1!=v2&&v1!=v3&&v2!=v3);
		assert (v0!=-1&&v1!=-1&&v2!=-1&&v3!=-1);
		return new Tet(v0,v1,v2,v3, main);
	}
	public Tet buildInteriorTet(int[] tab, boolean main){
		assert (tab[0]!=tab[1]&&tab[0]!=tab[2]&&tab[0]!=tab[3]&&tab[1]!=tab[2]&&tab[1]!=tab[3]&&tab[2]!=tab[3]);
		assert (tab[0]!=-1&&tab[1]!=-1&&tab[2]!=-1&&tab[3]!=-1);
		return new Tet(tab,main);
	}
	
	/**
	 * opposite operation on faces
	 * @param f : face id
	 * @return face id
	 */
	public Face opposite(Face f) throws BorderFaceException {
		Tet tet = f.t;
		int i=f.relf;
		int triangle;
		if ((triangle=f.isWindow())!=-1){
			int o;
			if ((o=tipVertex(triangle, !tet.main))!=-1){
				tet = buildBorderTet(triangle, !tet.main);
				int[] tab = tet.Vertices();
				for (int op=0;op<4;op++){
					if (tab[op]==o) return new Face(tet,op);
				}
			}
			if (tetids!=null){
				throw new BorderFaceException(f.toInt()+4*maxTetID());
			}
			else {
				throw new BorderFaceException(4*maxTetID());
			}
		}
		Set<Integer> s = commonNeighbors(tet, i);
		
		if (s.size()==0){
//			System.out.println("false border " + oppositeVertex(border.neighbor(0,tet.t[0]))+"  "+ oppositeVertex(border.neighbor(1,tet.t[0]))+"  "+ oppositeVertex(border.neighbor(2,tet.t[0])));
//			Set<Integer> set = VertexNeighbor(tet.Vertex((i + 1) % 4));
			System.out.println(tet.Vertex((i + 2) % 4)+"    "+VertexNeighbor(tet.Vertex((i + 2) % 4)));
			System.out.println(tet.Vertex((i + 3) % 4)+"    "+VertexNeighbor(tet.Vertex((i + 3) % 4)));
//			set.retainAll(VertexNeighbor(tet.Vertex((i + 2) % 4)));
//			set.retainAll(VertexNeighbor(tet.Vertex((i + 3) % 4)));
//			System.out.println(set+"   "+tet.Vertex(i)+"  "+tet);
//			pt A=border.G(tet.Vertex((i+1)%4));
//			vec n = Utils.normal(tet.Vertex((i+1)%4), tet.Vertex((i+2)%4), tet.Vertex((i+3)%4), border);
//			for (Integer l:set){
////				double d =Utils.Side(n, vec.V(border.G(l),A))*Utils.Side(n,vec.V(border.G(tet.Vertex(i)),A));
//				System.out.println(VertexNeighbor(l));
//			}
//			throw new BorderFaceException(4*maxTetID());
			if (!f.t.interior){
				System.out.println(border.FaceToSting(f.t.t[0]));
				System.out.println(border.FaceToSting(border.neighbor(0, f.t.t[0], false)/6)+" "+tipVertex(border.neighbor(0, f.t.t[0], false)/6, f.t.main));
				System.out.println(border.FaceToSting(border.neighbor(1, f.t.t[0], true)/6)+" "+tipVertex(border.neighbor(1, f.t.t[0], true)/6, f.t.main));
				System.out.println(border.FaceToSting(border.neighbor(1, f.t.t[0], false)/6)+" "+tipVertex(border.neighbor(1, f.t.t[0], false)/6, f.t.main));
				System.out.println(border.FaceToSting(border.neighbor(2, f.t.t[0], true)/6)+" "+tipVertex(border.neighbor(2, f.t.t[0], true)/6, f.t.main));
				System.out.println(border.FaceToSting(border.neighbor(2, f.t.t[0], false)/6)+" "+tipVertex(border.neighbor(2, f.t.t[0], false)/6, f.t.main));
				System.out.println(border.FaceToSting(border.neighbor(0, f.t.t[0], true)/6)+" "+tipVertex(border.neighbor(0, f.t.t[0], true)/6, f.t.main));
			}
			System.out.println(tet);
			throw new Error(""+f+"  "+f.t.interior+"  "+VertexNeighbor(tet.Vertex((i+1)%4)));
		}
		if (s.size()==1){
			if (!f.t.interior){
//				System.out.println("window");
			}
			if (s.iterator().next()==-1) throw new Error();
			Face o = oppositeFace(tet, s.iterator().next(),i);
			return o;
		}
		else {
//			throw new BorderFaceException(4*maxTetID());
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
					return o;
				}
			}
			Face o= oppositeFace(tet, s.iterator().next(), i);
			return o;
//			throw new Error("opposite Error"+s);
		}
	}
//	private Set<Integer> commonNeighbors(Tet tet, int i) {
//		Set<Integer> ss = VertexNeighbor(tet.Vertex((i + 1) % 4));
//		vec n = Utils.normal(tet.Vertex((i+1)%4), tet.Vertex((i+2)%4), tet.Vertex((i+3)%4), border);
//		ss = Utils.removeSide(tet.Vertex(i), tet.Vertex((i + 1) % 4), ss, n, false, border);
//		Set<Integer> s = new HashSet<>();
//		//TODO optimize this operation
//		for (Integer ver : ss)
//			if (ver!=tet.Vertex(i))
//				if (isNeighbor(ver, tet.Vertex((i + 2) % 4)))
//					if (isNeighbor(ver, tet.Vertex((i + 3) % 4)))
//						s.add(ver);
//		return s;
//	}
	private Set<Integer> commonNeighbors(Tet tet, int i) {
		Set<Integer> s = VertexNeighbor(tet.Vertex((i + 1) % 4));
		s.remove(tet.Vertex(i));
		s.retainAll(VertexNeighbor(tet.Vertex((i + 2) % 4)));
		s.retainAll(VertexNeighbor(tet.Vertex((i + 3) % 4)));
		//TODO optimize this operation
		vec n = Utils.normal(tet.Vertex((i+1)%4), tet.Vertex((i+2)%4), tet.Vertex((i+3)%4), border);
		s = Utils.removeSide(tet.Vertex(i), tet.Vertex((i + 1) % 4), s, n, false, border);
		return s;
	}
	
//	static boolean isFront(int a,int b,int c, int t, pt[] G, pt E){
//		vec n = vec.N(vec.V(G[a],G[b]), vec.V(G[a],G[c]));
//		return vec.dot(n,vec.V(G[a],G[t]))*vec.dot(n,vec.V(G[a],E))>0;
//	}

	private Face oppositeFace(Tet tet, Integer v,int o) {
		Tet id=null;
		int[] vert = tet.Vertices();
		Iterable<Integer> wcol = border.incidentCorner(v);
		for (Integer w:wcol){
			int k=0;
			for (int j=0;j<4;j++)
				if (j!=o)
					if (border.vw(Vlist.nw(w))==vert[j]||border.vw(Vlist.pw(w))==vert[j]){
						k++;
					}
			if (k==2){
				if (tipVertex(w/3, tet.main)==-1) throw new Error(w/3+" ("+v+","+vert[(o+1)%4]+","+vert[(o+2)%4]+","+vert[(o+3)%4]+") ("+border.vw(w)+","+border.vw(Vlist.nw(w))+","+border.vw(Vlist.pw(w))+") "+tet.interior+"  "+tet.main);
				id = buildBorderTet(w/3,tet.main);
				break;
			}
		}
		if (id==null){
			int[] idvert = new int[]{v,vert[(o+1)%4],vert[(o+2)%4],vert[(o+3)%4]};
			Arrays.sort(idvert);
			if (vec.m(vec.V(border.G(idvert[0]),border.G(idvert[1])),vec.V(border.G(idvert[0]),border.G(idvert[2])),vec.V(border.G(idvert[0]),border.G(idvert[3])))>0)
				id = buildInteriorTet(idvert,tet.main);//TODO verifier orientation
			else id = buildInteriorTet(idvert[0],idvert[1],idvert[3],idvert[2],tet.main);
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
		HashSet<Tet> set = new HashSet<>();
		HashSet<Tet> s = new HashSet<>();
		HashSet<Tet> res = new HashSet<>();
		set.add(new Tet().fromInt(this.iterator().next()));
		while (!set.isEmpty()){
			HashSet<Tet> temp = new HashSet<>();
			for (Tet i : set){
//				System.out.println("build "+i);
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
		if (f>=4*maxTetID()) return f-4*maxTetID();
		Face ff=new Face().fromInt(f);
		Face fa = opposite(ff);
		return fa.toInt();
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
					while (t<border.sizeOfFaces()&&!isATet(t)){
						if (t>=0) t=-t-1;
						else t=-t;
					}
				if (t>=border.sizeOfFaces()&&t<maxfaces) t=maxfaces;
				if (t>=maxfaces){
					return t++;
				}
				if (isATet(t)){
					if (t>=0){
						t=-t-1;
						return -t-1;
					}
					t=-t;
					return -t;
				}
				return t++;
			}
			
		};
	}
	class Tet {
		Boolean interior;
		boolean main;
		int[] t=new int[4];
		Tet(){};
		Tet(int f,boolean main){
			interior=false;
			this.main=main;
			t[0]=f;
			Vertices();
//			System.out.println(main);
		}
		Tet(int v0,int v1,int v2,int v3, boolean main){
			this.main=main;
			assert v0!=-1&&v1!=-1&&v2!=-1&&v3!=-1;
			interior=true;
			t[0]=v0;t[1]=v1;t[2]=v2;t[3]=v3;
//			System.out.println(main);
		}
		Tet(int[] tab, boolean main){
			this.main=main;
			interior=true;
			t=tab;
//			System.out.println(main);
		}
		/**
		 * return the four vertices of a tetrahedron
		 * @param tetid : tet id
		 * @return vertex ids Array
		 */
		int[] Vertices(){
			int[] res = new int[4];
			if (!interior) {
				res[0] = tipVertex(t[0],main);
				res[1] = border.vface(t[0],2);
				res[2] = border.vface(t[0],1);
				res[3] = border.vface(t[0],0);
//				System.out.println("back "+t[0]+" "+oppositeVertexback[t[0]]);
//				System.out.println("front "+t[0]+" "+oppositeVertexfront[t[0]]);
				if (res[0]==-1) throw new Error(main +"  "+ res[1]+" "+res[2]+" "+res[3]);
				return res;
			}else{
				return t;
			}
		}
		
		boolean hasVertex(int v){
			int[] tab = Vertices();
			for (int i=0;i<4;i++){
				if (v==tab[i]) return true;
			}
			return false;
		}
		/**
		 * Vertex operation 
		 * @param tetid : tet id
		 * @param relativeVertex : relative vertex id 
		 * @return vertex id
		 */
		int Vertex(int i){
			if (!interior) {
				switch (i) {
				case 0:
					assert tipVertex(t[0],main)!=-1;
					return tipVertex(t[0],main);
				case 1:
					return border.vface(t[0],2);
				case 2:
					return border.vface(t[0],1);
				case 3:
					return border.vface(t[0],0);
				default:
					return -1;
				}
			}else{
				return t[i];
			}
		}
		int toInt(){
			if (interior==null) return t[0]+maxTetID();
			if (!interior) if (main) return t[0]; else return -t[0]-1;
			if (main) return maxfaces+rank(); else return -maxfaces-rank()-1;
		}
		public Tet fromInt(int tetid){
			main=tetid>=0;
			if (tetid<0) tetid=-tetid-1;
//			if (tetid > maxTetID()){
//				interior=null;
//				t[0]=tetid-maxTetID();
//			}
			if (tetid < maxfaces){
				if (tetid>border.sizeOfFaces()) throw new Error(""+tetid+"  "+border.sizeOfFaces());
				interior=false;
				t=buildBorderTet(tetid,main).t;
			}
			else{
				this.interior=true;
				this.t = tetids[tetid-maxfaces].t;
			}
			Vertices();
//			System.out.println(main);
			return this;
		}
		public String toString(){
			int[] vs = Vertices();
			return "["+vs[0]+","+vs[1]+","+vs[2]+","+vs[3]+"]";
		}
		@Override
		public boolean equals(Object o){
			Tet tet = (Tet) o;
			int k=0;
			for (int i=0;i<4;i++)
				for (int j=0;j<4;j++)
					if (Vertex(i)==tet.Vertex(j))k++;
			return k==4;
		}
		@Override
		public int hashCode(){
			if (interior)
				return t[0]+t[1]+t[2]+t[3];
			return t[0];
		}
		private int rank(int min,int max){
			if (max-min<2) return min;
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
			int tet=t.toInt();
			if (tet>=0) return 4*tet+relf;
			return 4*t.toInt()-relf;
		}
		Face fromInt(int faceid){
			t = new Tet().fromInt(faceid/4);
			if (faceid>0){
				relf= faceid%4;
			}
			else {
				relf = (-faceid)%4;
			}
			return this;
		}
		public String toString(){
			int[] vs = t.Vertices();
//			return relf+"";
			return "["+vs[(relf+1)%4]+","+vs[(relf+2+(1+relf)%2)%4]+","+vs[(relf+3-(1+relf)%2)%4]+"]";
		}
		public int isWindow(){
			if (!t.interior){
				if (relf==0){
					return t.t[0];
				}
				int v=t.Vertex(relf);
				for (int k=0;k<3;k++){
//					System.out.println("windo "+t+"  "+relf+"  "+tipVertex(k,t.t[0],t.front)+"  "+Vlist.tc(border.neighbor(k,t.t[0],t.front)));
//					System.out.println("windo "+tipVertex(border.neighbor(k,t.t[0],!t.front))+"  "+border.neighbor(k,t.t[0],t.front));
					int Bf = Vlist.tc(border.neighbor(k,t.t[0],t.main));
					if (tipVertex(Bf,t.main)==v){
						return Bf;
					}
					Bf =Vlist.tc( border.neighbor(k,t.t[0],!t.main));
					if (tipVertex(Bf,t.main)==v){
						return Bf;
					}
//					if (tipVertex(k,t.t[0],!t.main)==t.Vertex(relf)){
//						return Vlist.tc(border.neighbor(k,t.t[0],!t.main));
//					}
				}
				int[] vert= t.Vertices();
				for (int vv=0;vv<3;vv++){
					v=vert[(relf+vv+1)%4];
					Iterable<Integer> wcol = border.incidentCorner(v);
					for (Integer w:wcol){
						int k=0;
						for (int j=0;j<4;j++)
							if (j!=relf)
								if (border.vw(Vlist.nw(w))==vert[j]||border.vw(Vlist.pw(w))==vert[j]){
									k++;
								}
						if (k==2){
							System.out.println("wrong window");
							return w/3;
						}
					}
				}
			}
			return -1;
		}
//		public boolean isFront(){
//			int f;
//			if ((f=isWindow())!=-1){
//				int[] tab = t.Vertices();
//				return border.clockWise(f, border.G[tab[relf]]);
////				System.out.println(tab[0]+", "+tab[1]+", "+tab[2]+", "+tab[3]);
////				vec n = vec.N(vec.V(border.G(tab[(relf+1)%4]),border.G(tab[(relf+2)%4])), vec.V(border.G(tab[(relf+1)%4]),border.G(tab[(relf+3)%4])));
////				return vec.dot(n, vec.V(border.G(tab[(relf+1)%4]),border.G(tab[relf])))*vec.dot(n, vec.V(border.G(tab[(relf+1)%4]),border.E))>0;
//			}
//			else {
//				throw new Error("not a window tet");
//			}
//		}
		public boolean equals(Object o) {
			Face f = (Face) o;
			return f.relf==relf&&f.t.equals(t);
		}
	}
}