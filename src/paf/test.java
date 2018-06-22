package paf;


import java.util.ArrayList;
import java.util.BitSet;


public class test {
	public static void main (String arg[]) {
		BitSet a = new BitSet(1);
		System.out.println(a);
		a = plusUn(a);
		System.out.println(a);
		a = plusUn(a);
		System.out.println(a);
		a = plusUn(a);
		System.out.println(a);
		a = plusUn(a);
		System.out.println(a);
		a = plusUn(a);
		System.out.println(a);
		a = plusUn(a);
		System.out.println(a);
	
	
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
