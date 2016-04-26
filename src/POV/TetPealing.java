package POV;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

import POV.BorderFaceException;
import POV.POV;

public class TetPealing {
	POV pov;
	
	public TetPealing(POV pov) {
		this.pov = pov;
	}
	
	void flipFalseYtets(){
		for (int i=0;i<12*pov.nt;i++){
			Set<Integer> l = pov.edgeNeighbors(i);
			if (l.size()==3&&!l.contains(-1))
				flipFalseYtets(l,i);
		}
	}
	
	void flipFalseYtets(Set<Integer> s,int c){
		Set<Integer> neigh= new HashSet<>();
		Set<Integer> ver1= new HashSet<>();
		Set<Integer> ver2= new HashSet<>();
		Set<Integer> neigh1 = new HashSet<>();
		ver1.add(pov.v(c));
		ver2.add(pov.v(pov.n(c)));
		for (Integer i:s){
			for (int j=0;j<4;j++){
				if (pov.V[4*i+j]!=pov.v(c))
					ver2.add(pov.V[4*i+j]);
				if (pov.V[4*i+j]!=pov.v(pov.n(c)))
					ver1.add(pov.V[4*i+j]);
				try {
					neigh.add(pov.tetraFromFace(pov.O(4*i+j)));
				} catch (BorderFaceException e) {
				}
			}
		}
		for (Integer i:s){
			neigh.remove((Integer)i);
		}
		for (Integer i:neigh){
			for (int j=0;j<4;j++){
				if (pov.V[4*i+j]==pov.v(c))
					neigh1.add(i);
			}
		}
		neigh.removeAll(neigh1);
		int[] V1=new int[]{-1,-1,-1,-1};
		int[] O1=new int[]{-1,-1,-1,-1};
		int[] V2=new int[]{-1,-1,-1,-1};
		int[] O2=new int[]{-1,-1,-1,-1};
		int k=0;
		for (Integer i:neigh1){
			O1[k]=i;
			for (Integer v:ver1){
				if (!isInTetra(v, i)){
					V1[k]=v;
					ver1.remove((Integer)v);
				}
			}
			k++;
		}
		k=0;
		for (Integer i:neigh){
			O2[k]=i;
			for (Integer v:ver2){
				if (!isInTetra(v, i)){
					V2[k]=v;
					ver2.remove((Integer)v);
				}
			}
			k++;
		}
		
	}
	
	boolean isInTetra(int v, int t){
		for (int i=0;i<4;i++){
			if (pov.V[4*t+i]==v)return true;
		}
		return false;
	}
	

	int removeDtets(){
		int c=0;
		for (int t=pov.nt-1;t>=0;t--){
			int k=0;
			for (int j=0;j<4;j++){
				try {
					pov.O(4*t+j);
				} catch (BorderFaceException e) {
					k++;
				}
			}
			if (k==4){
				pov.removeTetrahedron(t);
				c++;
			}
		}
		return c;
	}
	
	void removeYtets(){
		ArrayList<Integer> l = new ArrayList<Integer>();
		for (int i=0;i<pov.nt;i++){
			l.add(i);
		}
		removeYtets(l,new LinkedList<>());
	}
	
	/**
	 * remove Y tetrahedrons that are in the list  
	 * @param list : list of tets (all tets for example)
	 * @param s : list of known E tets (being updated)
	 * @return updated known E tets
	 */
	private LinkedList<Integer> removeYtets(ArrayList<Integer> list, LinkedList<Integer> s){
		PriorityQueue<Integer> l = new PriorityQueue<Integer>(new Comparator<Integer>(){
			@Override
			public int compare(Integer arg0, Integer arg1) {
				return arg1-arg0;
			}
		});
		l.addAll(list);
		int reltet=-1;
		while(!l.isEmpty()){
			int t=l.poll();
			int k=0;
			for (int j=0;j<4;j++){
				try {
					pov.O(4*t+j);
					reltet=j;
				} catch (BorderFaceException e) {
					k++;
				}
			}
			if (k==3){
				if (s.contains((Integer)t)) s.remove((Integer)t);
				if (s.contains((Integer) (pov.nt - 1))) {
					s.remove((Integer) (pov.nt - 1));
					s.add((Integer) t);
				}
				pov.invertTets(t, pov.nt-1);
				try {
					l.add(pov.tetraFromFace(pov.O(4*(pov.nt-1)+reltet)));
				} catch (BorderFaceException e) {
					e.printStackTrace();
				}
//				if (tet==nt-1){
//					l.add(t);
//				}
//				else l.add(tet);
				pov.removeTetrahedron(pov.nt-1);
//				c++;
			}
			else{
				if (t>=pov.nt) System.out.println(t);
				if (isEtet(t,pov))
					s.addFirst(t);
			}
//			}
//			l=temp;
		}
		return s;
	}
	
	static int getTetType(int t, POV pov){
		int k=0;
		int f0=-1,f1=-1;
		for (int j=0;j<4;j++){
			try {
				pov.O(4*t+j);
			} catch (BorderFaceException e) {
				if (f0==-1){
					f0=4*t+j;
				}else f1=4*t+j;
				k++;
			}
		}
		if (k==2){
			if (!pov.edgeNeighbors(pov.cornerOftetra(f0, f1)).contains(-1)){
				return 20;
			}
			return 21;
		}
		return k;
	}
	
	static boolean isEtet(int t, POV pov){
		int k=0;
		int f0=-1,f1=-1;
		for (int j=0;j<4;j++){
			try {
				pov.O(4*t+j);
			} catch (BorderFaceException e) {
				if (f0==-1){
					f0=4*t+j;
				}else f1=4*t+j;
				k++;
			}
		}
		if (k==2){
			if (!pov.edgeNeighbors(pov.cornerOftetra(f0, f1)).contains(-1)){
				return true;
			}
		}
		return false;
	}
	static boolean isYtet(int t,POV pov){
		int k=0;
		for (int j=0;j<4;j++){
			try {
				pov.O(4*t+j);
			} catch (BorderFaceException e) {
				k++;
			}
		}
		if (k==3){
			return true;
		}
		return false;
	}
	static boolean isEetet(int t,POV pov){
		int k=0;
		int f0=-1,f1=-1;
		for (int j=0;j<4;j++){
			try {
				pov.O(4*t+j);
			} catch (BorderFaceException e) {
				if (f0==-1){
					f0=4*t+j;
				}else f1=4*t+j;
				k++;
			}
		}
		if (k==2){
			if (pov.edgeNeighbors(pov.cornerOftetra(f0, f1)).contains(-1)){
				return true;
			}
		}
		return false;
	}
	static boolean isFtet(int t,POV pov){
		int k=0;
		for (int j=0;j<4;j++){
			try {
				pov.O(4*t+j);
			} catch (BorderFaceException e) {
				k++;
			}
		}
		if (k==1){
			return true;
		}
		return false;
	}
	/**
	 * return a E tet that can be removed
	 * @return tet ID
	 */
	int getEtet(){
		for (int i=0;i<pov.nt;i++){
			if (isEtet(i, pov)) return i;
		}
		System.out.println("E");
		return -1;
	}
	
	public void simplePeal(){
		System.out.println("nt : "+pov.nt);
//		int Y=0;
		removeYtets();
		LinkedList<Integer> S = new LinkedList<>();
		boolean b= true;
		int E=0;
		int D=0;
		int e=0;
		int e2=0;
		while(pov.nt!=0&&b){
			boolean bool=true;
			while (bool){
				if (S.isEmpty()){
					e2++;
					e = getEtet();
					bool=false;
				}
				else {
					e=S.pollFirst();
					bool = !isEtet(e,pov);
					if (bool) {
						System.out.println(getTetType(e,pov));
						S.remove((Integer)e);
					}
				}
			}
//			System.out.println("nt : "+nt);
			if (b=(e!=-1)){
				assert (e<pov.nt);
				ArrayList<Integer> l = new ArrayList<>();
				pov.invertTets(e, pov.nt-1);
				for (int j=0;j<4;j++)
					try {l.add(pov.tetraFromFace(pov.O(4*(pov.nt-1)+j)));} catch (BorderFaceException e1) {}
				E++;
				if (S.contains((Integer)e)) S.remove((Integer)e);
				if (S.contains((Integer) (pov.nt - 1))) {
					S.remove((Integer) (pov.nt - 1));
					S.addFirst((Integer) e);
				}
				pov.removeTetrahedron(pov.nt-1);
				S=removeYtets(l,S);
			}
		}
		D+= removeDtets();
		System.out.println("E : "+E);
		System.out.println("new E : "+e2);
//		System.out.println("Y : "+Y);
		System.out.println("D : "+D);
		if (pov.nt==0) System.out.println("pealing success");
		else System.out.println("pealing failed : "+pov.nt);
		System.out.println("genus "+pov.computegenus());
	}
	
	public void Cut1Loops(){
		
	}
}
