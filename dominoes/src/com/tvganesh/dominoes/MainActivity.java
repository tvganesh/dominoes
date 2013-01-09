package com.tvganesh.dominoes;

/* Developed by Tinniam V Ganesh 9 Jan 2013
 * Domino effect based on Dominos demo by Daniel Murphy at http://www.jbox2d.org/
 * Uses Box2D physics engine and AndEngine
 */
import java.util.Vector;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;


public class MainActivity extends SimpleBaseGameActivity implements IAccelerationListener {
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;
	private static final float  DEGTORAD = 0.0174532925199432957f;
	public static final float PIXEL_TO_METER_RATIO_DEFAULT = 32.0f;
	
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mPlatformTextureRegion;
	private ITextureRegion mBrickTextureRegion;
	
	private ITextureRegion mFaceTextureRegion;
	private ITextureRegion mBoxTextureRegion;
	
    private ITextureRegion mTexture;
    
    private Scene mScene;
    
    private PhysicsWorld mPhysicsWorld;
    
    private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(50f, 0.1f, 0.5f);
	public EngineOptions onCreateEngineOptions() {
		
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}
	
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 610, 75, TextureOptions.BILINEAR);
		
		this.mPlatformTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "platform1.png", 0, 0);
		this.mBitmapTextureAtlas.load();
		
		this.mBrickTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "brick1.png", 600, 10);
		this.mBitmapTextureAtlas.load();
		
		
		
	}
	
	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		//this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		
		this.initDominoes(mScene);
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		return mScene;		
		
	}
	
	public void initDominoes(Scene mScene){
		
		Sprite platform1,platform2,platform3;
		Sprite brick;
		final Body platformBody1,platformBody2,platformBody3;
		
		
		Body brickBody;
		
		// Create a Physics World
		//this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

		
		//Create the floor		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);

	

		
		//Create platform 1
		platform1 = new Sprite(50, 100, this.mPlatformTextureRegion, this.getVertexBufferObjectManager());
		platformBody1 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, platform1, BodyType.StaticBody, FIXTURE_DEF);	
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(platform1, platformBody1, true, true));
		this.mScene.attachChild(platform1);
		
		platform1.setUserData(platformBody1);
		
		// Create 37 bricks
		for(int i=0; i < 37; i++) {
			
			  brick = new Sprite(50 + i * 15, 50, this.mBrickTextureRegion, this.getVertexBufferObjectManager());			
			  brickBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, brick, BodyType.DynamicBody, FIXTURE_DEF);	  
			  float angle = brickBody.getAngle();
			  Log.d("Angle","angle:"+ angle);
			  // Tilt first 4 bricks
			  if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4) {
			      brickBody.setTransform(120/PIXEL_TO_METER_RATIO_DEFAULT,80/PIXEL_TO_METER_RATIO_DEFAULT,(65 - (i*10)) * DEGTORAD);
			  }
			  this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(brick, brickBody, true, true));
			this.mScene.attachChild(brick);	
			brick.setUserData(brickBody);
			
		}
		//Create platform 2
		platform2 = new Sprite(100, 200, this.mPlatformTextureRegion, this.getVertexBufferObjectManager());
		platformBody2 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, platform2, BodyType.StaticBody, FIXTURE_DEF);	
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(platform2, platformBody2, true, true));
		this.mScene.attachChild(platform2);
		platform2.setUserData(platformBody2);
		
		// Create 37 bricks
		for(int i=0; i < 37; i++) {
			
			  brick = new Sprite(100 + i * 15, 150, this.mBrickTextureRegion, this.getVertexBufferObjectManager());			
			  brickBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, brick, BodyType.DynamicBody, FIXTURE_DEF);	  
			  float angle = brickBody.getAngle();
			  Log.d("Angle","angle:"+ angle);
			  // Tilt the last brick backward
			  if (i == 36) {
				 
			      brickBody.setTransform(600/PIXEL_TO_METER_RATIO_DEFAULT,170/PIXEL_TO_METER_RATIO_DEFAULT, 95 * DEGTORAD);
			  }
			  this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(brick, brickBody, true, true));
			this.mScene.attachChild(brick);	
			brick.setUserData(brickBody);
			
		}
		
		//Create  platform 3
		platform3 = new Sprite(40, 300, this.mPlatformTextureRegion, this.getVertexBufferObjectManager());
		platformBody3 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, platform3, BodyType.StaticBody, FIXTURE_DEF);	
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(platform3, platformBody3, true, true));
		this.mScene.attachChild(platform3);
		platform3.setUserData(platformBody3);
		
		// Create 37 bricks
		for(int i=0; i < 37; i++) {
			
			  brick = new Sprite(40 + i * 15, 250, this.mBrickTextureRegion, this.getVertexBufferObjectManager());			
			  brickBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, brick, BodyType.DynamicBody, FIXTURE_DEF);	  
			  float angle = brickBody.getAngle();
			  Log.d("Angle","angle:"+ angle);
			  if (i == 0)  {
			      brickBody.setTransform(60/PIXEL_TO_METER_RATIO_DEFAULT,280/PIXEL_TO_METER_RATIO_DEFAULT,45 * DEGTORAD);
			  }
			  this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(brick, brickBody, true, true));
			this.mScene.attachChild(brick);	
			brick.setUserData(brickBody);
			
		}
		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
	}


	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
		
	}
	

}
