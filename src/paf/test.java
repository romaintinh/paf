package paf;


import java.util.ArrayList;
import java.util.BitSet;


public class test {
	public static void main (String arg[]) {
		AddBitSet a = new AddBitSet(5);
		int i=1;
		
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		a.printSet();
		System.out.println(i);
		i+=1;
		a.suivantDeMemeNorme();
		
	
	
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
}
