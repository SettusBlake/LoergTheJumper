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
import at.blackariesstudios.generator.LevelGenerator;
import at.blackariesstudios.manager.ResourcesManager;
import at.blackariesstudios.manager.SceneManager;
import at.blackariesstudios.manager.SceneManager.SceneType;
import at.blackariesstudios.preferences.Preferences;
import at.blackariesstudios.preferences.Preferences.LEVELTYPE;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{
	
	private MenuScene menuChildScene;
	private IMenuItem playMenuItem;
	private IMenuItem levelSelectorMenuItem;
	private IMenuItem randomLevelMenuItem;
	
	private final int MENU_PLAY = 0;
	private final int MENU_LEVEL = 1;
	private final int MENU_RANDOM_LEVEL = 2;
	
	
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
		
		playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 0.9f, 1.1f);
		levelSelectorMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_LEVEL, resourcesManager.levelSelector_region, vbom), 0.9f, 1.1f);
		randomLevelMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_RANDOM_LEVEL, resourcesManager.randomLevel_region, vbom), 0.9f, 1.1f);
		
		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(levelSelectorMenuItem);
		menuChildScene.addMenuItem(randomLevelMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		playMenuItem.setPosition(width/2, (height/2)+playMenuItem.getHeight());
		levelSelectorMenuItem.setPosition(width/2, (height/2)-levelSelectorMenuItem.getHeight()/2);
		randomLevelMenuItem.setPosition(width/2, (height/2)-randomLevelMenuItem.getHeight()-60);
		
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
            SceneManager.getInstance().loadGameScene(engine, 1, LEVELTYPE.NORMAL);
        	return true;
        case MENU_LEVEL:
        	// Level Selector laden
        	resourcesManager.levelSelector.createTiles(resourcesManager.level_selector_tile_region, resourcesManager.level_font);
        	resourcesManager.levelSelector.show();
            return true;
        case MENU_RANDOM_LEVEL:
        	// Random Level laden
        	Preferences.getInstance().setCurrentLevel(1);
    		LevelGenerator lvlg = new LevelGenerator();
    		lvlg.generate();
    		SceneManager.getInstance().loadGameScene(engine, 0, LEVELTYPE.RANDOM);
        	return true;
        default:
            return false;
        }
	}
}
