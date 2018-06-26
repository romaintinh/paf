package paf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
					if (Integer.valueOf(content[5])==1) 
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

		
	// recherche de la solution récursivement
	public void recSearch(TreeMap<OrderedAddBitSet,ArrayList<OrderedAddBitSet>> maps, Iterator<OrderedAddBitSet> positionY, ListIterator<OrderedAddBitSet> positionX)
	{	
	/* if (stop>300) return;
		stop+=1;  */
	//	printSol(Sol);
		if (positionY == null) 
		{
			NavigableSet<OrderedAddBitSet> keys = maps.navigableKeySet();
		//	print(keys.toString());
			positionY = keys.iterator();
			ArrayList<OrderedAddBitSet> values = maps.get(positionY.next());
			positionX = values.listIterator();
			positionY = keys.iterator();
		}
		//une solution a été trouvé on vérifie sa qualité
		if (unionY.cardinality() == maps.keySet().size()) 
		{
			print("calcul de U");
			double Utemp=0;
			for ( OrderedAddBitSet[] Solution : Sol) 
			{
				Utemp += this.UtilisationFromBitSet(Solution[1]);
			}
			if (Utemp>maxU) 
			{
				maxU = Utemp;
				maxSol.clear();
				// copie de la solution
				for (OrderedAddBitSet[] elt : Sol)
				{
					maxSol.add(elt.clone());					
				}
			}
			// on explore les solutions suivantes
			printSol(Sol);
			if(Sol.isEmpty()) return;
			// deux cas selon X.hasnext 
			if (positionX.hasNext()) 
			{
		
			}
			Sol.remove(Sol.size()-1);
			printSol(Sol);
			// condition d'arret
			if (positionY.hasNext()==false && positionX.hasNext()==false) 
			{
				return;
			}
			this.recSearch(maps, positionY, positionX);
		}
		// la solution partielle n'est pas complète
		// condition d'arret
		if (positionY.hasNext()==false && positionX.hasNext()==false) return;
		while (positionY.hasNext())
		{	
			print("pass1");
			OrderedAddBitSet y = positionY.next();
			print("y est : " + y.toString());
			if (y.intersects(unionY)) // cas ou y = {} = unionY pas pris en compte pas intersect
			{
				print("continue 1");
				continue ;
			}
			while(positionX.hasNext()) 
			{
				print("pass2");
				OrderedAddBitSet x = (OrderedAddBitSet) positionX.next();
				print("x est : " + x.toString());
				if (x.intersects(unionX)) // cas ou x = {} = unionX pas pris en compte pas intersect
				{
					print("continue 2");
					continue;
				}
				unionY.or(y);
				print("unionY est : "+unionY.toString());
				unionX.or(x);
				print("unionX est : " + unionX.toString());
				OrderedAddBitSet[] data = {y,x};
				Sol.add(data.clone());		
				printSol(Sol);
		//		print(positionY.hasNext());
				this.recSearch(maps, positionY, positionX);
			}
		}
		// il n'y a pas de solution qui commence par le contenu de Sol donc on retire le dernière élément et on continue
		if (Sol.isEmpty() && positionY.hasNext()==false)
		{
			return ;
		} 
		unionY.andNot(Sol.get(Sol.size()-1)[0]);
		unionX.andNot(Sol.get(Sol.size()-1)[1]);
		Sol.remove(Sol.size()-1);
		if (Sol.isEmpty() && positionY.hasNext()==false)
		{
			return ;
		} 
		printSol(Sol);
		positionY = maps.navigableKeySet().tailSet(Sol.get(Sol.size()-1)[0]).iterator();
		ArrayList<OrderedAddBitSet> values = maps.get(positionY.next());
		positionY = maps.navigableKeySet().tailSet(Sol.get(Sol.size()-1)[0]).iterator(); // pour annuler le .next()
		positionX = values.listIterator(values.indexOf(Sol.get(Sol.size()-1)[1]));
		printSol(Sol);
		recSearch(maps,positionY,positionX);		
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
		printM(this.maps);
		recSearch(maps,null, null);
		System.out.println("l'utilisation maximale est " + String.valueOf(maxU));
		System.out.println("la solution qui produit ce résultat est :");
		for (OrderedAddBitSet[] elt : maxSol) 
		{
			System.out.print("tache(s) Hi" + elt[0].toString());
			System.out.println("|    tache(s) Lo" + elt[1].toString());
		}
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
