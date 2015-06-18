package at.blackariesstudios.extras;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import at.blackariesstudios.manager.ResourcesManager;
import at.blackariesstudios.manager.SceneManager;
import at.blackariesstudios.preferences.Preferences;
import at.blackariesstudios.scene.MainMenuScene;

public class LevelSelectorWindow extends Sprite {

	private final static int TILE_DIMENSION = 70; // Hoehe und Breite eines Feldes
	private final static int TILE_PADDING = 20; // Abstand zwischen den Feldern
	private final static int COLUMNS = 5;
	private final static int ROWS = 4;

	private float mInitialX;
	private float mInitialY;
	private float mCameraWidth;
	private float mCameraHeight;

	private boolean mHidden;

	private Scene scene;
	private Engine engine;
	private int mMaxLevel;

	public LevelSelectorWindow(VertexBufferObjectManager pSpriteVertexBufferObject) {
		
		super(0, 0, 650, 400, ResourcesManager.getInstance().level_base_window_region, pSpriteVertexBufferObject);
		setPosition(ResourcesManager.getInstance().camera.getCenterX(), ResourcesManager.getInstance().camera.getCenterY());
		
		this.mCameraHeight = 400;
		this.mCameraWidth = 650;
		
		final float halfLevelSelectorWidth = ((TILE_DIMENSION * COLUMNS) + TILE_PADDING
				* (COLUMNS - 1)) * 0.5f;

		this.mInitialX = (this.mCameraWidth * 0.5f) - halfLevelSelectorWidth;

		final float halfLevelSelectorHeight = ((TILE_DIMENSION * ROWS) + TILE_PADDING
				* (ROWS - 1)) * 0.5f;
		this.mInitialY = (this.mCameraHeight * 0.5f) + halfLevelSelectorHeight;
		
//		// For Debbuging - setzt den LevelCount auf zurück
//		if (Preferences.getInstance().getUnlockedLevelsCount() > 1)
//		{
//			Preferences.getInstance().resetLevel();
//		}
		this.setMaxLevel(Preferences.getInstance().getUnlockedLevelsCount());
	}
	
	public void setMaxLevel(int lvl)
	{
		if (lvl > 1)
		{
			this.mMaxLevel = lvl;
		}
		else
		{
			this.mMaxLevel = 1;
		}
	}

	public void createTiles(final ITextureRegion pTextureRegion, final Font pFont) {
		this.scene = SceneManager.getInstance().getCurrentScene();
		this.engine = ResourcesManager.getInstance().engine;
		
		Sprite closeButton = new Sprite(20, this.mCameraHeight-20, ResourcesManager.getInstance().level_close_button, ResourcesManager.getInstance().vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown()){
					hide();
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		this.attachChild(closeButton);
		scene.registerTouchArea(closeButton);
		
		/* Temp coordinates for placing level tiles */
		float tempX = this.mInitialX + TILE_DIMENSION * 0.5f;
		float tempY = this.mInitialY - TILE_DIMENSION * 0.5f;
		/* Current level of the tile to be placed */
		int currentTileLevel = 1;
		/*
		 * Loop through the Rows, adjusting tempY coordinate after each
		 * iteration
		 */
		for (int i = 0; i < ROWS; i++) {
			/*
			 * Loop through the column positions, placing a LevelTile in each
			 * column
			 */
			for (int o = 0; o < COLUMNS; o++) {
				final boolean locked;
				/* Determine whether the current tile is locked or not */
				if (currentTileLevel <= mMaxLevel) {
					locked = false;
				} else {
					locked = true;
				}
				/* Create a level tile */
				LevelTile levelTile = new LevelTile(tempX, tempY, locked,
						currentTileLevel, pTextureRegion, pFont);

				/*
				 * Attach the level tile's text based on the locked and
				 * currentTileLevel variables pass to its constructor
				 */
				levelTile.attachText();
				/*
				 * Register & Attach the levelTile object to the LevelSelector
				 */
				scene.registerTouchArea(levelTile);
				this.attachChild(levelTile);
				/* Increment the tempX coordinate to the next column */
				tempX = tempX + TILE_DIMENSION + TILE_PADDING;
				/* Increment the level tile count */
				currentTileLevel++;
			}
			/*
			 * Reposition the tempX coordinate back to the first row (far left)
			 */
			tempX = mInitialX + TILE_DIMENSION * 0.5f;
			/*
			 * Reposition the tempY coordinate for the next row to apply tiles
			 */
			tempY = tempY - TILE_DIMENSION - TILE_PADDING;
		}
	}

	/* Display the LevelSelector on the Scene. */
	public void show() {
		mHidden = false;
		/*
		 * Attach the LevelSelector the the Scene if it currently has no parent
		 */
		MainMenuScene mainMenuScene = (MainMenuScene) SceneManager.getInstance().getCurrentScene();
		mainMenuScene.getMenuChildScene().setVisible(false);
		mainMenuScene.getMenuChildScene().clearTouchAreas();
		
		if (!this.hasParent()) {
			scene.attachChild(this);
		}
		this.setVisible(true);
		mainMenuScene = null;
	}

	/* Hide the LevelSelector on the Scene. */
	public void hide() {
		MainMenuScene mainMenuScene = (MainMenuScene) SceneManager.getInstance().getCurrentScene();
		
		mainMenuScene.getMenuChildScene().setVisible(true);
		mainMenuScene.getMenuChildScene().registerTouchArea(mainMenuScene.getMenuChildScene().getChildByIndex(0));
		mainMenuScene.getMenuChildScene().registerTouchArea(mainMenuScene.getMenuChildScene().getChildByIndex(1));
				
		mHidden = true;
		this.setVisible(false);
	}

	private class LevelTile extends Sprite {

		private Font mFont;
		private boolean mIsLocked;
		private int mLevelNumber;
		private Text mTileText;

		public LevelTile(float pX, float pY, boolean pIsLocked,
				int pLevelNumber, ITextureRegion pTextureRegion, Font pFont) {
			super(pX, pY, LevelSelectorWindow.TILE_DIMENSION,
					LevelSelectorWindow.TILE_DIMENSION, pTextureRegion,
					LevelSelectorWindow.this.engine
							.getVertexBufferObjectManager());
			/* Initialize the necessary variables for the LevelTile */
			this.mFont = pFont;
			this.mIsLocked = pIsLocked;
			this.mLevelNumber = pLevelNumber;
		}

		/*
		 * Method used to obtain whether or not this level tile represents a
		 * level which is currently locked
		 */
		public boolean isLocked() {
			return this.mIsLocked;
		}

		/* Method used to obtain this specific level tiles level number */
		public int getLevelNumber() {
			return this.mLevelNumber;
		}

		public void attachText() {
			String tileTextString = null;
			/* If the tile's text is currently null... */
			if (this.mTileText == null) {
				/*
				 * Determine the tile's string based on whether it's locked or
				 * not
				 */
				if (this.mIsLocked) {
					tileTextString = "X";
				} else {
					tileTextString = String.valueOf(this.mLevelNumber);
				}
				/*
				 * Setup the text position to be placed in the center of the
				 * tile
				 */
				final float textPositionX = LevelSelectorWindow.TILE_DIMENSION * 0.5f;
				final float textPositionY = textPositionX;
				/* Create the tile's text in the center of the tile */
				this.mTileText = new Text(textPositionX, textPositionY,
						this.mFont, tileTextString, tileTextString.length(),
						LevelSelectorWindow.this.engine
								.getVertexBufferObjectManager());
				/* Attach the Text to the LevelTile */
				this.attachChild(mTileText);
			}
		}

		@Override
		public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
				float pTouchAreaLocalX, float pTouchAreaLocalY) {
			/*
			 * If the LevelSelector is not hidden, proceed to execute the touch
			 * event
			 */
			if (!LevelSelectorWindow.this.mHidden) {
				/* If a level tile is initially pressed down on */
				if (pSceneTouchEvent.isActionDown()) {
					/* If this level tile is locked... */
					if (this.mIsLocked) {
						/* Tile Locked event... */
						LevelSelectorWindow.this.scene.getBackground().setColor(
								org.andengine.util.adt.color.Color.RED);
					} else {

						/*
						 * Tile unlocked event... This event would likely prompt
						 * level loading but without getting too complicated we
						 * will simply set the Scene's background color to green
						 */
						LevelSelectorWindow.this.scene.getBackground().setColor(
								org.andengine.util.adt.color.Color.GREEN);
						
						
						/**
						 * Example level loading: LevelSelector.this.hide();
						 * SceneManager.loadLevel(this.mLevelNumber);
						 */
						
						SceneManager.getInstance().loadGameScene(engine, this.mLevelNumber);
					}
					return true;
				}
			}
			return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
					pTouchAreaLocalY);
		}
	}

}
