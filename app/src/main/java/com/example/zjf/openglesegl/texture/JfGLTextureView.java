package com.example.zjf.openglesegl.texture;

import android.content.Context;
import android.util.AttributeSet;

import com.example.zjf.openglesegl.JfEGLSurfaceView;

public class JfGLTextureView extends JfEGLSurfaceView {
	public JfGLTextureView(Context context) {
		this(context,null);
	}

	public JfGLTextureView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public JfGLTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setRender(new JfTextureRender(context));
	}
}
