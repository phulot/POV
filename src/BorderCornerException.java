
class BorderCornerException extends Exception{
	int o;
	private static final long serialVersionUID = 1L;

	public BorderCornerException(int corner) {
		o=corner;
		// TODO Auto-generated constructor stub
	}

}

class BorderFaceException extends Exception{
	int o;
	private static final long serialVersionUID = 1L;

	public BorderFaceException(int corner) {
		o=corner;
		// TODO Auto-generated constructor stub
	}

}
