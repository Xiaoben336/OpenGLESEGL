package com.example.zjf.openglesegl;

import android.content.Context;
import android.util.AttributeSet;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLContext;


public abstract class JfEGLSurfaceVIew extends SurfaceView implements SurfaceHolder.Callback {
	private Surface surface;
	private EGLContext eglContext;

	private JfEGLThread jfEGLThread;
	private JfGLRender jfGLRender;

	public final static int RENDERMODE_WHEN_DIRTY = 0;
	public final static int RENDERMODE_CONTINUOUSLY = 1;
	private int mRenderMode = RENDERMODE_CONTINUOUSLY;//默认自动刷新

	public JfEGLSurfaceVIew(Context context) {
		this(context,null);
	}

	public JfEGLSurfaceVIew(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public JfEGLSurfaceVIew(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		getHolder().addCallback(this);
	}

	public void setSurfaceAndEglContext(Surface surface,EGLContext eglContext){
		this.surface = surface;
		this.eglContext = eglContext;
	}


	public void setRender(JfGLRender jfGLRender) {
		this.jfGLRender = jfGLRender;
	}


	public void setRenderMode(int mRenderMode) {
		if (jfGLRender == null) {
			throw new RuntimeException("must set render before");
		}
		this.mRenderMode = mRenderMode;
	}

	public EGLContext getEglContext() {
		if (jfEGLThread != null) {
			return jfEGLThread.getEglContext();
		}
		return null;
	}

	public void requestRender(){
		if (jfEGLThread != null) {
			jfEGLThread.requestRender();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (surface == null) {
			surface = holder.getSurface();
		}

		jfEGLThread = new JfEGLThread(new WeakReference<JfEGLSurfaceVIew>(this));
		jfEGLThread.isCreate = true;
		jfEGLThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		jfEGLThread.width = width;
		jfEGLThread.height = height;
		jfEGLThread.isChange = true;

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		jfEGLThread.onDestroy();
		jfEGLThread = null;
		surface = null;
		eglContext = null;
	}


	public interface JfGLRender{
		void onSurfaceCreated();
		void onSurfaceChanged(int width,int height);
		void onDrawFrame();
	}


	static class JfEGLThread extends Thread{
		private WeakReference<JfEGLSurfaceVIew> jfEGLSurfaceVIewWeakReference = null;
		private EglHelper eglHelper = null;
		private Object object = null;

		private boolean isExit = false;
		private boolean isCreate = false;
		private boolean isChange = false;
		private boolean isStart = false;

		private int width;
		private int height;

		public JfEGLThread(WeakReference<JfEGLSurfaceVIew> jfEGLSurfaceVIewWeakReference) {
			this.jfEGLSurfaceVIewWeakReference = jfEGLSurfaceVIewWeakReference;
		}

		@Override
		public void run() {
			super.run();
			//绘制操作
			isExit = false;
			isStart = false;
			object = new Object();
			eglHelper = new EglHelper();

			eglHelper.initEgl(jfEGLSurfaceVIewWeakReference.get().surface,jfEGLSurfaceVIewWeakReference.get().eglContext);

			while (true) {
				//Log.d("run","绘制中");
				if (isExit) {
					//释放资源
					release();
					break;
				}

				if (isStart) {
					if (jfEGLSurfaceVIewWeakReference.get().mRenderMode == RENDERMODE_WHEN_DIRTY){//手动刷新
						synchronized (object) {
							try {
								object.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} else if (jfEGLSurfaceVIewWeakReference.get().mRenderMode == RENDERMODE_CONTINUOUSLY) {
						try {
							Thread.sleep(1000 / 60);//每秒刷新60帧
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						throw new RuntimeException("RenderMode is wrong");
					}
				}
				onCreate();
				onChange(width,height);
				onDraw();

				isStart = true;
			}
		}

		private void onCreate(){
			if (isCreate && jfEGLSurfaceVIewWeakReference.get().jfGLRender != null) {
				isCreate = false;
				jfEGLSurfaceVIewWeakReference.get().jfGLRender.onSurfaceCreated();
			}
		}

		private void onChange(int width,int height){
			if (isChange && jfEGLSurfaceVIewWeakReference.get().jfGLRender != null) {
				isChange = false;
				jfEGLSurfaceVIewWeakReference.get().jfGLRender.onSurfaceChanged(width,height);
			}
		}

		private void onDraw(){
			if (jfEGLSurfaceVIewWeakReference.get().jfGLRender != null && eglHelper != null) {
				jfEGLSurfaceVIewWeakReference.get().jfGLRender.onDrawFrame();
				if (!isStart){
					jfEGLSurfaceVIewWeakReference.get().jfGLRender.onDrawFrame();
				}

				eglHelper.swapBuffers();
			}
		}

		private void requestRender(){
			if (object != null) {
				synchronized (object) {
					object.notifyAll();
				}
			}
		}


		public void onDestroy(){
			isExit = true;
			requestRender();
		}


		private void release(){
			if (eglHelper != null){
				eglHelper.destroyEgl();
				eglHelper = null;
				object = null;
				jfEGLSurfaceVIewWeakReference = null;
			}
		}

		public EGLContext getEglContext(){
			if (eglHelper != null) {
				return eglHelper.getEglContext();
			}
			return null;
		}
	}
}
