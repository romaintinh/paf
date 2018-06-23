package paf;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Set;
import java.util.SortedMap;

public class SolutionExacte {
	public int taille;
	public int hi;
	public int lo;
	public TreeMap<AddBitSet,ArrayList<AddBitSet>> maps;
	public ArrayList<Task> hiTasks;
	public ArrayList<Task> loTasks;
	public AddBitSet unionY ;
	public AddBitSet unionX ;
	public ArrayList<AddBitSet[]> maxSol ;
	public ArrayList<AddBitSet[]> Sol ;
	public double maxU =0;
	
	public SolutionExacte( ArrayList<Task> hit, ArrayList<Task> lot) {
		this.hiTasks = hit;
		this.loTasks = lot;
		this.hi = hiTasks.size();
		this.lo = loTasks.size();
		this.taille = hi +lo;
		unionY = new AddBitSet(hi);
		unionX = new AddBitSet(lo);
	}
	
	public void recSearch(TreeMap<AddBitSet,ArrayList<AddBitSet>> maps, Iterator<AddBitSet> positionY,Iterator<AddBitSet> positionX) {
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
	
	public TreeMap<AddBitSet,ArrayList<AddBitSet>> maps() {
		
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
		
		for (int i =0;i<Math.pow(2, lo);i++) 
		{
			Xsorted.add(X);
			X.plusUn();
		}
		Xsorted.sort(AddBitSet.BitSetCardinalityComparator);
	
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
	
	public Server BitSet2Server(AddBitSet hi, AddBitSet lo) {
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
