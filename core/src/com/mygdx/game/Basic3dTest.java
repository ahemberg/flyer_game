package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;


/**
 * TODO: THis class demos drawing a green field. Todo create some terrain generation
 * for the pixmap. Understand how the heightmap method works. Make the camera move around proper
 */
public class Basic3dTest extends InputAdapter implements ApplicationListener {

  PerspectiveCamera camera;
  CameraInputController inputController;
  ModelBatch modelBatch;
  SpriteBatch batch;
  Model model;
  Model axesModel;
  BitmapFont font;


  HeightField field;
  Renderable ground;
  Environment environment;
  Texture texture;
  ModelInstance instance;
  final Color bgColor = new Color(0, 0.5f, 1, 1);

  public ModelInstance axesInstance;
  public boolean showAxes = true;
  public Array<ModelInstance> instances = new Array<ModelInstance>();

  @Override
  public void create() {
    modelBatch = new ModelBatch(new DefaultShaderProvider());
    batch = new SpriteBatch();

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1.0f, -0.8f));

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(10f, 10f, 10f);
    camera.lookAt(0,0,0);
    camera.near = 1f;
    camera.far = 10000f; //Render Distance
    camera.update();

    ModelBuilder modelBuilder = new ModelBuilder();
    model = modelBuilder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), VertexAttributes.Usage.Position
        | VertexAttributes.Usage.Normal);
    instance = new ModelInstance(model);

    Gdx.input.setInputProcessor(new InputMultiplexer(this, inputController = new CameraInputController(camera)));

    texture = new Texture(Gdx.files.internal("grass.jpg"));

    final Pixmap data = new Pixmap(20, 20, Format.RGBA8888);
    //final Pixmap data = new Pixmap(Gdx.files.internal("perlin3.jpg"));
    field = new HeightField(true, data, true, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);
    data.dispose();
    field.corner00.set(-100f, 0, -100f);
    field.corner10.set(100f, 0, -100f);
    field.corner01.set(-100f, 0, 100f);
    field.corner11.set(100f, 0, 100f);
    field.color00.set(0, 1, 0, 1);
    field.color01.set(0, 1, 0, 1);
    field.color10.set(0, 1, 0, 1);
    field.color11.set(0, 1, 0, 1);
    field.magnitude.set(0f, 5f, 0f);
    field.update();

    ground = new Renderable();
    ground.environment = environment;
    ground.meshPart.mesh = field.mesh;
    ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
    ground.meshPart.offset = 0;
    ground.meshPart.size = field.mesh.getNumIndices();
    ground.meshPart.update();
    //ground.material = new Material(TextureAttribute.createDiffuse(texture));
    ground.material = new Material();

    font = new BitmapFont();

    createAxes();

  }

  final float GRID_MIN = -1000f;
  final float GRID_MAX = 1000f;
  final float GRID_STEP = 50f;

  private void createAxes () {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
    builder.setColor(Color.BLACK);
    for (float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
      builder.line(t, 0, GRID_MIN, t, 0, GRID_MAX);
      builder.line(GRID_MIN, 0, t, GRID_MAX, 0, t);
    }
    builder = modelBuilder.part("axes", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
    builder.setColor(Color.RED);
    builder.line(0, 0, 0, 100, 0, 0);
    builder.setColor(Color.GREEN);
    builder.line(0, 0, 0, 0, 100, 0);
    builder.setColor(Color.BLUE);
    builder.line(0, 0, 0, 0, 0, 100);
    axesModel = modelBuilder.end();
    axesInstance = new ModelInstance(axesModel);
  }


  @Override
  public void resize(int width, int height) {

  }

  void render (ModelBatch batch, Array<ModelInstance> instances) {
    batch.render(instances);
    batch.render(ground);
  }

  public void render (final Array<ModelInstance> instances) {
    modelBatch.begin(camera);
    if (showAxes) modelBatch.render(axesInstance);
    if (instances != null) render(modelBatch, instances);
    modelBatch.end();
  }

  @Override
  public void render() {
    inputController.update();

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);

    render(instances);

    batch.begin();
    font.draw(batch, String.format("Camera pos: %f %f %f", camera.position.x, camera.position.y, camera.position.z), 10, 50);
    font.draw(batch, String.format("Camera dir: %f %f %f", camera.direction.x, camera.direction.y, camera.direction.z), 10, 20);
    batch.end();

  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void dispose() {
    modelBatch.dispose();
    batch.dispose();
    model.dispose();
    //texture.dispose();
    field.dispose();
    font.dispose();
  }
}
