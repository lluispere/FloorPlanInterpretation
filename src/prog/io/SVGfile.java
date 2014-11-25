package prog.io;

import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import prog.relations.Relation;
import prog.strelement.*;

/**
 * Parser of the SVG Files
 * @author Lluís-Pere de las Heras
 * @date 07/11/13
 */
public class SVGfile {

	public static String header = "<?xml version=" + '"' + "1.0" +
	'"' + "?>\n<!DOCTYPE svg PUBLIC " + '"' + "-//W3C//DTD SVG 1.1//EN" + '"' + " " + '"' +
	"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd" + '"'+ " >\n<svg xmlns=" + '"' +
	"http://www.w3.org/2000/svg" + '"' + " version=" + '"' + "1.1" + '"'+">\n" ;
	private FileReader svgFile;
	private String fileName;

	// default constructor
	public SVGfile(String fileName)
	{
		this.fileName = fileName;
		try {
			svgFile = new FileReader(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File does not exist");
		}
	}

	// get and set
	public void setSvgFile(FileReader svgFile){this.svgFile = svgFile;}
	public FileReader getSvgFile(){return svgFile;}
	public void setFileName(String fileName) {this.fileName = fileName;}
	public String getFileName() {return fileName;}

	/**
	 * Read the file and create an Array of {@link StructuralElement}
	 * @return an Array of {@link StructuralElement}.
	 * @throws IOException 
	 */
	public FloorPlan read() throws IOException
	{
		// variable in.
		BufferedReader svg = new BufferedReader(svgFile);
		String txtLine = "";
		Pattern pat; Matcher mat;
		FloorPlan fp = new FloorPlan(this.fileName);
		Set<StructuralElement> stList = new HashSet<StructuralElement>();
		Set<Relation> rList = new HashSet<Relation>();

		// read each line of the file until EOF
		while((txtLine = svg.readLine()) != null)
		{
			if (txtLine.contains("<width>"))
			{
				// width parser
				pat = Pattern.compile("\\d+");
				mat = pat.matcher(txtLine);
				if (mat.find())
				{
					int value = Integer.parseInt(txtLine.substring(mat.start(),mat.end()));
					fp.setWidth(value);
				}
			}
			else if (txtLine.contains("<height>"))
			{
				// height parser
				pat = Pattern.compile("\\d+");
				mat = pat.matcher(txtLine);
				if (mat.find())
				{
					int value = Integer.parseInt(txtLine.substring(mat.start(),mat.end()));
					fp.setHeight(value);
				}
			}
			else if (txtLine.contains("<polygon"))
			{
				// read the structural element
				StructuralElement el = null;
				Polygon pol = null;
				int id = -1;

				// polygon points parser
				pat = Pattern.compile("points="+'"');
				mat = pat.matcher(txtLine);
				if (mat.find())
				{
					// search for x y coordinates
					String txt = txtLine.substring(mat.end());
					pat = Pattern.compile("(\\d+(\\.\\d+)?)");
					mat = pat.matcher(txt);

					ArrayList<Integer> punts = new ArrayList<Integer>();
					
					// get the coordinates
					while(mat.find())
					{
						punts.add((int)Double.parseDouble(mat.group()));
					}
					int []x1 = new int[punts.size()/2];
					int []y1 = new int[punts.size()/2];
					// convert to array
					int cont = 0;
					for (int i = 0; i<punts.size(); i=i+2)
					{
						x1[cont] = punts.get(i);
						y1[cont] = punts.get(i+1);
						cont++;
					}
					// store the polygon
					pol = new Polygon(x1, y1, x1.length);
				}

				// id parser
				pat = Pattern.compile("id=\"\\d+");
				mat = pat.matcher(txtLine);
				if (mat.find())
				{
					id = Integer.parseInt(txtLine.substring(mat.start()+4,mat.end()));
				}

				// type parser
				if (txtLine.contains("Wall"))
				{
					el = new Wall(id,txtLine,pol);
					stList.add(el);
				}
				else if (txtLine.contains("Door"))
				{
					el = new Door(id,txtLine,pol);
					stList.add(el);
				}
				else if (txtLine.contains("Window"))
				{
					el = new Window(id,txtLine,pol);
					stList.add(el);
				}
				else if (txtLine.contains("Room"))
				{
					el = new Room(id,txtLine,pol);
					stList.add(el);
				}
				else if (txtLine.contains("Separation"))
				{
					el = new Separation(id, txtLine, pol);
					stList.add(el);
				}
				else if (txtLine.contains("Parking"))
				{
					el = new Parking(id, txtLine, pol);
					stList.add(el);
				}
			}
			// read the relation
			else if (txtLine.contains("<relation"))
			{
				Relation r = null;

				// read the Id's of the structural elements involved
				pat = Pattern.compile("\\d+");
				mat = pat.matcher(txtLine);
				List<Integer> idsElements = new ArrayList<Integer>();
				while(mat.find())
				{
					idsElements.add(Integer.parseInt(txtLine.substring(mat.start(),mat.end())));
				}
				// read the type of the relation
				if (txtLine.contains("incident"))
				{
					r = new Relation(txtLine,"incident",idsElements);
					rList.add(r);
				}
				else if (txtLine.contains("neighbour"))
				{
					r = new Relation(txtLine,"neighbour",idsElements);
					rList.add(r);
				}
				else if (txtLine.contains("access"))
				{
					r = new Relation(txtLine,"access",idsElements);
					rList.add(r);
				}
				else if (txtLine.contains("surround"))
				{
					r = new Relation(txtLine,"surround",idsElements);
					rList.add(r);
				}
				else if (txtLine.contains("outerP"))
				{
					r = new Relation(txtLine,"outerP",idsElements);
					rList.add(r);
				}
			}
		}
		// add the stList to the FloorPlan object
		fp.setStructuralElements(stList);
		fp.setRelations(rList);
		// return the Floor Plan object
		return fp;
	}




}
