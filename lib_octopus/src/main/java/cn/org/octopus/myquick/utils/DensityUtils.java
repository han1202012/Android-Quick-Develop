package cn.org.octopus.myquick.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

/**
 * 常用单位转换的辅助类
 * 
 * @author 韩曙亮
 * 
 */
public class DensityUtils
{
	private DensityUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * dp转px
	 * 
	 * @param context
	 * @param dpVal
	 * @return
	 */
	public static int dp2px(Context context, float dpVal)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, context.getResources().getDisplayMetrics());
	}

	/**
	 * sp转px
	 * 
	 * @param context
	 * @param spVal
	 * @return
	 */
	public static int sp2px(Context context, float spVal)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVal, context.getResources().getDisplayMetrics());
	}

	/**
	 * px转dp
	 * 
	 * @param context
	 * @param pxVal
	 * @return
	 */
	public static float px2dp(Context context, float pxVal)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (pxVal / scale);
	}

	/**
	 * px转sp
	 * 
	 * @param context
	 * @param pxVal
	 * @return
	 */
	public static float px2sp(Context context, float pxVal)
	{
		return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
	}

	/**
	 * 根据上下文对象获取屏幕的高度
	 * @param context
	 * @return
	 */
	public static float getScreenHeight(Context context){
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}

	/**
	 * 根据上下文对象获取屏幕的宽度
	 * @param context
	 * @return
	 */
	public static float getScreenWidtht(Context context){
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		return width;
	}


	/**
	 * 根据给定的 Activity 获取屏幕的宽度
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Activity context){
		Display disp = context.getWindowManager().getDefaultDisplay();
		Point outP = new Point();
		disp.getSize(outP);
		int width = outP.x ;
		int height = outP.y;
		return width;
	}

	/**
	 * 根据给定的 Activity 获取屏幕的高度
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Activity context){
		Display disp = context.getWindowManager().getDefaultDisplay();
		Point outP = new Point();
		disp.getSize(outP);
		int width = outP.x ;
		int height = outP.y;
		return height;
	}

	/**
	 * 应用区域宽度
	 * 获取应用区域 去除了手机最上层的系统状态栏 (包含 电池 网络 信号 蓝牙 连接 等系统状态信息)
	 * 包含 页面内容 和 标题栏信息的长度
	 * @param activity
	 * @return
	 */
	public static int getApplicationWidth(Activity activity){
		Rect outRect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
		int width = outRect.width() ;
		int height = outRect.height();
		return width;
	}

	/**
	 * 应用区域高度
	 * 获取应用区域 去除了手机最上层的系统状态栏 (包含 电池 网络 信号 蓝牙 连接 等系统状态信息)
	 * 包含 页面内容 和 标题栏信息的长度
	 * @param activity
	 * @return
	 */
	public static int getApplicationHeight(Activity activity){
		Rect outRect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
		int width = outRect.width() ;
		int height = outRect.height();
		return height;
	}

	/**
	 * 用户绘制区域宽度
	 * 去除了 系统状态栏 和 标题栏剩下的内容
	 * @param activity
	 * @return
	 */
	public static int getActivityContentWidth(Activity activity){
		// 用户绘制区域
		Rect outRect = new Rect();
		activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect);
		int width = outRect.width() ;
		int height = outRect.height();
		// end
		return width;
	}

	/**
	 * 用户绘制区域高度
	 * 去除了 系统状态栏 和 标题栏剩下的内容
	 * @param activity
	 * @return
	 */
	public static int getActivityContentHeight(Activity activity){
		// 用户绘制区域
		Rect outRect = new Rect();
		activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect);
		int width = outRect.width() ;
		int height = outRect.height();
		// end
		return height;
	}

}
