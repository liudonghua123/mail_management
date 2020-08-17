package org.jeecg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utils {

  public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
  public static SimpleDateFormat commonDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  public static <T> String encode(Map<String, T> requestParams) throws Exception {
    return requestParams.entrySet().stream().filter(item -> item.getValue() != null)
        .map(item -> item.getKey() + "="
            + encodeValue(
                item instanceof Date ? simpleDateFormat.format(item) : item.getValue().toString()))
        .collect(Collectors.joining("&"));
  }

  public static <T> String encode(String[] keys, T[] values) throws Exception {
    Map<String, Object> requestParams =
        IntStream.range(0, keys.length).mapToObj(index -> new Object[] {keys[index], values[index]})
            .filter(a -> a[1] != null).collect(Collectors.toMap(a -> (String) (a[0]), a -> (a[1])));
    return encode(requestParams);
  }

  public static Map<String, String> decode(String searchParamString) {
    return Arrays.stream(searchParamString.split("&")).map(s -> s.split("="))
        .collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? decodeValue(a[1]) : ""));
  }

  private static String encodeValue(String value) {
    try {
      // 注：coremail中使用的编码是GBK，而不是UTF-8
      return URLEncoder.encode(value, "GBK");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return "";
    }
  }

  private static String decodeValue(String value) {
    try {
      // 注：coremail中使用的编码是GBK，而不是UTF-8
      return URLDecoder.decode(value, "GBK");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return "";
    }
  }

  public static String doPost(String url, Map<String, Object> params) {
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


  public static String doGet(String url, Map<String, Object> params) {
    CloseableHttpClient client = null;
    CloseableHttpResponse response = null;
    try {
      // 定义HttpClient
      client = HttpClients.createDefault();
      // 实例化HTTP方法
      HttpGet request = new HttpGet();
      // 设置参数
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
        String name = iter.next();
        String value = String.valueOf(params.get(name));
        nvps.add(new BasicNameValuePair(name, value));
      }
      URI uri = new URIBuilder(url).setParameters(nvps).build();
      request.setURI(uri);
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
