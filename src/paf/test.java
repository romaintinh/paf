package paf;


import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;


public class test {
	public static void main (String arg[]) 
	{
		File folder = new File(arg[0]); 
		File[] subFolder = folder.listFiles();
		for (File f : subFolder) {
			if (f.isDirectory()) { // seul les dossiers contiennent les fichier contenant des lots de tâches
				File[] expFile = f.listFiles();
				for (File sf : expFile) { // pour tout lots de tâche
					if(sf.getName().contains("modal")) continue; // le fichier contenant "modal" n'est pas un lot de tâche
					SolutionExacte S = new SolutionExacte();
					S.loadFromTxt(sf.getAbsolutePath());
					S.WriteResult(sf.getName(),arg[0], S.resolution());
				}
			}
		}
	}
}
