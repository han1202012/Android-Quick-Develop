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
 * 解码时的缩放类型
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.0
 */
public enum ImageScaleType {
	/** 图片不会被缩放 */
	NONE,

    /**
     * 只有当图片的大小比 2048x2048 大时, 图片才会被缩小
     * 如果 图片 需要被展示, 它的大小一定不能是这个大小 (另外你会收到 图片过大的异常信息 如下 :
     * OpenGLRenderer: Bitmap too large to be uploaded into a texture".)<br />
     * 图片会被以一个 数字 进行采样, 数字按照 1, 2, 3 ... 到设备所能展示的最多的 texture 数目.
     */
	NONE_SAFE,
	/**
     * 图片会被缩小 2 倍, 直到下一个步骤让图片缩小到指定的大小.<br />
     * 这是个快速处理的类型 并且 该类型倾向于在 List/Grid/Gallery (或者其它 AdapterView 子类)中使用.<br />
	 * 与 {@link android.graphics.BitmapFactory.Options#inSampleSize} 相关<br />
     * 注意 : 如果原始图片的大小比目标大小小, 原始图片不会被缩放;
	 */
	IN_SAMPLE_POWER_OF_2,
	/**
     * 图片会被按照一个数字采样 (该数字从 1, 2, 3, ...). 使用该类型对于内存节约很重要. <br/>
	 * 与 {@link android.graphics.BitmapFactory.Options#inSampleSize} 相关<br />
	 * 注意: 如果原始图片比目标图片小小, 那么原始图片不会被缩放.
	 */
	IN_SAMPLE_INT,
	/**
     * 图片会精确的被缩小到与目标大小相同 (宽度和高度的缩放后与目标大小都会相同, 即图片有可能失真, 这个基于 ImageView 的缩放类型).
     * 如果对于内存节约方面要求很精密时, 可以使用该类型.<br />
	 *
	 * 注意: 如果原始图片比目标图片小小, 那么原始图片不会被缩放.<br />
	 * <br />
     * 注意 : 对于创建一个最终的 Bitmap (或者确定的大小) , 会调用下面的方法创建额外的 Bitmap.
	 * {@link android.graphics.Bitmap#createBitmap(android.graphics.Bitmap, int, int, int, int, android.graphics.Matrix, boolean)
	 * Bitmap.createBitmap(...)}.<br />
	 * <b>优点 :</b> 通过在内存中缓存小图片来节省内存 (与 IN_SAMPLE... 缩放类型比较)<br />
	 * <b>缺点 :</b> 在一瞬间需要更多内存来创建最终图片.
	 */
	EXACTLY,
	/**
	 * 图片会精确的被缩小到与目标大小相同 (宽度和高度的缩放后与目标大小都会相同, 即图片有可能失真, 这个基于 ImageView 的缩放类型).
	 * 如果对于内存节约方面要求很精密时, 可以使用该类型.<br />
	 *
	 * <b>注意:</b> 如果原始图片比目标图片小小, <b>原始图片会被拉伸到</b> 目标大小.<br />
	 * <br />
	 * <b>注意:</b> 对于创建 精确大小的结果 Bitmap , 会从For creating result Bitmap (of exact size) additional Bitmap will be created with
	 * {@link android.graphics.Bitmap#createBitmap(android.graphics.Bitmap, int, int, int, int, android.graphics.Matrix, boolean)
	 * Bitmap.createBitmap(...)}.<br />
	 * <b>优点 :</b> 通过在内存中缓存小图片来节省内存 (与 IN_SAMPLE... 缩放类型比较)<br />
	 * <b>缺点 :</b> 在一瞬间需要更多内存来创建最终图片.
	 */
	EXACTLY_STRETCHED
}
