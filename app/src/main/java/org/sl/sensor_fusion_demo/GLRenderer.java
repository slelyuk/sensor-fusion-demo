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
import android.util.Log;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class GLRenderer implements GLSurfaceView.Renderer {

  private boolean mFirstDraw;
  private boolean mSurfaceCreated;
  private int mWidth;
  private int mHeight;
  private long mLastTime;
  private int mFPS;

  public GLRenderer() {
    mFirstDraw = true;
    mSurfaceCreated = false;
    mWidth = -1;
    mHeight = -1;
    mLastTime = System.currentTimeMillis();
    mFPS = 0;
  }

  @Override
  public void onSurfaceCreated(GL10 notUsed,
      EGLConfig config) {
    if (BuildConfig.DEBUG) {
      Log.i("GLRenderer", "Surface created.");
    }
    mSurfaceCreated = true;
    mWidth = -1;
    mHeight = -1;
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width,
      int height) {
    if (!mSurfaceCreated && width == mWidth
        && height == mHeight) {
      if (BuildConfig.DEBUG) {
        Log.i("GLRenderer",
            "Surface changed but already handled.");
      }
      return;
    }
    if (BuildConfig.DEBUG) {
      // Android honeycomb has an option to keep the
      // context.
      String msg = "Surface changed width:" + width
          + " height:" + height;
      if (mSurfaceCreated) {
        msg += " context lost.";
      } else {
        msg += ".";
      }
      Log.i("GLRenderer", msg);
    }

    mWidth = width;
    mHeight = height;

    onCreate(mWidth, mHeight, mSurfaceCreated);
    mSurfaceCreated = false;
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    onDrawFrame(gl, mFirstDraw);

    if (BuildConfig.DEBUG) {
      mFPS++;
      long currentTime = System.currentTimeMillis();
      if (currentTime - mLastTime >= 1000) {
        mFPS = 0;
        mLastTime = currentTime;
      }
    }

    if (mFirstDraw) {
      mFirstDraw = false;
    }
  }

  public int getFPS() {
    return mFPS;
  }

  public abstract void onCreate(int width, int height,
      boolean contextLost);

  public abstract void onDrawFrame(GL10 gl, boolean firstDraw);
}