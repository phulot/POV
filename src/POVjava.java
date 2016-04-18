import java.nio.*;
import processing.core.PMatrix3D;
import processing.event.MouseEvent;
import processing.opengl.PGL;
import processing.opengl.PGraphics3D;

import processing.core.*;

public class POVjava extends PApplet {
	 public static void main(String args[]) {
		    PApplet.main(new String[] { "--present", "POVjava" });
	 }
	// POV representation of tetrahedron meshes
	float dz=0; // distance to camera. Manipulated with wheel or when 
	//float rx=-0.06*TWO_PI, ry=-0.04*TWO_PI;    // view angles manipulated when space pressed but not mouse
	float rx=0, ry=0;    // view angles manipulated when space pressed but not mouse
	Boolean twistFree=false, animating=true, tracking=false, center=true, gouraud=true, 
	  showVertices=true, showWalls=true, showNormals=false;
	float t=0, s=0;
	boolean viewpoint=false;
	int iter =Integer.MAX_VALUE;
	int step=1;
	String Meshname="";
	pt Viewer = pt.P();
	pt F = pt.P(0,0,0);  // focus point:  the camera is looking at it (moved when 'f or 'F' are pressed
	pt Of=pt.P(100,100,0), Ob=pt.P(110,110,0); // red point controlled by the user via mouseDrag : used for inserting vertices ...
	pt Vf=pt.P(0,0,0), Vb=pt.P(0,0,0);
	int pp=1; // index of picked vertex
	subSurface Sub;
	pov Mesh; // tet mesh

	public void settings() {
		size(900, 900, "processing.opengl.PGraphics3D");
		noSmooth();
	}
	
	public void setup() {
	  myFace = loadImage("data/pic.jpg");  // load image from file pic.jpg in folder data *** replace that file with your pic of your own face
	  textureMode(NORMAL);          
	  //Mesh.declare();
	  Meshname = "brain";
	//  Mesh.loadpov("data/cilindre");  // loads saved model from file
	  Mesh = pov.loadpov("data/"+Meshname,this);  // loads saved model from file
	//  Mesh.loadsma("data/"+Meshname, 5f);
	//  Mesh.checkMesh();
	  Mesh.orientMesh();
	//  Mesh.savepov(Meshname);
	//  Mesh.createRandomMesh(1100,1);
	  Sub = new subSurface(Mesh);
	  pt p = pt.P(0,0,0);
	  for (int i=0;i<Mesh.nv;i++){
	    p=pt.A(p,Mesh.G[i]);
	  }  
	  F = p.div(Mesh.nv);
	  //Mesh.loadPV("data/pts3"); 
	  //Mesh.initiManual();
	  }

	public void draw() {
	  background(255);
	  pushMatrix();   // to ensure that we can restore the standard view before writing on the canvas

	    float fov = PI/3.0f;
	    float cameraZ = (height/2.0f) / tan(fov/2.0f);
	    camera(0,0,cameraZ,0,0,0,0,1,0  );       // sets a standard perspective
	    perspective(fov, 1.0f, 0.1f, 10000);
	    
	    translate(0,0,dz); // puts origin of model at screen center and moves forward/away by dz
	    lights();  // turns on view-dependent lighting
	    rotateX(rx); rotateY(ry); // rotates the model around the new origin (center of screen)
	    rotateX(PI/2); // rotates frame around X to make X and Y basis vectors parallel to the floor
	    if(center) translate(-F.x,-F.y,-F.z);
	    noStroke(); // if you use stroke, the weight (width) of it will be scaled with you scaleing factor
	     showFrame(50); // X-red, Y-green, Z-blue arrows
	   //fill(yellow); pushMatrix(); translate(0,0,-1.5); box(400,400,1); popMatrix(); // draws floor as thin plate
	   fill(magenta); show(F,1); // magenta focus point (stays at center of screen)
	   fill(magenta,100); showShadow(F,5); // magenta translucent shadow of focus point (after moving it up with 'F'

	   computeProjectedVectors(); // computes screen projections I, J, K of basis vectors (see bottom of pv3D): used for dragging in viewer's frame    
	   pp=Mesh.idOfVertexWithClosestScreenProjectionTo(Mouse()); // id of vertex of P with closest screen projection to mouse (us in keyPressed 'x'...

	   //PtQ.setToL(P,s,Q); // compute interpolated control polygon

	   if(showVertices) {
	     fill(blue,100); Sub.drawBalls(1f); // draw semitransluent green balls around the vertices
	     fill(red,100); Mesh.showPicked(0.1f); // shows currently picked vertex in red (last key action 'x', 'z'
	     }       
	   Mesh.drawSelectedCorner();

	   if(showWalls) {
	     stroke(red); strokeWeight(2); noFill(); 
	     //Mesh.showWall(); strokeWeight(6); 
	     //Mesh.showWall(); 
	     fill(yellow,300); strokeWeight(1); noStroke(); 
	     //Mesh.showWall();
	     Sub.showMarkedWall(); 
	     fill(red,300); strokeWeight(1); noStroke(); 
	     Sub.showShowWall(); 
	   }

	   if(viewpoint) { 
	     Viewer = viewPoint(); 
	     viewpoint=false;
	   }
	   //noFill(); stroke(red); show(Viewer,P(200,200,0)); show(Viewer,P(200,-200,0)); show(Viewer,P(-200,200,0)); show(Viewer,P(-200,-200,0));
	   noStroke(); fill(red,100); show(Viewer,5); noFill();
	   
	   fill(green); show(Of,3);  // fill(red,100); showShadow(Of,5); } // show ret tool point and its shadow
	  
	//   fill(red); arrow(Vc,V(50,Nc),5); fill(yellow,100); show(P(Vc,15,Nc),15);  
	   if(tracking) F.setTo(pt.P(F,0.01f,Vf));

	   noFill(); stroke(blue); strokeWeight(2); 

	  popMatrix(); // done with 3D drawing. Restore front view for writing text on canvas

	  if(scribeText) {fill(black); displayHeader();} // dispalys header on canvas, including my face
	  if(scribeText && !filming) displayFooter(); // shows menu at bottom, only if not filming
	  if (animating) { t+=PI/180/2; if(t>=TWO_PI) t=0; s=(cos(t)+1.f)/2; } // periodic change of time 
	  if(filming && (animating || change)) saveFrame("FRAMES/F"+nf(frameCounter++,4)+".tif");  // save next frame to make a movie
	  change=false; // to avoid capturing frames when nothing happens (change is set uppn action)
	  }
	  
	  
	// **** KEYS AND MOUSE  
	public void keyPressed() {
	  if(key=='`') picking=true; 
	  if(key=='+') iter+=step; 
	  if(key=='-') {step--;System.out.println(step);}
	  if(key=='/') {step/=2;System.out.println(step);}
	  if(key=='r') Sub.startingcorner = (int)(Math.random()*(4*Mesh.nt));
	  if(key=='?') scribeText=!scribeText;
	  if(key=='!') snapPicture();
	  if(key=='~') filming=!filming;
	  if(key=='.') showVertices=!showVertices;
	  if(key=='|') showNormals=!showNormals;
	  if(key=='G') gouraud=!gouraud;
	  if(key=='q') {Sub.s.saveTestStat(iter);}
	  if(key=='n') Mesh.n();
	  if(key=='o') Mesh.o();
	  if(key=='s') Mesh.s();
	  if(key=='p') println(Mesh.currentCorner);
	  if(key=='e') Mesh.edgeContraction();
	  if(key=='m') Sub.markCorner();
	  if(key=='M') Sub.markNeighbours();
	  if(key=='S') {Sub.createOneSurface(iter);}//Mesh.displayirregularPoints();}
	  if (key==CODED && keyCode==UP) {F.x+=5*cos(rx)*sin(ry); F.y-=5*cos(rx)*cos(ry);F.z+=5*sin(rx);}
	  if (key==CODED && keyCode==DOWN) {F.x-=5*cos(rx)*sin(ry); F.y+=5*cos(rx)*cos(ry);F.z-=5*sin(rx);}
	  if (key==CODED && keyCode==LEFT){F.x-=5*cos(ry);F.y-=5*sin(ry);}         
	  if (key==CODED && keyCode==RIGHT){F.x+=5*cos(ry);F.y+=5*sin(ry);}
	  //if(key=='S') Mesh.createSurface(0);
	  if(key=='=') ;
	  if(key=='c') Sub.checkSurface();//center=!center; // snaps focus F to the selected vertex of P (easier to rotate and zoom while keeping it in center)
	  if(key=='t') tracking=!tracking; // snaps focus F to the selected vertex of P (easier to rotate and zoom while keeping it in center)
	  if(key=='x' || key=='z' || key=='d') Mesh.setPickedTo(pp); // picks the vertex of P that has closest projeciton to mouse
	  if(key=='d') Mesh.deletePicked();
	  if(key=='i') Mesh.insertClosestProjection(Of); // Inserts new vertex in P that is the closeset projection of O
	  if(key=='W') Mesh.savepov("data/pts2");   // save vertices to pts2
	  if(key=='L') {Mesh=pov.loadpov("data/pts2", this);Sub=new subSurface(Mesh);}    // loads saved model
	  if(key=='w') Mesh.savepov("data/pts");   // save vertices to pts
	  if(key=='l'){Mesh=pov.loadpov("data/pts", this);Sub=new subSurface(Mesh);}
	  if(key=='a') animating=!animating; // toggle animation
	  if(key==',') viewpoint=!viewpoint;
	  if(key=='#') exit();
	  change=true;
	  }

	public void mouseWheel(MouseEvent event) {dz += event.getAmount()*10; change=true;}

	public void mousePressed() {
	   if (!keyPressed) picking=true;
	  }
	  
	public void mouseMoved() {
	  if (keyPressed && key==' ') {rx-=PI*(mouseY-pmouseY)/height; ry+=PI*(mouseX-pmouseX)/width;};
	  if (keyPressed && key=='s') dz+=(float)(mouseY-pmouseY); // approach view (same as wheel)
	  }
	public void mouseDragged() {
	  if (!keyPressed) {Of.add(ToIJ(vec.V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); }
	  if (keyPressed && key==CODED && keyCode==SHIFT) {Of.add(ToK(vec.V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0)));};
	  if (keyPressed && key=='x') Mesh.movePicked(ToIJ(vec.V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
	  if (keyPressed && key=='z') Mesh.movePicked(ToK(vec.V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
	  if (keyPressed && key=='X') Mesh.moveAll(ToIJ(vec.V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
	  if (keyPressed && key=='Z') Mesh.moveAll(ToK(vec.V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
	  if (keyPressed && key=='f') { // move focus point on plane
	    if(center) F.sub(ToIJ(vec.V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
	    else F.add(ToIJ(vec.V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
	    }
	  if (keyPressed && key=='F') { // move focus point vertically
	    if(center) F.sub(ToK(vec.V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
	    else F.add(ToK(vec.V((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
	    }
	  }  

	// **** Header, footer, help text on canvas
	void displayHeader() { // Displays title and authors face on screen
	    scribeHeader(title,0); scribeHeaderRight(name); 
	    fill(white); //image(myFace, width-myFace.width/2,25,myFace.width/2,myFace.height/2); 
	    }
	void displayFooter() { // Displays help text at the bottom
	    scribeFooter(guide,1); 
	    scribeFooter(menu,0); 
	    }

	String title ="POV 2016: Tet mesh", name ="Pierre Hulot",
	       menu="?:help, !:picture, ~:(start/stop)capture, space:rotate, s/wheel:closer, f/F:refocus, #:quit",
	       guide="CURVES x/z:select&edit, e:exchange, q/p:copy, l/L: load, w/W:write to file"; // user's guide
	
	
	pt ToScreen(pt P) {return pt.P(screenX(P.x,P.y,P.z),screenY(P.x,P.y,P.z),0);}  // O+xI+yJ+kZ
	pt ToModel(pt P) {return pt.P(modelX(P.x,P.y,P.z),modelY(P.x,P.y,P.z),modelZ(P.x,P.y,P.z));}  // O+xI+yJ+kZ
	// ===== mouse
	pt Mouse() {return pt.P(mouseX,mouseY,0);};                                          // current mouse location
	pt Pmouse() {return pt.P(pmouseX,pmouseY,0);};
	vec MouseDrag() {return vec.V(mouseX-pmouseX,mouseY-pmouseY,0);};                     // vector representing recent mouse displacement
	pt ScreenCenter() {return pt.P(width/2,height/2);}                                                        //  point in center of  canvas

	// ===== measures
	public static float d(vec U, vec V) {return U.x*V.x+U.y*V.y+U.z*V.z; };                                            //U*V dot product
	public static float dot(vec U, vec V) {return U.x*V.x+U.y*V.y+U.z*V.z; };                                            //U*V dot product
	public static float det2(vec U, vec V) {return -U.y*V.x+U.x*V.y; };                                       // U|V det product
	public static float det3(vec U, vec V) {return sqrt(d(U,U)*d(V,V) - sq(d(U,V))); };                                       // U|V det product
	public static float m(vec U, vec V, vec W) {return d(U,vec.N(V,W)); };                                                 // (UxV)*W  mixed product, determinant
	public static float m(pt E, pt A, pt B, pt C) {return m(vec.V(E,A),vec.V(E,B),vec.V(E,C));}                                    // det (EA EB EC) is >0 when E sees (A,B,C) clockwise
	public static float n2(vec V) {return sq(V.x)+sq(V.y)+sq(V.z);};                                                   // V*V    norm squared
	public static float n(vec V) {return sqrt(n2(V));};                                                                // ||V||  norm
	public static float d(pt P, pt Q) {return sqrt(sq(Q.x-P.x)+sq(Q.y-P.y)+sq(Q.z-P.z)); };                            // ||AB|| distance
	public static float area(pt A, pt B, pt C) {return n(vec.N(A,B,C))/2; };                                               // area of triangle 
	public static float volume(pt A, pt B, pt C, pt D) {return m(vec.V(A,B),vec.V(A,C),vec.V(A,D))/6; };                           // volume of tet 
	public static boolean parallel (vec U, vec V) {return n(vec.N(U,V))<n(U)*n(V)*0.00001; }                              // true if U and V are almost parallel
	public static float angle(vec U, vec V) {return acos(d(U,V)/n(V)/n(U)); };                                       // angle(U,V)
	public static boolean cw(vec U, vec V, vec W) {return m(U,V,W)>0; };                                               // (UxV)*W>0  U,V,W are clockwise
	public static boolean cw(pt A, pt B, pt C, pt D) {return volume(A,B,C,D)>0; };                                     // tet is oriented so that A sees B, C, D clockwise 
	public static boolean projectsBetween(pt P, pt A, pt B) {return dot(vec.V(A,P),vec.V(A,B))>0 && dot(vec.V(B,P),vec.V(B,A))>0 ; };
	public static float disToLine(pt P, pt A, pt B) {return det3(vec.U(A,B),vec.V(A,P)); };
	public static pt projectionOnLine(pt P, pt A, pt B) {return pt.P(A,dot(vec.V(A,B),vec.V(A,P))/dot(vec.V(A,B),vec.V(A,B)),vec.V(A,B));}

	// ===== rotate 
	public static vec R(vec V) {return vec.V(-V.y,V.x,V.z);} // rotated 90 degrees in XY plane
	public static pt R(pt P, float a, vec I, vec J, pt G) {float x=d(vec.V(G,P),I), y=d(vec.V(G,P),J); float c=cos(a), s=sin(a); return pt.P(P,x*c-x-y*s,I,x*s+y*c-y,J); }; // Rotated P by a around G in plane (I,J)
	public static vec R(vec V, float a, vec I, vec J) {float x=d(V,I), y=d(V,J); float c=cos(a), s=sin(a); return vec.A(V,vec.V(x*c-x-y*s,I,x*s+y*c-y,J)); }; // Rotated V by a parallel to plane (I,J)
	public static pt R(pt Q, pt C, pt P, pt R) { // returns rotated version of Q by angle(CP,CR) parallel to plane (C,P,R)
	   vec I0=vec.U(C,P), I1=vec.U(C,R), V=vec.V(C,Q); 
	   float c=d(I0,I1), s=(float) Math.sqrt(1.-sq(c)); 
	     if(abs(s)<0.00001) return Q;
	   vec J0=vec.V(1.f/s,I1,-c/s,I0);  
	   vec J1=vec.V(-s,I0,c,J0);  
	   float x=d(V,I0), y=d(V,J0);
	                                //  stroke(red); show(C,400,I0); stroke(blue); show(C,400,I1); stroke(orange); show(C,400,J0); stroke(magenta); show(C,400,J1); noStroke();
	   return pt.P(Q,x,vec.M(I1,I0),y,vec.M(J1,J0)); 
	  } 
	public static pt R(pt Q, float a) {float dx=Q.x, dy=Q.y, c=cos(a), s=sin(a); return pt.P(c*dx+s*dy,-s*dx+c*dy,Q.z); };  // Q rotated by angle a around the origin
	public static pt R(pt Q, float a, pt C) {float dx=Q.x-C.x, dy=Q.y-C.y, c=cos(a), s=sin(a); return pt.P(C.x+c*dx-s*dy, C.y+s*dx+c*dy, Q.z); };  // Q rotated by angle a around point P

	// ===== render
	void normal(vec V) {normal(V.x,V.y,V.z);};                                          // changes normal for smooth shading
	void vertex(pt P) {vertex(P.x,P.y,P.z);};                                           // vertex for shading or drawing
	void v(pt P) {vertex(P.x,P.y,P.z);};                                           // vertex for shading or drawing
	void nv(vec N) {normal(N.x,N.y,N.z);};                                           // vertex for shading or drawing
	void vTextured(pt P, float u, float v) {vertex(P.x,P.y,P.z,u,v);};                          // vertex with texture coordinates
	void show(pt P, pt Q) {line(Q.x,Q.y,Q.z,P.x,P.y,P.z); };                       // draws edge (P,Q)
	void show(pt P, vec V) {line(P.x,P.y,P.z,P.x+V.x,P.y+V.y,P.z+V.z); };          // shows edge from P to P+V
	void show(pt P, float d , vec V) {line(P.x,P.y,P.z,P.x+d*V.x,P.y+d*V.y,P.z+d*V.z); }; // shows edge from P to P+dV
	void show(pt A, pt B, pt C) {beginShape(); vertex(A);vertex(B); vertex(C); endShape(CLOSE);};                      // volume of tet 
	void show(pt A, pt B, pt C, pt D) {beginShape(); vertex(A); vertex(B); vertex(C); vertex(D); endShape(CLOSE);};                      // volume of tet 
	void show(pt P, float r) {pushMatrix(); translate(P.x,P.y,P.z); sphere(r); popMatrix();}; // render sphere of radius r and center P
	void show(pt P, float s, vec I, vec J, vec K) {noStroke(); fill(yellow); show(P,5); stroke(red); show(P,s,I); stroke(green); show(P,s,J); stroke(blue); show(P,s,K); }; // render sphere of radius r and center P
	void show(pt P, String s) {text(s, P.x, P.y, P.z); }; // prints string s in 3D at P
	void show(pt P, String s, vec D) {text(s, P.x+D.x, P.y+D.y, P.z+D.z);  }; // prints string s in 3D at P+D
	void showShadow(pt P, float r) {pushMatrix(); translate(P.x,P.y,0); scale(1f,1f,0.01f); sphere(r); popMatrix();}

	String toText(vec V){ return "("+nf(V.x,1,5)+","+nf(V.y,1,5)+","+nf(V.z,1,5)+")";}
	// ==== curve
	void bezier(pt A, pt B, pt C, pt D) {bezier(A.x,A.y,A.z,B.x,B.y,B.z,C.x,C.y,C.z,D.x,D.y,D.z);} // draws a cubic Bezier curve with control points A, B, C, D
	void bezier(pt [] C) {bezier(C[0],C[1],C[2],C[3]);} // draws a cubic Bezier curve with control points A, B, C, D
	pt bezierPoint(pt[] C, float t) {return pt.P(bezierPoint(C[0].x,C[1].x,C[2].x,C[3].x,t),bezierPoint(C[0].y,C[1].y,C[2].y,C[3].y,t),bezierPoint(C[0].z,C[1].z,C[2].z,C[3].z,t)); }
	vec bezierTangent(pt[] C, float t) {return vec.V(bezierTangent(C[0].x,C[1].x,C[2].x,C[3].x,t),bezierTangent(C[0].y,C[1].y,C[2].y,C[3].y,t),bezierTangent(C[0].z,C[1].z,C[2].z,C[3].z,t)); }
	void PT(pt P0, vec T0, pt P1, vec T1) {float d=d(P0,P1)/3;  bezier(P0, pt.P(P0,-d,vec.U(T0)), pt.P(P1,-d,vec.U(T1)), P1);} // draws cubic Bezier interpolating  (P0,T0) and  (P1,T1) 
	void PTtoBezier(pt P0, vec T0, pt P1, vec T1, pt [] C) {float d=d(P0,P1)/3;  C[0].set(P0); C[1].set(pt.P(P0,-d,vec.U(T0))); C[2].set(pt.P(P1,-d,vec.U(T1))); C[3].set(P1);} // draws cubic Bezier interpolating  (P0,T0) and  (P1,T1) 
	vec vecToCubic (pt A, pt B, pt C, pt D, pt E) {return vec.V( (-A.x+4*B.x-6*C.x+4*D.x-E.x)/6, (-A.y+4*B.y-6*C.y+4*D.y-E.y)/6, (-A.z+4*B.z-6*C.z+4*D.z-E.z)/6);}
	vec vecToProp (pt B, pt C, pt D) {float cb=d(C,B);  float cd=d(C,D); return vec.V(C,pt.P(B,cb/(cb+cd),D)); };  

	// ==== perspective
	pt Pers(pt P, float d) { return pt.P(d*P.x/(d+P.z) , d*P.y/(d+P.z) , d*P.z/(d+P.z) ); };
	pt InverserPers(pt P, float d) { return pt.P(d*P.x/(d-P.z) , d*P.y/(d-P.z) , d*P.z/(d-P.z) ); };

	// ==== intersection
	boolean intersect(pt P, pt Q, pt A, pt B, pt C, pt X)  {return intersect(P,vec.V(P,Q),A,B,C,X); } // if (P,Q) intersects (A,B,C), return true and set X to the intersection point
	boolean intersect(pt E, vec T, pt A, pt B, pt C, pt X) { // if ray from E along T intersects triangle (A,B,C), return true and set X to the intersection point
	  vec EA=vec.V(E,A), EB=vec.V(E,B), EC=vec.V(E,C), AB=vec.V(A,B), AC=vec.V(A,C); 
	  boolean s=cw(EA,EB,EC), sA=cw(T,EB,EC), sB=cw(EA,T,EC), sC=cw(EA,EB,T); 
	  if ( (s==sA) && (s==sB) && (s==sC) ) return false;
	  float t = m(EA,AC,AB) / m(T,AC,AB);
	  X.set(pt.P(E,t,T));
	  return true;
	  }
	boolean rayIntersectsTriangle(pt E, vec T, pt A, pt B, pt C) { // true if ray from E with direction T hits triangle (A,B,C)
	  vec EA=vec.V(E,A), EB=vec.V(E,B), EC=vec.V(E,C); 
	  boolean s=cw(EA,EB,EC), sA=cw(T,EB,EC), sB=cw(EA,T,EC), sC=cw(EA,EB,T); 
	  return  (s==sA) && (s==sB) && (s==sC) ;};
	boolean edgeIntersectsTriangle(pt P, pt Q, pt A, pt B, pt C)  {
	  vec PA=vec.V(P,A), PQ=vec.V(P,Q), PB=vec.V(P,B), PC=vec.V(P,C), QA=vec.V(Q,A), QB=vec.V(Q,B), QC=vec.V(Q,C); 
	  boolean p=cw(PA,PB,PC), q=cw(QA,QB,QC), a=cw(PQ,PB,PC), b=cw(PA,PQ,PC), c=cw(PQ,PB,PQ); 
	  return (p!=q) && (p==a) && (p==b) && (p==c);
	  }
	float rayParameterToIntersection(pt E, vec T, pt A, pt B, pt C) {vec AE=vec.V(A,E), AB=vec.V(A,B), AC=vec.V(A,C); return - m(AE,AC,AB) / m(T,AC,AB);}
	   
	float angleDraggedAround(pt G) {  // returns angle in 2D dragged by the mouse around the screen projection of G
	   pt S=pt.P(screenX(G.x,G.y,G.z),screenY(G.x,G.y,G.z),0);
	   vec T=vec.V(S,Pmouse()); vec U=vec.V(S,Mouse());
	   return atan2(d(R(U),T),d(U,T));
	   }
	 
	float scaleDraggedFrom(pt G) {pt S=pt.P(screenX(G.x,G.y,G.z),screenY(G.x,G.y,G.z),0); return d(S,Mouse())/d(S,Pmouse()); }

	// FANS, CONES, AND ARROWS
	void disk(pt P, vec V, float r) {  
	  vec I = vec.U(vec.Normal(V));
	  vec J = vec.U(vec.N(I,V));
	  disk(P,I,J,r);
	  }

	void disk(pt P, vec I, vec J, float r) {
	  float da = TWO_PI/36;
	  beginShape(TRIANGLE_FAN);
	    v(P);
	    for(float a=0; a<=TWO_PI+da; a+=da) v(pt.P(P,r*cos(a),I,r*sin(a),J));
	  endShape();
	  }
	  

	void fan(pt P, vec V, float r) {  
	  vec I = vec.U(vec.Normal(V));
	  vec J = vec.U(vec.N(I,V));
	  fan(P,V,I,J,r);
	  }

	void fan(pt P, vec V, vec I, vec J, float r) {
	  float da = TWO_PI/36;
	  beginShape(TRIANGLE_FAN);
	    v(pt.P(P,V));
	    for(float a=0; a<=TWO_PI+da; a+=da) v(pt.P(P,r*cos(a),I,r*sin(a),J));
	  endShape();
	  }
	  
	void collar(pt P, vec V, float r, float rd) {
	  vec I = vec.U(vec.Normal(V));
	  vec J = vec.U(vec.N(I,V));
	  collar(P,V,I,J,r,rd);
	  }
	 
	void collar(pt P, vec V, vec I, vec J, float r, float rd) {
	  float da = TWO_PI/36;
	  beginShape(QUAD_STRIP);
	    for(float a=0; a<=TWO_PI+da; a+=da) {v(pt.P(P,r*cos(a),I,r*sin(a),J,0,V)); v(pt.P(P,rd*cos(a),I,rd*sin(a),J,1,V));}
	  endShape();
	  }

	void cone(pt P, vec V, float r) {fan(P,V,r); disk(P,V,r);}

	void stub(pt P, vec V, float r, float rd) {
	  collar(P,V,r,rd); disk(P,V,r); disk(pt.P(P,V),V,rd); 
	  }
	  
	void arrow(pt P, vec V, float r) {
	  stub(P,vec.V(.8f,V),r*2/3,r/3); 
	  cone(pt.P(P,vec.V(.8f,V)),vec.V(.2f,V),r); 
	  }  

	// **************************** PRIMITIVE
	void showFrame(float d) { 
	  noStroke(); 
	  fill(metal); sphere(d/10);
	  fill(blue);  showArrow(d,d/10);
	  fill(red); pushMatrix(); rotateY(PI/2); showArrow(d,d/10); popMatrix();
	  fill(green); pushMatrix(); rotateX(-PI/2); showArrow(d,d/10); popMatrix();
	  }

	void showFan(float d, float r) {
	  float da = TWO_PI/36;
	  beginShape(TRIANGLE_FAN);
	    vertex(0,0,d);
	    for(float a=0; a<=TWO_PI+da; a+=da) vertex(r*cos(a),r*sin(a),0);
	  endShape();
	  }

	void showCollar(float d, float r, float rd) {
	  float da = TWO_PI/36;
	  beginShape(QUAD_STRIP);
	    for(float a=0; a<=TWO_PI+da; a+=da) {vertex(r*cos(a),r*sin(a),0); vertex(rd*cos(a),rd*sin(a),d);}
	  endShape();
	  }
	  
	void showCone(float d, float r) {showFan(d,r);  showFan(0,r);}

	void showStub(float d, float r, float rd) {
	  showCollar(d,r,rd); showFan(0,r);  pushMatrix(); translate(0,0,d); showFan(0,rd); popMatrix();
	  }

	void showArrow() {showArrow(1f,0.08f);}
	 
	void showArrow(float d, float r) {
	  float dd=d/5;
	  showStub(d-dd,r*2/3,r/3); pushMatrix(); translate(0,0,d-dd); showCone(dd,r); popMatrix();
	  }  
	  
	void showBlock(float w, float d, float h, float x, float y, float z, float a) {
	  pushMatrix(); translate(x,y,h/2); rotateZ(TWO_PI*a); box(w, d, h); popMatrix(); 
	  }

	//*********** PICK
	vec I=vec.V(1,0,0), J=vec.V(0,1,0), K=vec.V(0,0,1); // screen projetions of global model frame

	void computeProjectedVectors() { 
	  pt O = ToScreen(pt.P(0,0,0));
	  pt A = ToScreen(pt.P(1,0,0));
	  pt B = ToScreen(pt.P(0,1,0));
	  pt C = ToScreen(pt.P(0,0,1));
	  I=vec.V(O,A);
	  J=vec.V(O,B);
	  K=vec.V(O,C);
	  }

	vec ToIJ(vec V) {
	 float x = det2(V,J) / det2(I,J);
	 float y = det2(V,I) / det2(J,I);
	 return vec.V(x,y,0);
	 }
	 
	vec ToK(vec V) {
	 float z = dot(V,K) / dot(K,K);
	 return vec.V(0,0,z);
	 }
	pt PP=pt.P(); // picked point
	Boolean  picking=false;

	public pt pick(int mX, int mY) { // returns point on visible surface at pixel (mX,My)
	  PGL pgl = beginPGL();
	  FloatBuffer depthBuffer = ByteBuffer.allocateDirect(1 << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
	  pgl.readPixels(mX, height - mY - 1, 1, 1, PGL.DEPTH_COMPONENT, PGL.FLOAT, depthBuffer);
	  float depthValue = depthBuffer.get(0);
	  depthBuffer.clear();
	  endPGL();

	  //get 3d matrices
	  PGraphics3D p3d = (PGraphics3D)g;
	  PMatrix3D proj = p3d.projection.get();
	  PMatrix3D modelView = p3d.modelview.get();
	  PMatrix3D modelViewProjInv = proj; modelViewProjInv.apply( modelView ); modelViewProjInv.invert();
	  
	  float[] viewport = {0, 0, p3d.width, p3d.height};
	  float[] normalized = new float[4];
	  normalized[0] = ((mX - viewport[0]) / viewport[2]) * 2.0f - 1.0f;
	  normalized[1] = ((height - mY - viewport[1]) / viewport[3]) * 2.0f - 1.0f;
	  normalized[2] = depthValue * 2.0f - 1.0f;
	  normalized[3] = 1.0f;
	  
	  float[] unprojected = new float[4];
	  
	  modelViewProjInv.mult( normalized, unprojected );
	  return pt.P( unprojected[0]/unprojected[3], unprojected[1]/unprojected[3], unprojected[2]/unprojected[3] );
	  }

	public pt pick(float mX, float mY, float mZ) { 
	  //get 3d matrices
	  PGraphics3D p3d = (PGraphics3D)g;
	  PMatrix3D proj = p3d.projection.get();
	  PMatrix3D modelView = p3d.modelview.get();
	  PMatrix3D modelViewProjInv = proj; modelViewProjInv.apply( modelView ); modelViewProjInv.invert();
	  float[] viewport = {0, 0, p3d.width, p3d.height};
	  float[] normalized = new float[4];
	  normalized[0] = ((mX - viewport[0]) / viewport[2]) * 2.0f - 1.0f;
	  normalized[1] = ((height - mY - viewport[1]) / viewport[3]) * 2.0f - 1.0f;
	  normalized[2] = mZ * 2.0f - 1.0f;
	  normalized[3] = 1.0f;
	  float[] unprojected = new float[4];
	  modelViewProjInv.mult( normalized, unprojected );
	  return pt.P( unprojected[0]/unprojected[3], unprojected[1]/unprojected[3], unprojected[2]/unprojected[3] );
	  }

	pt viewPoint() {return pick( 0,0, (height/2) / tan(PI/6));}
	// ************************************ IMAGES & VIDEO 
	int pictureCounter=0, frameCounter=0;
	Boolean filming=false, change=false;
	PImage myFace; // picture of author's face, should be: data/pic.jpg in sketch folder
	void snapPicture() {saveFrame("PICTURES/P"+Meshname+nf(pictureCounter++,3)+".jpg"); }

	// ******************************************COLORS 
	int black=color(0,0,0), white=color(255,255,255), // set more colors using Menu >  Tools > Color Selector
	   red=color(255,0,0), green=color(0,255,0), blue=color(0,0,255), yellow=color(255,255,0), cyan=color(0,253,255), magenta=color(255,0,255),
	   grey=color(128,128,128), orange=color(255,165,0), brown=color(180,96,5), metal=color(181,204,222), dgreen=color(21,121,1);
	void pen(int c, float w) {stroke(c); strokeWeight(w);}

	// ******************************** TEXT , TITLE, and USER's GUIDE
	Boolean scribeText=true; // toggle for displaying of help text
	void scribe(String S, float x, float y) {fill(0); text(S,x,y); noFill();} // writes on screen at (x,y) with current fill color
	void scribeHeader(String S, int i) {fill(0); text(S,10,20+i*20); noFill();} // writes black at line i
	void scribeHeaderRight(String S) {fill(0); text(S,width-7.5f*S.length(),20); noFill();} // writes black on screen top, right-aligned
	void scribeFooter(String S, int i) {fill(0); text(S,10,height-10-i*20); noFill();} // writes black on screen at line i from bottom
	void scribeAtMouse(String S) {fill(0); text(S,mouseX,mouseY); noFill();} // writes on screen near mouse
	void scribeMouseCoordinates() {fill(black); text("("+mouseX+","+mouseY+")",mouseX+7,mouseY+25); noFill();}
}
