package prog.io;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import prog.planegraph.Region;

public class MyImage {

	/**** VARIABLE DECLARATION ****/

	// height and weight of the image
	int height, weigth;
	// buffered image to write in a file
	BufferedImage bImage;


	/**** CONSTRUCTORS ****/
	// Constructor 1
	public MyImage(int h, int w)
	{
		this.height = h;
		this.weigth = w;

		bImage = new BufferedImage(weigth, height, BufferedImage.TYPE_BYTE_BINARY);
	}


	/**** CLASS ELEMENT RETRIEVAL FUNCTIONS ****/

	// get the buffered image
	public BufferedImage getBufferedImage()
	{
		return bImage;
	}


	/**** UTILS AND FUNCTIONS ****/

	// Function to draw polygons into the image
	public void drawPolygons(ArrayList<Polygon> polygons)
	{
		// Create a graphics contents on the buffered image
		Graphics2D g2d = bImage.createGraphics();

		for(int i=0; i<polygons.size();i++)
		{
			g2d.fillPolygon(polygons.get(i));
		}
	}

	// Function to draw polygons in negated color into the image
	public void drawPolygons(ArrayList<Polygon> polygons, int negated)
	{
		// Create a graphics contents on the buffered image
		Graphics2D g2d = bImage.createGraphics();

		for(int i=0; i<polygons.size();i++)
		{
			g2d.fillPolygon(polygons.get(i));
		}
		// negates the image for the written polygons
		if(negated == 1)
		{
			for(int x = 0; x<height; x++)
			{
				for(int y = 0; y<weigth; y++)
				{
					if(bImage.getRGB(y,x)==-16777216)
						bImage.setRGB(y,x, -1);
					else bImage.setRGB(y,x, -16777216);
				}
			}
		}
	}


	// This functions writes the buffered image into a specified file
	public void writeImage(File file, String type) throws IOException
	{
		ImageIO.write(bImage, type, file);
	}


	public void drawRegions(ArrayList<Region> regions, File name, String type) throws IOException
	{
		// Create a graphics contents on the buffered image
		Graphics2D g2d = bImage.createGraphics();



		ArrayList<Shape> shapes = new ArrayList<Shape>();

		// for each region in the image
		for(Region r : regions)
		{
			ArrayList<Line2D> linesOfTheRegion = r.getLinesOfTheRegion();
			int[] xPoint = new int[linesOfTheRegion.size()];
			int[] yPoint = new int[linesOfTheRegion.size()];
			for(int i = 0; i<linesOfTheRegion.size(); i++)
			{
				Line2D l = linesOfTheRegion.get(i);
				xPoint[i] = (int) l.getP1().getX();
				yPoint[i] = (int) l.getP1().getY();

				// create a basic stroke
				BasicStroke bs = new BasicStroke(10, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
				// create the path for the stroke, which is actually the line
				Path2D p = new Path2D.Float();
				p.moveTo(l.getP1().getX(), l.getP1().getY());
				p.lineTo(l.getP1().getX(), l.getP1().getY());
				p.lineTo(l.getP2().getX(), l.getP2().getY());
				// create the stroke
				Shape s = bs.createStrokedShape(p);
				shapes.add(s);

			}

			Polygon p = new Polygon(xPoint,yPoint, xPoint.length);

			g2d.fillPolygon(p);
		}
		
		for(int x = 0; x<height; x++)
		{
			for(int y = 0; y<weigth; y++)
			{
				if(bImage.getRGB(y,x)==-16777216)
					bImage.setRGB(y,x, -1);
				else bImage.setRGB(y,x, -16777216);
			}
		}
		
		for(Shape s : shapes)
			g2d.draw(s);
		



		ImageIO.write(bImage, type, name);
	}

}
