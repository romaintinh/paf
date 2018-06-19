package paf;

import java.util.ArrayList;

public class Task {
	public int period;
	public int cHi;
	public int cLo;
	public boolean hiPriority;
	//la variable function contient la sbf si la priorité est hi et la dbf sinon
	public ArrayList<Double> function = new ArrayList<Double>();
	public int hyperP ;



	
	public Task(int p, int cHi, int cLo, boolean hiPriority) {
		this.cHi = cHi;
		this.cLo = cLo;
		this.period = p;
		this.hyperP = p;
		this.hiPriority = hiPriority;
	}
	
	public double getULo() {
		return cLo/period;
	}
	
	public double getUHi() {
		return cHi/period;
	}
	
	
}
