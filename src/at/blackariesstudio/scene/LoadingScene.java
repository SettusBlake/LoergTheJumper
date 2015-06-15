package at.blackariesstudio.scene;

import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

import at.blackariesstudio.base.BaseScene;
import at.blackariesstudio.manager.ResourcesManager;
import at.blackariesstudio.manager.SceneManager;
import at.blackariesstudio.manager.SceneManager.SceneType;

public class LoadingScene extends BaseScene{

	@Override
	public void createScene() {
		//setBackground(new Background(Color.GREEN));
		
		int width = (int) ResourcesManager.getInstance().camera.getWidth();
		int height = (int) ResourcesManager.getInstance().camera.getHeight();
		
		Sprite backgroundSprite = new Sprite(width/2, height/2, resourcesManager.menu_background_region, vbom);

		final float red = 0;
		final float green = 0;
		final float blue = 0;
		
		SpriteBackground background = new SpriteBackground(red, green, blue, backgroundSprite);

		setBackground(background);
		setBackgroundEnabled(true);
		attachChild(new Text(240, 50, resourcesManager.level_font, "Loading....", vbom));
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
