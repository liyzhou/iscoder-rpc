package com.github.leeyazhou.scf.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class URLUtil {

  /**
   * url decode
   * 
   * @param url
   * @param encoding
   * @return
   * @throws UnsupportedEncodingException
   */
  public static String decode(String url, String encoding) throws UnsupportedEncodingException {
    if (url != null && !url.equals("")) {
      return URLDecoder.decode(url, encoding);
    }
    return "";
  }

  /**
   * url decode
   * 
   * @param url
   * @return
   * @throws UnsupportedEncodingException
   */
  public static String decode(String url) throws UnsupportedEncodingException {
    return decode(url, "utf-8");
  }

  /**
   * get url without para
   * 
   * @param url
   * @return
   * @throws UnsupportedEncodingException
   */
  public static String getOnlyUrl(String url) throws UnsupportedEncodingException {
    url = URLDecoder.decode(url, "utf-8");
    return url.split("\\?")[0];
  }

  /**
   * get paras
   * 
   * @param url
   * @return
   * @throws UnsupportedEncodingException
   */
  public static Map<String, String> getParas(String url) throws UnsupportedEncodingException {
    url = URLDecoder.decode(url, "utf-8");
    String[] urlAry = url.split("\\?");
    Map<String, String> mapParas = new HashMap<String, String>();
    if (urlAry.length > 1) {
      for (int i = 1; i < urlAry.length; i++) {
        String[] paras = urlAry[i].split("&");
        for (String para : paras) {
          String[] kv = para.split("=");
          if (kv.length == 2) {
            mapParas.put(kv[0], kv[1]);
          }
        }
      }
    }
    return mapParas;
  }
}