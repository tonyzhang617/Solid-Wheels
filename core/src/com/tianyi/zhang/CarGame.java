package com.tianyi.zhang;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;

public class CarGame extends ApplicationAdapter implements InputProcessor {
	private World world;
	private OrthographicCamera camera;
	private Box2DDebugRenderer debugRenderer;
	// private ShapeRenderer shapeRenderer;
	
	private GameObject terrain, car, wheel1, wheel2, statue;
	private WheelJoint jointWheel1, jointWheel2;
	
	private SpriteBatch batch;
	
	private float widthInMeters = 24f, heightInMeters;
	private float screenWidth, screenHeight;
	
	private BodyEditorLoader loader;
	
	private static final float DEFAULT_SPEED = 30f;
	
	@Override
	public void create () {
		world = new World(new Vector2(0f, -9.8f), true);
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		heightInMeters = widthInMeters * screenHeight / screenWidth;
		camera = new OrthographicCamera(widthInMeters, heightInMeters);
		// camera.position.set(56f, 0f, 0f);
		batch = new SpriteBatch();
		// debugRenderer = new Box2DDebugRenderer(true, true, true, true, false, false);
		debugRenderer = new Box2DDebugRenderer();
		loader = new BodyEditorLoader(Gdx.files.internal("bodies.json"));
		
		terrain = new GameObject(world, loader, BodyType.StaticBody, "terrain", 
				100f, new Vector2(0, 0), 1f, 0.72f, 0.48f);
		car = new GameObject(world, loader, BodyType.DynamicBody, "car", 
				5f, new Vector2(0f, 1f), 0.25f, 0.72f, 0.48f);
		wheel1 = new GameObject(world, loader, BodyType.DynamicBody, "wheel", 
				0.8f, new Vector2(0f, 1f), 2f, 0.92f, 0.12f);
		wheel2 = new GameObject(world, loader, BodyType.DynamicBody, "wheel", 
				0.8f, new Vector2(-2.8f, 1f), 2f, 0.92f, 0.12f);
		statue = new GameObject(world, loader, BodyType.DynamicBody, "statue", 
				5f, new Vector2(56f, -3.0f), 0.1f, 0.12f, 0.48f);
		
		// createRevoluteJoint(car.getBody(), wheel1.getBody());
		// createRevoluteJoint(car.getBody(), wheel2.getBody());
		
		jointWheel1 = createWheelJoint(car.getBody(), wheel1.getBody());
		jointWheel2 = createWheelJoint(car.getBody(), wheel2.getBody());
		
		Gdx.input.setInputProcessor(this);
	}
	
	protected WheelJoint createWheelJoint(Body bodyA, Body bodyB) {
		WheelJointDef jointDef = new WheelJointDef();
		jointDef.initialize(bodyA, bodyB, bodyB.getPosition(), new Vector2(0f, 1f));
//		jointDef.bodyA = bodyA;
//		jointDef.bodyB = bodyB;
//		jointDef.localAnchorA.set(bodyA.getLocalPoint(bodyB.getWorldCenter()));
//		jointDef.localAnchorB.set(bodyB.getLocalCenter());
//		jointDef.localAxisA.set(new Vector2(0, 0.2f));
		jointDef.collideConnected = false;
		jointDef.dampingRatio = 0.24f;
		jointDef.enableMotor = true;
		jointDef.maxMotorTorque = 100f;
		jointDef.motorSpeed = 0f;
		jointDef.frequencyHz = 5f;
		return (WheelJoint) world.createJoint(jointDef);
	}
	
	protected RevoluteJoint createRevoluteJoint(Body bodyA, Body bodyB) {
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.initialize(bodyA, bodyB, bodyB.getPosition());
		jointDef.collideConnected = false;
		jointDef.enableMotor = true;
		jointDef.maxMotorTorque = 50f;
		jointDef.motorSpeed = 0f;
		return (RevoluteJoint) world.createJoint(jointDef);
	}

	@Override
	public void render () {
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		camera.position.set(car.getBody().getPosition().x, car.getBody().getPosition().y, 0);
		camera.update();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		// debugRenderer.render(world, camera.combined);
		terrain.drawSprite(batch);
		car.drawSprite(batch);
		wheel1.drawSprite(batch);
		wheel2.drawSprite(batch);
		statue.drawSprite(batch);
		batch.end();
	}
	
	@Override
	public void resize(int width, int height) {
		heightInMeters = widthInMeters * height / width;
		camera.viewportWidth = widthInMeters;
		camera.viewportHeight = heightInMeters;
		camera.update();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	protected void setMotorsSpeed(float speed) {
		jointWheel1.setMotorSpeed(speed);
		jointWheel2.setMotorSpeed(speed);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (screenX < screenWidth / 2f) {
			setMotorsSpeed(DEFAULT_SPEED);
		} else {
			setMotorsSpeed(-DEFAULT_SPEED);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		setMotorsSpeed(0f);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
