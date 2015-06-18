package at.blackariesstudios.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import at.blackariesstudios.base.BaseScene;
import at.blackariesstudios.manager.ResourcesManager;
import at.blackariesstudios.manager.SceneManager;
import at.blackariesstudios.manager.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{
	
	private MenuScene menuChildScene;
	private IMenuItem playMenuItem;
	private IMenuItem levelSelectorMenuItem;
	
	private final int MENU_PLAY = 0;
	private final int MENU_LEVEL = 1;
	
	
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}

	private void createBackground()
	{
		int width = (int) ResourcesManager.getInstance().camera.getWidth();
		int height = (int) ResourcesManager.getInstance().camera.getHeight();
		
		// What it does: Creates a new sprite in the middle of the screen, for our background, 
		//using background texture region, we also enabled dithering to improve gradient quality.
		attachChild(new Sprite(width/2, height/2, resourcesManager.menu_background_region, vbom)
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});
	}
	
	private void createMenuChildScene()
	{
		int width = (int) ResourcesManager.getInstance().camera.getWidth();
		int height = (int) ResourcesManager.getInstance().camera.getHeight();
		
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);	
		
		playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1f, 1.2f);
		levelSelectorMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_LEVEL, resourcesManager.levelSelector_region, vbom), 1f, 1.2f);
		
		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(levelSelectorMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		playMenuItem.setPosition(width/2, (height/2)+playMenuItem.getHeight()-30);
		levelSelectorMenuItem.setPosition(width/2, (height/2)-levelSelectorMenuItem.getHeight());
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
	}

	public MenuScene getMenuChildScene() {
		return menuChildScene;
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		
		switch(pMenuItem.getID())
        {
        case MENU_PLAY:
        	//Load Game Scene!
            SceneManager.getInstance().loadGameScene(engine, 1);
        	return true;
        case MENU_LEVEL:
        	// Level Selector laden
        	resourcesManager.levelSelector.createTiles(resourcesManager.level_selector_tile_region, resourcesManager.level_font);
        	resourcesManager.levelSelector.show();
            return true;
        default:
            return false;
        }
	}
}
