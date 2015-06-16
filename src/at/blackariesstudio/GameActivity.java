package at.blackariesstudio;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.view.KeyEvent;
import at.blackariesstudio.manager.ResourcesManager;
import at.blackariesstudio.manager.SceneManager;
import at.blackariesstudio.preferences.Preferences;

public class GameActivity extends BaseGameActivity{
	
	private ZoomCamera camera;
	private ResourcesManager resourcesManager;
	private Preferences preferences;
	
	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;

	// Engine Optionen definieren. Wie verhält sich das Gerät während das Programm läuft
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	    EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.camera);
	    engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
	    engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON); // Bildschirm wird daudurch nicht ausgeschaltet
	    return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager()); // prepareManager aufrufen und alles nötige übergeben
		resourcesManager = ResourcesManager.getInstance();
		resourcesManager.setCameraHeight(GameActivity.CAMERA_HEIGHT);
		resourcesManager.setCameraWidth(GameActivity.CAMERA_WIDTH);
		
		preferences = Preferences.getInstance();
		preferences.init(this.getApplicationContext());

		pOnCreateResourcesCallback.onCreateResourcesFinished(); // Wird am Ende aufgerufen
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {
		
//		What it does:
//		It will display the splash screen until different tasks
//		have been executed (Loading the menu resources, 
//		the menu scene and setting the scene to menu scene.) 
		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);		
				SceneManager.getInstance().loadMenuScene(mEngine);
			}
		}));
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	// Eine überschriebene Methode die extra eingefügt wird um LimitedFPSEngine zurück zu liefern
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) 
	{
	    return new LimitedFPSEngine(pEngineOptions, 60);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		System.exit(0);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
	    }
	    return false; 
	}
}
