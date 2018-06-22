package paf;

import java.util.ArrayList;
import java.util.BitSet;

public class Server {
	public ArrayList<Task> hiTasks;
	public ArrayList<Task> loTasks;
	// peut etre inutile
	public int cs;
	public int ts;
	// fin
	public int hyperP;
	
	public Server() {
		this.hiTasks =null ;
		this.loTasks =null ;
		this.hyperP = 0;
	}
	
	public Server(ArrayList<Task> hiTasks, ArrayList<Task> loTasks) {
		this.hiTasks =hiTasks ;
		this.loTasks =loTasks ;
		this.hyperP = HyperPeriod();
	}
	// renvoie l'utilisation haute du serveur
	private double UHi() {
		double resultat =0;
		for(Task hiTask : hiTasks) {
			resultat += hiTask.getUHi();
		}
		return resultat;
	}
	// renvoie l'utilisation basse du serveur
	private double ULo() {
		double resultat =0;
		for(Task loTask : loTasks) {
			resultat += loTask.getUHi();
		}
		return resultat;
	}
	
	public boolean isDiv() {
		// possibilité de stocker les valeurs déjà testés pour gagner du temps
		for(Task hiTask : hiTasks) {
			for (Task loTask : loTasks) {
				if (loTask.period%hiTask.period!=0) return false;
				
			}
		}
		if (ULo()>UHi()) return false;
		return true;
	}

	
	public boolean SDBF(){
		double sbf=0;
		double dbf=0;
		for (int i=0; i <= hyperP; i++) {		
			for(Task hiTask : hiTasks) {
				sbf+= hiTask.func(i*hyperP);
			}
			for(Task loTask : loTasks) {
				dbf+= loTask.func(i*hyperP);
			}
			if (sbf<dbf) return false;
			sbf=0;
			dbf=0;
		}
		return true;
	}
	
	
	
	private int PPCM(int a, int b){
		int A=a;
		int B=b;
		while(A!=B) {
			while(A>B) B+=b;
			while(A<B) A+=a;
		}
		return A;
	}
	
	public int HyperPeriod() {		
		ArrayList<Task> list = new ArrayList<Task>();
		list.addAll(hiTasks);
		list.addAll(loTasks);
		int i=2;
		int ppcm = PPCM(list.get(0).period ,list.get(1).period);
		while (i<list.size()) {
			ppcm = PPCM(list.get(i).period,ppcm);
			i+=1;
		}
		return ppcm;
	}
	
	public double Utilisation() {
		int cLo = 0;
		for (Task loTask : loTasks) {
			cLo += loTask.cLo;
		}
		return cLo;
	}
	
	public Server BitSet2Server(BitSet hi, BitSet lo, ArrayList<Task> globalHiTasks, ArrayList<Task> globalLoTasks) {
		ArrayList<Task> hiServerTask = new ArrayList<Task>() ;
		ArrayList<Task> loServerTask = new ArrayList<Task>() ;
		for ( int indice : getSetBits(hi)) {
			hiServerTask.add(globalHiTasks.get(indice));
		}
		for ( int indice : getSetBits(lo)) {
			loServerTask.add(globalLoTasks.get(indice));
		}
		return new Server(hiServerTask,loServerTask);
	}
	
	private static ArrayList<Integer> getSetBits(BitSet b)
	{
		ArrayList<Integer> resultat = new ArrayList<Integer>();
		for (int i=0; i<b.size(); i++) 
		{
			if(b.get(i)) resultat.add(i);
		}
		return resultat;
	}
	// test du critère de séquentialité
	public boolean testSeqY() 
	{
		double temp = this.UHi();
		if (temp>1) return false;
		return true;
	}
	
	public boolean testSeqX() 
	{
		double temp =this.ULo();
		if (temp>1) return false;
		return true;
	}
}
