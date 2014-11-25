package prog.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import prog.statistics.Statistics;
import prog.strelement.FloorPlan;

public class FloorPlanIO {
	
	public FloorPlanIO()
	{
		
	}
	
	public void storeFloorPlan(FloorPlan fp, String fileName) throws IOException
	{
		FileOutputStream f_out = new FileOutputStream(fileName);
		ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
		obj_out.writeObject(fp);
	}
	
	public void storeStatistics(Statistics stat, String fileName) throws IOException
	{
		FileOutputStream f_out = new FileOutputStream(fileName);
		ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
		obj_out.writeObject(stat);
	}
	
	public FloorPlan loadFloorPlan(String fileName) throws IOException, ClassNotFoundException
	{
		FileInputStream f_in = new FileInputStream(fileName);
		ObjectInputStream obj_in = new ObjectInputStream(f_in);
		Object obj = obj_in.readObject();
		if (obj instanceof FloorPlan)
			return (FloorPlan) obj;
		else return null;
	}
	
	public Statistics loadStatistics(String fileName) throws IOException, ClassNotFoundException
	{
		FileInputStream f_in = new FileInputStream(fileName);
		ObjectInputStream obj_in = new ObjectInputStream(f_in);
		Object obj = obj_in.readObject();
		if (obj instanceof Statistics)
			return (Statistics) obj;
		else return null;
	}

}
