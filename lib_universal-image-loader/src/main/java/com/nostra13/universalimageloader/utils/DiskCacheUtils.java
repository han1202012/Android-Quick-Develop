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
package com.nostra13.universalimageloader.utils;

import com.nostra13.universalimageloader.cache.disc.DiskCache;

import java.io.File;

/**
 * 方便执行磁盘缓存工作的实用工具 .<br />
 * <b>注意:</b> 避免在应用主线程 使用该针对文件系统的功能.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.0
 */
public final class DiskCacheUtils {

	private DiskCacheUtils() {
	}

	/** 返回被缓存的图片文件 {@link File}, 如果图片没有在磁盘中缓存, 返回 <b>null</b> */
	public static File findInCache(String imageUri, DiskCache diskCache) {
		File image = diskCache.get(imageUri);
		return image != null && image.exists() ? image : null;
	}

	/**
	 * 从磁盘缓存中移除被缓存的图片文件 (如果图片之前被缓存到磁盘中)
	 *
	 * @return <b>true</b> - 如果被缓存的图片存在并且已经成功删除; <b>false</b> - 其它情况.
	 */
	public static boolean removeFromCache(String imageUri, DiskCache diskCache) {
		File image = diskCache.get(imageUri);
		return image != null && image.exists() && image.delete();
	}
}
