package at.blackariesstudio.extras;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import at.blackariesstudio.manager.ResourcesManager;
import at.blackariesstudio.manager.SceneManager;
import at.blackariesstudio.preferences.Preferences;

public class LevelCompleteWindow extends Sprite implements IOnMenuItemClickListener
{
    private TiledSprite head1;
    private TiledSprite head2;
    private TiledSprite head3;
    
    private MenuScene menuChildScene;
    private IMenuItem okMenuItem;
    private IMenuItem notOkMenuItem;
    
    private Scene mScene;
    private ZoomCamera mCamera;
    private VertexBufferObjectManager mVbom;
    
	private final int MENU_OK = 0;
	private final int MENU_NOT_OK = 1;
    
    public enum LoergEndCount
    {
        ONE,
        TWO,
        THREE
    }
    
    public LevelCompleteWindow(Scene scene, ZoomCamera camera, VertexBufferObjectManager vbom)
    {
        super(0, 0, 650, 400, ResourcesManager.getInstance().complete_window_region, vbom);
    	this.mScene = scene;
    	this.mCamera = camera;
    	this.mVbom = vbom;
        attachStars();
    }
    
    private void attachStars()
    {
        head1 = new TiledSprite(150, 150, ResourcesManager.getInstance().complete_stars_region, mVbom);
        head2 = new TiledSprite(325, 150, ResourcesManager.getInstance().complete_stars_region, mVbom);
        head3 = new TiledSprite(500, 150, ResourcesManager.getInstance().complete_stars_region, mVbom);
        
        attachChild(head1);
        attachChild(head2);
        attachChild(head3);
    }
    
    public void init(Scene scene, ZoomCamera camera, VertexBufferObjectManager vbom)
    {
    	this.mScene = scene;
    	this.mCamera = camera;
    	this.mVbom = vbom;
    }
    
    public void display(LoergEndCount count)
    {
        // Change stars tile index, based on stars count (1-3)
        switch (count)
        {
            case ONE:
            	head1.setCurrentTileIndex(0);
            	head2.setCurrentTileIndex(1);
            	head3.setCurrentTileIndex(1);
                break;
            case TWO:
            	head1.setCurrentTileIndex(0);
            	head2.setCurrentTileIndex(0);
            	head3.setCurrentTileIndex(1);
                break;
            case THREE:
            	head1.setCurrentTileIndex(0);
            	head2.setCurrentTileIndex(0);
            	head3.setCurrentTileIndex(0);
                break;
        }
        
        // Hide HUD
        mCamera.getHUD().setVisible(false);
        
        // Disable camera chase entity
        mCamera.setChaseEntity(null);
        
        // Attach our level complete panel in the middle of camera
        setPosition(mCamera.getCenterX(), mCamera.getCenterY());
        mScene.attachChild(this);
    }
    
    public void hideAndChoose()
    {
    	setVisible(false);
    	Text weiterText = new Text(0,0, ResourcesManager.getInstance().game_font, "Nächstes Level?", mVbom);
    	weiterText.setPosition(mCamera.getCenterX(), mCamera.getCenterY()+80);
    	
    	menuChildScene = new MenuScene(mCamera);
    	menuChildScene.setPosition(mCamera.getCenterX(), mCamera.getCenterY()-20);
    	
    	okMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_OK, ResourcesManager.getInstance().ok_button_region, mVbom), 1f, 1.2f);
		notOkMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_NOT_OK, ResourcesManager.getInstance().no_button_region, mVbom), 1f, 1.2f);
    	
		menuChildScene.setOnMenuItemClickListener(this);
		
    	mScene.attachChild(weiterText);
    	mScene.attachChild(menuChildScene);
    }
    
    @Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
	float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()){
			hideAndChoose();
		}
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
    
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		
		switch(pMenuItem.getID())
        {
        case MENU_OK:
        	//Game Scene mit korrekten Level laden
            SceneManager.getInstance().loadGameScene(ResourcesManager.getInstance().engine, Preferences.getInstance().getUnlockedLevelsCount());
        	return true;
        case MENU_NOT_OK:
        	// Level Selector laden

            return true;
        default:
            return false;
        }
	}
}