package paf;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;

public class OrderedAddBitSet extends BitSet implements Comparable<OrderedAddBitSet>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int sizeWhenInstanciated;

	
	public OrderedAddBitSet(int i) 
	{
		super(i);
		sizeWhenInstanciated = i;
	}
	
	// converti le bitset en nombre binaire (pour la relation d'ordre)
	private int toBin() {
		int res =0;
		for (int i=0;i<sizeWhenInstanciated;i++) {
			if (this.get(i)) res+=Math.pow(2, i);
		}
		return res;
	}
	
	// augmente la valeur en binaire de 1
	public void plusUn() 
	{
		for (int i =0; i<sizeWhenInstanciated;i++) 
		{
			if (!(this.get(i))) 
			{
				this.set(i);
				break;
			}
			else 
			{
				this.flip(i);
			}
		}
	}
	
	// renvoie un ArrayList contenant les indices des bit valant 1
	public ArrayList<Integer> getSetBits()
	{
		ArrayList<Integer> resultat = new ArrayList<Integer>();
		for (int i=0; i<this.size(); i++) 
		{
			if(this.get(i)) resultat.add(i);
		}
		return resultat;
	} 
	// pour tester 
	public void printSet() 
	{
		String output = "";
		for (int i = 0; i<sizeWhenInstanciated;i++) 
		{
			if (this.get(i)) output += "1 ";
			else output+= "0 ";
		}
		System.out.println(output);
	}
	// ajoute un ordre pour pouvoir trier les ensembles de AddBitSet
		@Override
		public int compareTo(OrderedAddBitSet o) 
		{
			return (this.toBin()-o.toBin());
		}
		
		public static Comparator<OrderedAddBitSet> BitSetCardinalityComparator = new Comparator<OrderedAddBitSet>() 
		{
			public int compare(OrderedAddBitSet AddBitSet1, OrderedAddBitSet AddBitSet2) 
			{
				return AddBitSet1.compareTo(AddBitSet2);
			}
		};
}
