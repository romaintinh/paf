package paf;

import java.util.ArrayList;

public class Server {
	public ArrayList<Task> hiTasks;
	public ArrayList<Task> loTasks;
	// peut etre inutile
	public int cs;
	public int ts;
	// fin
	public int hyperP;
	
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
	// fonction analytique de sbf
	private double sbf(int t, Task ta) {
		double temp = t-(ta.period - ta.cHi);
		temp = Math.floor(temp/ta.period)*ta.cHi;
		temp+= Math.max(t-2*(ta.period-ta.cHi)-ta.period*Math.floor(t-(ta.period - ta.cHi)/ta.period), 0);
		return(temp);
	}
	// fonction analytique de dbf
	
	private double dbf(int t, Task ta) {
		return Math.floor(t/ta.period)*ta.cLo;
	}
	
	private ArrayList<Double> SBF(){
		for(Task hiTask : hiTasks) {
			if ()
		}
	}
	
	
	public boolean SDBF() {
		
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
}
