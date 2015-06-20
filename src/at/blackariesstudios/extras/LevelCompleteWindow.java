package at.blackariesstudios.extras;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import at.blackariesstudios.manager.ResourcesManager;
import at.blackariesstudios.manager.SceneManager;
import at.blackariesstudios.preferences.Preferences;

public class LevelCompleteWindow extends Sprite
{
    private TiledSprite head1;
    private TiledSprite head2;
    private TiledSprite head3;
 
    private Scene mScene;
    private ZoomCamera mCamera;
    private VertexBufferObjectManager mVbom;
    
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
    	attachHeads();
    }
    
    private void attachHeads()
    {
        head1 = new TiledSprite(150, 150, ResourcesManager.getInstance().complete_loergs_region, mVbom);
        head2 = new TiledSprite(325, 150, ResourcesManager.getInstance().complete_loergs_region, mVbom);
        head3 = new TiledSprite(500, 150, ResourcesManager.getInstance().complete_loergs_region, mVbom);
        
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
    
    
    @Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
	float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()){
			if (Preferences.getInstance().getCurr_level() < Preferences.getInstance().getMaxLevel())
			{
				setVisible(false);
				SceneManager.getInstance().loadYesNoMenuScene(ResourcesManager.getInstance().engine);
				ResourcesManager.getInstance().resetCamera();
			}
			else
			{
				detachChildren();
		        SceneManager.getInstance().loadMenuScene(ResourcesManager.getInstance().engine);
		        ResourcesManager.getInstance().resetCamera();
			}
		}
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
}