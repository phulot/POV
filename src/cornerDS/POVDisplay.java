package cornerDS;
import java.util.Iterator;

import Applet.*;
import POV.BorderCornerException;
import POV.pt;
import POV.vec;
import POV.TetPealing;

public class POVDisplay {

	protected cornerBasedDS pov;
	protected POVjava display; 
	/** current corner index */
	public int currentCorner = 0; 
	/** picked vertex index, */
	protected int pv = 0; 
	/** insertion vertex index */
	protected int iv = 0; 

	
	public POVDisplay(POVjava dis,cornerBasedDS p) {
		pov=p;
		display=dis;
	}
	
	/**
	 * opposite operation on current corner
	 */
	public void o() {
		try {
			currentCorner = pov.o(currentCorner);
		} catch (BorderCornerException e) {
			currentCorner = e.o;
		}
	}
	
	/**
	 * swing operation on current corner
	 */
	public void s() {
		currentCorner = pov.s(currentCorner);
	}

	/**
	 * draw a corner in blue
	 * @param corner id
	 */
	public void drawCorner(int c) {
		if (pov.DS.borderCorner(c)) {
			try {
				drawCorner(pov.o(c));
			} catch (BorderCornerException e) {
				e.printStackTrace();
			}
		} else {
			pt id = pov.DS.G(pov.v(c));
			pt cor = new pt(id);
			pt ver = new pt(id);
			vec v1 = vec.V(id, pov.DS.G(pov.v(pov.n(c))));
			ver.add(1.f / 3f, v1);
			vec v2 = vec.V(id, pov.DS.G(pov.v(pov.n(pov.n(c)))));
			cor.add(vec.V(1.f / 3f, vec.V(v1, v2)));
			display.noStroke();
			display.show(pov.DS.G(pov.v(c)), ver, cor);
			ver = new pt(id);
			ver.add(1f / 3, v2);
			display.show(pov.DS.G(pov.v(c)), ver, cor);
		}
	}

	/**
		draw the selected corner in blue
		used to navigate into the surface
	 */
	public void drawSelectedCorner() {
		drawCorner(currentCorner);
	}
	
	public void n() {
		currentCorner = pov.n(currentCorner);
	}
	
//	public void edgeContraction() {
//		pov.edgeContraction(currentCorner);
//		for (int i : pov.DS.allTetIDS()) {
//			if (pov.DS.V(4 * i) != -1) {
//				currentCorner = 12 * i;
//				break;
//			}
//		}
//	}

	/**
	 * display the tetrahedrization
	 */
	public void showWall() {
		long time = System.currentTimeMillis();
		for (int t : pov.DS) {
//			if (pov.DS.V(4 * t) != -1&&TetPealing.getTetType(t, pov)!=21) {
			int[] V= pov.DS.Vertices(t);
			display.show(pov.DS.G(V[1]), pov.DS.G(V[2]), pov.DS.G(V[3]));
			display.show(pov.DS.G(V[0]), pov.DS.G(V[2]), pov.DS.G(V[3]));
			display.show(pov.DS.G(V[0]), pov.DS.G(V[1]), pov.DS.G(V[3]));
			display.show(pov.DS.G(V[0]), pov.DS.G(V[1]), pov.DS.G(V[2]));
//			}
		}
		System.out.println("display time : "+(System.currentTimeMillis()-time));
	}
	/**
	 * display the tetrahedrization, 
	 */
	public void showtetTypes() {
		int k;
		for (int t : pov.DS) {
			k=TetPealing.getTetType(t, pov);
			if (k==20){
				display.fill(display.yellow, 300);display.strokeWeight(1);display.noStroke();
			}
			if (k==3){
				display.fill(display.blue, 300);display.strokeWeight(1);display.noStroke();
			}
			if (k==21){
				display.fill(display.red, 300);display.strokeWeight(1);display.noStroke();
			}
			if (k==10){
				display.fill(display.green, 300);display.strokeWeight(1);display.noStroke();
			}
			if (k==11){
				display.fill(display.cyan, 300);display.strokeWeight(1);display.noStroke();
			}
			if (k==0){
				display.fill(display.black, 300);display.strokeWeight(1);display.noStroke();
			}
			if (pov.DS.V(4 * t) != -1) {
				display.show(pov.DS.G(pov.DS.V(4 * t + 1)), pov.DS.G(pov.DS.V(4 * t + 2)), pov.DS.G(pov.DS.V(4 * t + 3)));
				display.show(pov.DS.G(pov.DS.V(4 * t)), pov.DS.G(pov.DS.V(4 * t + 2)), pov.DS.G(pov.DS.V(4 * t + 3)));
				display.show(pov.DS.G(pov.DS.V(4 * t)), pov.DS.G(pov.DS.V(4 * t + 1)), pov.DS.G(pov.DS.V(4 * t + 3)));
				display.show(pov.DS.G(pov.DS.V(4 * t)), pov.DS.G(pov.DS.V(4 * t + 1)), pov.DS.G(pov.DS.V(4 * t + 2)));
			}
		}
	}
	
	public int idOfVertexWithClosestScreenProjectionTo(pt M) { // for picking a vertex
		// with the mouse
		display.pp = 0;
		for (int i : pov.DS)
			if (pt.d(M, display.ToScreen(pov.DS.G(i))) <= pt.d(M, display.ToScreen(pov.DS.G(display.pp))))
				display.pp = i;
		return display.pp;
	}
	
	void showPicked() {
		display.show(pov.DS.G(pv), 13);
	}
	

	/**
	 * draw vertices
	 * @param r : radius
	 * @return
	 */
	public void drawBalls(float r) {
		for (int v = 0; v < pov.DS.getnv(); v++)
			display.show(pov.DS.G(v), r);
	}

	public void showPicked(float r) {
		display.show(pov.DS.G(pv), r);
	}

	void drawClosedCurve(float r) {
		for (int v = 0; v < pov.DS.getnv() - 1; v++)
			display.stub(pov.DS.G(v), vec.V(pov.DS.G(v), pov.DS.G(v + 1)), r, r / 2);
		display.stub(pov.DS.G(pov.DS.getnv() - 1), vec.V(pov.DS.G(pov.DS.getnv() - 1), pov.DS.G(0)), r, r / 2);
	}
	
	public void deletePicked() {
		for (int i = pv; i < pov.DS.getnv(); i++)
			pov.DS.G(i).setTo(pov.DS.G(i + 1));
		pv = Math.max(0, pv - 1);
		pov.DS.setNv(pov.DS.getnv()-1);
	}

	void setPt(pt P, int i) {
		pov.DS.G(i).setTo(P);
	}
	
	public void setPickedTo(int pp) {
		pv = pp;
		System.out.println("picked" + pv);
	}

	public void movePicked(vec V) {
		pov.DS.G(pv).add(V);
	} // moves selected point (index p) by amount mouse moved recently

	pt Picked() {
		return pov.DS.G(pv);
	}
	
	void insertPt(pt P) { // inserts new vertex after vertex with ID iv
		for (int v = pov.DS.getnv() - 1; v > iv; v--)
			pov.DS.G(v + 1).setTo(pov.DS.G(v));
		iv++;
		pov.DS.G(iv).setTo(P);
		pov.DS.setNv(pov.DS.getnv()+1); // increments vertex count
	}
	
	public void insertClosestProjection(pt M) {
		pt P = closestProjectionOf(M); // also sets iv
		insertPt(P);
	}
	
	pt closestProjectionOf(pt M) { // for picking inserting O. Returns
		// projection but also CHANGES iv !!!!
		pt C = pt.P(pov.DS.G(0));
		float d = pt.d(M, C);
		for (int i = 1; i < pov.DS.getnv(); i++)
			if (pt.d(M, pov.DS.G(i)) <= d) {
				iv = i;
				C = pt.P(pov.DS.G(i));
				d = pt.d(M, C);
			}
		for (int i = pov.DS.getnv() - 1, j = 0; j < pov.DS.getnv(); i = j++) {
			pt A = pov.DS.G(i), B = pov.DS.G(j);
			if (pt.projectsBetween(M, A, B) && pt.disToLine(M, A, B) < d) {
				d = pt.disToLine(M, A, B);
				iv = i;
				C = pt.projectionOnLine(M, A, B);
			}
		}
		return C;
	}

	public cornerBasedDS getPov() {
		return pov;
	}

}
