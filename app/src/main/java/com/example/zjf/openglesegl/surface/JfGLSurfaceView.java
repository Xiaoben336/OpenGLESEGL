package com.example.zjf.openglesegl.surface;

import android.content.Context;
import android.util.AttributeSet;

import com.example.zjf.openglesegl.JfEGLSurfaceView;

public class JfGLSurfaceView extends JfEGLSurfaceView {

	public JfGLSurfaceView(Context context) {
		this(context,null);
	}

	public JfGLSurfaceView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public JfGLSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setRender(new JfRender());
		setRenderMode(JfEGLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
}
