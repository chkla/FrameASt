package edu.uma.nlp.graphseg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class IOHandler {
	public static void writeSegmentation(List<String> rawLines, List<List<Integer>> segmentation, String path)
	{
		try {
			File fout = new File(path);
			FileOutputStream fos;
			fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			for(int i = 0; i < segmentation.size(); i++)
			{
				for(int j = 0; j < segmentation.get(i).size(); j++)
				{
					bw.write(rawLines.get(segmentation.get(i).get(j)) + "\n");
				}
				bw.write("==========\n");
			}
			bw.close();
		}
        catch (Exception e) {
		// TODO Auto-generated catch block
        	e.printStackTrace();
        }
	}
}
