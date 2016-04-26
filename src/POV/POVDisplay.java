package POV;
import Applet.*;

public class POVDisplay {

	protected POV pov;
	protected POVjava display; 
	/** current corner index */
	public int currentCorner = 0; 
	/** picked vertex index, */
	protected int pv = 0; 
	/** insertion vertex index */
	protected int iv = 0; 

	
	protected POVDisplay(POVjava dis,POV p) {
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
		if (pov.borderCorner(c)) {
			try {
				drawCorner(pov.o(c));
			} catch (BorderCornerException e) {
				e.printStackTrace();
			}
		} else {
			pt id = pov.G[pov.v(c)];
			pt cor = new pt(id);
			pt ver = new pt(id);
			vec v1 = vec.V(id, pov.G[pov.v(pov.n(c))]);
			ver.add(1.f / 3f, v1);
			vec v2 = vec.V(id, pov.G[pov.v(pov.n(pov.n(c)))]);
			cor.add(vec.V(1.f / 3f, vec.V(v1, v2)));
			display.fill(display.blue, 300);
			display.noStroke();
			display.show(pov.G[pov.v(c)], ver, cor);
			ver = new pt(id);
			ver.add(1f / 3, v2);
			display.show(pov.G[pov.v(c)], ver, cor);
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
	
	public void edgeContraction() {
		pov.edgeContraction(currentCorner);
		for (int i = 0; i < pov.nt; i++) {
			if (pov.V[4 * i] != -1) {
				currentCorner = 12 * i;
				break;
			}
		}
	}

	/**
	 * display the tetrahedrization
	 */
	public void showWall() {
		for (int t = 0; t < pov.nt; t++) {
			if (pov.V[4 * t] != -1&&TetPealing.getTetType(t, pov)!=21) {
				display.show(pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 3]]);
				display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]]);
			}
		}
	}
	/**
	 * display the tetrahedrization, 
	 */
	public void showtetTypes() {
		int k;
		for (int t = 0; t < pov.nt; t++) {
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
			if (k==1){
				display.fill(display.green, 300);display.strokeWeight(1);display.noStroke();
			}
			if (k==0){
				display.fill(display.black, 300);display.strokeWeight(1);display.noStroke();
			}
			if (pov.V[4 * t] != -1&&k!=21) {
				display.show(pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 3]]);
				display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]]);
			}
		}
	}
	
	public int idOfVertexWithClosestScreenProjectionTo(pt M) { // for picking a vertex
		// with the mouse
		display.pp = 0;
		for (int i = 1; i < pov.nv; i++)
			if (pt.d(M, display.ToScreen(pov.G[i])) <= pt.d(M, display.ToScreen(pov.G[display.pp])))
				display.pp = i;
		return display.pp;
	}
	
	void showPicked() {
		display.show(pov.G[pv], 13);
	}
	

	/**
	 * draw vertices
	 * @param r : radius
	 * @return
	 */
	public void drawBalls(float r) {
		for (int v = 0; v < pov.nv; v++)
			display.show(pov.G[v], r);
	}

	public void showPicked(float r) {
		display.show(pov.G[pv], r);
	}

	void drawClosedCurve(float r) {
		for (int v = 0; v < pov.nv - 1; v++)
			display.stub(pov.G[v], vec.V(pov.G[v], pov.G[v + 1]), r, r / 2);
		display.stub(pov.G[pov.nv - 1], vec.V(pov.G[pov.nv - 1], pov.G[0]), r, r / 2);
	}
	
	public void deletePicked() {
		for (int i = pv; i < pov.nv; i++)
			pov.G[i].setTo(pov.G[i + 1]);
		pv = Math.max(0, pv - 1);
		pov.nv--;
	}

	void setPt(pt P, int i) {
		pov.G[i].setTo(P);
	}
	
	public void setPickedTo(int pp) {
		pv = pp;
		System.out.println("picked" + pv);
	}

	public void movePicked(vec V) {
		pov.G[pv].add(V);
	} // moves selected point (index p) by amount mouse moved recently

	pt Picked() {
		return pov.G[pv];
	}
	
	void insertPt(pt P) { // inserts new vertex after vertex with ID iv
		for (int v = pov.nv - 1; v > iv; v--)
			pov.G[v + 1].setTo(pov.G[v]);
		iv++;
		pov.G[iv].setTo(P);
		pov.nv++; // increments vertex count
	}
	
	public void insertClosestProjection(pt M) {
		pt P = closestProjectionOf(M); // also sets iv
		insertPt(P);
	}
	
	pt closestProjectionOf(pt M) { // for picking inserting O. Returns
		// projection but also CHANGES iv !!!!
		pt C = pt.P(pov.G[0]);
		float d = pt.d(M, C);
		for (int i = 1; i < pov.nv; i++)
			if (pt.d(M, pov.G[i]) <= d) {
				iv = i;
				C = pt.P(pov.G[i]);
				d = pt.d(M, C);
			}
		for (int i = pov.nv - 1, j = 0; j < pov.nv; i = j++) {
			pt A = pov.G[i], B = pov.G[j];
			if (pt.projectsBetween(M, A, B) && pt.disToLine(M, A, B) < d) {
				d = pt.disToLine(M, A, B);
				iv = i;
				C = pt.projectionOnLine(M, A, B);
			}
		}
		return C;
	}

	public POV getPov() {
		return pov;
	}

}
