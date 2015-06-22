package at.blackariesstudios.generator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;
import at.blackariesstudios.preferences.Preferences;

public class LevelGenerator {
	
	private final static int START_POS_X = 150;
	private final static int START_POS_MAX_Y = 600;
	private final static int PLATFORM_WIDTH = 128;
	private final static int PLATFORM_HEIGHT = 31;
	private final static int PLATFORM_X_MAX_DISTANCE_NORMAL = 256;
	private final static int PLATFORM_X_MAX_DISTANCE_UP = 250;
	private final static int PLATFORM_X_MAX_DISTANCE_DOWN = 300;
	private final static int PLATFORM_Y_MAX_DISTANCE_UP = 90;
	private final static int PLATFORM_Y_MAX_DISTANCE_DOWN = 90;
	private final static int PLATFORM_MIN_DISTANCE = 30;
	
	private final static int COIN_DISTANCE = 70;
	private final static int GOAL_DISTANCE = 50;
	
	private final static int LEVEL_HEIGHT = 1000;
	private final static int MAX_LEVEL_WIDTH = 8000;
	private final static int MIN_LEVEL_HEIGHT = PLATFORM_HEIGHT*3;
	
	File xmlFile;
	XmlSerializer serializer;
	int levelLength;
	int minPlatformDistanceX = 0;
	
	// Plattform Counter für Gelb und Rot
	int counterYellow = 0;
	int counterRed = 0;
	
	public void generate(int levelLenght)
	{
		this.levelLength = levelLenght;
		generate();
	}
	
	public void generate()
	{
		levelLength = 0;
		try {
			
			int lvl = Preferences.getInstance().getRandomLevelCount();
			levelLength = randInt((int) (MAX_LEVEL_WIDTH*0.5), MAX_LEVEL_WIDTH);
			
			xmlFile= new File(Preferences.getInstance().getRandomLevelPath() + String.valueOf(0) + ".xml");   
			xmlFile.getParentFile().mkdirs();
		    
			FileOutputStream fos = new FileOutputStream(xmlFile);

			serializer = Xml.newSerializer();
			serializer.setOutput(fos, "UTF-8");
			serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "level");
	        serializer.attribute("", "width", String.valueOf(levelLength));
			serializer.attribute("", "height", String.valueOf(LEVEL_HEIGHT));

			generateEntities();
			
			serializer.endTag("", "level");
			
			serializer.endDocument();
			serializer.flush();

			fos.close();
			
			Preferences.getInstance().increaseRandomLevelCount();

		} catch (Exception e) {
			Log.e("Exception", "error occurred while creating xml file");
		}
	}
	
	private void generateEntities()
	{
		int akt_x = START_POS_X;
		int akt_y = randInt(MIN_LEVEL_HEIGHT, START_POS_MAX_Y);
				
		// aktuelle Plattform
		int plat_akt;
		
		int direction_y = 0;
		
		try {
			// first platform
			serializer.startTag("", "entity");
			serializer.attribute("", "x", String.valueOf(akt_x));
			serializer.attribute("", "y", String.valueOf(akt_y));
			serializer.attribute("", "type", "platform1");
			serializer.endTag("", "entity");
			serializer.startTag("", "entity");
			serializer.attribute("", "x", String.valueOf(akt_x));
			serializer.attribute("", "y", String.valueOf(akt_y+30));
			serializer.attribute("", "type", "player");
			serializer.endTag("", "entity");

			while ((akt_x+(PLATFORM_X_MAX_DISTANCE_NORMAL*2)) < levelLength)
			 {
				serializer.startTag("", "entity");

				// damit es wenn es ganz unten ist, nicht noch weiter nach unten geht
				if (akt_y <= MIN_LEVEL_HEIGHT*1.5)
				{
					direction_y = 1;
				}
				
				direction_y = randInt(1, 3);
				// Die Chance gerade aus zu laufen, soll etwas geringer sein
				if (direction_y == 3)
				{
					direction_y = randInt(1, 3);
				}
				
				// Damit das ganze nicht oben in der Decke verschwindet
				// wird geprüft, ob schon gewisse Grenzen erreicht wurden und
				// danach soll direction_y nach unten gehen
				if (akt_y >= (LEVEL_HEIGHT-(PLATFORM_WIDTH*2)))
				{
					direction_y = 2;
				}
				
				// Wenn der untereste Minimum Rand erreicht wird, soll es wieder nach oben gehen
				if (akt_y <= MIN_LEVEL_HEIGHT)
				{
					direction_y = 1;
				}
				
				// Position der Plattform
				switch (direction_y) {
				case 1: // up
					akt_y += randInt(PLATFORM_MIN_DISTANCE, PLATFORM_Y_MAX_DISTANCE_UP);
					akt_x += randInt(PLATFORM_WIDTH+PLATFORM_MIN_DISTANCE, PLATFORM_X_MAX_DISTANCE_UP+PLATFORM_MIN_DISTANCE);
					break;
				case 2: // down
					akt_y -= randInt(PLATFORM_MIN_DISTANCE, PLATFORM_Y_MAX_DISTANCE_DOWN);
					akt_x += randInt(PLATFORM_WIDTH+PLATFORM_MIN_DISTANCE, PLATFORM_X_MAX_DISTANCE_DOWN+PLATFORM_MIN_DISTANCE);
					break;
				case 3: // gerade
					akt_x += randInt(PLATFORM_WIDTH, PLATFORM_X_MAX_DISTANCE_NORMAL+PLATFORM_MIN_DISTANCE);
					break;
				default:
					break;
				}
				
				serializer.attribute("", "x", String.valueOf(akt_x));
				serializer.attribute("", "y", String.valueOf(akt_y));
				
				// Plattform Typ bestimmen
				plat_akt = randInt(1,3);
				
				// Ersten und letzten Plattformen sollen Normal sein
				if (((akt_x+(PLATFORM_X_MAX_DISTANCE_NORMAL*2)) > levelLength) || (akt_x < (PLATFORM_X_MAX_DISTANCE_NORMAL*2)))
				{
					plat_akt = 1;
				}
								
				switch (plat_akt) {
				case 1: // normale
					serializer.attribute("", "type", "platform1");
					break;
				case 2: // böse
					if (counterRed == 0) 
					{
						serializer.attribute("", "type", "platform2");
						counterRed++;
					}
					else
					{
						serializer.attribute("", "type", "platform1");
						counterYellow = 0;
						counterRed = 0;
					}
					
					break;
				case 3: // gelbe
					if ((counterYellow <= 1) && (counterRed <= 1))
					{
						serializer.attribute("", "type", "platform3");
						counterYellow++;
					}
					else
					{
						serializer.attribute("", "type", "platform1");
						counterRed = 0;
						counterYellow = 0;
					}
					break;
				default:
					break;
				}
				
				// End Tag für die Platform
				serializer.endTag("", "entity");
				
				// Münze oder nicht Münze, dass ist hier die Frage
				switch(randInt(1, 5))
				{
				case 1:
				case 3:
					serializer.startTag("", "entity");
					serializer.attribute("", "x", String.valueOf(akt_x));
					serializer.attribute("", "y", String.valueOf(akt_y+COIN_DISTANCE));
					serializer.attribute("", "type", "coin");
					serializer.endTag("", "entity");
					break;
				default:
					break;
				}
			};
			
			// letzte Plattform mit dem Ziel
			serializer.startTag("", "entity");
			serializer.attribute("", "x", String.valueOf(akt_x+PLATFORM_X_MAX_DISTANCE_NORMAL));
			serializer.attribute("", "y", String.valueOf(akt_y));
			serializer.attribute("", "type", "platform1");
			serializer.endTag("", "entity");
			
			serializer.startTag("", "entity");
			serializer.attribute("", "x", String.valueOf(akt_x+PLATFORM_X_MAX_DISTANCE_NORMAL));
			serializer.attribute("", "y", String.valueOf(akt_y+GOAL_DISTANCE));
			serializer.attribute("", "type", "goal");
			serializer.endTag("", "entity");

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public InputStream getInputStream()
	{
		InputStream is = null;                 
		try {
			xmlFile = new File(Preferences.getInstance().getRandomLevelPath() + String.valueOf(0) + ".xml");
			xmlFile.getParentFile().mkdirs();
			is = new BufferedInputStream(new FileInputStream(xmlFile));
			return is;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}
	
	private static int randInt(int min, int max) {
	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}
