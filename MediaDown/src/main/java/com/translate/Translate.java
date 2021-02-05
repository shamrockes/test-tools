package com.translate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utils.ScriptUtils;
import com.utils.TransferUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Translate {

	public static String analysisDrug(String html) {
		Document doc = Jsoup.parse(html);
		Elements s = doc.getElementsByTag("script");
		String result = null;
		for (int i = 0; i < s.size(); i++) {
			Element s2 = s.get(i);
			List<Node> nodes = s2.childNodes();
			if (nodes != null && nodes.size() > 0) {
				for (Node node : nodes) {
					String str = node.attr("data");
					if (str.startsWith("var")) {
						String str2 = str.replace(" ", "");
						if (str2.startsWith("vard")) {
							str2 = str2.split("=")[1];
							result = str2;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * vardatas=(FonHen_JieMa('*51*48*54*54*48*47*121*111*117*115*104*101*110*103*47*29572*24187*22855*24187*47*22823*29579*-26250*21629*47*31532*48*51*53*-26938*95*-30263*-28270*-32763*19981*29992*30561*-30263*46*109*112*51*38*56*56*51*38*116*99').split('&'));vardatas2={playpage:datas2};varpart='´óÍõÈÄÃü-µÚ35¼¯';varplay_vid='3212';var_book={name:'´óÍõÈÄÃü',no:'µÚ35¼¯',vid:'3212',url:window.location.href}
	 *
	 * @return
	 */
	public static String subStringStr(String str) {
		String result = str.split("\\)")[0];
		result = result.replace("'", "");
		String[] result2 = result.split("\\(");
		return result2[result2.length - 1];
	}

	public static String getUrlTarget(String url) {
		String result = TransferUtils.postToPsp(url);
		String str = Translate.analysisDrug(result);
		//List<String> urls = new ArrayList<String>();
		String str2 = Translate.subStringStr(str);
		String targetName = ScriptUtils.JavaScriptOperation(str2).split("&")[0];
		String getkeyUrl = "https://www.gushiciju.com/player/key.php";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("url", String.valueOf(targetName)));
		String target = TransferUtils.getFromPsp(params, getkeyUrl);
		JSONObject jsonObject = JSON.parseObject(target);
		String fileName = targetName.split("/")[4];
		String t = jsonObject.getString("url");
		/*String filePath = "D:\\data\\345\\" + fileName;
		try {
			down(t, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return t;
	}

	public static void downLoad(String fileUrl, String savePath){
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = fileUrl;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		System.out.println(result);

	}



	public static void downloadFile(String fileUrl, String savePath) throws Exception {
		File file = new File(savePath);
		//判断文件是否存在，不存在则创建文件
		if (!file.exists()) {
			file.createNewFile();
		}
		URL url = new URL(fileUrl);
		HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
		urlCon.setConnectTimeout(6000);
		urlCon.setReadTimeout(6000);
		urlCon.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
		urlCon.setRequestProperty("Accept","*/*");
		int code = urlCon.getResponseCode();
		/*if (code != HttpURLConnection.HTTP_OK) {
			throw new Exception("文件读取失败");
		}*/
		DataInputStream in = new DataInputStream(urlCon.getInputStream());
		DataOutputStream out = new DataOutputStream(new FileOutputStream(savePath));
		byte[] buffer = new byte[2048];
		int count = 0;
		while ((count = in.read(buffer)) > 0) {
			out.write(buffer, 0, count);
		}
		try {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void down(String fileUrl, String savePath) throws MalformedURLException {
		// 下载网络文件
		int bytesum = 0;
		int byteread = 0;
		// System.out.println(fileName);

		String cookie = "Hm_lvt_23992734af4233e687b7daf0bfda8402=1612317950,1612318363,1612329966; ASPSESSIONIDSUTSRQDR=DNAMNGJAOBENANGALLAOAHJI; ASPSESSIONIDCGRTRRQB=KGDACIEBCIALKPLBHNBANPCH; Hm_lpvt_23992734af4233e687b7daf0bfda8402=1612418888; videoHistory=3212$%E5%A4%A7%E7%8E%8B%E9%A5%B6%E5%91%BD$%E7%AC%AC35%E9%9B%86$https://m.tingshubao.com/video/?3212-0-34.html$1612418888";

		URL url = new URL(fileUrl);

		try {
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setRequestProperty("Host", "t3344.tingchina.com");
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Accept-Language", "zh,zh-CN;q=0.9,en;q=0.8");
			conn.setRequestProperty("Accept-Encoding", "identity;q=1, *;q=0");//注意编码，gzip可能会乱码
			//conn.setRequestProperty("Content-Encoding", "utf8");
			conn.setRequestProperty("Connection", "keep-alive");
			//conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
			conn.setRequestProperty("Cookie", cookie);
			//conn.setRequestProperty("Cache-Control", "max-age=0");
			conn.setRequestProperty("Content-Type", "audio/mpeg");
			conn.setRequestProperty("Referer", fileUrl);

			// savePage(page,savePath,fileName);

			InputStream inStream = conn.getInputStream();
			FileOutputStream fs = new FileOutputStream(savePath);

			byte[] buffer = new byte[1204];
			int length;
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				// System.out.println(bytesum);
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
