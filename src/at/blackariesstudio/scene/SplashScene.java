package at.blackariesstudio.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import at.blackariesstudio.base.BaseScene;
import at.blackariesstudio.manager.ResourcesManager;
import at.blackariesstudio.manager.SceneManager.SceneType;

public class SplashScene extends BaseScene{
	
	private Sprite splash;

	@Override
	public void createScene() {
		splash = new Sprite(0,0, resourcesManager.splash_region, vbom)
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither(); // verbessert die Qualit�t bie manchen Bildern
			}
		};
		
		splash.setScale(1.5f);
		splash.setPosition((ResourcesManager.getInstance().camera.getWidth()/2) , ResourcesManager.getInstance().camera.getHeight()/2);
		attachChild(splash); // hinzuf�gen zur Scene
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	// Splash und Szene dispose (dispose = beseitigen)
	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		this.detachSelf();
		this.dispose();		
	}

}
