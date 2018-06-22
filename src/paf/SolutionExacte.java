package paf;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Set;
import java.util.SortedMap;

public class SolutionExacte {
	public int taille;
	public int hi;
	public int lo;
	public TreeMap<BitSet,ArrayList<BitSet>> maps;
	public ArrayList<Task> hiTasks;
	public ArrayList<Task> loTasks;
	public BitSet unionY = new BitSet();
	public BitSet unionX = new BitSet();;
	public ArrayList<BitSet[]> maxSol ;
	public ArrayList<BitSet[]> Sol ;
	public double maxU =0;
	
	public SolutionExacte( ArrayList<Task> hit, ArrayList<Task> lot) {
		this.hiTasks = hit;
		this.loTasks = lot;
		this.hi = hiTasks.size();
		this.lo = loTasks.size();
		this.taille = hi +lo;
	}
	
	public void recSearch(TreeMap<BitSet,ArrayList<BitSet>> maps, Iterator<BitSet> positionY,Iterator<BitSet> positionX) {
		if (positionY == null) 
		{
			Set<BitSet> keys = maps.keySet();
			positionY = keys.iterator();
			
		}
		if ((positionX == null) && positionY.hasNext())
		{
			ArrayList<BitSet> values = maps.get(positionY.next());
			positionX = values.iterator();
		}
		//condition d'arret
		if (positionY.hasNext()==false && positionX.hasNext()==false) return;
		//une solution a été trouvé on vérifie sa qualité
		if (unionY.cardinality() == hi) 
		{
			double Utemp=0;
			for ( BitSet[] Solution : Sol) 
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
			BitSet y = positionY.next();
			if (y.intersects(unionY)) continue;
			while(positionX.hasNext()) 
			{
				BitSet x = positionX.next();
				if (x.intersects(unionX)) continue;
				unionY.or(y);
				unionX.or(x);
				BitSet[] data = {y,x};
				Sol.add(data);
				this.recSearch(maps, positionY, positionX);
			}
		}
		// il n'y a pas de solution qui commence par le contenu de Sol donc on retire le dernière élément et on continue
		unionY.andNot(Sol.get(Sol.size()-1)[0]);
		unionX.andNot(Sol.get(Sol.size()-1)[1]);
		Sol.remove(Sol.size()-1);	
		SortedMap<BitSet,ArrayList<BitSet>> newmaps =  maps.tailMap(Sol.get(Sol.size()-1)[0]);
		positionY = newmaps.keySet().iterator();
		ArrayList<BitSet> values = maps.get(positionY.next());
		positionX = values.iterator();
		recSearch(maps,positionY,positionX);		
	}
	
	public TreeMap<BitSet,ArrayList<BitSet>> maps() {
		TreeMap<BitSet,ArrayList<BitSet>> maps = new TreeMap<BitSet,ArrayList<BitSet>>();
		ArrayList<BitSet> tempLo = new ArrayList<BitSet>();
		Server s = new Server();
		BitSet Y = new BitSet(hi);
		BitSet X = new BitSet(lo);
		// possibilité de prendre avantage des inclusion pour éviter des tests
		for (int i = 0;i< Math.pow(2, hi);i++) 
		{
			for (int j = 0;j< Math.pow(2, lo);j++) 
			{
				s.BitSet2Server(Y, X, hiTasks, loTasks);
				if (s.testSeqY()==false) break;
				if (s.testSeqX()==false) break;
				if (s.isDiv()) tempLo.add(X);
				else 
				{
					if(s.SDBF()) tempLo.add(X);
				}
				X = plusUn(X);
			}
			maps.put(Y, tempLo);
			Y=plusUn(Y);
			tempLo.clear();
		}
		return maps;
	}
	
	public Server BitSet2Server(BitSet hi, BitSet lo) {
		ArrayList<Task> hiServerTask = new ArrayList<Task>() ;
		ArrayList<Task> loServerTask = new ArrayList<Task>() ;
		for ( int indice : getSetBits(hi)) {
			hiServerTask.add(hiTasks.get(indice));
		}
		for ( int indice : getSetBits(lo)) {
			loServerTask.add(loTasks.get(indice));
		}
		return new Server(hiServerTask,loServerTask);
	}
	
	private ArrayList<Integer> getSetBits(BitSet b)
	{
		ArrayList<Integer> resultat = new ArrayList<Integer>();
		for (int i=0; i<b.size(); i++) 
		{
			if(b.get(i)) resultat.add(i);
		}
		return resultat;
	} 
	
	private static BitSet plusUn(BitSet b) 
	{
		for (int i =0; i<b.size();i++) 
		{
			if (!(b.get(i))) 
			{
				b.set(i);
				break;
			}
			else 
			{
				b.flip(i);
			}
		}
		return b;
	}
	
	private double UtilisationFromBitSet(BitSet X) 
	{
		ArrayList<Integer> SetBITS = this.getSetBits(X);
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
		for (BitSet[] elt : maxSol) 
		{
			System.out.print("tache(s) Hi" + elt[0].toString());
			System.out.println("|    tache(s) Lo" + elt[1].toString());
		}
	}
}
