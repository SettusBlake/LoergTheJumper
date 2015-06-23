package at.blackariesstudios.scene;

import java.io.IOException;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import android.R;
import android.view.KeyEvent;
import at.blackariesstudios.base.BaseScene;
import at.blackariesstudios.extras.LevelCompleteWindow;
import at.blackariesstudios.extras.LevelCompleteWindow.LoergEndCount;
import at.blackariesstudios.generator.LevelGenerator;
import at.blackariesstudios.manager.ResourcesManager;
import at.blackariesstudios.manager.SceneManager;
import at.blackariesstudios.manager.SceneManager.SceneType;
import at.blackariesstudios.object.Player;
import at.blackariesstudios.preferences.Preferences;
import at.blackariesstudios.preferences.Preferences.LEVELTYPE;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameScene extends BaseScene implements IOnSceneTouchListener {
	
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	    
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1 = "platform1";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2 = "platform2";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3 = "platform3";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN = "coin";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_GOAL = "goal";

	private Player player;
	private boolean firstTouch = false;
	
	private HUD gameHUD;
	private Text scoreText;
	private Text levelText;
	private Text highScoreText;
	private int score = 0;
	private int oldRLHighScore = 0;
	private int levelID = 1;
	private int coincount = 0;
	
	// Level Typ für verschiedene Operationen bei Random oder Normalen Leveln
	LEVELTYPE levelType;

	private PhysicsWorld physicsWorld;
	
	private Text gameOverText;
	private Text gameWonText;
	private boolean gameOverDisplayed = false;
	private boolean gameWon = false;
	private boolean isPaused = false;
	
	private Preferences prefs;
	
	private LevelCompleteWindow levelCompleteWindow;

	public GameScene()
	{
	}
	
	public GameScene(int level, LEVELTYPE type)
	{
		prefs = Preferences.getInstance();
		this.levelID = level;
		this.levelType = type;
		prefs.setLevelType(type);
	}
	
	@Override
	public void createScene() {
		levelCompleteWindow = new LevelCompleteWindow(GameScene.this, (ZoomCamera) camera, vbom);
	    createBackground();
	    createHUD();
	    createPhysics();
	    createGameOverText();
	    createPauseButton();
	    setOnSceneTouchListener(this);
	}
	
	private void createPauseButton() {
		final TiledSprite pauseButton = new TiledSprite(resourcesManager.camera.getWidth()-50, resourcesManager.camera.getHeight()-50, resourcesManager.pause_button_region, vbom)
		{
	        @Override
	        public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
	            if (pSceneTouchEvent.isActionDown()) {
	            	if (isPaused == false)
	            	{
		        		//setIgnoreUpdate(true);
		        		player.stopRunning();
		        	    setCurrentTileIndex(1);
		        	    physicsWorld.setGravity(new Vector2(0,0));
		        		isPaused = true;
	            	}
	            	else
	            	{
	            		//setIgnoreUpdate(false);
		            	player.setRunning();
		            	setCurrentTileIndex(0);
		            	physicsWorld.setGravity(new Vector2(0, -17));
		            	isPaused = false;
	            	}
	            }
	            return true;
	        }           
	    };
	    gameHUD.registerTouchArea(pauseButton);
	    gameHUD.setTouchAreaBindingOnActionDownEnabled(true);
		gameHUD.attachChild(pauseButton);
	}

	private void createPhysics()
	{
	    physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false); 
	    physicsWorld.setContactListener(contactListener());
	    registerUpdateHandler(physicsWorld);
	}
	
	@Override
	public void onBackKeyPressed() {
		disposeScene();
		player.stopAnimation();
		detachChildren();
        SceneManager.getInstance().loadMenuScene(engine);
        ResourcesManager.getInstance().resetCamera();
        prefs.setCurrentLevel(1);
        prefs.setLastScore(0);
	}

	@Override
	public SceneType getSceneType() {
		return SceneManager.SceneType.SCENE_GAME;			
	}

	@Override
	public void disposeScene() {
		ResourcesManager.getInstance().camera.setHUD(null);
		ResourcesManager.getInstance().camera.setChaseEntity(null); // nötig, da sonst beim zurück wechseln in das Menü, die kamera noch dem spieler folgt
	}

	private void createBackground()
	{
		int width = (int) ResourcesManager.getInstance().camera.getWidth();
		
		final float textureHeight = resourcesManager.game_background_region_back.getHeight();
		
		Sprite back = new Sprite(width * 0.5f, textureHeight *
				0.5f + 50, resourcesManager.game_background_region_back,
				engine.getVertexBufferObjectManager());
		
		Sprite front = new Sprite(width * 0.5f, textureHeight *
				0.5f, resourcesManager.game_background_region_front,
				engine.getVertexBufferObjectManager());

		// Float werte sind für den standard rgp wert - hintergrundfarbe
		ParallaxBackground background = new ParallaxBackground(0.3f, 0.3f, 0.3f) {

			float cameraPreviousX = 0;
			float parallaxValueOffset = 0;

			@Override
			public void onUpdate(float pSecondsElapsed) {
				final float cameraCurrentX = resourcesManager.camera
						.getCenterX();

				if (cameraPreviousX != cameraCurrentX) {
					parallaxValueOffset += cameraCurrentX - cameraPreviousX;
					this.setParallaxValue(parallaxValueOffset);

					cameraPreviousX = cameraCurrentX;
				}
				super.onUpdate(pSecondsElapsed);
			}
		};
		
		// Zahlen geben die geschwindigkeit an / 0 = statisch
		background.attachParallaxEntity(new ParallaxEntity(0, back));
		background.attachParallaxEntity(new ParallaxEntity(0, front));
		
		setBackground(background);
		setBackgroundEnabled(true);
	}
	
	private void createHUD()
	{
	    gameHUD = new HUD();
	    int y = (int) ResourcesManager.getInstance().camera.getHeight()-50;
	    int x = (int) ResourcesManager.getInstance().camera.getWidth()-230;
	    
	 // CREATE SCORE TEXT
	    highScoreText = new Text(15, y, resourcesManager.game_font, "Highscore 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
	    scoreText = new Text(15, y-40, resourcesManager.game_font, "Score 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
	    levelText = new Text(x-40, y, resourcesManager.game_font, "LVL 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
	    
	    scoreText.setAnchorCenter(0, 0);
	    levelText.setAnchorCenter(0, 0);
	    highScoreText.setAnchorCenter(0, 0);
	    
	    scoreText.setText("Score 0");
	    levelText.setText("LVL 0");
	    highScoreText.setText("Highscore 0");
	    gameHUD.attachChild(scoreText);
	    gameHUD.attachChild(levelText);
	    gameHUD.attachChild(highScoreText);
	    
	    camera.setHUD(gameHUD);
	}
	
	private void addToScore(int i)
	{
	    score += i;
	    scoreText.setText("Score " + score);
	}
	
	private void setLevelText(int lvl)
	{
		levelText.setText("LVL " + String.valueOf(lvl));
	}
	
	public void loadLevel()
	{
	    final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
	    
	    final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
	    
	    if (levelID == 0)
	    {
	    	levelID = 1;
	    }

	    if (levelType == LEVELTYPE.NORMAL)
	    {
		    setLevelText(levelID);
		    prefs.setCurrentLevel(levelID);
		    highScoreText.setText("Highscore " + String.valueOf(prefs.getHighScore(levelID, LEVELTYPE.NORMAL)));
	    }
	    else
	    {
	    	// Damit der fortlaufenden Score behalten/angezeigt wird
	    	int currLevel = prefs.getCurrentLevel();
	    	setLevelText(currLevel);
	    	if (currLevel >= 1)
	    	{
	    		int lastScore = prefs.getLastScore();
	    		scoreText.setText("Score " + String.valueOf(lastScore));
	    		score = lastScore;
	    	}
			oldRLHighScore = prefs.getHighScore(0, LEVELTYPE.RANDOM);
			highScoreText.setText("Highscore " + String.valueOf(oldRLHighScore));
	    }
	    
	    //Mutter Entity
	    levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL)
	    {
	        public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException 
	        {
	            final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
	            final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
	            
	            camera.setBounds(0, 0, width, height); // here we set camera bounds
	            camera.setBoundsEnabled(true);

	            return GameScene.this;
	        }
	    });
	    
	    levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY)
	    {
	        public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
	        {
	            final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
	            final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
	            final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
	            
	            final Sprite levelObject;
	            
	            if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1))
	            {
	                levelObject = new Sprite(x, y, resourcesManager.platform1_region, vbom);
	                PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF).setUserData("platform1");
	            } 
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2))
	            {
	                levelObject = new Sprite(x, y, resourcesManager.platform2_region, vbom);
	                final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
	                body.setUserData("platform2");
	                physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
	            }
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3))
	            {
	                levelObject = new Sprite(x, y, resourcesManager.platform3_region, vbom);
	                final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
	                body.setUserData("platform3");
	                physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
	            }
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN))
	            {
	            	// This code check on every update, if player collides with coin, and if does, we are adding 10 points to the score
	                levelObject = new Sprite(x, y, resourcesManager.coin_region, vbom)
	                {
	                    @Override
	                    protected void onManagedUpdate(float pSecondsElapsed) 
	                    {
	                        super.onManagedUpdate(pSecondsElapsed);
	                        
	                        if (player.collidesWith(this))
	                        {
	                            addToScore(10);
	                            this.setVisible(false); // Münze wird ausgeblendet
	                            this.setIgnoreUpdate(true); // MÜnze wird vom UpdateHandler nicht mehr berücksichtigt
	                        }
	                    }
	                };
	                coincount++;
	                levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1.2f, 1.3f, 1.5f)));
	            } 
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER))
	            {
	            	player = new Player(x, y, vbom, camera, physicsWorld)
	                {
	            		@Override
	            		public void onDie()
	            		{
	            		    if (!gameOverDisplayed)
	            		    {
	            		        displayGameOverText();
	            		    }
	            		}
	                };
	                levelObject = player;
	            }
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_GOAL))
	            {
	            	levelObject = new Sprite(x, y, resourcesManager.goal_region, vbom)
	            	{
	                    @Override
	                    protected void onManagedUpdate(float pSecondsElapsed) 
	                    {
	                        super.onManagedUpdate(pSecondsElapsed);
	                        
	                        if (player.collidesWith(this))
	                        {                   	
	        	                if (!gameWon)
	        	                {
	        	                	score /= 10;
	        	                	if (score >= (coincount/3)*2)
	        	                	{
	        	                		levelCompleteWindow.display(LoergEndCount.THREE);
	        	                	}
	        	                	else if ((score >= (coincount/3)))
	        	                	{
	        	                		levelCompleteWindow.display(LoergEndCount.TWO);
	        	                	}
	        	                	else if (score <= (coincount)/3)
	        	                	{
	        	                		levelCompleteWindow.display(LoergEndCount.ONE);
	        	                	}
	        	                	else 
	        	                	{
	        	                		levelCompleteWindow.display(LoergEndCount.ONE);
	        	                	}

	        	                	physicsWorld.clearPhysicsConnectors();
	        	                	levelIncrease();
	        	                	GameScene.this.registerTouchArea(levelCompleteWindow);
	        	            	    player.stopAnimation();
	        	            	    player.setVisible(false);
	        	            	    if (levelType == LEVELTYPE.RANDOM)
	        	            	    {
	        	            	    	if (oldRLHighScore < (score*10))
	        	            	    	{
	        	            	    		prefs.saveHighScore(score*10, 0, levelType);
	        	            	    	}
	        	            	    	prefs.setLastScore(score*10);
	        	            	    }
	        	            	    else
	        	            	    {
	        	            	    	prefs.saveHighScore(score*10, levelID, LEVELTYPE.NORMAL);
	        	            	    }
	        	                }
	                            this.setIgnoreUpdate(true); // Münze wird vom UpdateHandler nicht mehr berücksichtigt
	                        }
	                    }
	            	};
	            }
	            else
	            {
	                throw new IllegalArgumentException();
	            }

	            levelObject.setCullingEnabled(true);
	            return levelObject;
	        }
	    });

	    if (levelType == LEVELTYPE.NORMAL)
	    {
	    	levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".xml");
	    }
	    else
	    {
	    	LevelGenerator lvlg = new LevelGenerator();
	    	levelLoader.loadLevelFromStream(lvlg.getInputStream());
	    }
	    
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown())
	    {
	        if (!firstTouch)
	        {
	            player.setRunning();
	            firstTouch = true;
	        }
	        else
	        {
	            player.jump(isPaused);
	        }
	        
	        if (gameOverDisplayed == true)
	        {
	        	onBackKeyPressed();
	        }
	    }
	    return false;
	}
	
	private void createGameOverText()
	{
	    gameOverText = new Text(0, 0, resourcesManager.game_font, "Game Over!", vbom);
	}
	
	private void displayGameOverText()
	{
	    camera.setChaseEntity(null);
	    gameOverText.setPosition(camera.getCenterX(), camera.getCenterY());
	    attachChild(gameOverText);
	    gameOverDisplayed = true;
	}
	
	private void createWonText(String str)
	{
	    gameWonText = new Text(0, 0, resourcesManager.game_font, str, vbom);
	}
	
	private void displayWonText()
	{
	    camera.setChaseEntity(null);
	    gameWonText.setPosition(camera.getCenterX(), camera.getCenterY());
	    attachChild(gameWonText);
	    gameWon = true;
	}
	
	private void levelIncrease() {
		if (levelType == LEVELTYPE.NORMAL)
		{
			// Wenn das aktuelle Level-1 mit dem gespeicherten übereinstimmt, wird
			// das nächste Level freigeschalten
			if ((prefs.getUnlockedLevelsCount() == levelID) && (levelID < prefs.getMaxLevel())) {
				prefs.unlockNextLevel();
			}
			prefs.setCurrentLevel(levelID);
		}
		else
		{
			prefs.setCurrentLevel(levelID+1);
		}
		
	}
	
	//It lets us to execute code while events such as contact begin/end between fixtures occurs.
	//We can recognise fixtures/bodies by setting their user data as we did for example for players body by settings its user data to "player".
	private ContactListener contactListener()
	{
		ContactListener contactListener = new ContactListener() {
			public void beginContact(Contact contact) {
				
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if (x1.getBody().getUserData() != null
						&& x2.getBody().getUserData() != null) {
					if (x1.getBody().getUserData().equals("platform3")
							&& x2.getBody().getUserData().equals("player")) {
						engine.registerUpdateHandler(new TimerHandler(0.3f,
								new ITimerCallback() {
									public void onTimePassed(
											final TimerHandler pTimerHandler) {
										pTimerHandler.reset();
										engine.unregisterUpdateHandler(pTimerHandler);
										x1.getBody().setType(
												BodyType.DynamicBody);
									}
								}));
					}

					if (x2.getBody().getUserData().equals("platform3")
							&& x1.getBody().getUserData().equals("player")) {
						engine.registerUpdateHandler(new TimerHandler(0.3f,
								new ITimerCallback() {
									public void onTimePassed(
											final TimerHandler pTimerHandler) {
										pTimerHandler.reset();
										engine.unregisterUpdateHandler(pTimerHandler);
										x2.getBody().setType(
												BodyType.DynamicBody);
									}
								}));
					}

					if (x1.getBody().getUserData().equals("platform2")
							&& x2.getBody().getUserData().equals("player")) {
						engine.registerUpdateHandler(new TimerHandler(0.1f,
								new ITimerCallback() {
									public void onTimePassed(
											final TimerHandler pTimerHandler) {
										pTimerHandler.reset();
										engine.unregisterUpdateHandler(pTimerHandler);
										x1.getBody().setType(
												BodyType.DynamicBody);
									}
								}));
					}

					if (x2.getBody().getUserData().equals("platform2")
							&& x1.getBody().getUserData().equals("player")) {
						engine.registerUpdateHandler(new TimerHandler(0.1f,
								new ITimerCallback() {
									public void onTimePassed(
											final TimerHandler pTimerHandler) {
										pTimerHandler.reset();
										engine.unregisterUpdateHandler(pTimerHandler);
										x2.getBody().setType(
												BodyType.DynamicBody);
									}
								}));
					}

					if (x2.getBody().getUserData().equals("player")
							|| x1.getBody().getUserData().equals("player")) {
						player.increaseFootContacts();
					}
				}

			}

	        // wenn der kontakt nicht mehr besteht
	        public void endContact(Contact contact)
	        {
	            final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if (x1.getBody().getUserData() != null
						&& x2.getBody().getUserData() != null) {
					if (x2.getBody().getUserData().equals("player")
							|| x1.getBody().getUserData().equals("player")) {
						player.decreaseFootContacts();
					}
				}
	        }

	        public void preSolve(Contact contact, Manifold oldManifold)
	        {

	        }

	        public void postSolve(Contact contact, ContactImpulse impulse)
	        {

	        }
	    };
	    return contactListener;
	}
}
