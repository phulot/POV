package subsurface;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class CreateSurfaceY extends SurfaceCreation{
	HashMap<Integer,HashSet<Integer>> apartments = new HashMap<Integer, HashSet<Integer>>(); 
	int[] indices = new int[s.getPov().nt];
	
	CreateSurfaceY(subSurface sur, int c, String meshName) {
		super(sur, c, meshName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Create() {
		for (int i=0;i<s.getPov().nt;i++){
			indices[i]=i;
			HashSet<Integer> set = new HashSet<>();
			set.add(i);
			apartments.put(i,set);
		}
		boolean mod=true;
		while (mod){
			
		}
		
	}

	@Override
	public void CreateRepresentation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void saveTestStatSurface(FileWriter fw) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	void saveTestStatAppartement(FileWriter fw) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
