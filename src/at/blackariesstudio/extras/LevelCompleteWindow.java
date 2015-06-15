package at.blackariesstudio.extras;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import at.blackariesstudio.manager.ResourcesManager;

public class LevelCompleteWindow extends Sprite
{
    private TiledSprite head1;
    private TiledSprite head2;
    private TiledSprite head3;
    
    public enum LoergEndCount
    {
        ONE,
        TWO,
        THREE
    }
    
    public LevelCompleteWindow(VertexBufferObjectManager pSpriteVertexBufferObject)
    {
        super(0, 0, 650, 400, ResourcesManager.getInstance().complete_window_region, pSpriteVertexBufferObject);
        attachStars(pSpriteVertexBufferObject);
    }
    
    private void attachStars(VertexBufferObjectManager pSpriteVertexBufferObject)
    {
        head1 = new TiledSprite(150, 150, ResourcesManager.getInstance().complete_stars_region, pSpriteVertexBufferObject);
        head2 = new TiledSprite(325, 150, ResourcesManager.getInstance().complete_stars_region, pSpriteVertexBufferObject);
        head3 = new TiledSprite(500, 150, ResourcesManager.getInstance().complete_stars_region, pSpriteVertexBufferObject);
        
        attachChild(head1);
        attachChild(head2);
        attachChild(head3);
    }
    
    public void display(LoergEndCount count, Scene scene, Camera camera)
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
        camera.getHUD().setVisible(false);
        
        // Disable camera chase entity
        camera.setChaseEntity(null);
        
        // Attach our level complete panel in the middle of camera
        setPosition(camera.getCenterX(), camera.getCenterY());
        scene.attachChild(this);
    }
}