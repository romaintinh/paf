package paf;


import java.util.ArrayList;
import java.util.BitSet;


public class test {
	@SuppressWarnings("unchecked")
	public static void main (String arg[]) 
	{
		
		SolutionExacte S = new SolutionExacte();
		S.loadFromTxt("testdiv");

		Server S1 = new Server(S.hiTasks,S.loTasks);
		System.out.println(S1.isDiv());
		
	/*	loT = (ArrayList<Task>) S.hiTasks;
		hiT = (ArrayList<Task>) S.loTasks;
		loT.remove(3);
		Server S2 = new Server(hiT,loT);
		System.out.println(S2.isDiv());
		
		loT = (ArrayList<Task>) S.hiTasks;
		hiT = (ArrayList<Task>) S.loTasks;
		Server S3 = new Server(hiT,loT);
		System.out.println(S3.isDiv());
*/
	}
	
	public static void printArrayList(ArrayList<Task> L) 
	{
		for (Task t : L) 
		{
			System.out.print(t.period+"\t");
			System.out.print(t.cHi+"\t");
			System.out.println(t.cLo);
		}
	}

}
