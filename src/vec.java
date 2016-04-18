
class vec {
	float x = 0, y = 0, z = 0;

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

	vec normalize() {
		float n = norm();
		if (n > 0.000001) {
			div(n);
		}
		;
		return this;
	};

	vec rotate(float a, vec I, vec J) { // Rotate this by angle a parallel in
										// plane (I,J) Assumes I and J are
										// orthogonal
		float x = POVjava.d(this, I), y = POVjava.d(this, J); // dot products
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
}
