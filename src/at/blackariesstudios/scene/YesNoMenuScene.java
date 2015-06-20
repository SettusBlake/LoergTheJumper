package at.blackariesstudios.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.util.GLState;

import at.blackariesstudios.base.BaseScene;
import at.blackariesstudios.generator.LevelGenerator;
import at.blackariesstudios.manager.ResourcesManager;
import at.blackariesstudios.manager.SceneManager;
import at.blackariesstudios.manager.SceneManager.SceneType;
import at.blackariesstudios.preferences.Preferences;
import at.blackariesstudios.preferences.Preferences.LEVELTYPE;

public class YesNoMenuScene extends BaseScene implements IOnMenuItemClickListener {

    private MenuScene menuChildScene;
    private IMenuItem okMenuItem;
    private IMenuItem notOkMenuItem;
    
	private final int MENU_OK = 0;
	private final int MENU_NOT_OK = 1;
	
	@Override
	public void createScene() {
		createBackground();
		createMenu();
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);		
	}

	@Override
	public SceneType getSceneType() {
		// TODO Auto-generated method stub
		return SceneType.SCENE_YESNO;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
	
	private void createBackground()
	{
		int width = (int) ResourcesManager.getInstance().camera.getWidth();
		int height = (int) ResourcesManager.getInstance().camera.getHeight();
		
		resourcesManager.resetCamera();
		
		// What it does: Creates a new sprite in the middle of the screen, for our background, 
		//using background texture region, we also enabled dithering to improve gradient quality.
		attachChild(new Sprite(width/2, height/2, resourcesManager.yesno_background_region, vbom)
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});
	}
	
	private void createMenu()
	{
    	Text weiterText = new Text(0,0, resourcesManager.game_font, "Nächstes Level?", vbom);
    	weiterText.setPosition(camera.getCenterX(), camera.getCenterY()+80);
    	attachChild(weiterText);
    	
    	menuChildScene = new MenuScene(camera);
    	menuChildScene.setPosition(0,0);
    	
    	okMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_OK, resourcesManager.ok_button_region, vbom), 0.7f, 1f);
		notOkMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_NOT_OK, resourcesManager.no_button_region, vbom), 0.6f, 0.9f);
		
		menuChildScene.addMenuItem(okMenuItem);
		menuChildScene.addMenuItem(notOkMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		okMenuItem.setPosition(camera.getCenterX()-100, camera.getCenterY()-50);
		notOkMenuItem.setPosition(camera.getCenterX()+100, camera.getCenterY()-50);
						
		menuChildScene.setOnMenuItemClickListener(this);
    	setChildScene(menuChildScene);
    	menuChildScene.setVisible(true);
	}
	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		
		switch(pMenuItem.getID())
        {
        case MENU_OK:
        	//Game Scene mit korrekten Level laden
        	if (Preferences.getInstance().getLevelType() == LEVELTYPE.NORMAL)
        	{
        		SceneManager.getInstance().loadGameScene(engine, Preferences.getInstance().getCurrentLevel()+1, LEVELTYPE.NORMAL);
        	}
        	else
        	{
        		LevelGenerator lvlg = new LevelGenerator();
        		lvlg.generate();
        		SceneManager.getInstance().loadGameScene(engine, 0, LEVELTYPE.RANDOM);
        	}
        	return true;
        case MENU_NOT_OK:
        	Preferences.getInstance().setLastScore(0);
        	Preferences.getInstance().setCurrentLevel(1);
        	SceneManager.getInstance().loadMenuScene(engine);
            return true;
        default:
            return false;
        }
	}

}
