package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainGame extends Game {

  SpriteBatch batch;
  BitmapFont bitmapFont;

  private final int height;
  private final int width;

  public MainGame(final int width, final int height) {
    this.height = height;
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  @Override
  public void create() {
    batch = new SpriteBatch();
    bitmapFont = new BitmapFont();
    this.setScreen(new MainMenuScreen(this));
  }

  @Override
  public void render() {
    super.render();
  }

  @Override
  public void dispose() {
    batch.dispose();
    bitmapFont.dispose();
  }
}
