/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
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
package com.nostra13.universalimageloader.core.assist;

/**
 * 展示 图片 加载 和 展示失败的原因
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class FailReason {

	private final FailType type;

	private final Throwable cause;

	public FailReason(FailType type, Throwable cause) {
		this.type = type;
		this.cause = cause;
	}

	/** @return {@linkplain FailType Fail type} */
	public FailType getType() {
		return type;
	}

	/** @return Thrown exception/error, can be <b>null</b> */
	public Throwable getCause() {
		return cause;
	}

	/** 展示加载图片失败的原因 */
	public static enum FailType {
		/** 输入/输出错误. 可能的原因是 1. 由于网络网络连接失败 或 出错 2. 在文件系统缓存图片出错. */
		IO_ERROR,
		/**
		 * 将 图片 解码成 Birmap 时出错 (BitmapFactory.decodeStream).
		 * {@linkplain android.graphics.BitmapFactory#decodeStream(java.io.InputStream, android.graphics.Rect, android.graphics.BitmapFactory.Options)
		 */
		DECODING_ERROR,
		/**
		 * 网络下载被拒绝, 需要的图片在这之前没有会被缓存到磁盘缓存中
		 * {@linkplain com.nostra13.universalimageloader.core.ImageLoader#denyNetworkDownloads(boolean) 网络下载被拒绝}
		 */
		NETWORK_DENIED,
		/** 没有足够的内存去为 图片 创建足够需要的 Bitmap. */
		OUT_OF_MEMORY,
		/** 在加载图片时发生的未知错误. */
		UNKNOWN
	}
}