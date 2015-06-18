package at.blackariesstudios.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import at.blackariesstudios.base.BaseScene;
import at.blackariesstudios.manager.ResourcesManager;
import at.blackariesstudios.manager.SceneManager.SceneType;

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
				pGLState.enableDither(); // verbessert die Qualität bie manchen Bildern
			}
		};
		
//    	Text companyText = new Text(0,0, resourcesManager.base_font, "Black Aries Studios", vbom);
//    	companyText.setPosition(camera.getCenterX(), camera.getCenterY()-150);
//    	attachChild(companyText);
		
		splash.setScale(1.5f);
		splash.setPosition((ResourcesManager.getInstance().camera.getWidth()/2) , ResourcesManager.getInstance().camera.getHeight()/2);
		attachChild(splash); // hinzufügen zur Scene
		
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
