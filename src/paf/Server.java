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
			resultat += loTask.getULo();
		}
		return resultat;
	}
	
	// test du premier critère de "Scheduability" reposant sur la divisibilité de la période de chaque tâche lo par celle de chaque tâche hi
	public boolean isDiv() {
		// vérification de la divisibilité
		for(Task hiTask : hiTasks) {
			for (Task loTask : loTasks) {
				if (loTask.period%hiTask.period!=0) 
				{
					return false;
				}
				
			}
		}
		// vérification de la condition sur l'utilisation
		if (ULo()>UHi()) return false;
		return true;
	}

	// test du second critère de "Scheduability"
	public boolean SDBF(){
		double sbf=(double) 0;
		double dbf=(double) 0;
		for (int i=0; i <= hyperP; i++) 
		{		
			for(Task hiTask : hiTasks) 
			{
				sbf+= hiTask.func(i);
			}
			for(Task loTask : loTasks) 
			{
				dbf+= loTask.func(i);
			}
		//	System.out.println((Double.compare(sbf, dbf)==-1));
			if (Double.compare(sbf, dbf)==-1) 
			{
			/*	System.out.println(i);
				System.out.println(sbf);
				System.out.println(dbf); */
				return false;
			}
			sbf=(double) 0;
			dbf=(double) 0;
		}
		return true;
	}
	
	// ppcm de deux entiers
	private int PPCM(int a, int b){
		int A=a;
		int B=b;
		while(A!=B) {
			while(A>B) B+=b;
			while(A<B) A+=a;
		}
		return A;
	}
	
	// ppcm des periodes d'un ensemble de taches
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

	// remplissage des tâches hi et lo à partir d'un OrderedAddbitSet
	public void BitSet2Server(OrderedAddBitSet hi, OrderedAddBitSet lo, ArrayList<Task> globalHiTasks, ArrayList<Task> globalLoTasks) {
		ArrayList<Task> hiServerTask = new ArrayList<Task>() ;
		ArrayList<Task> loServerTask = new ArrayList<Task>() ;
		for ( int indice : hi.getSetBits()) {
			hiServerTask.add(globalHiTasks.get(indice));
		}
		for ( int indice : lo.getSetBits()) {
			loServerTask.add(globalLoTasks.get(indice));
		}
		this.hiTasks = hiServerTask;
		this.loTasks = loServerTask;
	}
	// remplissage des tâches hi à partir d'un OrderedAddbitSet
	public void BitSet2ServerHI(OrderedAddBitSet hi, ArrayList<Task> globalHiTasks) {
		ArrayList<Task> hiServerTask = new ArrayList<Task>() ;
		for ( int indice : hi.getSetBits()) {
			hiServerTask.add(globalHiTasks.get(indice));
		}
		this.hiTasks = hiServerTask;
	}
	// remplissage des tâches lo à partir d'un OrderedAddbitSet
	public void BitSet2ServerLO(OrderedAddBitSet lo, ArrayList<Task> globalLoTasks) {
		ArrayList<Task> loServerTask = new ArrayList<Task>() ;
		for ( int indice : lo.getSetBits()) {
			loServerTask.add(globalLoTasks.get(indice));
		}
		this.loTasks = loServerTask;
	}
	
	// tests du critère de séquentialité
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
	// utilisé pour le debogage
	public static void printArrayList(ArrayList<Task> L) 
	{
		for (Task t : L) 
		{
			System.out.print(t.period+"\t");
			System.out.print(t.cHi+"\t");
			System.out.println(t.cLo);
		}
	}

}
