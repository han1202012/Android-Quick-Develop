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
package com.nostra13.universalimageloader.core.download;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ContentLengthInputStream;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 根据 URI 地址获取图片的输入流, 从网络 或 文件系统 或 app 资源中获取. <br />
 * {@link URLConnection} 被用于从网络中获取 图片输入流.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.0
 */
public class BaseImageDownloader implements ImageDownloader {
	/** {@value} */
	public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // 单位 : 毫秒
	/** {@value} */
	public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // 单位 : 毫秒

	/** {@value} */
	protected static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
	/** {@value} */
	protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

	protected static final int MAX_REDIRECT_COUNT = 5;

	protected static final String CONTENT_CONTACTS_URI_PREFIX = "content://com.android.contacts/";

	private static final String ERROR_UNSUPPORTED_SCHEME = "UIL doesn't support scheme(protocol) by default [%s]. " + "You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))";

	protected final Context context;
	protected final int connectTimeout;
	protected final int readTimeout;

	public BaseImageDownloader(Context context) {
		this(context, DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT);
	}

	public BaseImageDownloader(Context context, int connectTimeout, int readTimeout) {
		this.context = context.getApplicationContext();
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
	}

	/**
	 * 根据 Uri 获取输入流
	 * @param imageUri 图片 URI
	 * @param extra    传递给 Builder.extraForDownloader 或 DisplayImageOptions.extraForDownloader 的辅助类, 可以是 null.
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public InputStream getStream(String imageUri, Object extra) throws IOException {
		switch (Scheme.ofUri(imageUri)) {
			case HTTP:
			case HTTPS:
				return getStreamFromNetwork(imageUri, extra);
			case FILE:
				return getStreamFromFile(imageUri, extra);
			case CONTENT:
				return getStreamFromContent(imageUri, extra);
			case ASSETS:
				return getStreamFromAssets(imageUri, extra);
			case DRAWABLE:
				return getStreamFromDrawable(imageUri, extra);
			case UNKNOWN:
			default:
				return getStreamFromOtherSource(imageUri, extra);
		}
	}

	/**
	 * 根据 URI 地址获取 InputStream 输入流 (图片在网络上).
	 *
	 * @param imageUri 图片 URI 地址
	 * @param extra    被传递给 {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *                 DisplayImageOptions.extraForDownloader(Object)} 的辅助类; 可以为 null
	 * @return {@link InputStream} of image
	 * @throws IOException 如果在网络请求时发生了 I/O 错误, 或者 从 URI 地址不能创建输入流.
	 */
	protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
		HttpURLConnection conn = createConnection(imageUri, extra);

		int redirectCount = 0;
		while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
			conn = createConnection(conn.getHeaderField("Location"), extra);
			redirectCount++;
		}

		InputStream imageStream;
		try {
			imageStream = conn.getInputStream();
		} catch (IOException e) {
			// 读取所有的数据 允许复用连接 (http://bit.ly/1ad35PY)
			IoUtils.readAndCloseStream(conn.getErrorStream());
			throw e;
		}
		if (!shouldBeProcessed(conn)) {
			IoUtils.closeSilently(imageStream);
			throw new IOException("Image request failed with response code " + conn.getResponseCode());
		}

		return new ContentLengthInputStream(new BufferedInputStream(imageStream, BUFFER_SIZE), conn.getContentLength());
	}

	/**
	 * 判断一个链接是否可用
	 * @param conn 已经打开的请求连接 (返回码是可用的)
	 * @return <b>true</b> - 如果从连接获取的数据是正确的 并且 应该被读取 和 处理;
	 *         <b>false</b> - 如果返回码包含不想管的数据 并且 不应该被处理
	 * @throws IOException
	 */
	protected boolean shouldBeProcessed(HttpURLConnection conn) throws IOException {
		return conn.getResponseCode() == 200;
	}

	/**
	 * 根据输入的 URL 地址 创建 {@linkplain HttpURLConnection HTTP 连接}
	 *
	 * @param url   要连接的 URL 地址
	 * @param extra 被传给 {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *              DisplayImageOptions.extraForDownloader(Object)} 的附加对象; 可以为 null
	 * @return {@linkplain HttpURLConnection 连接} 传入的 URL 地址. 链接没有被创建, 因此它是可配置的.
	 * @throws IOException if some I/O error occurs during network request or if no InputStream could be created for
	 *                     URL.
	 */
	protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
		String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
		HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		return conn;
	}

	/**
	 * 根据 URI 获取 输入流 {@link InputStream} 的引用 (图片在本地文件系统 或 SD 卡中).
	 *
	 * @param imageUri 图片的 URI 地址
	 * @param extra    被传给 {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *              DisplayImageOptions.extraForDownloader(Object)} 的附加对象; 可以为 null
	 * @return {@link InputStream} of image
	 * @throws IOException if some I/O error occurs reading from file system
	 */
	protected InputStream getStreamFromFile(String imageUri, Object extra) throws IOException {
		String filePath = Scheme.FILE.crop(imageUri);
		if (isVideoFileUri(imageUri)) {
			return getVideoThumbnailStream(filePath);
		} else {
			BufferedInputStream imageStream = new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE);
			return new ContentLengthInputStream(imageStream, (int) new File(filePath).length());
		}
	}

	/**
	 * 获取视频流的 URI
	 * @param filePath
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.FROYO)
	private InputStream getVideoThumbnailStream(String filePath) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			Bitmap bitmap = ThumbnailUtils
					.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
			if (bitmap != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.PNG, 0, bos);
				return new ByteArrayInputStream(bos.toByteArray());
			}
		}
		return null;
	}

	/**
	 * 根据 URI 获取 输入流 {@link InputStream} 的引用 (使用 ContentProvider {@link ContentResolver} 获取图片).
	 *
	 * @param imageUri Image URI
	 * @param extra    被传给 {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *              DisplayImageOptions.extraForDownloader(Object)} 的附加对象; 可以为 null
	 * @return {@link InputStream} of image
	 * @throws FileNotFoundException if the provided URI could not be opened
	 */
	protected InputStream getStreamFromContent(String imageUri, Object extra) throws FileNotFoundException {
		ContentResolver res = context.getContentResolver();

		Uri uri = Uri.parse(imageUri);
		if (isVideoContentUri(uri)) { // video thumbnail
			Long origId = Long.valueOf(uri.getLastPathSegment());
			Bitmap bitmap = MediaStore.Video.Thumbnails
					.getThumbnail(res, origId, MediaStore.Images.Thumbnails.MINI_KIND, null);
			if (bitmap != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.PNG, 0, bos);
				return new ByteArrayInputStream(bos.toByteArray());
			}
		} else if (imageUri.startsWith(CONTENT_CONTACTS_URI_PREFIX)) { // contacts photo
			return getContactPhotoStream(uri);
		}

		return res.openInputStream(uri);
	}

	/**
	 * 获取照片中的图片输入流
	 * @param uri
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected InputStream getContactPhotoStream(Uri uri) {
		ContentResolver res = context.getContentResolver();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return ContactsContract.Contacts.openContactPhotoInputStream(res, uri, true);
		} else {
			return ContactsContract.Contacts.openContactPhotoInputStream(res, uri);
		}
	}

	/**
	 * 根据 URI 获取 输入流 {@link InputStream} 的引用  (图片在应用的 assets 目录下).
	 *
	 * @param imageUri Image URI
	 * @param extra    被传给 {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *              DisplayImageOptions.extraForDownloader(Object)} 的附加对象; 可以为 null
	 * @return {@link InputStream} of image
	 * @throws IOException if some I/O error occurs file reading
	 */
	protected InputStream getStreamFromAssets(String imageUri, Object extra) throws IOException {
		String filePath = Scheme.ASSETS.crop(imageUri);
		return context.getAssets().open(filePath);
	}

	/**
	 * 根据 URI 获取 输入流 {@link InputStream} 的引用  (图片在应用的 drawable 资源目录中).
	 *
	 * @param imageUri Image URI
	 * @param extra    被传给 {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *              DisplayImageOptions.extraForDownloader(Object)} 的附加对象; 可以为 null
	 * @return {@link InputStream} of image
	 */
	protected InputStream getStreamFromDrawable(String imageUri, Object extra) {
		String drawableIdString = Scheme.DRAWABLE.crop(imageUri);
		int drawableId = Integer.parseInt(drawableIdString);
		return context.getResources().openRawResource(drawableId);
	}

	/**
	 * 通过 URI 获取图片的输入流引用中, URI 类型是不支持的类型.
	 * 该方法需要被重写去实现下载特殊 种类的 URI 资源.<br />
	 *
	 * 只有在 图片的 URI 不支持的时候才会回调该方法. 默认抛出 {@link UnsupportedOperationException} 异常
	 *
	 * @param imageUri Image URI
	 * @param extra    被传给 {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *              DisplayImageOptions.extraForDownloader(Object)} 的附加对象; 可以为 null
	 * @return {@link InputStream} of image
	 * @throws IOException                   if some I/O error occurs
	 * @throws UnsupportedOperationException if image URI has unsupported scheme(protocol)
	 */
	protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
		throw new UnsupportedOperationException(String.format(ERROR_UNSUPPORTED_SCHEME, imageUri));
	}

	private boolean isVideoContentUri(Uri uri) {
		String mimeType = context.getContentResolver().getType(uri);
		return mimeType != null && mimeType.startsWith("video/");
	}

	private boolean isVideoFileUri(String uri) {
		String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		return mimeType != null && mimeType.startsWith("video/");
	}
}
