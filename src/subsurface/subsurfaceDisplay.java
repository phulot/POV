package subsurface;
import java.util.Set;

import Applet.POVjava;
import POV.*;

public class subsurfaceDisplay extends POVDisplay{
	/** surface data*/
	subSurface s;

	public subsurfaceDisplay(POVjava pA, subSurface s) {
		super(pA,s.getPov());
		this.s = s;
	}
	/**
	 * mark current corner's face
	 */
	public void markCorner() {
		s.markFace(pov.faceFromCorner(currentCorner));
	}
	/**
	 * mark current corner's tet
	 */
	public void markTetrahedron() {
		s.markTetrahedron(pov.tetraFromCorner(currentCorner));
	}
	/**
	 * unmark current corner's tet
	 */
	public void unmarkTetrahedron() {
		s.unmarkTetrahedron(pov.tetraFromCorner(currentCorner));
	}
	/**
	 * mark all neighbors of current corner vertex
	 */
	public void markNeighbours() {
		Set<Integer> l = pov.edgeNeighbors(currentCorner);
		for (Integer i : l)
			if (i!=-1)
				s.markTetrahedron(i);
	}
	/**
	 * unmark all neighbors of current corner vertex
	 */
	public void unmarkNeighbours() {
		Set<Integer> l = pov.edgeNeighbors(currentCorner);
		for (Integer i : l)
			if (i!=-1)
				s.unmarkTetrahedron(i);
	}
	
	/**
	 * display marked walls (marked[])
	 */
	public void showMarkedWall() {
		for (int t = 0; t < pov.nt; t++) {
			if (pov.V[4 * t] != -1&&!s.show[t]) {
				if (s.marked[4 * t])
					display.show(pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				if (s.marked[4 * t + 1])
					display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				if (s.marked[4 * t + 2])
					display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 3]]);
				if (s.marked[4 * t + 3])
					display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]]);
			}
		}
	}
	/**
	 * display show&marked walls (show[],marked[])
 	 */
	public void showShowWall() {
		for (int t = 0; t < pov.nt; t++) {
			if (pov.V[4 * t] != -1&&s.show[t]) {
				if (s.marked[4 * t])
					display.show(pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				if (s.marked[4 * t + 1])
					display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				if (s.marked[4 * t + 2])
					display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 3]]);
				if (s.marked[4 * t + 3])
					display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]]);
			}
		}
	}
	/**
	 * display show walls (show[])
 	 */
	public void showWallm() {
		for (int t = 0; t < pov.nt; t++) {
			if (pov.V[4 * t] != -1 && s.show[t]) {
				display.show(pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 2]], pov.G[pov.V[4 * t + 3]]);
				display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 3]]);
				display.show(pov.G[pov.V[4 * t]], pov.G[pov.V[4 * t + 1]], pov.G[pov.V[4 * t + 2]]);
			}
		}
	}
	
	/**
	 * display all vertices 
	 * green if showv[]
	 * red if vermarked[] ==-1
	 * blue else where
	 * @param r radius
	 * @return
	 */
	public void drawBalls(float r) {
		for (int v = 0; v < pov.nv; v++) {
			if (s.showv[v]) {
				display.fill(display.green, 100);
				display.show(pov.G[v], 3*r);
			} else if (s.vertexmarked[v] == -1) {
				display.fill(display.red, 100);
				display.show(pov.G[v], 3 * r);
			} else {
				display.fill(display.blue, 100);
				display.show(pov.G[v], r);
			}
		}
	}

}
