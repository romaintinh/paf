package paf;

import java.util.ArrayList;
import java.util.HashMap;

public class Task {
	public int period;
	public double cHi;
	public double cLo;
	public boolean hiPriority;
	//la variable function contient la sbf si la priorité est hi et la dbf sinon
	public HashMap<Integer,Double> function = new HashMap<Integer,Double>();

	public Task(int p, double cHi, double cLo, boolean hiPriority) {
		this.cHi = cHi;
		this.cLo = cLo;
		this.period = p;
		this.hiPriority = hiPriority;
	}
	
	public double getULo() {
		return cLo/period;
	}
	
	public double getUHi() {
		return cHi/period;
	}
	// fonction analytique de sbf et dbf
	public double func(int t) {
		Double image = function.get(t);
		if ( image !=null) return image;
		if(hiPriority) {
			double temp = t-(period - cHi);
			temp = Math.floor(temp/period)*cHi;
			temp+= Math.max(t-2*(period-cHi)-period*Math.floor((t-(period - cHi))/period), 0);
			function.put(t, temp);
			return(Math.max(temp,0));
		}
		else {
			double temp = Math.floor(t/period)*cLo;
			function.put(t,temp);
			return temp;
		}
	}
	
	
}
