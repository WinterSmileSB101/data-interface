package cn.shaines.datainterface.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: data-interface
 * @description: 网络请求工具类
 * @author: houyu
 * @create: 2018-12-06 22:57
 */
public class URLConnectionUtil {

    private Logger logger = Logger.getLogger(URLConnectionUtil.class);
    private CommonUtil commonUtil = CommonUtil.get();

    /**
     * 通过GET请求获取网页数据
     *
     * @param urlString      url
     * @return
     */
    public String getHtml(String urlString) {

        if (commonUtil.isNotEmpty(urlString)) {
            return doGet(urlString, null, null, null);
        } else {
            throw new IllegalArgumentException("urlString can not be empty!");
        }
    }

    /**
     * GET请求
     *
     * @param urlString      url
     * @param requestHeader  请求头参数
     * @param htmlCharset    网页编码方式
     * @param proxy          代理对象 : HttpUtils.get().setProxy(host, port, useName, password)
     * @return
     */
    public String doGet(String urlString, Map<String, Object> requestHeader, String htmlCharset,Map<String, Object> proxy) {

        URLConnection connection = doHttpURLConnectionGet(urlString, requestHeader, proxy);
        try {
            if (checkURLConnection(connection)) {
                return toString(IOUtils.toByteArray(connection.getInputStream()), connection.getContentType(), htmlCharset);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * POST请求
     *
     * @param urlString          url
     * @param requestHeader      请求头参数
     * @param requestBody        请求体
     * @param requestBodyType    POST请求体类型  :
     *                           <br>    1:HttpUtils.PostType.FORM_DATA
     *                           <br>    2:HttpUtils.PostType.JSON_DATA (tip:最简单的方式 >> requestBody.put("SEND_DATA",JSONString))
     * @param requestBodyCharset 请求体编码
     * @param htmlCharset        网页编码
     * @param proxy              代理对象 : HttpUtils.get().setProxy(host, port, useName, password)
     * @return
     */
    public String doPost(String urlString, Map<String, Object> requestHeader, Map<String, Object> requestBody, int requestBodyType, String requestBodyCharset, String htmlCharset, Map<String, Object> proxy) {

        URLConnection connection = doHttpURLConnectionPost(urlString, requestHeader, requestBody, requestBodyType, requestBodyCharset, proxy);
        try {
            if (checkURLConnection(connection)) {
                return toString(IOUtils.toByteArray(connection.getInputStream()), connection.getContentType(), htmlCharset);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "html data error!";
    }

///////////////////////////////////////////////////////////       HTTP_URL_CONNECTION      ///////////////////////////////////////////////////////////////////////

    /**
     * GET获取URLConnection
     *
     * @param urlString     url
     * @param requestHeader 请求头参数
     * @param proxy         代理对象 : HttpUtils.get().setProxy(host, port, useName, password)
     * @return
     */
    public URLConnection doHttpURLConnectionGet(String urlString, Map<String, Object> requestHeader, Map<String, Object> proxy) {
        URLConnection connection = null;
        try {
            URL url = new URL(urlString);
            // 得到connection对象。
            connection = checkUrlReturnURLConnection(url, "GET", proxy);
            setURLConnectionTimeout(connection);
            // 设置请求头参数
            setURLConnectionrequestHeader(requestHeader, connection, url);
            // 连接
            connection.connect();
            // connection.setInstanceFollowRedirects(false);//设置重定向地址不跳转到本地
            // Map<String, List<String>> headerFields = connection.getHeaderFields();// 获取响应头参数
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeURLConnection(connection);
        }
        return connection;
    }

    /**
     * POST获取URLConnection
     *
     * @param urlString          url
     * @param requestHeader      请求头参数
     * @param requestBody        请求体
     * @param requestBodyType    POST请求体类型  :
     *                           <br>    1:HttpUtils.PostType.FORM_DATA
     *                           <br>    2:HttpUtils.PostType.JSON_DATA (tip:最简单的方式 >> requestBody.put("SEND_DATA",JSONString))
     * @param requestBodyCharset 请求体编码
     * @param proxy              代理对象 : HttpUtils.get().setProxy(host, port, useName, password)
     * @return
     */
    public URLConnection doHttpURLConnectionPost(String urlString, Map<String, Object> requestHeader, Map<String, Object> requestBody, int requestBodyType, String requestBodyCharset, Map<String, Object> proxy) {

        String requestBodyString = "";
        if (PostType.FORM_DATA == requestBodyType) {
            // 设置参数类型是FORM_DATA(表单)格式
            requestBodyString = postFromDataHandle(requestBody);
        } else if (PostType.JSON_DATA == requestBodyType) {
            // 设置参数类型是JSON
            if (requestBody.containsKey("SEND_DATA")) {// 判断JSON_DATA是否存在
                requestBodyString = requestBody.get("SEND_DATA").toString();
            } else {
                requestBodyString = postJsonDataHandle(requestBody);
            }
        } else {
            requestBodyString = requestBody.get("SEND_DATA").toString();
        }
        try {
            return doHttpURLConnectionPost(urlString, requestHeader, requestBodyString.getBytes(StringUtils.isEmpty(requestBodyCharset) ? "UTF-8" : requestBodyCharset), proxy);
        } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
            logger.warn(e);
        }
        return null;
    }

    /**
     * POST获取URLConnection
     *
     * @param urlString       url
     * @param requestHeader   请求头参数
     * @param requestBodyByte 请求体        "{'':'','':''}".getBytes("UTF-8") | "key1:value1&key2:value2".getBytes("UTF-8")
     * @param proxy           代理对象 : HttpUtils.get().setProxy(host, port, useName, password)
     * @return
     */
    public URLConnection doHttpURLConnectionPost(String urlString, Map<String, Object> requestHeader, byte[] requestBodyByte, Map<String, Object> proxy) {

        URLConnection connection = null;
        try {
            URL url = new URL(urlString);
            // 得到connection对象。
            connection = checkUrlReturnURLConnection(url, "POST", proxy);
            // 设置POST请求的一些特殊参数
            setURLConnectionPostParam(connection);
            setURLConnectionTimeout(connection);
            // 获取参数
            setURLConnectionrequestHeader(requestHeader, connection, url);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBodyByte);
            outputStream.flush();
            outputStream.close();
            // 连接
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeURLConnection(connection);
        }
        return connection;
    }

    /**
     * POST FROM 处理器   >> user=admin&pwd=admin
     *
     * @param requestBody 请求体
     * @return
     */
    private String postFromDataHandle(Map<String, Object> requestBody) {
        String fromDataString = "";
        if (requestBody != null) {
            StringBuilder stringBuilder = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = requestBody.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                stringBuilder.append(String.format("%s=%s&", entry.getKey(), entry.getValue()));
            }
            fromDataString = stringBuilder.toString();
            fromDataString = fromDataString.substring(0, fromDataString.length() - 1);
        }
        return fromDataString;
    }

    /**
     * POST JSON 处理器 >> {"":"","":""}
     *
     * @param requestBody 请求体
     * @return
     */
    private String postJsonDataHandle(Map<String, Object> requestBody) {
        String jsonString = "";
        if (requestBody != null) {
            JSONObject jsonObject = new JSONObject(requestBody);
            jsonString = jsonObject.toJSONString();
        }
        return jsonString;
    }

    /**
     * 设置URLConnection POST 方式的特殊请求参数
     *
     * @param connection URLConnection
     * @throws ProtocolException
     */
    private void setURLConnectionPostParam(URLConnection connection) throws ProtocolException {
        setURLConnectionTimeout(connection);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
    }

    /**
     * 设置URLConnection 的 ConnectTimeout & ReadTimeout
     *
     * @param connection HttpURLConnection
     */
    private void setURLConnectionTimeout(URLConnection connection) {
        connection.setConnectTimeout(1000 * 30);
        connection.setReadTimeout(1000 * 30);
    }

    /**
     * 设置URLConnection 请求头参数
     *
     * @param requestHeader 请求头参数
     * @param connection    URLConnection
     * @param url           url
     */
    private void setURLConnectionrequestHeader(Map<String, Object> requestHeader, URLConnection connection, URL url) {
        // 获取一个默认的请求头map
        Map<String, Object> defaultHeaderMap = getDefaultHeaderMap();
        if (requestHeader != null && requestHeader.size() > 0) {
            defaultHeaderMap.putAll(requestHeader);// 拼接组合请求头
        }
        checkRefererParam(url, defaultHeaderMap);
        setURLConnectionParamMap(connection, defaultHeaderMap);
    }

    /**
     * 检查请求头参数最终的Referer
     *
     * @param url           url
     * @param requestHeader 请求头参数
     */
    private void checkRefererParam(URL url, Map<String, Object> requestHeader) {
        if ("https://www.baidu.com/".equals(requestHeader.get("Referer"))) {
            requestHeader.put("Host", url.getHost());
            requestHeader.put("Referer", String.format("%s://%s", url.getProtocol(), url.getHost()));
        }
        checkRequestHeader(requestHeader);
    }

    /**
     * 检查请求头    remove("Content-Length") & remove("Accept-Encoding");
     *
     * @param requestHeader
     */
    private void checkRequestHeader(Map<String, Object> requestHeader) {
        if (requestHeader != null) {//去除请求失败参数
            requestHeader.remove("Content-Length");
            requestHeader.remove("Accept-Encoding");
        }
    }

    /**
     * 检查URLConnection 响应码
     *
     * @param connection
     * @return
     * @throws IOException
     */
    private boolean checkURLConnection(URLConnection connection) throws IOException {
        boolean flag = false;
        if (connection instanceof HttpURLConnection) {
            flag = HttpURLConnection.HTTP_OK == ((HttpURLConnection) connection).getResponseCode();
        } else if (connection instanceof HttpsURLConnection) {
            flag = HttpURLConnection.HTTP_OK == ((HttpsURLConnection) connection).getResponseCode();
        }
        return flag;
    }

    /**
     * 设置HttpURLConnection请求头参数
     *
     * @param connection    URLConnection
     * @param requestHeader 请求头参数
     */
    private void setURLConnectionParamMap(URLConnection connection, Map<String, Object> requestHeader) {
        Set<Map.Entry<String, Object>> paramEntry = requestHeader.entrySet();
        for (Map.Entry<String, Object> entry : paramEntry) {
            connection.setRequestProperty(entry.getKey(), entry.getValue().toString());
        }
    }

    /**
     * 检查URL返回URLConnection
     *
     * @param url           url
     * @param requestMethod 请求方法
     * @param proxy         代理
     * @return
     */
    public URLConnection checkUrlReturnURLConnection(URL url, String requestMethod, Map<String, Object> proxy) {
        HttpURLConnection connection = null;
        try {
            if (proxy == null) {
                //不设置代理
                connection = (HttpURLConnection) url.openConnection();
            } else if (proxy != null && commonUtil.isNotEmpty(proxy.get("host")) && commonUtil.isEmpty(proxy.get("username"))) {
                logger.debug("HttpURLConnection setting up the proxy host!");
                //设置代理主机和端口
                java.net.Proxy javaNetProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new java.net.InetSocketAddress(proxy.get("host").toString(), Integer.parseInt(proxy.get("port").toString())));
                connection = (HttpURLConnection) url.openConnection(javaNetProxy);
            } else if (proxy != null && commonUtil.isNotEmpty(proxy.get("host")) && commonUtil.isNotEmpty(proxy.get("username"))) {
                // 设置代理服务器
                logger.debug("HttpURLConnection setting up the proxy service!");
                System.getProperties().put("proxySet", "true");
                // System.getProperties().put("proxyHost", proxy.get("host"));
                // System.getProperties().put("proxyPort", proxy.get("port"));
                String authString = proxy.get("username") + ":" + proxy.get("password");
                String auth = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
                java.net.Proxy javaNetProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new java.net.InetSocketAddress(proxy.get("host").toString(), Integer.parseInt(proxy.get("port").toString())));
                URLConnection conn = url.openConnection(javaNetProxy);
                conn.setRequestProperty("Proxy-Authorization", auth);
                connection = (HttpURLConnection) url.openConnection(javaNetProxy);
            } else {
                //不设置代理
                connection = (HttpURLConnection) url.openConnection();
            }
            String protocol = url.getProtocol();
            if ("https".equalsIgnoreCase(protocol)) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                trustAllHosts(https);
                https.getHostnameVerifier();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                https.setRequestMethod(requestMethod);
                return https;
            }
            connection.setRequestMethod(requestMethod);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * HttpURLConnection设置覆盖java默认的证书验证
     */
    private final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }};

    /**
     * HttpURLConnection设置不验证主机
     */
    private final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * HttpURLConnection设置信任所有证书
     *
     * @param connection HttpsURLConnection
     * @return
     */
    private SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldFactory;
    }

    /**
     * 关闭URLConnection
     *
     * @param connection
     */
    private void closeURLConnection(URLConnection connection) {
        logger.debug("closeURLConnection:" + connection);
        try {
            if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection) connection).disconnect();
            } else if (connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) connection).disconnect();
            }
        } finally {
            connection = null;
        }
    }

    /**
     * To String
     *
     * @param contentBytes 返回的内容字节数据
     * @param contentType  响应头内容类型
     * @param charset      自定义的编码方式
     * @return
     * @throws IOException
     */
    public String toString(byte[] contentBytes, String contentType, String charset) throws IOException {

        String htmlString = "";
        if (commonUtil.isNotEmpty(charset)) {
            // 不为空,说明有设置编码方式了,使用自定义的编码方式
            htmlString = new String(contentBytes, charset);
        } else {
            String detectCharset = detectCharset(contentBytes, contentType);
            logger.debug("automatic matching html charset : " + detectCharset);
            htmlString = new String(contentBytes, commonUtil.isEmpty(detectCharset) ? "UTF-8" : detectCharset);
        }
        return htmlString;
    }


    /**
     * 自动识别网页编码
     *
     * @param contentType  响应头内容类型
     * @param contentBytes 返回的内容字节数据
     * @return
     * @throws IOException
     */
    public String detectCharset(byte[] contentBytes, String contentType) throws IOException {
        String charset = "";
        // 1、encoding in http header Content-Type
        final Pattern patternForCharset = Pattern.compile("charset\\s*=\\s*['\"]*([^\\s;'\"]*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patternForCharset.matcher(contentType);
        if (matcher.find()) {
            charset = matcher.group(1);
            if (!Charset.isSupported(charset)) {
                charset = "";
            }
        } else if (commonUtil.isEmpty(charset)) {
            // use default charset to decode first time
            String content = new String(contentBytes, Charset.defaultCharset());
            // 2、charset in meta
            if (commonUtil.isNotEmpty(content)) {
                Document document = Jsoup.parse(content);
                Elements links = document.select("meta");
                for (Element link : links) {
                    // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                    String metaContent = link.attr("content");
                    String metaCharset = link.attr("charset");
                    if (metaContent.indexOf("charset") != -1) {
                        metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                        charset = metaContent.split("=")[1];
                        break;
                    } else if (commonUtil.isNotEmpty(metaCharset)) {
                        // 2.2、html5 <meta charset="UTF-8" />
                        charset = metaCharset;
                        break;
                    }
                }
            }
            // 3、todo use tools as cpdetector for content decode
        }
        return charset;
    }

    /**
     * 获取默认的请求头Map
     * 默认去掉isDisableCookieManagement:boolean
     * cookie:Map<String, String>
     */
    private Map<String, Object> getDefaultHeaderMap() {
        Map<String, Object> defaultHeaders = new LinkedHashMap<>();
        defaultHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        defaultHeaders.put("Accept-Language", "zh-CN,zh;q=0.9");
        defaultHeaders.put("Cache-Control", "no-cache");
        defaultHeaders.put("Connection", "keep-alive");
        // defaultHeaders.put("Host", "www.baidu.com");
        defaultHeaders.put("Pragma", "no-cache");
        defaultHeaders.put("Referer", "https://www.baidu.com/");
        defaultHeaders.put("Upgrade-Insecure-Requests", "1");
        defaultHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        return defaultHeaders;
    }

    // POST参数类型
    public static abstract class PostType {
        // Form 表单数据
        public static final int FORM_DATA = 1;
        // JSON 数据
        public static final int JSON_DATA = 2;
    }

    /**--------------------------------------------------------------------------------------*/
    private static class SingletonHolder {
        private static final URLConnectionUtil INSTANCE = new URLConnectionUtil();
    }
    public static URLConnectionUtil get(){
        return SingletonHolder.INSTANCE;
    }
    /**--------------------------------------------------------------------------------------*/

}
