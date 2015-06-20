package at.blackariesstudios.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import at.blackariesstudios.base.BaseScene;
import at.blackariesstudios.scene.GameScene;
import at.blackariesstudios.scene.LoadingScene;
import at.blackariesstudios.scene.MainMenuScene;
import at.blackariesstudios.scene.SplashScene;
import at.blackariesstudios.scene.YesNoMenuScene;

public class SceneManager
{
    //---------------------------------------------
    // SCENES
    //---------------------------------------------
    
    private BaseScene splashScene;
    private BaseScene menuScene;
    private BaseScene gameScene;
    private BaseScene loadingScene;
    private BaseScene yesNoScene;
    
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------
    
    private static final SceneManager INSTANCE = new SceneManager();
    
    private SceneType currentSceneType = SceneType.SCENE_SPLASH;
    
    private BaseScene currentScene;
    
    private Engine engine = ResourcesManager.getInstance().engine;
    
    public enum SceneType
    {
        SCENE_SPLASH,
        SCENE_MENU,
        SCENE_GAME,
        SCENE_LOADING,
        SCENE_YESNO,
    }
    
    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------
    
    // Setzt die richtige Szene in der Engine. Engine ist ResourcenManager Engine
    public void setScene(BaseScene scene)
    {
    	//Engine hat eine eigene Funktion die das Szenen setzen leicht macht.
        engine.setScene(scene);
        currentScene = scene;
        currentSceneType = scene.getSceneType();
    }
    
    // Ruft obere setScene Methode auf und setzt die Szene aufgrund des SceneTypes
    public void setScene(SceneType sceneType)
    {
        switch (sceneType)
        {
            case SCENE_MENU:
                setScene(menuScene);
                break;
            case SCENE_GAME:
                setScene(gameScene);
                break;
            case SCENE_SPLASH:
                setScene(splashScene);
                break;
            case SCENE_LOADING:
                setScene(loadingScene);
                break;
            case SCENE_YESNO:
                setScene(yesNoScene);
                break;
            default:
                break;
        }
    }
    
    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------
    
    public static SceneManager getInstance()
    {
        return INSTANCE;
    }
    
    public SceneType getCurrentSceneType()
    {
        return currentSceneType;
    }
    
    public BaseScene getCurrentScene()
    {
        return currentScene;
    }
    
    public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback)
    {
    	ResourcesManager.getInstance().loadSplashScreen();
    	splashScene = new SplashScene();
    	currentScene = splashScene;
    	pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }
    
    private void createLoadingScene()
    {
    	ResourcesManager.getInstance().loadLoadingResources();
    	loadingScene = new LoadingScene();
    }
    
    public void loadGameScene(final Engine mEngine, final int level)
    {
    	if (this.loadingScene == null)
    	{
    		createLoadingScene();
    	}
                
        if (this.currentScene.equals(this.menuScene))
        {
            ResourcesManager.getInstance().unloadMenuTextures();
            menuScene.dispose();
        }
        
        setScene(loadingScene);

        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
        {
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadGameResources();
                gameScene = new GameScene();
                ((GameScene) gameScene).loadLevel(level);
                setScene(gameScene);
            }
        }));
    }
    
    public void disposeSplashScene()
    {
    	ResourcesManager.getInstance().unloadSplashScreen();
    	splashScene.dispose();
    	splashScene = null;
    }
    
    public void _createMenuScene()
    {
    	ResourcesManager.getInstance().loadMenuResources();
    	ResourcesManager.getInstance().loadLoadingResources();
    	menuScene = new MainMenuScene();
    	loadingScene = new LoadingScene();
    	
    	setScene(menuScene);
    	if (this.splashScene != null )
    	{
    		disposeSplashScene();
    	}
    }
    
    public void loadMenuScene(final Engine mEngine)
    {
    	// Muss gemacht werden, da nachdem eine GameScene aktiv war die Kamera Bounds nicht mehr passen und der Level-Editor schon aufgerufen wurde, bevor die game-scene disposed wird
    	ResourcesManager.getInstance().resetCamera(); 
    	
    	ResourcesManager.getInstance().loadMenuResources();
   		menuScene = new MainMenuScene();

    	if (this.loadingScene == null)
    	{
    		createLoadingScene();
    	}
 	
    	if (this.splashScene != null )
    	{
    		disposeSplashScene();
    		setScene(menuScene);
    	}

        setScene(loadingScene);
        
		// Wird gemacht, sobald die alles davor erledigt wurde
		mEngine.registerUpdateHandler(new TimerHandler(0.1f,
				new ITimerCallback() {
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
				    	// ToDo: Performance noch nicht perfekt
				        if (gameScene != null)
				        {
				        	ResourcesManager.getInstance().unloadGameTextures();
				        	gameScene.dispose();
				        }
						setScene(menuScene);
					}
				}));
    }

	public void loadYesNoMenuScene(final Engine mEngine) 
	{
		ResourcesManager.getInstance().loadYesNoMenuResources();
		yesNoScene = new YesNoMenuScene();
		
		// Wird gemacht, sobald die alles davor erledigt wurde
		mEngine.registerUpdateHandler(new TimerHandler(0.3f,
				new ITimerCallback() {
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						if (gameScene != null) 
						{
							ResourcesManager.getInstance().unloadGameTextures();
							gameScene.disposeScene();
						}
						setScene(yesNoScene);
					}
				}));
	}
}