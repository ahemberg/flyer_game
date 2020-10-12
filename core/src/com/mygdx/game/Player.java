package com.mygdx.game;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Player {
  private final Vector3 position;
  private final Vector3 speed;
  private final Vector3 viewDirection;
  private final PerspectiveCamera camera;


  Player(final PerspectiveCamera camera) {
    this.camera = camera;
    this.position = new Vector3(camera.position);
    this.viewDirection = new Vector3(camera.direction);
    this.speed = new Vector3(0, 0, 0);
  }

  public void update() {
    this.speed.set(new Vector3(camera.position).sub(position));
    this.position.set(camera.position);
    this.viewDirection.set(camera.direction);
  }

  public Vector3 getPosition() {
    return this.position;
  }

  public Vector3 getSpeed() {
    return this.speed;
  }

  public Vector3 getViewDirection() {
    return this.viewDirection;
  }

  public PerspectiveCamera getCamera() {
    return this.camera;
  }
}
