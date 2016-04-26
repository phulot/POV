package subsurface;

abstract class Tree {
	Cell cell;
	subSurface s;
	Tree father;
	Tree _1 = null;
	Tree _2 = null;
	Tree _3 = null;
	Tree(){}
	Tree(int cc, Tree t, subSurface sur) {
		cell = new Cell(cc, sur);
		father = t;
		s=sur;
		if (sur.getPov().borderCorner(cc))
			throw new Error("Illegal node");
	}


	abstract Tree next();

	public String toString(){
		return cell.toString();
	}
	
	public String borderToString(){
		return cell.borderToString();
	}
	
	public String toStat(){
		String ss = toString();
		s.tree += "," + ss;
		s.tetraType[GetType()]++;
		return ss;
	}
	
	public int GetType() {
		return cell.GetType();
	}
}
