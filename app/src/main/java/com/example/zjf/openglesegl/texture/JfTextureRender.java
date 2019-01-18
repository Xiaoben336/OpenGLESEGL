package com.example.zjf.openglesegl.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;


import com.example.zjf.openglesegl.JfEGLSurfaceView;
import com.example.zjf.openglesegl.JfShaderUtil;
import com.example.zjf.openglesegl.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class JfTextureRender implements JfEGLSurfaceView.JfGLRender {
	private Context context;

	//顶点坐标
	private float[] vertexData = {
			-1f,-1f,
			1f,-1f,
			-1f,1f,
			1f,1f
	};
	private FloatBuffer vertexBuffer;

	//纹理坐标
	private float[] fragmentData = {
//			0f,1f,
//			1f,1f,
//			0f,0f,
//			1f,0f
			0f,0.5f,
			0.5f,0.5f,
			0f,0f,
			0.5f,0f
	};
	private FloatBuffer fragmentBuffer;


	private int program;//程序
	private int vPosition;//顶点着色器属性
	private int fPosition;//片元着色器属性
	private int textureId;
	private int sampler;

	public JfTextureRender(Context context){
		this.context = context;
		vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(vertexData);
		vertexBuffer.position(0);

		fragmentBuffer = ByteBuffer.allocateDirect(fragmentData.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(fragmentData);
		fragmentBuffer.position(0);
	}
	@Override
	public void onSurfaceCreated() {
		//获取源码
		String vertexSource = JfShaderUtil.getRawResource(context,R.raw.vertex_shader);
		String fragmentSource = JfShaderUtil.getRawResource(context,R.raw.fragment_shader);
		//前6步完成
		program = JfShaderUtil.createProgram(vertexSource,fragmentSource);

		//得到着色器中的属性
		vPosition = GLES20.glGetAttribLocation(program,"v_Position");
		fPosition = GLES20.glGetAttribLocation(program,"f_Position");
		sampler = GLES20.glGetUniformLocation(program,"sTexture");
		//开始绘制纹理,只绘制一个图片纹理
		int[] textureIds = new int[1];

		GLES20.glGenTextures(1,textureIds,0);//创建纹理
		textureId = textureIds[0];

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);//绑定纹理

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glUniform1i(sampler,0);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.fengjing);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
		bitmap.recycle();
		bitmap = null;
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);//解绑纹理
	}

	@Override
	public void onSurfaceChanged(int width, int height) {
		GLES20.glViewport(0,0,width,height);
	}

	@Override
	public void onDrawFrame() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glClearColor(1f,0f,0f,1f);

		//使用源程序
		GLES20.glUseProgram(program);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);//再次绑定纹理

		//使顶点属性数组有效
		GLES20.glEnableVertexAttribArray(vPosition);
		//为顶点属性赋值
		GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
				vertexBuffer);
		GLES20.glEnableVertexAttribArray(fPosition);
		GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
				fragmentBuffer);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);//再次解绑纹理
	}
}
