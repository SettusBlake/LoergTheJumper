package at.blackariesstudio.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import at.blackariesstudio.base.BaseScene;
import at.blackariesstudio.scene.GameScene;
import at.blackariesstudio.scene.LoadingScene;
import at.blackariesstudio.scene.MainMenuScene;
import at.blackariesstudio.scene.SplashScene;

public class SceneManager
{
    //---------------------------------------------
    // SCENES
    //---------------------------------------------
    
    private BaseScene splashScene;
    private BaseScene menuScene;
    private BaseScene gameScene;
    private BaseScene loadingScene;
    
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
    
    public void loadGameScene(final Engine mEngine, final int level)
    {
        setScene(loadingScene);
        ResourcesManager.getInstance().unloadMenuTextures();
        this.menuScene.dispose();
        
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
    
    public void createMenuScene()
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
        setScene(loadingScene);
        if (this.gameScene != null)
        {
        	ResourcesManager.getInstance().unloadGameTextures();
        }
        
        // Wird gemacht, sobald die alles davor erledigt wurde
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
        {
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                setScene(menuScene);
            }
        }));
    }
}