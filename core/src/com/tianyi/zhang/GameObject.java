package com.tianyi.zhang;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class GameObject {
	private World world;
	private BodyEditorLoader loader;
	
	private Body body;
	private Vector2 origin;
	private Sprite sprite;
	private float scale;

	public GameObject(World world, BodyEditorLoader loader, BodyType type, String bodyName,
			float scale, Vector2 anchor, float density, float friction, float restitution) {
		this.world = world;
		this.loader = loader;
		this.scale = scale;
		body = createBody(type, bodyName, scale, anchor, density, friction, restitution);
		sprite = new Sprite(new Texture(Gdx.files.internal(loader.getImagePath(bodyName))));
		origin = loader.getOrigin(bodyName, scale).cpy();
		configSprite();
		updateSprite();
	}
	
	protected void configSprite() {
		float width = sprite.getWidth();
		float height = sprite.getHeight();
		sprite.setSize(1f * scale, 1f * height / width * scale);
		sprite.setOrigin(origin.x, origin.y);
	}
	
	public void printSpriteAttr() {
		System.out.println("Sprite position: " + sprite.getX() + ", " + sprite.getY());
		System.out.println("Sprite size: " + sprite.getWidth() + ", " + sprite.getHeight());
		System.out.println("Sprite origin: " + origin.x + ", " + origin.y);
		System.out.println();
	}
	
	protected void updateSprite() {
		Vector2 pos = body.getPosition().cpy().sub(origin);
		sprite.setPosition(pos.x, pos.y);
		float angle = body.getAngle() * MathUtils.radiansToDegrees;
		sprite.rotate(angle - sprite.getRotation());
	}
	
	public void drawSprite(SpriteBatch batch) {
		updateSprite();
		sprite.draw(batch);
	}
	
	protected Body createBody(BodyDef.BodyType type, String name, float scale, Vector2 anchor, float density, float friction, float restitution) {		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(anchor);
		
		Body body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;
		fixtureDef.friction = friction;
		
		loader.attachFixture(body, name, fixtureDef, scale);
		return body;
	}
	
	public Body getBody() {
		return body;
	}
	public void setBody(Body body) {
		this.body = body;
	}
	public World getWorld() {
		return world;
	}
	public void setWorld(World world) {
		this.world = world;
	}
	public BodyEditorLoader getLoader() {
		return loader;
	}
	public void setLoader(BodyEditorLoader loader) {
		this.loader = loader;
	}
	public Sprite getSprite() {
		return sprite;
	}
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
}
