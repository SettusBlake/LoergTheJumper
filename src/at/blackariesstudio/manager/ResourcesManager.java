package at.blackariesstudio.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import at.blackariesstudio.GameActivity;
import at.blackariesstudio.extras.LevelSelectorWindow;
import at.blackariesstudio.preferences.Preferences;

public class ResourcesManager
{
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------
    
    private static final ResourcesManager INSTANCE = new ResourcesManager();
    
	private int camera_width;
	private int camera_height;
    
    public Engine engine;
    public GameActivity activity;
    public ZoomCamera camera;
    public VertexBufferObjectManager vbom;
    
    public Font base_font;
    public Font game_font;
    public Font level_font;
    public Font loading_font;
    public LevelSelectorWindow levelSelector;
    
    //---------------------------------------------
    // TEXTURES & TEXTURE REGIONS
    //---------------------------------------------
    
    public ITextureRegion splash_region;
    private BitmapTextureAtlas splashTextureAtlas;
    public ITextureRegion menu_background_region;
    public ITextureRegion play_region;
    public ITextureRegion levelSelector_region;
        
    private BuildableBitmapTextureAtlas menuTextureAtlas;
    public ITiledTextureRegion player_region;
    
    // Loading Scene
    private BitmapTextureAtlas loadingTextureAtlas;
    public ITextureRegion loading_background_region;
    
    // Game Texture
    public BuildableBitmapTextureAtlas gameTextureAtlas;
        
    // Game Texture Regions
    public ITextureRegion game_background_region_back;
    public ITextureRegion game_background_region_front;
    public ITextureRegion platform1_region;
    public ITextureRegion platform2_region;
    public ITextureRegion platform3_region;
    public ITextureRegion coin_region;
    public ITextureRegion goal_region;
    
    // Level beendet
    public ITextureRegion complete_window_region;
    public ITiledTextureRegion complete_stars_region;
    
    // Level Selector
    public ITextureRegion level_selector_tile_region;
    public ITextureRegion level_base_window_region;
    public ITextureRegion level_close_button;
    
    // Yes No Auswahl-Fenster
    private BuildableBitmapTextureAtlas yesNoMenuTextureAtlas;
    public ITextureRegion ok_button_region;
    public ITextureRegion no_button_region;
    public ITextureRegion yesno_background_region;
    
    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------

    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadMenuAudio();
        loadLoadingFont();
        loadLevelFonts();
        createLevelSelector();
    }
    
    public void loadGameResources()
    {
    	loadGameFont();
        loadGameGraphics();
        loadGameAudio();
    }
    
    public void loadYesNoMenuResources()
    {
    	loadBaseFont();
    	loadYesNoGraphics();
    	loadGameAudio();
    }
    
    public void createLevelSelector()
    {
    	this.levelSelector = new LevelSelectorWindow(this.vbom);
    }
    
	public void resetCamera()
	{
		camera.setBounds(0, 0, this.camera_width, this.camera_height);
		camera.setXMin(0);
		camera.setYMin(0);
		camera.setXMax(this.camera_width);
		camera.setYMax(this.camera_height);
		camera.reset();
		camera.setZoomFactor(1.0f);
		camera.setCenter(this.camera_width/2, this.camera_height/2);
	}
	
	public void resetPrefLevel()
	{
		Preferences.getInstance().resetLevel();
	}
    
	private void loadMenuGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
    	
    	// Buildable Bitmap, damit wir uns nicht um die Position kümmern müssen
    	menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
    	menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
    	play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "start.png");
    	levelSelector_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "level.png");
    	level_selector_tile_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "level_icon.png");
    	level_base_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "level_base_window.png");
    	level_close_button = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "close_button.png");
    	       
    	try 
    	{
    	    this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    	    this.menuTextureAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e)
    	{
    	        Debug.e(e);
    	}
    }
    
	public void unloadYesNoMenuTextures()
	{
		this.yesNoMenuTextureAtlas.unload();
	}
	
    public void unloadGameTextures()
    {
        this.gameTextureAtlas.unload();
    }
    
    public void unloadMenuTextures()
    {
        menuTextureAtlas.unload();
        this.levelSelector = null;
    }
        
    public void loadMenuTextures()
    {
        menuTextureAtlas.load();
    }
    
    private void loadMenuAudio()
    {
        
    }
    
    private void loadYesNoGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/extras/");
    	yesNoMenuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
    	
        // Buttons
        ok_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(yesNoMenuTextureAtlas, activity, "ok_button.png");
        no_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(yesNoMenuTextureAtlas, activity, "no_button.png");
        yesno_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(yesNoMenuTextureAtlas, activity, "yesno_background.png");
        
        try 
        {
            this.yesNoMenuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.yesNoMenuTextureAtlas.load();
        } 
        catch (final TextureAtlasBuilderException e)
        {
            Debug.e(e);
            System.out.println(e.getMessage());
        }
    }

    private void loadGameGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
        gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
        
        game_background_region_back = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "background_back.png");
        game_background_region_front = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "background_front.png");
        platform1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "platform_g.png");
        platform2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "platform_r.png");
        platform3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "platform_y.png");
        coin_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "loin.png");
        goal_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "goal.png");
        
        // Level Ende Fenster
        complete_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "level_complete_window.png");
        complete_stars_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "level_complete_tiled_loerg.png", 2, 1);
        
        player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "LoergTiledSpriteVersuch_Klein.png", 2, 1);
       
        try 
        {
            this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.gameTextureAtlas.load();
        } 
        catch (final TextureAtlasBuilderException e)
        {
            Debug.e(e);
            System.out.println(e.getMessage());
        }
    }
    
    private void loadBaseFont()
    {
    	FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);  	
    	base_font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "levelfont.ttf", 40, true, android.graphics.Color.TRANSPARENT, 2, android.graphics.Color.BLACK);
    	base_font.load();
    }
    
    private void loadLevelFonts()
    {
    	FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);  	
    	level_font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "levelfont.ttf", 40, true, android.graphics.Color.TRANSPARENT, 2, android.graphics.Color.BLACK);
    	level_font.load();
    }
    
    private void loadGameFont()
    {
    	FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);   	
    	game_font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, android.graphics.Color.BLACK, 2, android.graphics.Color.WHITE);
    	game_font.load();
    }
    
    private void loadLoadingFont()
    {
    	FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    	loading_font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 65, true, android.graphics.Color.BLACK, 2, android.graphics.Color.WHITE);
    	loading_font.load();
    }
    
    
    private void loadGameAudio()
    {
        
    }
    
    public void loadSplashScreen()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    	splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
    	splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
    	splashTextureAtlas.load();
    }
    
    public void unloadSplashScreen()
    {
    	splashTextureAtlas.unload();
    	splash_region = null;
    }
    
    public void loadLoadingResources()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
    	loadingTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
    	loading_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(loadingTextureAtlas, activity, "menu_background.png", 0, 0);
    	loadingTextureAtlas.load();
    	loadLoadingFont();
    }
    
    public void unloadLoadingScreen()
    {
    	loadingTextureAtlas.unload();
    	loading_background_region = null;
    }
    
    
    /**
     * We use this method at beginning of game loading, to prepare Resources Manager properly,
     * setting all needed parameters, so we can latter access them from different classes (eg. scenes)
     */
    
    public static void prepareManager(Engine engine, GameActivity activity, ZoomCamera camera, VertexBufferObjectManager vbom)
    {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
    
    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------
    
    public int getCameraWidth() {
		return camera_width;
	}

	public void setCameraWidth(int camera_width) {
		this.camera_width = camera_width;
	}

	public int getCameraHeight() {
		return camera_height;
	}

	public void setCameraHeight(int camera_height) {
		this.camera_height = camera_height;
	}
    
    public static ResourcesManager getInstance()
    {
        return INSTANCE;
    }
}