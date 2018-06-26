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
import java.util.Set;
import java.util.SortedMap;

public class SolutionExacte {
	// variables constitutives
	public int stop =0;
	public int hi;
	public int lo;
	public ArrayList<Task> hiTasks;
	public ArrayList<Task> loTasks;
	// variable de la fonction maps
	public TreeMap<AddBitSet,ArrayList<AddBitSet>> maps;
	// variable de la fonction récursive
	public AddBitSet unionY ;
	public AddBitSet unionX ;
	public ArrayList<AddBitSet[]> maxSol = new ArrayList<AddBitSet[]>();
	public ArrayList<AddBitSet[]> Sol = new ArrayList<AddBitSet[]>();
	public double maxU =0;
	public boolean wasLast = false;
	
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
				unionY = new AddBitSet(hi);
				unionX = new AddBitSet(lo);
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
		unionY = new AddBitSet(hi);
		unionX = new AddBitSet(lo);
	}

		
	// recherche de la solution récursivement
	public void recSearch(TreeMap<AddBitSet,ArrayList<AddBitSet>> maps, Iterator<AddBitSet> positionY, ListIterator positionX)
	{	
		stop+=1;
		if(stop>300) return;
	//	printSol(Sol);
		if (positionY == null) 
		{
			NavigableSet<AddBitSet> keys = maps.navigableKeySet();
		//	print(keys.toString());
			positionY = keys.iterator();
			ArrayList<AddBitSet> values = maps.get(positionY.next()); // probleme avec le next
			positionX = values.listIterator();
			positionY = keys.iterator();
		}
		//une solution a été trouvé on vérifie sa qualité
		if (unionY.cardinality() == hi) 
		{
			print("calcul de U");
			double Utemp=0;
			for ( AddBitSet[] Solution : Sol) 
			{
				Utemp += this.UtilisationFromBitSet(Solution[1]);
			}
			if (Utemp>maxU) 
			{
				maxU = Utemp;
				maxSol.clear();
				// copie de la solution
				for (AddBitSet[] elt : Sol)
				{
					maxSol.add(elt.clone());					
				}
			}
			// on explore les solutions suivantes
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
			
			AddBitSet y = positionY.next();
			if (y.intersects(unionY) || y.equals(unionY)) // cas ou y = {} = unionY pas pris en compte pas intersect
			{
				print("continue 1");
				continue;
			}
			while(positionX.hasNext()) 
			{
			//	print("pass2");
				AddBitSet x = (AddBitSet) positionX.next();
			//	print(x.toString());
				if (x.intersects(unionX) || x.equals(unionX)) // cas ou x = {} = unionX pas pris en compte pas intersect
				{
					print("continue 2");
					continue;
				}
				unionY.or(y);
		//		print(unionY.toString());
				unionX.or(x);
		//		print(unionX.toString());
				AddBitSet[] data = {y,x};
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
		ArrayList<AddBitSet> values = maps.get(positionY.next());
		positionY = maps.navigableKeySet().tailSet(Sol.get(Sol.size()-1)[0]).iterator(); // pour annuler le .next()
		positionX = values.listIterator(values.indexOf(Sol.get(Sol.size()-1)[1]));
		printSol(Sol);
		recSearch(maps,positionY,positionX);		
	}
	
	
	// creation de l'ensemble maps qui contient les éléments élémentaires (Y (lot de tâches hi),X (lot de taches lo)) 
	// suceptible de former une allocation complete
	public TreeMap<AddBitSet,ArrayList<AddBitSet>> maps() 
	{
		
		// variables d'optimisation
		ArrayList<AddBitSet> Xsorted = new ArrayList<AddBitSet>();
		ArrayList<AddBitSet> WrongCombination = new ArrayList<AddBitSet>();
		AddBitSet temp = new AddBitSet(lo);
		
		// variables utiles pour obtenir l'ensemble des mappings valides
		TreeMap<AddBitSet,ArrayList<AddBitSet>> maps = new TreeMap<AddBitSet,ArrayList<AddBitSet>>();
		ArrayList<AddBitSet> tempLo = new ArrayList<AddBitSet>();
		Server s = new Server();
		AddBitSet Y = new AddBitSet(hi);
		AddBitSet X = new AddBitSet(lo);
		
		// l'orde est important pour parcourir X car les inclusions peuvent simplifier les calculs
		for (int i =0;i<Math.pow(2, lo);i++) 
		{
			Xsorted.add((AddBitSet) X.clone());
			X.plusUn();
		}
		Xsorted.sort(AddBitSet.BitSetCardinalityComparator);
//		printXS(Xsorted); Le code fonctionne jusque là
		// début de l'exploration de l'espace de solution
		for (int i = 0;i< Math.pow(2, hi);i++) 
		{
			s.BitSet2ServerHI(Y, hiTasks);
			if (s.testSeqY()==false) 
			{
				Y.plusUn();
				continue;
			}
			outerloop:
			for (AddBitSet Xc : Xsorted) 
			{
				/* si on a déjà testé une partition de PIlo incluse dans celle que l'on va tester (à Y constant) et qu'elle n'était pas valide
				 * alors pas besoin de faire les tests, elle non plus n'est pas valable */
				for(AddBitSet WC : WrongCombination) 
				{
					temp = (AddBitSet) WC.clone();
					temp.and(Xc);
					if(temp.equals(WC)) 
					{
						continue outerloop;
					}
				} 
				s.BitSet2ServerLO(Xc, loTasks);
				if (s.testSeqX()==false) 
				{
					WrongCombination.add(Xc);
					break;
				} 
				if (s.isDiv()) tempLo.add(Xc);
				else 
				{
					if(s.SDBF()) tempLo.add(Xc);
					else WrongCombination.add(Xc);
				}
			}
			maps.put((AddBitSet)Y.clone(), new ArrayList<AddBitSet>(tempLo));
			Y.plusUn();
			tempLo.clear();
			WrongCombination.clear(); 
		}
		return maps;
	}
	
	// converti deux AddBitSet en un serveur
	public Server BitSet2Server(AddBitSet hi, AddBitSet lo) 
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
	
	// permet d'évaluer l'utilisation d'un AddBitSet( représentant une partie d'un des deux ensemble de taches) 
	// sans avoir à créer un serveur juste pour ce calcule
	private double UtilisationFromBitSet(AddBitSet X) 
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
		maps = this.maps();
		printM(maps);
		recSearch(maps,null, null);
		System.out.println("l'utilisation maximale est" + String.valueOf(maxU));
		System.out.println("la solution qui produit ce résultat est :");
		for (AddBitSet[] elt : maxSol) 
		{
			System.out.print("tache(s) Hi" + elt[0].toString());
			System.out.println("|    tache(s) Lo" + elt[1].toString());
		}
	}
	
	// debug / test
	public static void printM(TreeMap<AddBitSet,ArrayList<AddBitSet>> maps) 
	{
		NavigableSet<AddBitSet> keys = maps.navigableKeySet();
		for (AddBitSet key : keys) 
		{
			System.out.println(key.toString());
			for (AddBitSet value : maps.get(key)) 
			{
				System.out.println("\t"+value.toString());
			}
		}
	}
	
	public static void print(Object L) 
	{
		System.out.println(L);
	}
	
	public static void printXS(ArrayList<AddBitSet> Xsorted) 
	{
		for (AddBitSet elt : Xsorted) {
			print(elt.toString());
		}
	}
	public static void printSol(ArrayList<AddBitSet[]> Xsorted) 
	{
		for (AddBitSet[] elt : Xsorted) {
			print(elt[0].toString()+"  "+elt[1].toString());
		}
	}
}
