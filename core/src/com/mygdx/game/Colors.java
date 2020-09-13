package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;

// TODO These are alredy defined by badlogics Color. Do you really  need this?
public enum Colors {
  SKY(0.529f,0.808f,0.922f, 1);

  private final float red;
  private final float green;
  private final float blue;
  private final float alpha;

  Colors(final float red, final float green, final float blue, final float alpha) {
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.alpha = alpha;
  }

  public Color getColor() {
    return new Color(red, green, blue, alpha);
  }

}
