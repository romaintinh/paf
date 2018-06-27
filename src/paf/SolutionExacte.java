package paf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NavigableSet;
import java.util.TreeMap;


public class SolutionExacte {
	// variables constitutives
	int stop =0;
	public int hi;
	public int lo;
	public ArrayList<Task> hiTasks;
	public ArrayList<Task> loTasks;
	// variable de la fonction maps
	TreeMap<OrderedAddBitSet,ArrayList<OrderedAddBitSet>> maps = new TreeMap<OrderedAddBitSet,ArrayList<OrderedAddBitSet>>();
	// variable de la fonction récursive
	public OrderedAddBitSet unionY ;
	public OrderedAddBitSet unionX ;
	public ArrayList<OrderedAddBitSet[]> maxSol = new ArrayList<OrderedAddBitSet[]>();
	public ArrayList<OrderedAddBitSet[]> Sol = new ArrayList<OrderedAddBitSet[]>();
	public double maxU =0;
	public ArrayList<OrderedAddBitSet> keys = new ArrayList<OrderedAddBitSet>();

	
	// constructeur vide à utiliser de pair avec loadFromTxt
	public SolutionExacte() 
	{
		this.hiTasks = new ArrayList<Task>();
		this.loTasks = new ArrayList<Task>();
		this.hi = 0;
		this.lo = 0;
		unionY = null;
		unionX = null;
	}
	
	// load bundle of task from file in the test directory
		public void loadFromTxt(String name) 
		{
			String[] content;
			String path = "./test/" + name;
			try {
				BufferedReader br = new BufferedReader(new FileReader(path));
				String line = br.readLine();
				while(line!=null) 
				{
					line = line.trim();
					content = line.split("\t");
					if (Integer.valueOf(content[5])==2) 
					{

						this.hiTasks.add(new Task(Integer.valueOf(content[1]),
								Double.valueOf(content[3]),
								Double.valueOf(content[4]),
								true));
					}
					else 
					{

						this.loTasks.add(new Task(Integer.valueOf(content[1]),
								Double.valueOf(content[3]),
								Double.valueOf(content[4]),
								false));
					}
					line = br.readLine();
				}
				this.hi = hiTasks.size();
				this.lo = loTasks.size();
				unionY = new OrderedAddBitSet(hi);
				unionX = new OrderedAddBitSet(lo);
			} catch (FileNotFoundException e) {
				System.out.println("no such file found");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("could not read");
				e.printStackTrace();
			}
		}
	
	// constructeur complet
	public SolutionExacte( ArrayList<Task> hit, ArrayList<Task> lot) 
	{
		this.hiTasks = hit;
		this.loTasks = lot;
		this.hi = hiTasks.size();
		this.lo = loTasks.size();
		unionY = new OrderedAddBitSet(hi);
		unionX = new OrderedAddBitSet(lo);
	}
	
	
	public Double findBestAllocrec( HashMap<OrderedAddBitSet,OrderedAddBitSet> partialSol, int filteringIndexKeys, int filteringIndexLo, Double uWC ){
		// on est arrivé au bout de la liste des alloactions par serveurs... le processus de construction de l'allocation est fini. 
		Double result = new Double(0);
		OrderedAddBitSet x;
		OrderedAddBitSet y;


		if ( filteringIndexKeys>=keys.size()) {
			// calculer result
			
			for (OrderedAddBitSet allocatedLoTasks : partialSol.values()) {
				result=result+this.UtilisationFromBitSet(allocatedLoTasks);
			}
			System.out.println("Fin de recursion U allocated = "+result.toString()+ "for pallox "+partialSol.size() +partialSol.toString());

		}
		else { // réaliser les appels récursifs
			// on vérifie si le serveur du prochain mapping a considérer dans maps est compatible 
			y = keys.get(filteringIndexKeys);
			ArrayList<OrderedAddBitSet> xlist= maps.get(y); 
			OrderedAddBitSet unionY =new OrderedAddBitSet(hi);
			OrderedAddBitSet unionX =new OrderedAddBitSet(lo);
			for (OrderedAddBitSet s:partialSol.keySet()) {
				unionY.or(s);

			}
			//si non compatible result est egal à findbestAlloc (partisol, indexServeur +1, indexLoTaskset =0)
			if (y.intersects(unionY)) 
			{
				print("Serveur non compatible, on passe au suivant");
				result = findBestAllocrec(partialSol, filteringIndexKeys+1,0,uWC);
			}
			else  {

				HashMap<OrderedAddBitSet,OrderedAddBitSet> pSolwithYX;
				for (OrderedAddBitSet s: partialSol.values()) {
					unionX.or(s);
				}
				x=xlist.get(filteringIndexLo);
				//si x non compatible result est egal à findbestAlloc (partisol, indexServeur, indexLoTaskset+1)
				if (x.intersects(unionX)) 
				{	
					print("Mapping non compatible, on passe au suivant");
					if (filteringIndexLo+1<xlist.size()) {
						result = findBestAllocrec(partialSol, filteringIndexKeys,filteringIndexLo+1,uWC);}
					else {		
						result = findBestAllocrec(partialSol, filteringIndexKeys+1,0,uWC);
					}

				}		
				else {// si x compatible calculer le U pour le cas ou on ajoute (x,y) à l'allocation et le cas ou on ne le fait pas, puis on retourne le min des deux. 
					Double firstcall;
					Double secondcall;
					if (filteringIndexLo+1<xlist.size()) 
					{
						firstcall=findBestAllocrec(partialSol, filteringIndexKeys,filteringIndexLo+1,uWC);}
					else {
						firstcall=findBestAllocrec(partialSol, filteringIndexKeys+1,0,uWC);}	

					pSolwithYX=(HashMap<OrderedAddBitSet, OrderedAddBitSet>) partialSol.clone();
					pSolwithYX.put(y,x);
					secondcall = findBestAllocrec(pSolwithYX, filteringIndexKeys+1,0,uWC);
					result= Double.max(secondcall, firstcall);
					print("min(a,b)=c"+ firstcall+" "+secondcall+" "+ result);
				}
			}
		}	
		return result;
	}

	
	// creation de l'ensemble maps qui contient les éléments élémentaires (Y (lot de tâches hi),X (lot de taches lo)) 
	// suceptible de former une allocation complete
	public void maps() 
	{
		// variables d'optimisation 
		ArrayList<OrderedAddBitSet> Xsorted = new ArrayList<OrderedAddBitSet>();
		ArrayList<OrderedAddBitSet> Ysorted = new ArrayList<OrderedAddBitSet>();
		
		// variables utiles pour obtenir l'ensemble des mappings valides
		ArrayList<OrderedAddBitSet> tempLo = new ArrayList<OrderedAddBitSet>();
		Server s = new Server();
		OrderedAddBitSet Y = new OrderedAddBitSet(hi);
		OrderedAddBitSet X = new OrderedAddBitSet(lo);
		Y.plusUn();
		for (int i =1;i<Math.pow(2, hi);i++) 
		{
			Ysorted.add((OrderedAddBitSet) Y.clone());
			Y.plusUn();
		}

		for (int i =0;i<Math.pow(2, lo);i++) 
		{
			Xsorted.add((OrderedAddBitSet) X.clone());
			X.plusUn();
		}

		// début de l'exploration de l'espace de solution
		for (OrderedAddBitSet Ys : Ysorted) 
		{
			s.BitSet2ServerHI(Ys, hiTasks);
			if (s.testSeqY()==false) 
			{
				continue;
			}
			for (OrderedAddBitSet Xc : Xsorted) 
			{
				s.BitSet2ServerLO(Xc, loTasks);
				if (s.testSeqX()==false) 
				{
					continue;
				}  
				if (s.isDiv()) {
					tempLo.add(Xc);
				}
				else 
				{
					if(s.SDBF()) tempLo.add(Xc);
				}
			}
			maps.put(Ys, tempLo);
			tempLo = new ArrayList<OrderedAddBitSet>();
		}
	}
	
	// converti deux OrderedAddBitSet en un serveur
	public Server BitSet2Server(OrderedAddBitSet hi, OrderedAddBitSet lo) 
	{
		ArrayList<Task> hiServerTask = new ArrayList<Task>() ;
		ArrayList<Task> loServerTask = new ArrayList<Task>() ;
		for ( int indice : hi.getSetBits()) {
			hiServerTask.add(hiTasks.get(indice));
		}
		for ( int indice : lo.getSetBits()) {
			loServerTask.add(loTasks.get(indice));
		}
		return new Server(hiServerTask,loServerTask);
	}
	
	// permet d'évaluer l'utilisation d'un OrderedAddBitSet( représentant une partie d'un des deux ensemble de taches) 
	// sans avoir à créer un serveur juste pour ce calcule
	private double UtilisationFromBitSet(OrderedAddBitSet X) 
	{
		ArrayList<Integer> SetBITS = X.getSetBits();
		double temp = 0;
		for (Integer index : SetBITS) 
		{
			temp+=loTasks.get(index).getULo();
		}
		return temp;
	}
	
	// trouve la meilleur allocation
	public void resolution() 
	{
		maps();
		
		Object[] temp= maps.navigableKeySet().toArray();
		
		// acquire set of keys in a ArrayList 
		
		for (int i=0;i<temp.length;i++) {
			this.keys.add((OrderedAddBitSet) temp[i]);
		}
		int a = keys.size();
		printM(this.maps);
		int filteringIndexKeys =0;
		int filteringIndexLO =0;
		HashMap<OrderedAddBitSet,OrderedAddBitSet> partialSol = new HashMap<OrderedAddBitSet,OrderedAddBitSet>();
		print("U alloué : "+findBestAllocrec( partialSol, filteringIndexKeys, filteringIndexLO, new Double (0) ));
	}
	
	// debug / test
	public static void printKeys(TreeMap<OrderedAddBitSet,ArrayList<OrderedAddBitSet>> maps) 
	{
		NavigableSet<OrderedAddBitSet> keys = maps.navigableKeySet();
		for (OrderedAddBitSet key : keys) 
		{
			System.out.println(key.toString());
		}
	}
	
	public void printM(TreeMap<OrderedAddBitSet,ArrayList<OrderedAddBitSet>> maps) 
	{
		NavigableSet<OrderedAddBitSet> keys = maps.navigableKeySet();
		for (OrderedAddBitSet key : keys) 
		{
			print(key.toString()+"   "+maps.get(key).toString());
		}
	}
	
	public static void print(Object L) 
	{
		System.out.println(L);
	}
	
	public static void printXS(ArrayList<OrderedAddBitSet> Xsorted) 
	{
		for (OrderedAddBitSet elt : Xsorted) {
			print(elt.toString());
		}
	}
	public static void printSol(ArrayList<OrderedAddBitSet[]> Xsorted) 
	{
		System.out.print("état de la solution  ");
		for (OrderedAddBitSet[] elt : Xsorted) {
			print(elt[0].toString()+"  "+elt[1].toString());
		}
		if(Xsorted.isEmpty()) print("solution vide");
	}
}
