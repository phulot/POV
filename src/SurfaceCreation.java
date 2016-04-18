import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public abstract class SurfaceCreation {
	subSurface s;
	pov p;
	int startingCorner;
	Tree surfaceTree;
	double[][] tertatypeStat=new double[4][20];
	SurfaceCreation(subSurface sur,int c){
		s=sur;startingCorner=c;
		s.tetraType= new double[19];
		s.tree="";
		p=s.pov;
		s.vertexmarked = new int[p.nv+1];
		s.marked = new boolean[p.nf];
		s.mm = new boolean[p.maxnt];
		//Create();
	}
	
	public double getPerf(){
		int k = 0;
		for (int j = 0; j < p.nv; j++) {
			if (s.vertexmarked[j] != 0)
				k++;
		}
		return k/(double)p.nv;
	}
	abstract public void Create();
	abstract public void CreateRepresentation();
	
	public void saveTestStat(int it){
		int i=0;
		boolean b=true;
		while(b){
			try {
				new FileReader(new File("data/examplesStat/"+s.display.Meshname+"/"+i+".txt")).close();
				i++;
			} catch (FileNotFoundException e) {
				b=false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			File theDir = new File("data/examplesStat/");
			if (!theDir.exists()) {
			    try{theDir.mkdir();} catch(SecurityException se){}
			}
			theDir = new File("data/examplesStat/"+s.display.Meshname+"/");
			if (!theDir.exists()) {
			    try{theDir.mkdir();} catch(SecurityException se){}
			}
			FileWriter fw = new FileWriter(new File("data/examplesStat/"+s.display.Meshname+"/"+i+".txt"));
			fw.write("Mesh infos : \n");
			Scanner sc = new Scanner(System.in);
			System.out.println("how many tests ? ");
			int nbiter =Integer.valueOf(sc.nextLine());
			System.out.println("add a comment ");
			fw.write(sc.nextLine()+"\n");sc.close();
			for (int k=0;k<1;k++)
				writeFile(fw,it,nbiter);
			globalStat(fw);
			fw.close();
			System.out.println("similation complete\n results are printed in : "+ "data/examplesStat/"+s.display.Meshname+"/"+i+".txt");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void writeFile(FileWriter fw,int it,int nbiter) throws IOException {
		s.createSurface(it,nbiter);
		fw.write("Mesh name :"+s.display.Meshname+"\n");
		fw.write("nv : "+p.nv+"\n");
		fw.write("nf : "+p.nf+"\n");
		fw.write("nt : "+p.nt+"\n");
		fw.write("nbr of bounding faces : "+(p.nf-4*p.nt)+"\n");
		fw.write("\nSurface infos : \n");
		saveTestStatSurface(fw);
		int k = 0;
		for (int j = 0; j < p.nv; j++) {
			if (s.vertexmarked[j] != 0)
				k++;
		}
		fw.write("nbr vertices : "+k+" ("+(k / (double) p.nv*100)+"%)"+"\n");
		fw.write("nbr external vertices : "+(p.nv-k)+" ("+((p.nv-k) / (double) p.nv*100)+"%)"+"\n");
		k=0;
		ArrayList<Integer> l = new ArrayList<Integer>();
		for (int c=0;c<12*p.nt;c++){
			if (s.unmarkedNeighborsNbr(c)==0&&!l.contains(p.v(c)))l.add(c);
		}
		fw.write("nbr internal vertices : "+k+" ("+(k / (double) p.nv*100)+"%)"+"\n");
		//s.createApartementTree(startingCorner,true);
		s.mm=new boolean[p.maxnt];
		DiggingSurfaceTree.faceNumber();
		WriteStatistic(fw,s.tetraType,DiggingSurfaceTree.faceNumber());
		fw.write("\nAppartement infos : \n\n");
		saveTestStatAppartement(fw);
		apartementStat(fw);
	}
	
	abstract void saveTestStatSurface(FileWriter fw) throws IOException;
	abstract void saveTestStatAppartement(FileWriter fw) throws IOException;
	//create the tree that represent an apartment given one of his external faces 
	//and store it in s.tree
	Tree createApartementTree(int c, boolean b) {
		s.tree="";
		Tree t = new ApartmentTree(c, null, b,s);
		s.apartementTree(t, b);
		Tree q = t.next();
		while (q != null) {
			s.apartementTree(q, b);
			q = q.next();
		}
		return t;
	}
	private void WriteStatistic(FileWriter fw,double[] t,int[] weigth) throws IOException{
		double k = 0,f=2;
		for (int i = 0; i < 19; i++){
			k+=t[i];f+=t[i]*weigth[i];
		}
		fw.write("Border faces : "+f+"\n");
		fw.write("tetrahedron nbr : "+k+"\n");
		if (t[0]!=0)fw.write("E : " + t[0]/k*100+"%\n");
		if (t[1]!=0)fw.write("V : " + t[1]/k*100+"%\n");
		if (t[2]!=0)fw.write("A : " + t[2]/k*100+"%\n");
		if (t[3]!=0)fw.write("B : " + t[3]/k*100+"%\n");
		if (t[4]!=0)fw.write("C : " + t[4]/k*100+"%\n");
		if (t[5]!=0)fw.write("AB : " + t[5]/k*100+"%\n");
		if (t[6]!=0)fw.write("AC : " + t[7]/k*100+"%\n");
		if (t[7]!=0)fw.write("BC : " + t[6]/k*100+"%\n");
		if (t[8]!=0)fw.write("ABC : " + t[8]/k*100+"%\n");
		if (t[9]!=0)fw.write("a : " + t[9]/k*100+"%\n");
		if (t[10]!=0)fw.write("b : " + t[10]/k*100+"%\n");
		if (t[11]!=0)fw.write("c : " + t[11]/k*100+"%\n");
		if (t[12]!=0)fw.write("ab : " + t[12]/k*100+"%\n");
		if (t[13]!=0)fw.write("ac : " + t[14]/k*100+"%\n");
		if (t[14]!=0)fw.write("bc : " + t[13]/k*100+"%\n");
		if (t[15]!=0)fw.write("abc : " + t[15]/k*100+"%\n");
		if (t[16]!=0)fw.write("Aa : " + t[16]/k*100+"%\n");
		if (t[17]!=0)fw.write("Bb : " + t[17]/k*100+"%\n");
		if (t[18]!=0)fw.write("Cc : " + t[18]/k*100+"%\n");
		fw.write(s.tree+"\n");
	}
	private void apartementStat(FileWriter fw) throws IOException {
		s.mm = new boolean[p.maxnt];
		int i = 4 * p.nt;
		int k = 0;
		while (i < p.nf) {
			for (int l = 0; l < 4*p.nt; l++)
				s.marked[l] |= s.mm[p.tetraFromFace(l)];
			s.mm = new boolean[p.maxnt];
			while (s.marked[p.O[i]] && i < p.nf)
				i++;
			s.TreeStat(createApartementTree(3 * p.O[i],false));
			if (s.tetraType[0]<=10){
				tertatypeStat[0][19]++;
				for (int l=0;l<19;l++)
				tertatypeStat[0][l]+=s.tetraType[l];
			}
			else if (s.tetraType[0]<=100){
				tertatypeStat[1][19]++;
				for (int l=0;l<19;l++)
				tertatypeStat[1][l]+=s.tetraType[l];
			}
			else if (s.tetraType[0]<=1000){
				tertatypeStat[2][19]++;
				for (int l=0;l<19;l++)
				tertatypeStat[2][l]+=s.tetraType[l];
			}
			else {
				tertatypeStat[3][19]++;
				for (int l=0;l<19;l++)
				tertatypeStat[3][l]+=s.tetraType[l];
			}
			fw.write("\nApartment nbr : " + k+"\n");
			fw.write("starting corner : " + 3 * p.O[i]+"\n");
			WriteStatistic(fw,s.tetraType,ApartmentTree.faceNumber());
			i++;
			k++;
		}
	}
	private void globalStat(FileWriter fw) throws IOException{
		double[] t = new double[19];
		for (int j=0;j<4;j++){
			for (int i=0;i<19;i++)
				t[i]=tertatypeStat[j][i]/tertatypeStat[j][19];
			WriteStatistic(fw,t,ApartmentTree.faceNumber());
		}
	}
	
}
