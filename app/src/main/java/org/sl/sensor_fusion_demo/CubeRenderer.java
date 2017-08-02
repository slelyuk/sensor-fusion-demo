package org.sl.sensor_fusion_demo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import org.sl.sensor_fusion_demo.orientationProvider.OrientationProvider;
import org.sl.sensor_fusion_demo.representation.Quaternion;

/**
 * Class that implements the rendering of a cube with the current rotation of the device that is provided by a
 * OrientationProvider
 *
 * @author Stanislav Lelyuk
 */
public class CubeRenderer implements GLSurfaceView.Renderer {
  /**
   * The colour-cube that is drawn repeatedly
   */
  private Cube mCube;

  /**
   * For updating info view.
   */
  private OnDrawFrameListener drawFrameListener = null;
  /**
   * The current provider of the device orientation.
   */
  private OrientationProvider orientationProvider = null;
  /**
   * Flag indicating whether you want to view inside out, or outside in
   */
  private boolean showCubeInsideOut = true;

  /**
   * Initialises a new CubeRenderer
   */
  public CubeRenderer() {
    mCube = new Cube();
  }

  /**
   * Sets the orientationProvider of this renderer. Use this method to change which sensor fusion should be currently
   * used for rendering the cube. Simply exchange it with another orientationProvider and the cube will be rendered
   * with another approach.
   *
   * @param orientationProvider The new orientation provider that delivers the current orientation of the device
   */
  public void setOrientationProvider(OrientationProvider orientationProvider) {
    this.orientationProvider = orientationProvider;
  }

  public void setDrawFrameListener(OnDrawFrameListener drawFrameListener) {
    this.drawFrameListener = drawFrameListener;
  }

  /**
   * Perform the actual rendering of the cube for each frame
   *
   * @param gl The surface on which the cube should be rendered
   */
  public void onDrawFrame(GL10 gl) {
    // clear screen
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    gl.glClearColor(255f, 255f, 255f, 1f);

    // set-up modelview matrix
    gl.glMatrixMode(GL10.GL_MODELVIEW);
    gl.glLoadIdentity();

    if (showCubeInsideOut) {
      float dist = 3;
      gl.glTranslatef(0, 0, -dist);

      if (orientationProvider != null) {
        // All Orientation providers deliver Quaternion as well as rotation matrix.
        // Use your favourite representation:

        // Get the rotation from the current orientationProvider as rotation matrix
        //gl.glMultMatrixf(orientationProvider.getRotationMatrix().getMatrix(), 0);

        // Get the rotation from the current orientationProvider as quaternion
        Quaternion q = orientationProvider.getQuaternion();
        gl.glRotatef((float) (2.0f * Math.acos(q.getW()) * 180.0f / Math.PI), q.getX(), q.getY(), q.getZ());
      }

      // draw our object
      gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
      gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

      mCube.draw(gl);
    } else {

      if (orientationProvider != null) {
        // All Orientation providers deliver Quaternion as well as rotation matrix.
        // Use your favourite representation:

        // Get the rotation from the current orientationProvider as rotation matrix
        //gl.glMultMatrixf(orientationProvider.getRotationMatrix().getMatrix(), 0);

        // Get the rotation from the current orientationProvider as quaternion
        Quaternion q = orientationProvider.getQuaternion();
        gl.glRotatef((float) (2.0f * Math.acos(q.getW()) * 180.0f / Math.PI), q.getX(), q.getY(), q.getZ());
      }

      float dist = 3;
      drawTranslatedCube(gl, 0, 0, -dist);
      drawTranslatedCube(gl, 0, 0, dist);
      drawTranslatedCube(gl, 0, -dist, 0);
      drawTranslatedCube(gl, 0, dist, 0);
      drawTranslatedCube(gl, -dist, 0, 0);
      drawTranslatedCube(gl, dist, 0, 0);
    }

    // draw our object
    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

    mCube.draw(gl);

    if (drawFrameListener != null) {
      drawFrameListener.onDrawFrame();
    }
  }

  /**
   * Draws a translated cube
   *
   * @param gl the surface
   * @param translateX x-translation
   * @param translateY y-translation
   * @param translateZ z-translation
   */
  private void drawTranslatedCube(GL10 gl, float translateX, float translateY, float translateZ) {
    gl.glPushMatrix();
    gl.glTranslatef(translateX, translateY, translateZ);

    // draw our object
    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

    mCube.draw(gl);
    gl.glPopMatrix();
  }

  /**
   * Update view-port with the new surface
   *
   * @param gl the surface
   * @param width new width
   * @param height new height
   */
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    // set view-port
    gl.glViewport(0, 0, width, height);
    // set projection matrix
    float ratio = (float) width / height;
    gl.glMatrixMode(GL10.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    // dither is enabled by default, we don't need it
    gl.glDisable(GL10.GL_DITHER);
    // clear screen in black
    gl.glClearColor(0, 0, 0, 1);
  }

  /**
   * Toggles whether the cube will be shown inside-out or outside in.
   */
  public void toggleShowCubeInsideOut() {
    this.showCubeInsideOut = !showCubeInsideOut;
  }

  protected void drawText(GL10 gl, String text) {
    // Create an empty, mutable bitmap
    Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_4444);
    // get a canvas to paint over the bitmap
    Canvas canvas = new Canvas(bitmap);
    bitmap.eraseColor(0);

    // get a background image from resources
    // note the image format must match the bitmap format
    ColorDrawable background = new ColorDrawable(0xFFFFFFFF);
    background.setBounds(0, 0, 256, 256);
    background.draw(canvas); // draw the background to our bitmap

    // Draw the text
    Paint textPaint = new Paint();
    textPaint.setTextSize(32);
    textPaint.setAntiAlias(true);
    textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
    // draw the text centered
    canvas.drawText(text, 16, 112, textPaint);

    //Generate one texture pointer...
    int textures[] = new int[1];
    gl.glGenTextures(1, textures, 0);
    //...and bind it to our array
    gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

    //Create Nearest Filtered Texture
    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

    //Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

    //Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

    //Clean up
    bitmap.recycle();
  }
}
