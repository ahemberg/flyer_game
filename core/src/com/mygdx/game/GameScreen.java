package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
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

public class GameScreen extends InputAdapter implements Screen {

  private static final float RENDER_DISTANCE = 100_0000f;
  private static final boolean SHOW_AXES = true;
  private final float GRID_MIN = -1000f;
  private final float GRID_MAX = 1000f;
  private final float GRID_STEP = 50f;

  private final float GROUND_WIDTH = 10;

  private final MainGame game;
  private final PerspectiveCamera camera;
  private final Environment environment;
  private final ModelInstance modelInstance;
  private final Model axesModel;
  private final ModelInstance axesInstance;
  private final CameraInputController inputController;
  private final HeightField heightField;
  private final Renderable ground;
  private final BitmapFont font;
  private final ModelBatch modelBatch;
  private final SpriteBatch batch;
  private final Texture groundTexture;

  public static final Color BG_COLOR = new Color(0, 0.5f, 1, 1);

  GameScreen(final MainGame game) {
    this.modelBatch = new ModelBatch(new DefaultShaderProvider());
    this.batch = new SpriteBatch();
    this.game = game;
    this.environment = createEnvironment();
    this.modelInstance = createModelInstance();
    this.axesModel = createAxes();
    this.axesInstance = new ModelInstance(axesModel);
    this.heightField = generateHeightField();
    this.groundTexture = new Texture(Gdx.files.internal("grass.jpg"));
    this.ground = generateGround(this.heightField, groundTexture);
    this.font = new BitmapFont();

    this.camera = createCamera();
    Gdx.input.setInputProcessor(new InputMultiplexer(this, inputController = new CameraInputController(camera)));

  }

  private PerspectiveCamera createCamera() {
    final PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(10f, 10f, 10f);
    camera.lookAt(0, 0, 0);
    camera.near = 1f;
    camera.far = RENDER_DISTANCE;
    camera.update();
    return camera;
  }

  private Environment createEnvironment() {
    final Environment environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1.0f, -0.8f));
    return environment;
  }

  private ModelInstance createModelInstance() {
    final ModelBuilder modelBuilder = new ModelBuilder();
    final Model model = modelBuilder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), VertexAttributes.Usage.Position
        | VertexAttributes.Usage.Normal);
    return new ModelInstance(model);
  }

  private HeightField generateHeightField() {


    final Pixmap data = new Pixmap(Gdx.files.internal("heightmap.png"));

    final HeightField field = new HeightField(true, data, false, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);
    data.dispose();
    field.corner00.set(-GROUND_WIDTH, 0, -GROUND_WIDTH);
    field.corner10.set(GROUND_WIDTH, 0, -GROUND_WIDTH);
    field.corner01.set(-GROUND_WIDTH, 0, GROUND_WIDTH);
    field.corner11.set(GROUND_WIDTH, 0, GROUND_WIDTH);
    field.color00.set(0, 1, 0, 1);
    field.color01.set(0, 1, 0, 1);
    field.color10.set(0, 1, 0, 1);
    field.color11.set(0, 1, 0, 1);
    field.magnitude.set(0f, 5f, 0f);
    field.update();
    return field;
  }

  private Renderable generateGround(final HeightField field, final Texture groundTexture) {
    final Renderable ground = new Renderable();
    ground.environment = environment;
    ground.meshPart.mesh = field.mesh;
    ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
    ground.meshPart.offset = 0;
    ground.meshPart.size = field.mesh.getNumIndices();
    ground.meshPart.update();
    ground.material = new Material(TextureAttribute.createDiffuse(groundTexture));
    return ground;
  }

  private Model createAxes() {

    final ModelBuilder modelBuilder = new ModelBuilder();
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
    return modelBuilder.end();
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {
    inputController.update();
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, BG_COLOR.a);

    modelBatch.begin(camera);
    if (SHOW_AXES) modelBatch.render(axesInstance);
    modelBatch.render(ground);
    modelBatch.end();

    batch.begin();
    font.draw(batch, String.format("Camera pos: %f %f %f", camera.position.x, camera.position.y, camera.position.z), 10, 50);
    font.draw(batch, String.format("Camera dir: %f %f %f", camera.direction.x, camera.direction.y, camera.direction.z), 10, 20);
    font.draw(batch, String.format("Java Heap %fMB", Gdx.app.getJavaHeap() / 1E6), 10, game.getHeight() - 10);
    font.draw(batch, String.format("Frame Rate %d", Gdx.graphics.getFramesPerSecond()), game.getWidth() - 150, game.getHeight() - 10);


    batch.end();
    camera.update();
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void hide() {

  }

  @Override
  public void dispose() {
    modelInstance.model.dispose();
    heightField.dispose();
    modelBatch.dispose();
    batch.dispose();
    font.dispose();
    groundTexture.dispose();
  }
}
