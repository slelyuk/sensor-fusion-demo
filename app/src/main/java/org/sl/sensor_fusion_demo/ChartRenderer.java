/**
 * Copyright (C) 2015 Stas Lelyuk Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sl.sensor_fusion_demo;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ChartRenderer implements GLSurfaceView.Renderer {
  private Square mSquare;

  public ChartRenderer() {
    mSquare = new Square();
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    // Set the background color to black ( rgba ).
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
    // Enable Smooth Shading, default not really needed.
    gl.glShadeModel(GL10.GL_SMOOTH);
    // Depth buffer setup.
    gl.glClearDepthf(1.0f);
    // Enables depth testing.
    gl.glEnable(GL10.GL_DEPTH_TEST);
    // The type of depth testing to do.
    gl.glDepthFunc(GL10.GL_LEQUAL);
    // Really nice perspective calculations.
    gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    // Clears the screen and depth buffer.
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    // Replace the current matrix with the identity matrix
    gl.glLoadIdentity();
    // Translate to end up under the flat square.
    gl.glTranslatef(0, -3f, 0);
    // Draw our smooth square.
    mSquare.draw(gl);
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    // Sets the current view port to the new size.
    gl.glViewport(0, 0, width, height);
    // Select the projection matrix
    gl.glMatrixMode(GL10.GL_PROJECTION);
    // Reset the projection matrix
    gl.glLoadIdentity();
    // Calculate the aspect ratio of the window
    GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
        100.0f);
    // Select the modelview matrix
    gl.glMatrixMode(GL10.GL_MODELVIEW);
    // Reset the modelview matrix
    gl.glLoadIdentity();
  }
}
