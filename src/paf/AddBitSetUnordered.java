package paf;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;

public class AddBitSetUnordered extends BitSet implements Comparable<AddBitSetUnordered>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int sizeWhenInstanciated;

	
	public AddBitSetUnordered(int i) 
	{
		super(i);
		sizeWhenInstanciated = i;
	}
	
	public int toBin() {
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
		public int compareTo(AddBitSetUnordered o) 
		{
			return (this.toBin()-o.toBin());
		}
		
		public static Comparator<AddBitSetUnordered> BitSetCardinalityComparator = new Comparator<AddBitSetUnordered>() 
		{
			public int compare(AddBitSetUnordered AddBitSet1, AddBitSetUnordered AddBitSet2) 
			{
				return AddBitSet1.compareTo(AddBitSet2);
			}
		};
}
