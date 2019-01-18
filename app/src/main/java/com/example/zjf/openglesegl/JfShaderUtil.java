package com.example.zjf.openglesegl;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JfShaderUtil {
	/**
	 * 得到shade代码
	 * @param context
	 * @param rawId
	 * @return
	 */
	public static String getRawResource(Context context,int rawId){
		InputStream inputStream = context.getResources().openRawResource(rawId);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}


	/**
	 * 创建渲染程序
	 * @param vertexSource
	 * @param fragmentSoruce
	 * @return
	 */
	public static int createProgram(String vertexSource, String fragmentSoruce){
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexSource);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSoruce);

		if (vertexShader != 0 && fragmentShader != 0) {
			//4、创建一个渲染程序
			int program = GLES20.glCreateProgram();
			//5、将着色器程序添加到渲染程序中
			GLES20.glAttachShader(program,vertexShader);
			GLES20.glAttachShader(program,fragmentShader);
			//6、链接源程序
			GLES20.glLinkProgram(program);
			return program;
		}
		return 0;
	}


	/**
	 * 加载着色器源码
	 * @param shadeType	顶点、片元
	 * @param source
	 * @return
	 */
	public static int loadShader(int shadeType,String source){
		//1、创建shader（着色器：顶点或片元）
		int shader = GLES20.glCreateShader(shadeType);
		if (shader != 0) {
			//2、加载shader源码并编译shader
			GLES20.glShaderSource(shader, source);
			GLES20.glCompileShader(shader);

			//3、检查是否编译成功
			int[] compile = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,compile,0);

			if (compile[0] != GLES20.GL_TRUE) {
				Log.e("zjf","编译失败");
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
			return shader;
		} else {
			return 0;
		}
	}
}
