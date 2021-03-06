package at.blackariesstudios.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import at.blackariesstudios.manager.ResourcesManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public abstract class Player extends AnimatedSprite{
	
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------
	    
	private Body body;
	private boolean canRun = false;
	private int footContacts = 0;
	
	final long[] PLAYER_ANIMATE = new long[] { 200, 200 };

    // ---------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------
    
    public Player(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld)
    {
        super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
        createPhysics(camera, physicsWorld);
        camera.setChaseEntity(this);
    }

    public abstract void onDie();
    
    private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
    {        
        body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));

        body.setUserData("player");
        body.setFixedRotation(true);
                
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                super.onUpdate(pSecondsElapsed);
                camera.onUpdate(0.1f);
                
                if (getY() <= 0)
                {                    
                    onDie();
                }
                
                // Wenn der spieler rennen kann, l�uft er einfach nach vorne
                if (canRun)
                {    
                    body.setLinearVelocity(new Vector2(5, body.getLinearVelocity().y)); 
                }
            }
        });
    }

    public void setRunning()
    {
        canRun = true;
        // original: final long[] PLAYER_ANIMATE = new long[] { 100, 100, 100 };
        
        animate(PLAYER_ANIMATE, 0, 1, true); // 0 und 0  hei�t: vom 0ten zum 0ten tile. ich hab in dem fall nur einen
        body.setActive(true);
    }
    
    public void stopRunning()
    {
        canRun = false;
        stopAnimation();
        body.setLinearVelocity(0, 0);
        //body.setActive(false);
        
    }
    
    public void jump(boolean pause)
    {
    	if (footContacts >= 1 && pause == false)
    	{
    		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 11));
    		return;
    	}
    }
    
    public void increaseFootContacts()
    {
        footContacts++;
    }

    public void decreaseFootContacts()
    {
        footContacts--;
    }
    
    public int getFootContacts()
    {
    	
    	return footContacts;
    }
    
}
