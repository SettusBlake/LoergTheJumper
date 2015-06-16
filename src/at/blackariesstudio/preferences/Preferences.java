package at.blackariesstudio.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	private static Preferences INSTANCE;
	
	// FileName
	private static final String PREFS_NAME = "GAME_USERDATA";
	
	// Dies sind die Eigenschaften die gespeichert werden sollen
	private static final String UNLOCKED_LEVEL_KEY = "unlockedLevels"; // Levelfortschritt
	private static final String HIGH_SCORE_KEY = "highScore"; // höchster Highscore
	
	// SharedPreferences Objekt und der Editior zum bearbeiten (speichern/laden)
	private SharedPreferences mSettings;
	private SharedPreferences.Editor mEditor;
	
	// Die Datentypen für die Keys
	private int mUnlockedLevels;
	private int mHighScore;
	
	Preferences()
	{
		// nichts zu tun
	}
	
	// liefert die aktuelle instanz
	public synchronized static Preferences getInstance() {
		if(INSTANCE == null){
			INSTANCE = new Preferences();
		}
		return INSTANCE;
	}
	
	public synchronized void init(Context pContext) {
		if (mSettings == null) {

			mSettings = pContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			// Initialisierung des Editors, damit auf die Einstellungen zugegriffen werden kann
			mEditor = mSettings.edit();

			// Aktuelles Level. Wenn die Eigenschaft noch nicht existiert wird 1 genommen
			mUnlockedLevels = mSettings.getInt(UNLOCKED_LEVEL_KEY, 1);
			
			// Highscore: init = 0
			mHighScore = mSettings.getInt(HIGH_SCORE_KEY, 0);
		}
	}
	
	public synchronized int getUnlockedLevelsCount()
	{
		return mUnlockedLevels;
	}
	
	public synchronized int getHighScore()
	{
		return mHighScore;
	}
	
	public synchronized void unlockNextLevel()
	{
		mUnlockedLevels++;
		mEditor.putInt(UNLOCKED_LEVEL_KEY, mUnlockedLevels);
		mEditor.commit();		
	}
	
	public synchronized void setHighScore(int newHighscore)
	{
		mHighScore = newHighscore;
		mEditor.putInt(HIGH_SCORE_KEY, mHighScore);
		mEditor.commit();
	}
	
	public synchronized void resetLevel()
	{
		mEditor.putInt(UNLOCKED_LEVEL_KEY, 3);
		mEditor.commit();
	}
}
