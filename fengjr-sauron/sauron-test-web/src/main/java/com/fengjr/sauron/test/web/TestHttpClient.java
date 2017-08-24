package com.fengjr.sauron.test.web;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2016年11月8日 下午5:40:03
 * 
 */
public class TestHttpClient {

	public static void main(String[] args) {
		while (true) {
			try {
				get22();
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
	 */
	public static void post() {
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost("http://127.0.0.1:8080/rocketmq");
		// 创建参数队列

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("type", "house"));
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(uefEntity);

			System.out.println("executing request " + httppost.getURI());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					System.out.println("--------------------------------------");
					System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
					System.out.println("--------------------------------------");
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发送 get请求
	 */
	public static void get() {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			// 创建httpget.
			HttpGet httpget = new HttpGet("http://10.255.73.161:8080/common/test");
			// HttpGet httpget = new HttpGet("http://10.255.53.162:8080/common/test");
			System.out.println("executing request " + httpget.getURI());
			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				System.out.println("--------------------------------------");
				// 打印响应状态
				System.out.println(response.getStatusLine());
				if (entity != null) {
					// 打印响应内容长度
					System.out.println("Response content length: " + entity.getContentLength());
					// 打印响应内容
					System.out.println("Response content: " + EntityUtils.toString(entity));
				}
				System.out.println("------------------------------------");
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void post2() throws IOException {

		URL url = new URL("http://127.0.0.1:8080/rocketmq");
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

		httpURLConnection.setDoInput(true);
		httpURLConnection.setDoOutput(true); // 设置该连接是可以输出的
		httpURLConnection.setRequestMethod("POST"); // 设置请求方式
		httpURLConnection.setRequestProperty("charset", "utf-8");

		PrintWriter pw = new PrintWriter(new BufferedOutputStream(httpURLConnection.getOutputStream()));
		pw.write("name=welcome"); // 向连接中输出数据（相当于发送数据给服务器）
		pw.write("&age=14");
		pw.flush();
		pw.close();

		BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) { // 读取数据
			sb.append(line + "\n");
		}

		System.out.println(sb.toString());
	}

	public static void post22() throws IOException {

		URL url = new URL("https://www.baidu.com/index.php?tn=98012088_3_dg");
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

		httpURLConnection.setDoInput(true);
		httpURLConnection.setDoOutput(true); // 设置该连接是可以输出的
		httpURLConnection.setRequestMethod("POST"); // 设置请求方式
		httpURLConnection.setRequestProperty("charset", "utf-8");

		PrintWriter pw = new PrintWriter(new BufferedOutputStream(httpURLConnection.getOutputStream()));
		pw.write("name=welcome"); // 向连接中输出数据（相当于发送数据给服务器）
		pw.write("&age=14");
		pw.flush();
		pw.close();

		BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) { // 读取数据
			sb.append(line + "\n");
		}

		System.out.println(sb.toString());
	}

	public static void get22() throws Exception {

		// URL url = new URL("http://10.255.53.162:8080/common/test");
		URL url = new URL("http://10.255.73.161:8080/common/test");
		URLConnection urlConnection = url.openConnection(); // 打开连接
		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8")); // 获取输入流
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		System.out.println(sb.toString());
	}

	public static void getSelf() throws Exception {

		URL url = new URL("http://127.0.0.1:8080/common/test?a1=b&c1=d");
		URLConnection urlConnection = url.openConnection(); // 打开连接
		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8")); // 获取输入流
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}

		System.out.println(sb.toString());
	}

}
