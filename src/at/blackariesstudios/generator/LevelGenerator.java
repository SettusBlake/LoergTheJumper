package at.blackariesstudios.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
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
	private final static int PlATFORM_Y_MAX_DISTANCE_NORMAL = 92;
	private final static int PlATFORM_Y_MAX_DISTANCE_UP = 90;
	private final static int PlATFORM_Y_MAX_DISTANCE_DOWN = 90;
	private final static int COIN_DISTANCE = 70;
	private final static int GOAL_DISTANCE = 50;
	
	private final static int LEVEL_HEIGHT = 1000;
	private final static int MAX_LEVEL_WIDTH = 8000;
	private final static int MIN_LEVEL_HEIGHT = PLATFORM_HEIGHT;
	
	private String filepath = "/atblackariesstudios/randomlevel/r";
	
	File newxmlFile;
	XmlSerializer serializer;
	int levelLength;
	
	public void generate()
	{
		levelLength = 0;
		try {
			
			int lvl = Preferences.getInstance().getRandomLevelCount();
			levelLength = randInt((int) (MAX_LEVEL_WIDTH*0.5), MAX_LEVEL_WIDTH);
			
			newxmlFile= new File(Environment.getExternalStorageDirectory()+ filepath + String.valueOf(1) + ".xml");   
		    newxmlFile.getParentFile().mkdirs();
		    
			FileOutputStream fos = new FileOutputStream(newxmlFile);

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
		int akt_y = randInt(100, START_POS_MAX_Y);
				
		// Den Typ der zwei vorigen Plattformen
		int plat_prev_type = 1;
		int plat_prev_prev_type = 1;
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

			while (akt_x < (levelLength-(PLATFORM_WIDTH*3)))
			 {
				serializer.startTag("", "entity");
				
				direction_y = randInt(1, 3);
				// damit es wenn es ganz unten ist, nicht noch weiter nach unten geht
				if ((akt_y <= 100) && (direction_y == 2))
				{
					direction_y = 1;
				}
				
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
				
				// Position in der Y Achse
				switch (direction_y) {
				case 1: // up
					akt_y += randInt(0, PlATFORM_Y_MAX_DISTANCE_UP);
					akt_x += randInt(PLATFORM_WIDTH, PLATFORM_X_MAX_DISTANCE_UP);
					break;
				case 2: // down
					akt_y -= randInt(0, PlATFORM_Y_MAX_DISTANCE_DOWN);
					akt_x += randInt(PLATFORM_WIDTH, PLATFORM_X_MAX_DISTANCE_DOWN);
					break;
				case 3: 
					akt_x += randInt(PLATFORM_WIDTH, PLATFORM_X_MAX_DISTANCE_NORMAL);
					break;
				default:
					break;
				}
				serializer.attribute("", "x", String.valueOf(akt_x));
				serializer.attribute("", "y", String.valueOf(akt_y));

				// Plattformen 
				plat_akt = randInt(1,3);
				switch (plat_akt) {
				case 1: // normale
					serializer.attribute("", "type", "platform1");
					plat_prev_prev_type = plat_prev_type;
					plat_prev_type = plat_akt;
					break;
				case 2: // böse
					if ((plat_prev_type == 1) && (plat_prev_prev_type != 2)) 
					{
						serializer.attribute("", "type", "platform2");
						plat_prev_prev_type = plat_prev_type;
						plat_prev_type = plat_akt;
					}
					else
					{
						serializer.attribute("", "type", "platform1");
						plat_prev_prev_type = plat_prev_type;
						plat_prev_type = 1;
					}
					
					break;
				case 3: // gelbe
					if ((plat_prev_type == 1) || (plat_prev_type != 2))
					{
						serializer.attribute("", "type", "platform3");
						plat_prev_prev_type = plat_prev_type;
						plat_prev_type = plat_akt;
					}
					else
					{
						serializer.attribute("", "type", "platform1");
						plat_prev_prev_type = plat_prev_type;
						plat_prev_type = 1;
					}
					break;
				default:
					break;
				}
				
				serializer.endTag("", "entity");
				
				// Münze oder nicht Münze, dass ist hier die Frage - 1/3 Chance
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
