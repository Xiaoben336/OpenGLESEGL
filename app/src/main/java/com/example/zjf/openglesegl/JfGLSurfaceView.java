package com.example.zjf.openglesegl;

import android.content.Context;
import android.util.AttributeSet;

public class JfGLSurfaceView extends JfEGLSurfaceVIew {

	public JfGLSurfaceView(Context context) {
		this(context,null);
	}

	public JfGLSurfaceView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public JfGLSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setRender(new JfRender());
		setRenderMode(JfEGLSurfaceVIew.RENDERMODE_WHEN_DIRTY);
	}
}
