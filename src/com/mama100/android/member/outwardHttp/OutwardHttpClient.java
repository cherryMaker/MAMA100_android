package com.mama100.android.member.outwardHttp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.mama100.android.member.http.CoreHttpClient;
import com.mama100.android.member.http.HttpRefusedException;
import com.mama100.android.member.http.HttpServerException;
import com.mama100.android.member.util.LogUtils;


/**
 * 
 * <b>Description:</b> 微博等，对外的HttpClient封装类
 * @version 1.0 edited by ecoo
 */
public class OutwardHttpClient {
	private static final String LOG_TAG = OutwardHttpClient.class.getSimpleName();
	/** OK: Success! */
	public static final int OK = 200;
	
	/** Not Modified: There was no new data to return. */
	public static final int NOT_MODIFIED = 304;
	/**
	 * Bad Request: The request was invalid. An accompanying error message will
	 * explain why. This is the status code will be returned during rate
	 * limiting.
	 */
	public static final int BAD_REQUEST = 400;
	
	/** Not Authorized: Authentication credentials were missing or incorrect. */
	public static final int NOT_AUTHORIZED = 401;
	
	/**
	 * Forbidden: The request is understood, but it has been refused. An
	 * accompanying error message will explain why.
	 */
	public static final int FORBIDDEN = 403;
	
	/**
	 * Not Found: The URI requested is invalid or the resource requested, such
	 * as a user, does not exists.
	 */
	public static final int NOT_FOUND = 404;
	
	/**
	 * Not Acceptable: Returned by the Search API when an invalid format is
	 * specified in the request.
	 */
	public static final int NOT_ACCEPTABLE = 406;
	
	/**
	 * Internal Server Error: Something is broken. Please post to the group so
	 * the  team can investigate.
	 */
	public static final int INTERNAL_SERVER_ERROR = 500;
	
	/** Bad Gateway:  is down or being upgraded. */
	public static final int BAD_GATEWAY = 502;
	
	/**
	 * Service Unavailable: The  servers are up, but overloaded with
	 * requests. Try again later. The search and trend methods use this to
	 * indicate when you are being rate limited.
	 */
	public static final int SERVICE_UNAVAILABLE = 503;

	private static final int CONNECTION_TIMEOUT_MS = 5 * 1000;
	private static final int SOCKET_TIMEOUT_MS = 20 * 1000;

	public static final int RETRIEVE_LIMIT = 20;
	public static final int RETRIED_TIME = 3;

	private HttpClient httpClient;

	public OutwardHttpClient() {
		initHttpClient();
	}
	
	public void initHttpClient() {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 10);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		httpClient = getNewHttpClient();
		
	}
	
	/**
	 * 代替DefaultHttpClient
	 * @return
	 */
	public static HttpClient getNewHttpClient() {  
        try {  
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());  
            trustStore.load(null, null);  
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);  
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
            HttpParams params = new BasicHttpParams();  

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);  
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);  
            SchemeRegistry registry = new SchemeRegistry();  
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));  
            registry.register(new Scheme("https", sf, 443));  
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);  
            return new DefaultHttpClient(ccm, params);  
        } catch (Exception e) {  
            return new DefaultHttpClient();  
        }  
    }  

	
	/**
	 * GET方式提交数据
	 * @param url  http url
	 * @return
	 * @throws HttpException
	 * @throws HttpServerException 
	 * @throws HttpRefusedException 
	 */
	public HttpResponse get(StringBuffer url) throws HttpException,HttpResponseException, SocketTimeoutException, HttpRefusedException, HttpServerException {
		return get(url, null);
	}
	
	/**
	 * GET方式提交数据
	 * @param url  http url
	 * @param params  (key,value) 
	 * @return HttpResponse
	 * @throws HttpException
	 * @throws HttpServerException 
	 * @throws HttpRefusedException 
	 */
	public HttpResponse get(StringBuffer url, List<BasicNameValuePair> params) 
		throws HttpException, SocketTimeoutException,HttpResponseException, HttpRefusedException, HttpServerException {
		
		LogUtils.logd(LOG_TAG, "get " + " request to " + url);
		
		HttpResponse response = null;
		
		// Execute Request
		try {
			if (params != null && !params.isEmpty()) {		
				// 格式化参数 变成URL的形式. 如：   username=jimmy&password=123456
				String subUrlParamStr = URLEncodedUtils.format(params, HTTP.UTF_8); 
				if (url.indexOf("?")==-1) {
					url.append("?");
					url.append(subUrlParamStr);
				} else {
					url.append("&");
					url.append(subUrlParamStr);
				}		
			}

			URI uri = createURI(url);
			HttpGet request = new HttpGet(uri);
			
//			// Setup ConnectionParams, Request Headers
//			setupHTTPConnectionParams(request);

			response = httpClient.execute(request);
			
		} catch (ClientProtocolException e) {
			throw new HttpException(e.getMessage(), e);
		} catch (IOException ioe) {
			throw new HttpException(ioe.getMessage(), ioe);
		} 

		if (response != null) {
			int statusCode = response.getStatusLine().getStatusCode();
			// It will throw a Exception while status code is not 200
			handleResCode(statusCode);
		} else {
			LogUtils.loge(LOG_TAG, "response is null");
		}
		
		return response;		
	}
	
	
	/**
	 * POST方式提交数据（包括一个文件和多个参数）
	 * @param url   http url
	 * @param file  File或Bitmap对象,无文件则传递null。文件名默认为"pic"。
	 * @param params   (key,value) 类似map的结构
	 * @return HttpResponse
	 * @throws HttpException
	 * @throws HttpServerException 
	 * @throws HttpRefusedException 
	 */
	public HttpResponse post(StringBuffer url, Object file, List<BasicNameValuePair> params) 
			throws HttpException, SocketTimeoutException, HttpResponseException, HttpRefusedException, HttpServerException {
		return post(url, file,"pic", params);
	}
	/**
	 * POST方式提交数据（包括一个文件和多个参数）
	 * @param url   http url
	 * @param file  File或Bitmap对象,无文件则传递null。
	 * @param fileName 文件名
	 * @param params   (key,value) 类似map的结构
	 * @return HttpResponse
	 * @throws HttpException
	 * @throws HttpServerException 
	 * @throws HttpRefusedException 
	 */
	public HttpResponse post(StringBuffer url, Object file,String fileName, List<BasicNameValuePair> params) 
			throws HttpException, SocketTimeoutException, HttpResponseException, HttpRefusedException, HttpServerException {
		LogUtils.logd(LOG_TAG, "post file " + " request to " + url);
		URI uri = createURI(url);
		
		HttpResponse response = null;

		HttpPost request = new HttpPost(uri);
		
		// Execute Request
		try {
			HttpEntity httpEntity =null;
			if(file!=null&&file instanceof File){
				httpEntity = createMultipartEntity(fileName, (File) file, params);
			}
			//Bitmap
			else if(file!=null&&file instanceof Bitmap){
				httpEntity = createMultipartEntity(fileName, (Bitmap) file, params);
			}
			else{
				httpEntity=new UrlEncodedFormEntity(params, HTTP.UTF_8);
			}
			request.setEntity(httpEntity);
			
			// Setup ConnectionParams, Request Headers
			setupHTTPConnectionParams(request);

			response = httpClient.execute(request);
			
		} catch (ClientProtocolException e) {
			throw new HttpException(e.getMessage(), e);
		} catch (IOException ioe) {
			throw new HttpException(ioe.getMessage(), ioe);
		} 

		if (response != null) {
			int statusCode = response.getStatusLine().getStatusCode();
			// It will alert error while status code is not 200
//			try {
//				LogUtils.logd(getClass(),"view json?----->"+EntityUtils.toString(response.getEntity()));
//			} catch (ParseException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			handleResCode(statusCode);
		} else {
			LogUtils.loge(LOG_TAG, "response is null");
		}
		
		return response;
	}

	
	/**
	 * CreateURI from URL string
	 * @param url
	 * @return request URI
	 * @throws HttpException
	 *             Cause by URISyntaxException
	 */
	private URI createURI(StringBuffer url) throws HttpException {
		
		URI uri;
		try {
			uri = new URI(url.toString());
		} catch (URISyntaxException e) {
			LogUtils.loge(LOG_TAG, e.getMessage());
			throw new HttpException("Invalid URL.");
		}

		return uri;
	}

	/**
	 * 创建可带一个File的MultipartEntity
	 * 
	 * @param fileTag
	 *            文件tag
	 * @param file
	 *            文件
	 * @param postParams
	 *            其他POST参数
	 * @return 带文件和其他参数的Entity
	 * @throws UnsupportedEncodingException
	 */
	private MultipartEntity createMultipartEntity(String fileTag, File file,
			List<BasicNameValuePair> postParams)
			throws UnsupportedEncodingException {
		
		MultipartEntity entity = new MultipartEntity();
		// Don't try this. Server does not appear to support chunking.
		// entity.addPart("media", new InputStreamBody(imageStream, "media"));

		entity.addPart(fileTag, new FileBody(file));
		
		
		for (BasicNameValuePair param : postParams) {
			entity.addPart(param.getName(), new StringBody(param.getValue()));
		}
		
		return entity;
	}
	
	/**
	 * 创建可带一个File的MultipartEntity
	 * 
	 * @param fileTag
	 *            bitmap文件名
	 * @param bitmap
	 *            bitmap文件
	 * @param postParams
	 *            其他POST参数
	 * @return 带文件和其他参数的Entity
	 * @throws UnsupportedEncodingException
	 */
	private MultipartEntity createMultipartEntity(String fileTag, Bitmap bitmap,
			List<BasicNameValuePair> postParams)
			throws UnsupportedEncodingException {
		
		final MultipartEntity entity = new MultipartEntity();
		// Don't try this. Server does not appear to support chunking.
		// entity.addPart("media", new InputStreamBody(imageStream, "media"));

		final ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 50);
		bitmap.compress(CompressFormat.PNG, 75, out);
		final InputStream in= new ByteArrayInputStream(out.toByteArray());
		entity.addPart(fileTag, new InputStreamBody(in, fileTag));
		
		
		for (BasicNameValuePair param : postParams) {
			entity.addPart(param.getName(), new StringBody(param.getValue()));
		}
		
		return entity;
	}
	

	/**
	 * Setup HTTPConncetionParams
	 * @param request
	 */
	private void setupHTTPConnectionParams(HttpUriRequest request) {
		
		HttpConnectionParams.setConnectionTimeout(request.getParams(), CONNECTION_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(request.getParams(), SOCKET_TIMEOUT_MS);
		((DefaultHttpClient) httpClient).setHttpRequestRetryHandler(requestRetryHandler);
		//method.addHeader("Accept-Encoding", "gzip, deflate");
		request.addHeader("Accept-Charset", "UTF-8,*;q=0.5");
	}

	/**
	 * 解析HTTP错误码
	 * @param statusCode
	 * @return
	 */
	public static String getCause(int statusCode) {
		String cause = null;                      
		switch (statusCode) {
		case NOT_MODIFIED:
			break;
		case BAD_REQUEST:
			cause = "The request was invalid.  An accompanying error message will explain why. This is the status code will be returned during rate limiting.";
			break;
		case NOT_AUTHORIZED:
			cause = "Authentication credentials were missing or incorrect.";
			break;
		case FORBIDDEN:
			cause = "The request is understood, but it has been refused.  An accompanying error message will explain why.";
			break;
		case NOT_FOUND:
			cause = "The URI requested is invalid or the resource requested, such as a user, does not exists.";
			break;
		case NOT_ACCEPTABLE:
			cause = "Returned by the Search API when an invalid format is specified in the request.";
			break;
		case INTERNAL_SERVER_ERROR:
			cause = "internal server error";
			break;
		case BAD_GATEWAY:
			cause = "bad gateway error";
			break;
		case SERVICE_UNAVAILABLE:
			cause = "Service Unavailable: The servers are up, but overloaded with requests. Try again later. The search and trend methods use this to indicate when you are being rate limited.";
			break;
		default:
			cause = "";
		}
		return statusCode + ":" + cause;
	}

	public static void handleResCode(int statusCode) throws HttpResponseException, HttpRefusedException, HttpServerException
	{

		String msg = getCause(statusCode) + "\n";
		LogUtils.loge(CoreHttpClient.class, msg);

		switch (statusCode) {
		// It's OK, do nothing
		case OK:
			break;

		// Mine mistake, Check the Log
		case NOT_MODIFIED:
		case BAD_REQUEST:
		case NOT_FOUND:
		case NOT_ACCEPTABLE:
			throw new HttpResponseException(statusCode, msg);

			// Server will return a error message, use
			// HttpRefusedException#getError() to see.
		case FORBIDDEN:
			throw new HttpRefusedException(msg, statusCode);

			// Something wrong with server
		case INTERNAL_SERVER_ERROR:
		case BAD_GATEWAY:
		case SERVICE_UNAVAILABLE:
			throw new HttpServerException(msg, statusCode);

			// Others
		default:
			throw new HttpResponseException(statusCode, msg);
		}

	}

	/**
	 * 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
	 */
	private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
		
		// 自定义的恢复策略
		public boolean retryRequest(IOException exception, int executionCount,
				HttpContext context) {
			// 设置恢复策略，在发生异常时候将自动重试N次
			if (executionCount >= RETRIED_TIME) {
				// Do not retry if over max retry count
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				// Retry if the server dropped connection on us
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				// Do not retry on SSL handshake exception
				return false;
			}
			HttpRequest request = (HttpRequest) context
					.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// Retry if the request is considered idempotent
				return true;
			}
			
			return false;
		}
		
	};

	/**
	 * 关闭连接管理器并释放资源
	 */
    public void shutdownHttpClient() {
        if (httpClient != null && httpClient.getConnectionManager() != null) {
            httpClient.getConnectionManager().shutdown();
            httpClient = null;
        }
    }

	
}
