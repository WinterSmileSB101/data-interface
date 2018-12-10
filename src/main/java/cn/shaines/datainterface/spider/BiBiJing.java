package cn.shaines.datainterface.spider;

import cn.shaines.datainterface.util.CommonUtil;
import cn.shaines.datainterface.util.ParseUtil;
import cn.shaines.datainterface.util.URLConnectionUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.xml.xpath.XPath;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: data-interface
 * @description: 比比鲸比价系统
 * @author: houyu
 * @create: 2018-12-09 13:19
 */
@Component
public class BiBiJing {

    private static ParseUtil parseUtil;
    private static CommonUtil commonUtil;
    private static URLConnectionUtil urlConnectionUtil;
    private static XPath xPath;
    int requestCount = 0;

    static {
        parseUtil = ParseUtil.get();
        commonUtil = CommonUtil.get();
        urlConnectionUtil = URLConnectionUtil.get();
        xPath = parseUtil.getXPath();
    }

    public Map<String, Object> process(String key, int pageNum) {
        String url = "http://www.bibijing.com/";
        String cookieString = "";
        URLConnection urlConnection;
        Map<String, List<String>> headerMap;

        Map<String, Object> requestHeader = new HashMap<>();
        requestHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        requestHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.80 Safari/537.36");
        requestHeader.put("Upgrade-Insecure-Requests", 1);
        try {
            if (++requestCount % 10 == 1){
                urlConnection = urlConnectionUtil.doHttpURLConnectionGet(url, requestHeader, null);
                headerMap = urlConnection.getHeaderFields();
                List<String> cookie = headerMap.get("Set-Cookie");
                if (cookie != null){
                    cookieString = commonUtil.join(cookie.toArray(new String[]{}), "");
                }
                // System.out.println("======>>cookieString:" + cookieString);
            }

            // 遍历响应头
//            for(Map.Entry<String, List<String>> entry : headerMap.entrySet()){
//                System.out.println(entry.getKey() + "----" + entry.getValue());
//            }

            url = String.format("http://www.bibijing.com/search/main?k=%s&bp=&ep=&page=%s&sort=0", commonUtil.urlEncoder(key, "utf-8"), pageNum);
            // System.out.println("url:" + url);

            requestHeader.put("Cookie", cookieString);
            requestHeader.put("Refresh", url);

            urlConnection = urlConnectionUtil.doHttpURLConnectionGet(url, requestHeader, null);
            String html = urlConnectionUtil.toString(IOUtils.toByteArray(urlConnection.getInputStream()), urlConnection.getContentType(), null);

            String data = commonUtil.subStringBetween(html, "var ss = '", "if (ss != \"\") ").trim();
            data = commonUtil.deleteEndString(data, 2);
            data = commonUtil.subStringBetween(data, "\"page\":", "\"brand\":");
            data = commonUtil.deleteEndString(data, 1);
            data = "{\"page\":" + data + "}";

            return JSONObject.parseObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) {
//        ManManBuy manManBuy = new ManManBuy();
//        Map<String, Object> map = manManBuy.process("360 N7", 2);
//        System.out.println("-->>" + map);
//    }

}
