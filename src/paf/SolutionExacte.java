package paf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Set;
import java.util.SortedMap;

public class SolutionExacte {
	// variables constitutives
	public int hi;
	public int lo;
	public ArrayList<Task> hiTasks;
	public ArrayList<Task> loTasks;
	// variable de la fonction maps
	public TreeMap<AddBitSet,ArrayList<AddBitSet>> maps;
	// variable de la fonction récursive
	public AddBitSet unionY ;
	public AddBitSet unionX ;
	public ArrayList<AddBitSet[]> maxSol ;
	public ArrayList<AddBitSet[]> Sol ;
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
								Integer.valueOf(content[3]),
								Integer.valueOf(content[4]),
								true));
					}
					else 
					{

						this.loTasks.add(new Task(Integer.valueOf(content[1]),
								Integer.valueOf(content[3]),
								Integer.valueOf(content[4]),
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
	public void recSearch(TreeMap<AddBitSet,ArrayList<AddBitSet>> maps, Iterator<AddBitSet> positionY,Iterator<AddBitSet> positionX)
	{
		if (positionY == null) 
		{
			Set<AddBitSet> keys = maps.keySet();
			positionY = keys.iterator();
			
		}
		if ((positionX == null) && positionY.hasNext())
		{
			ArrayList<AddBitSet> values = maps.get(positionY.next());
			positionX = values.iterator();
		}
		//condition d'arret
		if (positionY.hasNext()==false && positionX.hasNext()==false) return;
		//une solution a été trouvé on vérifie sa qualité
		if (unionY.cardinality() == hi) 
		{
			double Utemp=0;
			for ( AddBitSet[] Solution : Sol) 
			{
				Utemp += this.UtilisationFromBitSet(Solution[1]);
			}
			if (Utemp>maxU) 
			{
				maxU = Utemp;
				maxSol = Sol;
			}
			// on explore les solutions suivantes
			Sol.remove(Sol.size()-1);
			this.recSearch(maps, positionY, positionX);
		}
		// la solution partielle n'est pas complète
		while (positionY.hasNext()) 
		{
			AddBitSet y = positionY.next();
			if (y.intersects(unionY)) continue;
			while(positionX.hasNext()) 
			{
				AddBitSet x = positionX.next();
				if (x.intersects(unionX)) continue;
				unionY.or(y);
				unionX.or(x);
				AddBitSet[] data = {y,x};
				Sol.add(data);
				this.recSearch(maps, positionY, positionX);
			}
		}
		// il n'y a pas de solution qui commence par le contenu de Sol donc on retire le dernière élément et on continue
		unionY.andNot(Sol.get(Sol.size()-1)[0]);
		unionX.andNot(Sol.get(Sol.size()-1)[1]);
		Sol.remove(Sol.size()-1);	
		SortedMap<AddBitSet,ArrayList<AddBitSet>> newmaps =  maps.tailMap(Sol.get(Sol.size()-1)[0]);
		positionY = newmaps.keySet().iterator();
		ArrayList<AddBitSet> values = maps.get(positionY.next());
		positionX = values.iterator();
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
			Xsorted.add(X);
			X.plusUn();
		}
		Xsorted.sort(AddBitSet.BitSetCardinalityComparator);
		
		// début de l'exploration de l'espace de solution
		for (int i = 0;i< Math.pow(2, hi);i++) 
		{
			s.BitSet2ServerHI(Y, hiTasks);
			if (s.testSeqY()==false) break;
			outerloop:
			for (AddBitSet Xc : Xsorted) 
			{
				/* si on a déjà testé une partition de PIlo incluse dans celle que l'on va tester (à Y constant) et qu'elle n'était pas valide
				 * alors pas besoin de faire les tests, elle non plus n'est pas valable */
				for(AddBitSet WC : WrongCombination) 
				{
					temp = (AddBitSet) WC.clone();
					temp.and(Xc);
					if(temp.equals(WC)) continue outerloop;
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
			maps.put(Y, tempLo);
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
		recSearch(maps,null, null);
		System.out.println("l'utilisation maximale est" + String.valueOf(maxU));
		System.out.println("la solution qui produit ce résultat est :");
		for (AddBitSet[] elt : maxSol) 
		{
			System.out.print("tache(s) Hi" + elt[0].toString());
			System.out.println("|    tache(s) Lo" + elt[1].toString());
		}
	}
	
	
}
