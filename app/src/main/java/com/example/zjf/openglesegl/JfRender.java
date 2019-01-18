package com.example.zjf.openglesegl;

import android.opengl.GLES20;
import android.util.Log;

public class JfRender implements JfEGLSurfaceVIew.JfGLRender {

	public JfRender(){

	}
	@Override
	public void onSurfaceCreated() {
		Log.d("render","onSurfaceCreated");
	}

	@Override
	public void onSurfaceChanged(int width, int height) {
		Log.d("render","onSurfaceChanged");
		GLES20.glViewport(0,0,width,height);
	}

	@Override
	public void onDrawFrame() {
		Log.d("render","onDrawFrame");
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glClearColor(1.0f,0.0f,0.0f,0.5f);
	}
}
