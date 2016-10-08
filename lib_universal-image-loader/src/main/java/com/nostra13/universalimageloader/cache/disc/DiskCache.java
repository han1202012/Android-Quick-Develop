/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
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
package com.nostra13.universalimageloader.cache.disc;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 硬盘缓存接口
 * 提供的方法 :
 * -- 1. 获取硬盘缓存根目录
 * -- 2. 根据图片 URI 地址获取被缓存的图片文件的 File 对象
 * -- 3. 保存图片到硬盘缓存
 * -- 4. 关闭硬盘缓存
 * -- 5. 清除硬盘缓存
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.2
 */
public interface DiskCache {
	/**
	 * 返回硬盘缓存的根目录
	 *
	 * @return 硬盘缓存根目录
	 */
	File getDirectory();

	/**
	 * 返回被缓存图片的 File 对象
	 *
	 * @param imageUri 图片的原始 URI 地址
	 * @return 被缓存图片的 File 对象, 如果 图片没有被缓存, 返回 null.
	 */
	File get(String imageUri);

	/**
	 * 保存图片流到硬盘缓存中.
	 * 输入的图片流不应该在该方法中关闭.
	 *
	 * @param imageUri    图片的原始 URI 地址
	 * @param imageStream 图片输入流 (不应该在该方法中关闭输入流)
	 * @param listener    保存进程监听器, 如果不使用 {@linkplain com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener
	 *                    progress listener} 监听器在图片加载回调时, 可以忽略该参数
	 * @return <b>true</b> - 如果文件被成功保存; <b>false</b> - 如果文件没有保存到硬盘缓存中.
	 * @throws java.io.IOException
	 */
	boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException;

	/**
	 * 保存图片到硬盘缓存中.
	 *
	 * @param imageUri 原始图片 URI 地址
	 * @param bitmap   Bitmap 图片
	 * @return <b>true</b> - 如果图片被成功保存; <b>false</b> - 如果图片没有被保存到硬盘缓存中.
	 * @throws IOException
	 */
	boolean save(String imageUri, Bitmap bitmap) throws IOException;

	/**
	 * 移除传入的 URI 地址(参数) 相关图片文件
	 *
	 * @param imageUri 图片 URI 地址
	 * @return <b>true</b> - 如果文件被成功删除; <b>false</b> - 如果传入的 URI 地址对应的图片文件不存在, 或者图片文件不能被删除
	 */
	boolean remove(String imageUri);

	/** 关闭硬盘缓存, 释放资源. */
	void close();

	/** 清除硬盘缓存. */
	void clear();
}
