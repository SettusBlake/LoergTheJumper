package at.blackariesstudios.scene;

import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

import at.blackariesstudios.base.BaseScene;
import at.blackariesstudios.manager.ResourcesManager;
import at.blackariesstudios.manager.SceneManager;
import at.blackariesstudios.manager.SceneManager.SceneType;

public class LoadingScene extends BaseScene{

	@Override
	public void createScene() {
		
		int width = (int) ResourcesManager.getInstance().camera.getWidth();
		int height = (int) ResourcesManager.getInstance().camera.getHeight();
		
		Sprite backgroundSprite = new Sprite(width/2, height/2, resourcesManager.loading_background_region, vbom);

		final float red = 0;
		final float green = 0;
		final float blue = 0;
		
		SpriteBackground background = new SpriteBackground(red, green, blue, backgroundSprite);

		setBackground(background);
		setBackgroundEnabled(true);
		attachChild(new Text(width/2, 40, resourcesManager.loading_font, "Loading....", vbom));
	}

	@Override
	public void onBackKeyPressed() {
		return;		// keine Aktion während Loading Screen läuft
	}

	@Override
	public SceneType getSceneType() {
		return SceneManager.SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {
				
	}

}
