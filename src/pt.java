import processing.core.*;
public class pt { float x=0,y=0,z=0; 
	pt () {}; 
	pt (float px, float py) {x = px; y = py;};
	pt (float px, float py, float pz) {x = px; y = py; z = pz; };
	pt (pt p){x=p.x;y=p.y;z=p.z;};
	pt set (float px, float py, float pz) {x = px; y = py; z = pz; return this;}; 
	pt set (pt P) {x = P.x; y = P.y; z = P.z; return this;}; 
	pt setTo(pt P) {x = P.x; y = P.y; z = P.z; return this;}; 
	pt setTo(float px, float py, float pz) {x = px; y = py; z = pz; return this;}; 
	pt add(pt P) {x+=P.x; y+=P.y; z+=P.z; return this;};
	pt add(vec V) {x+=V.x; y+=V.y; z+=V.z; return this;};
	pt sub(vec V) {x-=V.x; y-=V.y; z-=V.z; return this;};
	pt add(float s, vec V) {x+=s*V.x; y+=s*V.y; z+=s*V.z; return this;};
	pt sub(pt P) {x-=P.x; y-=P.y; z-=P.z; return this;};
	pt mul(float f) {x*=f; y*=f; z*=f; return this;};
	pt div(float f) {x/=f; y/=f; z/=f; return this;};
	pt div(int f) {x/=f; y/=f; z/=f; return this;};
	
	//===== point functions
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

}
