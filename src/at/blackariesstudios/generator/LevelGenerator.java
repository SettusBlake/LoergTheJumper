package at.blackariesstudios.generator;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class LevelGenerator {
	
	private final static int PLATFORM_X = 128;
	private final static int PLATFORM_Y = 31;
	private final static int PlATFORM_X_DISTANCE = 20;
	private final static int PlATFORM_Y_DISTANCE = 20;
	
	private final static int LEVEL_HEIGHT = 800;
	
	private int level_length;
	private String filename = "/assets/level/random_level.xml";
	
	File newxmlFile;
	XmlSerializer serializer;
	
	public void generate()
	{
		try {
			
			newxmlFile= new File(Environment.getExternalStorageDirectory()+"/at.blackariesstudios.randomlevel/random_level.xml");   
		    //newxmlfile.getParentFile().mkdirs();
		    
			FileOutputStream fos = new FileOutputStream(newxmlFile);

			serializer = Xml.newSerializer();
			serializer.setOutput(fos, "UTF-8");
			serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "level");
	        serializer.attribute("", "width", "2222");
			serializer.attribute("", "height", String.valueOf(LEVEL_HEIGHT));
			serializer.startTag("", "entity");
			serializer.attribute("", "x", "400");
			serializer.attribute("", "y", "2000");
			serializer.attribute("", "type", "platform1");
			serializer.endTag("", "entity");
			serializer.endTag("", "level");
			
			serializer.endDocument();
			serializer.flush();

			fos.close();

		} catch (Exception e) {
			Log.e("Exception", "error occurred while creating xml file");
		}

	}
	
	private void generatePlatforms()
	{
		
	}
	
	private void generateCoins()
	{
		
	}
	
	private void setGoal()
	{
		
	}
	
	private void setPlayer()
	{
		
	}
}
