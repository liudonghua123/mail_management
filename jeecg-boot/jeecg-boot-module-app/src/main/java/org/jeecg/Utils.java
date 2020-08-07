package org.jeecg;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {

  public static String encode(Map<String, String> requestParams) throws Exception {
    return requestParams.keySet().stream()
        .map(key -> key + "=" + encodeValue(requestParams.get(key)))
        .collect(Collectors.joining("&"));
  }

  public static String encode(String[] keys, String[] values) throws Exception {
    return IntStream.range(0, keys.length)
        .mapToObj(index -> String.format("%s=%s", keys[index], values[index]))
        .collect(Collectors.joining("&"));
  }

  public static Map<String, String> decode(String searchParamString) {
    return Arrays.stream(searchParamString.split("&")).map(s -> s.split("="))
        .collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? decodeValue(a[1]) : ""));
  }

  private static String encodeValue(String value) {
    try {
      return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return "";
    }
  }

  private static String decodeValue(String value) {
    try {
      return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return "";
    }
  }

}
