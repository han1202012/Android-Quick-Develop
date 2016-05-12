/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.core.listener;

import android.graphics.Bitmap;
import android.view.View;
import com.nostra13.universalimageloader.core.assist.FailReason;

/**
 * 图片加载过程监听器.<br />
 * 你可以使用 SimpleImageLoadingListener 类, 只实现需要的方法.  {@link SimpleImageLoadingListener} .
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see SimpleImageLoadingListener
 * @see com.nostra13.universalimageloader.core.assist.FailReason
 * @since 1.0.0
 */
public interface ImageLoadingListener {

	/**
	 * 当图片加载任务开始时回调
	 *
	 * @param imageUri 被加载图片的 URI 地址
	 * @param view     展示图片的 View 组件
	 */
	void onLoadingStarted(String imageUri, View view);

	/**
	 * 当图片加载过程中出现错误时回调
	 *
	 * @param imageUri   被加载图片的 URI 地址
	 * @param view       展示图片的 View 组件. 可以是 <b>null</b>.
	 * @param failReason 为什么图片加载失败的{@linkplain com.nostra13.universalimageloader.core.assist.FailReason 原因}
	 */
	void onLoadingFailed(String imageUri, View view, FailReason failReason);

	/**
	 * 当图片加载成功时回调 (并且已经在 View 中展示出来, 如果 View 被指定)
	 *
	 * @param imageUri    被加载图片的 URI 地址
	 * @param view        展示图片的 View 组件. 可以是 <b>null</b>.
	 * @param loadedImage 加载 和 解码 图片后的 Bitmap 对象
	 */
	void onLoadingComplete(String imageUri, View view, Bitmap loadedImage);

	/**
	 * 如果展示图片的 View 在其它新的任务中被复用了, 此时加载图片任务被取消 回调该方法
	 *
	 * @param imageUri 被加载图片的 URI 地址
	 * @param view     展示图片的 View 组件. 可以是 <b>null</b>.
	 */
	void onLoadingCancelled(String imageUri, View view);
}
