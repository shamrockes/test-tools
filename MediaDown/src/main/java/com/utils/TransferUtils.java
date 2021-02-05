package com.utils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class TransferUtils {

	public static String unicode2String(String unicode) {

		StringBuffer string = new StringBuffer();

		String[] hex = unicode.split(".");

		for (int i = 1; i < hex.length; i++) {

			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);

			// 追加成string
			string.append((char) data);
		}

		return string.toString();
	}


	public static String asciiToString(String value) {
		StringBuffer sbu = new StringBuffer();
		String[] chars = value.split(".");
		for (int i = 0; i < chars.length; i++) {
			sbu.append((char) Integer.parseInt(chars[i]));
		}
		return sbu.toString();
	}

	public static String postToPsp( String url) {
		HttpPost request = new HttpPost(url);

		String bodyAsString = null;
		CloseableHttpResponse response = null;
		try {
			CloseableHttpClient client = HttpClientBuilder.create().build();
			response = client.execute(request);
			if(response.getStatusLine().getStatusCode() != 200){
				return bodyAsString;
			}
			bodyAsString = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			// 异常返回失败
			return null;
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
			}
		}
		return bodyAsString;
	}

	public static String getFromPsp(List<NameValuePair> params, String url) {
		HttpGet request;
		String bodyAsString = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			if (params != null){
				uriBuilder.setParameters(params);
			}
			request = new HttpGet(uriBuilder.build());
			CloseableHttpClient client = HttpClientBuilder.create().build();
			CloseableHttpResponse response = client.execute(request);
			if(response.getStatusLine().getStatusCode() != 200){
				return bodyAsString;
			}
			bodyAsString = EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return bodyAsString;
	}

}
