package subsurface;
import java.util.ArrayList;

import POV.BorderFaceException;
import POV.POV;

public class divideMesh {
	ArrayList<POV> apartments;
	
	public divideMesh(subSurface s) {
		apartments = new ArrayList<POV>();
		POV p=s.getPov();
		int nt= p.nt;
		POV p0=new POV();
		p0.G=p.G;
		p0.nv=p.nv;
		p0.maxnv=p.maxnv;
		ArrayList<Integer> l=new ArrayList<Integer>();
		int k=0;
		for (int i=p.nt-1;i>=0;i--){
			if (s.mm[i]){
				for (int j=0;j<4;j++){
					l.add(p.V[4*i+j]);
				}
				k++;
				p.removeTetrahedron(i);
			}
		}
		p0.V = l.toArray(new Integer[0]);
		p0.nt=k;
		p0.maxnt=k;
		p0.maxnf=4*k;
		p0.nf=4*k;
		p0.createOtable();
		apartments.add(p0);
		for (int i=nt-1;i>=0;i--){
			s.mm[i]=i>=nt-k;
			for (int j=0;j<4;j++)
				s.marked[4*i+j]=s.mm[i];
		}
		while(p.nt>0){
			p.createOtable();
			boolean[] mm=new boolean[p.nt];
			int t=0;
			l=new ArrayList<Integer>();
			l.add(t);
			POV pi = new POV();
			pi.G=p.G;
			pi.nv=p.nv;
			pi.maxnv=p.maxnv;
			mm[0]=true;
			k=1;
			while (!l.isEmpty()){
				ArrayList<Integer> temp = new ArrayList<Integer>();
				for (Integer tet : l){
					for (int j=0;j<4;j++){
						try {
							if (!mm[p.tetraFromFace(p.O(4 * tet + j))]) {
								temp.add(p.tetraFromFace(p.O(4 * tet + j)));
								mm[p.tetraFromFace(p.O(4 * tet + j))]=true;
								k++;
							}
						} catch (BorderFaceException e) {
						}
					}
				}
				l=temp;
			}
			pi.nt=k;
			pi.maxnt=k;
			pi.maxnf=4*k;
			pi.nf=4*k;
			pi.V = new Integer[k*4];
			k=0;
			for (int i=p.nt-1;i>=0;i--){
				if (mm[i]){
					for (int j=0;j<4;j++){
						pi.V[k]=p.V[4*i+j];
						k++;
					}
					p.removeTetrahedron(i);
				}
			}
//			pi.refreshIntegers();
			pi.createOtable();
			apartments.add(pi);
		}
		System.out.println("apartment nbr : "+apartments.size());
		for (POV i:apartments){
			System.out.println(i.nt);
		}
		s.getPov().nt=nt;
//		p0.peal();
	}
}
