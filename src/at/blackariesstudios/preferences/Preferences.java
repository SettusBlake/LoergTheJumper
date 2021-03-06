package at.blackariesstudios.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

public class Preferences {

	private static Preferences INSTANCE;
	
	// FileName
	private static final String PREFS_NAME = "GAME_USERDATA";
	
	// Dies sind die Eigenschaften die gespeichert werden sollen
	private static final String UNLOCKED_LEVEL_KEY = "unlockedLevels"; // Levelfortschritt
	private static final String HIGH_SCORE_KEY = "highScore"; // h�chster Highscore
	private static final String MAX_LEVEL = "maxLevel";
	private static final String RANDOM_LEVEL_COUNT = "randomLevelCount";
	private static final String MAX_RANDOM_LEVEL = "maxRandomLevel";
	private static final String RANDOM_LEVEL_PATH = "randomLevelPath";
	
	// SharedPreferences Objekt und der Editior zum bearbeiten (speichern/laden)
	private SharedPreferences mSettings;
	private SharedPreferences.Editor mEditor;
	
	// Die Datentypen f�r die Keys
	private int mUnlockedLevels;
	private int mHighScore;
	private int mMaxLevel;
	private int mRandomLevelCount;
	private int mMaxRandomLevel;
	private String mRandomLevelPath;
	
	// Tempor�rer Speicherer w�hrend das Spiel l�uft
	private int mCurrentLevel;
	private LEVELTYPE mLevelType;
	private int mLastScore;
	
	public enum LEVELTYPE
    {
		NORMAL,
		RANDOM,
    }
	
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

			// Max Level
			mEditor.putInt(MAX_LEVEL, 20);
			mMaxLevel = mSettings.getInt(MAX_LEVEL, 20);
			
			// Random level Count
			mRandomLevelCount = mSettings.getInt(RANDOM_LEVEL_COUNT, 0);
			
			// Max Random Level
			mMaxRandomLevel = mSettings.getInt(MAX_RANDOM_LEVEL, 20);
			
			// Random Level Path - es fehlt danach nur noch das Level und .xml
			mRandomLevelPath = mSettings.getString(RANDOM_LEVEL_PATH, Environment.getExternalStorageDirectory()+"/atblackariesstudios/randomlevel/r");
			mEditor.commit();
		}
	}
	
	public int getCurrentLevel() {
		return mCurrentLevel;
	}

	public void setCurrentLevel(int curr_level) {
		this.mCurrentLevel = curr_level;
	}

	public synchronized int getUnlockedLevelsCount()
	{
		return mUnlockedLevels;
	}
	
	public synchronized int getHighScore(int level, LEVELTYPE type)
	{
		if (type == LEVELTYPE.NORMAL)
		{
			int temp = mSettings.getInt(HIGH_SCORE_KEY + String.valueOf(level), 0);
			return temp;
		}
		else 
		{
			int temp = mSettings.getInt(HIGH_SCORE_KEY + "RLE", 0);
			return temp;
		}
	}
	
	public synchronized void unlockNextLevel()
	{
		mUnlockedLevels++;
		mEditor.putInt(UNLOCKED_LEVEL_KEY, mUnlockedLevels);
		mEditor.commit();		
	}
	
	// Type Random Level oder Normales Level
	// Bei Random Level ist der �bergabeParameter des Levels aktuell noch nicht wichtig
	// Sollte sp�ter aber so gehandhabt werden:
	// 0 = EndlessRandomlevel und >1 ist dann mit level abspeichern..bis max mMaxRandomLevel
	public synchronized void saveHighScore(int newHighscore, int level, LEVELTYPE type)
	{		
		mHighScore = newHighscore;
		if (type == LEVELTYPE.NORMAL)
		{
			mEditor.putInt(HIGH_SCORE_KEY + String.valueOf(level), mHighScore);
		}
		else
		{
			//mHighScore = mSettings.getInt(HIGH_SCORE_KEY+"RLE", 0);
			mEditor.putInt(HIGH_SCORE_KEY+"RLE", mHighScore);
		}
		mEditor.commit();
		
	}
	
	public synchronized int getRandomEndlessLevelHighScore()
	{
		return mSettings.getInt(HIGH_SCORE_KEY+"RLE", 0);
	}
	
	public synchronized void resetLevel()
	{
		mEditor.putInt(UNLOCKED_LEVEL_KEY, 1);
		mEditor.commit();
	}
	
	public synchronized int getMaxLevel()
	{
		return mMaxLevel;
	}
	
	public synchronized int getRandomMaxLevel()
	{
		return mMaxRandomLevel;
	}
	
	public synchronized void setMaxLevel(int maxLevel)
	{
		mEditor.putInt(MAX_LEVEL, maxLevel);
		mEditor.commit();
	}
	
	public synchronized void increaseRandomLevelCount()
	{
		mRandomLevelCount++;
		mEditor.putInt(RANDOM_LEVEL_COUNT, mRandomLevelCount);
		mEditor.commit();
	}
	
	public synchronized int getRandomLevelCount()
	{
		return mRandomLevelCount;
	}
	
	public synchronized String getRandomLevelPath()
	{
		return mRandomLevelPath;
	}

	public LEVELTYPE getLevelType() {
		return mLevelType;
	}

	public void setLevelType(LEVELTYPE type) {
		this.mLevelType = type;
	}

	public int getLastScore() {
		return mLastScore;
	}
	
	public void setLastScore(int lastScore) {
			this.mLastScore = lastScore;
	}
}
