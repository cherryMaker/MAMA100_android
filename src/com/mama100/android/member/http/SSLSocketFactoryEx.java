package com.mama100.android.member.http;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.mama100.android.member.global.BasicApplication;


/**
 * 编写SSLSocketFactoryEx，以代替默认的SSLSocketFactory，将加载keystore、truststore，用于Https的双向验证。
 * 创建用于Https请求的HttpClient。
 * @author ecoo
 *
 */
public class SSLSocketFactoryEx extends SSLSocketFactory {
	private static final String CLIENT_AGREEMENT = "TLS";		//使用协议   
	private static final String CLIENT_KEY_MANAGER = "X509";	//密钥管理器   
	private static final String CLIENT_TRUST_MANAGER = "X509";	//信任证书管理器   
  
//  	private static final String SERVER_IP = "192.168.72.99";	//连接IP 
	private static final int SERVER_PORT = 80;				//端口号  
	private static final String CLIENT_KET_PASSWORD = "123456";//"changeit";//	//私钥密码   
	private static final String CLIENT_TRUST_PASSWORD = "123456";//"changeit";//信任证书密码  ,此处无密码
	private static final String CLIENT_KEY_KEYSTORE = "BKS";	//"JKS";//密库，这里用的是BouncyCastle密库   
	private static final String CLIENT_TRUST_KEYSTORE = "BKS";	//"JKS";//   

	private SSLContext sslContext ;
	
	/**
	 * 加载指定keystore、truststore
	 * @param keystore
	 * @param keystorePassword
	 * @param truststore
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 */
	public SSLSocketFactoryEx(KeyStore keystore, String keystorePassword,KeyStore truststore) 
	throws NoSuchAlgorithmException, KeyManagementException,
	KeyStoreException, UnrecoverableKeyException {
		super(keystore,keystorePassword,truststore);

        //取得KeyManagerFactory实例   
        KeyManagerFactory keyManager = KeyManagerFactory.getInstance(CLIENT_KEY_MANAGER);   
        //取得TrustManagerFactory的X509密钥管理器
        TrustManagerFactory trustManager = TrustManagerFactory.getInstance(CLIENT_TRUST_MANAGER); 
        //初始化密钥管理器、信任证书管理器
        if(keystorePassword!=null)
        	keyManager.init(keystore,keystorePassword.toCharArray());
        else
        	keyManager.init(keystore,null);
        trustManager.init(truststore);
        sslContext=SSLContext.getInstance(CLIENT_AGREEMENT);
		sslContext.init(keyManager.getKeyManagers(),trustManager.getTrustManagers(), null);
	}
	
	
	/**
	 * 加载空的信任证书，信任所有Https链接
	 * @param truststore 空的信任证书
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 */
	public SSLSocketFactoryEx(KeyStore truststore)
	throws NoSuchAlgorithmException, KeyManagementException,
	KeyStoreException, UnrecoverableKeyException {
		super(truststore);
		
		TrustManager tm = new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			@Override
			public void checkClientTrusted(
			java.security.cert.X509Certificate[] chain, String authType)
			throws java.security.cert.CertificateException {
			}

			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
							throws java.security.cert.CertificateException {
			}
		};
		
		sslContext=SSLContext.getInstance(CLIENT_AGREEMENT);
		sslContext.init(null, new TrustManager[] { tm }, null);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {

		return sslContext.getSocketFactory().createSocket(socket, host, port,
				autoClose);

	}
	@Override
	public Socket createSocket() throws IOException {
		return sslContext.getSocketFactory().createSocket();
	}
	
	/**
	 * 
	 * @return 获取经加载member客户端密钥及客户端信任证书的HttpClient实例，
	 * 用于Https通讯。
	 */
	public static HttpClient getmemberHttpsClient() { 
		   try { 
	            //取得BKS密库实例   
	            KeyStore keyKeyStore = KeyStore.getInstance(CLIENT_KEY_KEYSTORE);   
	            KeyStore trustKeyStore = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);   
	            
	            //加载证书和私钥,通过读取资源文件的方式读取密钥和信任证书
	            keyKeyStore.load(BasicApplication.getInstance().getAssets().open("clientKey4.keystore")
	            		,CLIENT_KET_PASSWORD.toCharArray());//密钥
	            trustKeyStore.load(BasicApplication.getInstance().getAssets()
	            		.open("clientTrustServer4.keystore")
	            		,CLIENT_TRUST_PASSWORD.toCharArray());//信任证书


	            
//	           KeyStore trustStore2 = KeyStore.getInstance(KeyStore.getDefaultType());  
//	           trustStore2.load(null, null);  
//	           final SSLSocketFactoryEx myFactory=new SSLSocketFactoryEx(trustStore2);
	           final SSLSocketFactoryEx myFactory=new SSLSocketFactoryEx(keyKeyStore, CLIENT_KET_PASSWORD,trustKeyStore);
	           
		       HttpParams params = new BasicHttpParams(); 
		       HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1); 
		       HttpProtocolParams.setContentCharset(params, HTTP.UTF_8); 

		       SchemeRegistry registry = new SchemeRegistry(); 
		       registry.register(new Scheme("https", myFactory, SERVER_PORT)); 

		       return new DefaultHttpClient(new ThreadSafeClientConnManager(params, registry), params); 
		   } catch (Exception e) { 
		       return new DefaultHttpClient(); 
		   } 
		}
	
	

	/**
	 * @param keyKeyStore 已加载文件的密钥实例
	 * @param keyStorePwd 密钥密码
	 * @param trustKeyStore 已加载文件的信任证书Keystore
	 * @return 获取经加载指定客户端密钥及客户端信任证书的HttpClient实例，
	 * 用于Https通讯。
	 */
	public static HttpClient getHttpsClient(KeyStore keyKeyStore,String keyStorePwd,KeyStore trustKeyStore) { 
		   try { 
	           final SSLSocketFactoryEx myFactory=new SSLSocketFactoryEx(keyKeyStore, CLIENT_KET_PASSWORD,trustKeyStore);
		       HttpParams params = new BasicHttpParams(); 
		       HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1); 
		       HttpProtocolParams.setContentCharset(params, HTTP.UTF_8); 

		       SchemeRegistry registry = new SchemeRegistry(); 
		       registry.register(new Scheme("https", myFactory, SERVER_PORT)); 

		       return new DefaultHttpClient(new ThreadSafeClientConnManager(params, registry), params); 
		   } catch (Exception e) { 
		       return new DefaultHttpClient(); 
		   } 
		}

	

}
