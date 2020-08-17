package org.jeecg.modules.message.handle.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jeecg.modules.message.handle.ISendMsgHandle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PushPlusSendMsgHandle implements ISendMsgHandle {

	@Value("${pushplus.token}")
	private String token;
	@Value("${pushplus.topic}")
	private String topic;
	@Value("${pushplus.template}")
	private String template;

	@Override
	public void SendMsg(String receiver, String title, String content) {
		log.info("PushPlusSendMsgHandle SendMsg begin...");
		Map<String, Object> params = new HashMap<>();
		params.put("token", token);
		params.put("topic", topic);
		params.put("title", title);
		params.put("content", content);
		params.put("template", template);
		String response = doPost("http://pushplus.hxtrip.com/send", params);
		if (response == null) {
			log.error("PushPlusSendMsgHandle SendMsg failed");
			return;
		}
		log.info("PushPlusSendMsgHandle SendMsg returns {}", response);
	}


	private static String doPost(String url, Map<String, Object> params) {
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			// 定义HttpClient
			client = HttpClients.createDefault();
			// 实例化HTTP方法
			HttpPost request = new HttpPost();
			URI uri = new URI(url);
			request.setURI(uri);
			// 设置参数
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
				String name = iter.next();
				String value = String.valueOf(params.get(name));
				nvps.add(new BasicNameValuePair(name, value));
			}
			request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			response = client.execute(request);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) { // 请求成功
				HttpEntity entity = response.getEntity();
				BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8"));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				while ((line = in.readLine()) != null) {
					sb.append(line + System.getProperty("line.separator"));
				}
				in.close();
				EntityUtils.consume(entity);
				return sb.toString();
			} else { // 请求失败
				log.info("请求失败 {} 状态码：{}", uri, code);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (client != null) {
					client.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
