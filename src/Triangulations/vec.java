package Triangulations;

public class vec {
	public float x = 0, y = 0, z = 0;
	public String toString(){return "("+x+" "+y+" "+z+")";}
	vec() {
	};

	vec(float px, float py, float pz) {
		x = px;
		y = py;
		z = pz;
	};

	vec(float px, float py) {
		x = px;
		y = py;
	};

	vec set(float px, float py, float pz) {
		x = px;
		y = py;
		z = pz;
		return this;
	};

	vec setTo(vec V) {
		x = V.x;
		y = V.y;
		z = V.z;
		return this;
	};

	vec set(vec V) {
		x = V.x;
		y = V.y;
		z = V.z;
		return this;
	};

	vec add(vec V) {
		x += V.x;
		y += V.y;
		z += V.z;
		return this;
	};

	vec add(float s, vec V) {
		x += s * V.x;
		y += s * V.y;
		z += s * V.z;
		return this;
	};

	vec sub(vec V) {
		x -= V.x;
		y -= V.y;
		z -= V.z;
		return this;
	};

	vec mul(float f) {
		x *= f;
		y *= f;
		z *= f;
		return this;
	};

	vec div(float f) {
		x /= f;
		y /= f;
		z /= f;
		return this;
	};

	vec div(int f) {
		x /= f;
		y /= f;
		z /= f;
		return this;
	};

	vec rev() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	};

	float norm() {
		return (float) (Math.sqrt(x * x + y * y + z * z));
	};

	public vec normalize() {
		float n = norm();
		if (n > 0.000001) {
			div(n);
		}
		else throw new Error();
		;
		return this;
	};

	vec rotate(float a, vec I, vec J) { // Rotate this by angle a parallel in
										// plane (I,J) Assumes I and J are
										// orthogonal
		float x = d(this, I), y = d(this, J); // dot products
		float c = (float) Math.cos(a), s = (float) Math.sin(a);
		add(x * c - x - y * s, I);
		add(x * s + y * c - y, J);
		return this;
	};

	// ===== vector functions
	public static vec V() {
		return new vec();
	}; // make vector (x,y,z)

	public static vec V(float x, float y, float z) {
		return new vec(x, y, z);
	}; // make vector (x,y,z)

	public static vec V(vec V) {
		return new vec(V.x, V.y, V.z);
	}; // make copy of vector V

	public static vec A(vec A, vec B) {
		return new vec(A.x + B.x, A.y + B.y, A.z + B.z);
	}; // A+B

	public static vec A(vec U, float s, vec V) {
		return V(U.x + s * V.x, U.y + s * V.y, U.z + s * V.z);
	}; // U+sV

	public static vec M(vec U, vec V) {
		return V(U.x - V.x, U.y - V.y, U.z - V.z);
	}; // U-V

	public static vec M(vec V) {
		return V(-V.x, -V.y, -V.z);
	}; // -V

	public static vec V(vec A, vec B) {
		return new vec((A.x + B.x) / 2.0f, (A.y + B.y) / 2.0f, (A.z + B.z) / 2.0f);
	} // (A+B)/2

	public static vec V(vec A, float s, vec B) {
		return new vec(A.x + s * (B.x - A.x), A.y + s * (B.y - A.y), A.z + s * (B.z - A.z));
	}; // (1-s)A+sB

	public static vec V(vec A, vec B, vec C) {
		return new vec((A.x + B.x + C.x) / 3.0f, (A.y + B.y + C.y) / 3.0f, (A.z + B.z + C.z) / 3.0f);
	}; // (A+B+C)/3

	public static vec V(vec A, vec B, vec C, vec D) {
		return V(V(A, B), V(C, D));
	}; // (A+B+C+D)/4

	public static vec V(float s, vec A) {
		return new vec(s * A.x, s * A.y, s * A.z);
	}; // sA

	public static vec V(float a, vec A, float b, vec B) {
		return A(V(a, A), V(b, B));
	} // aA+bB

	public static vec V(float a, vec A, float b, vec B, float c, vec C) {
		return A(V(a, A, b, B), V(c, C));
	} // aA+bB+cC

	public static vec V(pt P, pt Q) {
		return new vec(Q.x - P.x, Q.y - P.y, Q.z - P.z);
	}; // PQ

	public static vec U(vec V) {
		float n = V.norm();
		if (n < 0.0000001)
			return V(0, 0, 0);
		else
			return V(1f / n, V);
	}; // V/||V||

	public static vec U(pt P, pt Q) {
		return U(V(P, Q));
	}; // PQ/||PQ||

	public static vec U(float x, float y, float z) {
		return U(V(x, y, z));
	}; // make vector (x,y,z)

	public static vec N(vec U, vec V) {
		return V(U.y * V.z - U.z * V.y, U.z * V.x - U.x * V.z, U.x * V.y - U.y * V.x);
	}; // UxV cross product (normal to both)

	public static vec N(pt A, pt B, pt C) {
		return N(V(A, B), V(A, C));
	}; // normal to triangle (A,B,C), not normalized (proportional to area)

	public static vec B(vec U, vec V) {
		return U(N(N(U, V), U));
	}

	public static vec Normal(vec V) {
		if (Math.abs(V.z) <= Math.min(Math.abs(V.x), Math.abs(V.y)))
			return V(-V.y, V.x, 0);
		if (Math.abs(V.x) <= Math.min(Math.abs(V.z), Math.abs(V.y)))
			return V(0, -V.z, V.y);
		return V(V.z, 0, -V.x);
	}
	// ===== measures
	private static float sq(float x2) {return x2*x2;}
	public static float d(vec U, vec V) {return U.x*V.x+U.y*V.y+U.z*V.z; };                                            //U*V dot product
	public static float dot(vec U, vec V) {return U.x*V.x+U.y*V.y+U.z*V.z; };                                            //U*V dot product
	public static float det2(vec U, vec V) {return -U.y*V.x+U.x*V.y; };                                       // U|V det product
	public static float det3(vec U, vec V) {return (float) Math.sqrt(d(U,U)*d(V,V) - sq(d(U,V))); };                                       // U|V det product
	public static float m(vec U, vec V, vec W) {return d(U,vec.N(V,W)); };                                                 // (UxV)*W  mixed product, determinant
	public static float n2(vec V) {return sq(V.x)+sq(V.y)+sq(V.z);};                                                   // V*V    norm squared
	public static float n(vec V) {return (float) Math.sqrt(n2(V));};                                                                // ||V||  norm
	public static boolean parallel (vec U, vec V) {return n(vec.N(U,V))<n(U)*n(V)*0.00001; }                              // true if U and V are almost parallel
	public static float angle(vec U, vec V) {return (float) Math.acos(d(U,V)/n(V)/n(U)); };                                       // angle(U,V)
	public static boolean cw(vec U, vec V, vec W) {return m(U,V,W)>0; };                                               // (UxV)*W>0  U,V,W are clockwise
	// ===== rotate 

	public static vec R(vec V) {return vec.V(-V.y,V.x,V.z);} // rotated 90 degrees in XY plane
	public static vec R(vec V, float a, vec I, vec J) {float x=d(V,I), y=d(V,J); float c=(float) Math.cos(a), s=(float) Math.sin(a); return vec.A(V,vec.V(x*c-x-y*s,I,x*s+y*c-y,J)); }; // Rotated V by a parallel to plane (I,J)

}
