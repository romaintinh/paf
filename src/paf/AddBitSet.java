package paf;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;

public class AddBitSet extends BitSet implements Comparable<AddBitSet>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int sizeWhenInstanciated;

	
	public AddBitSet(int i) 
	{
		super(i);
		sizeWhenInstanciated = i;
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
	public int compareTo(AddBitSet o) {
		// TODO Auto-generated method stub
		return (this.cardinality()-o.cardinality());
	}
	
	public static Comparator<AddBitSet> BitSetCardinalityComparator = new Comparator<AddBitSet>() 
	{
		public int compare(AddBitSet AddBitSet1, AddBitSet AddBitSet2) 
		{
			return AddBitSet1.compareTo(AddBitSet2);
		}
	};
}
