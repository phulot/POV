package POV;

public class pt { 
	public float x=0,y=0,z=0; 
	public pt () {}; 
	public pt (float px, float py) {x = px; y = py;};
	public pt (float px, float py, float pz) {x = px; y = py; z = pz; };
	public pt (pt p){x=p.x;y=p.y;z=p.z;};
	public pt set (float px, float py, float pz) {x = px; y = py; z = pz; return this;}; 
	public pt set (pt P) {x = P.x; y = P.y; z = P.z; return this;}; 
	public pt setTo(pt P) {x = P.x; y = P.y; z = P.z; return this;}; 
	public pt setTo(float px, float py, float pz) {x = px; y = py; z = pz; return this;}; 
	public pt add(pt P) {x+=P.x; y+=P.y; z+=P.z; return this;};
	public pt add(vec V) {x+=V.x; y+=V.y; z+=V.z; return this;};
	public pt sub(vec V) {x-=V.x; y-=V.y; z-=V.z; return this;};
	public pt add(float s, vec V) {x+=s*V.x; y+=s*V.y; z+=s*V.z; return this;};
	public pt sub(pt P) {x-=P.x; y-=P.y; z-=P.z; return this;};
	public pt mul(float f) {x*=f; y*=f; z*=f; return this;};
	public pt div(float f) {x/=f; y/=f; z/=f; return this;};
	public pt div(int f) {x/=f; y/=f; z/=f; return this;};
	
	//===== point functions
	private static float sq(float x2) {return x2*x2;}
	public static pt P() {return new pt(); };                                                                          // point (x,y,z)
	public static pt P(float x, float y, float z) {return new pt(x,y,z); };                                            // point (x,y,z)
	public static pt P(float x, float y) {return new pt(x,y); };                                                       // make point (x,y)
	public static pt P(pt A) {return new pt(A.x,A.y,A.z); };                                                           // copy of point P
	public static pt P(pt A, float s, pt B) {return new pt(A.x+s*(B.x-A.x),A.y+s*(B.y-A.y),A.z+s*(B.z-A.z)); };        // A+sAB
	public static pt L(pt A, float s, pt B) {return new pt(A.x+s*(B.x-A.x),A.y+s*(B.y-A.y),A.z+s*(B.z-A.z)); };        // A+sAB
	public static pt P(pt A, pt B) {return P((A.x+B.x)/2.0f,(A.y+B.y)/2.0f,(A.z+B.z)/2.0f); }                             // (A+B)/2
	public static pt P(pt A, pt B, pt C) {return new pt((A.x+B.x+C.x)/3.0f,(A.y+B.y+C.y)/3.0f,(A.z+B.z+C.z)/3.0f); };     // (A+B+C)/3
	public static pt P(pt A, pt B, pt C, pt D) {return P(P(A,B),P(C,D)); };                                            // (A+B+C+D)/4
	public static pt P(float s, pt A) {return new pt(s*A.x,s*A.y,s*A.z); };                                            // sA
	public static pt A(pt A, pt B) {return new pt(A.x+B.x,A.y+B.y,A.z+B.z); };                                         // A+B
	public static pt P(float a, pt A, float b, pt B) {return A(P(a,A),P(b,B));}                                        // aA+bB 
	public static pt P(float a, pt A, float b, pt B, float c, pt C) {return A(P(a,A),P(b,B,c,C));}                     // aA+bB+cC 
	public static pt P(float a, pt A, float b, pt B, float c, pt C, float d, pt D){return A(P(a,A,b,B),P(c,C,d,D));}   // aA+bB+cC+dD
	public static pt P(pt P, vec V) {return new pt(P.x + V.x, P.y + V.y, P.z + V.z); }                                 // P+V
	public static pt P(pt P, float s, vec V) {return new pt(P.x+s*V.x,P.y+s*V.y,P.z+s*V.z);}                           // P+sV
	public static pt P(pt O, float x, vec I, float y, vec J) {return P(O.x+x*I.x+y*J.x,O.y+x*I.y+y*J.y,O.z+x*I.z+y*J.z);}  // O+xI+yJ
	public static pt P(pt O, float x, vec I, float y, vec J, float z, vec K) {return P(O.x+x*I.x+y*J.x+z*K.x,O.y+x*I.y+y*J.y+z*K.y,O.z+x*I.z+y*J.z+z*K.z);}  // O+xI+yJ+kZ
	public static void makePts(pt[] C) {for(int i=0; i<C.length; i++) C[i]=P();}
	// ===== measures
	public static float m(pt E, pt A, pt B, pt C) {return vec.m(vec.V(E,A),vec.V(E,B),vec.V(E,C));}                                    // det (EA EB EC) is >0 when E sees (A,B,C) clockwise
	public static float d(pt P, pt Q) {return (float) Math.sqrt(sq(Q.x-P.x)+sq(Q.y-P.y)+sq(Q.z-P.z)); };                            // ||AB|| distance
	public static float area(pt A, pt B, pt C) {return vec.n(vec.N(A,B,C))/2; };                                               // area of triangle 
	public static float volume(pt A, pt B, pt C, pt D) {return vec.m(vec.V(A,B),vec.V(A,C),vec.V(A,D))/6; };                           // volume of tet 
	public static boolean cw(pt A, pt B, pt C, pt D) {return volume(A,B,C,D)>0; };                                     // tet is oriented so that A sees B, C, D clockwise 
	public static boolean projectsBetween(pt P, pt A, pt B) {return vec.dot(vec.V(A,P),vec.V(A,B))>0 && vec.dot(vec.V(B,P),vec.V(B,A))>0 ; };
	public static float disToLine(pt P, pt A, pt B) {return vec.det3(vec.U(A,B),vec.V(A,P)); };
	public static pt projectionOnLine(pt P, pt A, pt B) {return pt.P(A,vec.dot(vec.V(A,B),vec.V(A,P))/vec.dot(vec.V(A,B),vec.V(A,B)),vec.V(A,B));}
	// ===== rotate 
	public static pt R(pt P, float a, vec I, vec J, pt G) {float x=vec.d(vec.V(G,P),I), y=vec.d(vec.V(G,P),J); float c=(float) Math.cos(a), s=(float) Math.sin(a); return pt.P(P,x*c-x-y*s,I,x*s+y*c-y,J); }; // Rotated P by a around G in plane (I,J)
	public static pt R(pt Q, pt C, pt P, pt R) { // returns rotated version of Q by angle(CP,CR) parallel to plane (C,P,R)
	   vec I0=vec.U(C,P), I1=vec.U(C,R), V=vec.V(C,Q); 
	   float c=vec.d(I0,I1), s=(float) Math.sqrt(1.-sq(c)); 
	     if(Math.abs(s)<0.00001) return Q;
	   vec J0=vec.V(1.f/s,I1,-c/s,I0);  
	   vec J1=vec.V(-s,I0,c,J0);  
	   float x=vec.d(V,I0), y=vec.d(V,J0);
	                                //  stroke(red); show(C,400,I0); stroke(blue); show(C,400,I1); stroke(orange); show(C,400,J0); stroke(magenta); show(C,400,J1); noStroke();
	   return pt.P(Q,x,vec.M(I1,I0),y,vec.M(J1,J0)); 
	  } 
	public static pt R(pt Q, float a) {float dx=Q.x, dy=Q.y, c=(float) Math.cos(a), s=(float) Math.sin(a); return pt.P(c*dx+s*dy,-s*dx+c*dy,Q.z); };  // Q rotated by angle a around the origin
	public static pt R(pt Q, float a, pt C) {float dx=Q.x-C.x, dy=Q.y-C.y, c=(float) Math.cos(a), s=(float) Math.sin(a); return pt.P(C.x+c*dx-s*dy, C.y+s*dx+c*dy, Q.z); };  // Q rotated by angle a around point P

}
