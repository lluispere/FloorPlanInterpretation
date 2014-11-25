package prog.matlab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import prog.exceptions.GraphCreationException;
import prog.exceptions.RelationException;
import prog.io.SVGfile;
import prog.statistics.Statistics;
import prog.strelement.FloorPlan;

public class ReadStatistics {

	private String gtDirectory; // the directory of the SVG GT files
	private String forceDirectory; // the directory of the java watershed files
	
	public ReadStatistics(String gtDirectory, String forceDirectory)
	{
		this.gtDirectory = gtDirectory;
		this.forceDirectory = forceDirectory;
	}
	
	public Statistics readFPStatistics() throws IOException, ClassNotFoundException
	{
		List<FloorPlan> fpList = new ArrayList<FloorPlan>();
		
		// list the files in the folder of the GT
		File folderGt = new File(gtDirectory);
		File folderForce = new File(forceDirectory);
		
		// for each file read the SVG
		for (File file : folderGt.listFiles())
		{
			// create the SVGfile object which parses the ground truth
			SVGfile svgFile = null;
			// create the FloorPlan object from the GT
			FloorPlan fp = null;
			
			try {
				svgFile = new SVGfile(file.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				fp = svgFile.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fp.createGraphsGT();
			} catch (GraphCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RelationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			fpList.add(fp);

		}
		
		Statistics statistics = new Statistics(fpList,folderForce.listFiles());
		return statistics;
	}
	
	
}
