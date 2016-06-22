/*******************************************************************************
 * Copyright 2013-2014 Sergey Tarasevich
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
package com.nostra13.universalimageloader.core.decode;

import android.annotation.TargetApi;
import android.graphics.BitmapFactory.Options;
import android.os.Build;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

/**
 * 包含 将 图片 解码成 Bitmap 对象所需的信息
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.3
 */
public class ImageDecodingInfo {

	private final String imageKey;					//内存缓存中使用的 key
	private final String imageUri;					//图片的 URI 地址
	private final String originalImageUri;			//被传递给 ImageLoader 的最初的 图片 URI 地址
	private final ImageSize targetSize;				//解码后的图片目标大小

	private final ImageScaleType imageScaleType;	//图片缩放类型
	private final ViewScaleType viewScaleType;		//View 组件的缩放类型

	private final ImageDownloader downloader;		//图片加载时的下载器
	private final Object extraForDownloader;		//附加对象

	private final boolean considerExifParams;		//是否考虑 EXIF 参数, 含有相机的各项参数
	private final Options decodingOptions;			//解析成 Bitmap 对象的参数

	public ImageDecodingInfo(String imageKey, String imageUri, String originalImageUri, ImageSize targetSize, ViewScaleType viewScaleType,
							 ImageDownloader downloader, DisplayImageOptions displayOptions) {
		this.imageKey = imageKey;
		this.imageUri = imageUri;
		this.originalImageUri = originalImageUri;
		this.targetSize = targetSize;

		this.imageScaleType = displayOptions.getImageScaleType();
		this.viewScaleType = viewScaleType;

		this.downloader = downloader;
		this.extraForDownloader = displayOptions.getExtraForDownloader();

		considerExifParams = displayOptions.isConsiderExifParams();
		decodingOptions = new Options();
		copyOptions(displayOptions.getDecodingOptions(), decodingOptions);
	}

	private void copyOptions(Options srcOptions, Options destOptions) {
		destOptions.inDensity = srcOptions.inDensity;
		destOptions.inDither = srcOptions.inDither;
		destOptions.inInputShareable = srcOptions.inInputShareable;
		destOptions.inJustDecodeBounds = srcOptions.inJustDecodeBounds;
		destOptions.inPreferredConfig = srcOptions.inPreferredConfig;
		destOptions.inPurgeable = srcOptions.inPurgeable;
		destOptions.inSampleSize = srcOptions.inSampleSize;
		destOptions.inScaled = srcOptions.inScaled;
		destOptions.inScreenDensity = srcOptions.inScreenDensity;
		destOptions.inTargetDensity = srcOptions.inTargetDensity;
		destOptions.inTempStorage = srcOptions.inTempStorage;
		if (Build.VERSION.SDK_INT >= 10) copyOptions10(srcOptions, destOptions);
		if (Build.VERSION.SDK_INT >= 11) copyOptions11(srcOptions, destOptions);
	}

	@TargetApi(10)
	private void copyOptions10(Options srcOptions, Options destOptions) {
		destOptions.inPreferQualityOverSpeed = srcOptions.inPreferQualityOverSpeed;
	}

	@TargetApi(11)
	private void copyOptions11(Options srcOptions, Options destOptions) {
		destOptions.inBitmap = srcOptions.inBitmap;
		destOptions.inMutable = srcOptions.inMutable;
	}

	/** @return Original {@linkplain com.nostra13.universalimageloader.utils.MemoryCacheUtils#generateKey(String, ImageSize) image key} (在内存缓冲中使用). */
	public String getImageKey() {
		return imageKey;
	}

	/** @return 需要解码的图片 URI 地址 (通常是从磁盘缓存获取的图片) */
	public String getImageUri() {
		return imageUri;
	}

	/** @return 被传递给 ImageLoader 的最初的 图片 URI 地址 */
	public String getOriginalImageUri() {
		return originalImageUri;
	}

	/**
	 * @return 图片的目标大小. 根据  {@linkplain ImageScaleType
	 * image scale type} 和 {@linkplain ViewScaleType view scale type} 解码后的 Bitmap 对象的图片大小应该接近这个大小.
	 */
	public ImageSize getTargetSize() {
		return targetSize;
	}

	/**
	 * @return {@linkplain ImageScaleType 图片采样和缩放比例的 缩放类型}. 该参数影响解码后的 Bitmap 对象的大小.
	 */
	public ImageScaleType getImageScaleType() {
		return imageScaleType;
	}

	/** @return {@linkplain ViewScaleType View 组件的缩放类型}. 该参数影响解码后的 Bitmap 对象的大小. */
	public ViewScaleType getViewScaleType() {
		return viewScaleType;
	}

	/** @return 图片加载时的下载器 */
	public ImageDownloader getDownloader() {
		return downloader;
	}

	/** @return 下载器的附加对象 */
	public Object getExtraForDownloader() {
		return extraForDownloader;
	}

	/** @return <b>true</b> - 是否考虑 EXIF 参数; <b>false</b> - otherwise */
	public boolean shouldConsiderExifParams() {
		return considerExifParams;
	}

	/** @return 解码的相关选项 */
	public Options getDecodingOptions() {
		return decodingOptions;
	}
}