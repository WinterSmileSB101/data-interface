package cn.shaines.datainterface.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: data-interface
 * @description: 基本工具类
 * @author: houyu
 * @create: 2018-12-06 16:13
 */
public class CommonUtil {
    private CommonUtil() {
    }

    /**
     * 判断 os 是否为空,支持并多个判断,如果有一个为空都返回true
     *
     * @param os
     * @return
     */
    public boolean isEmpty(Object[] os) {
        boolean flag = true;
        try {
            if (os != null) {
                for (Object o : os) {
                    flag = o == null;                       // 判断 null
                    if (!flag && o instanceof String) {
                        flag = ((String) o).isEmpty();      // 判断 value.length == 0;
                    }
                    if (flag) return flag;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean isNotEmpty(Object[] os) {
        return !isEmpty(os);
    }

    public boolean isEmpty(Object o) {
        return isEmpty(new Object[]{o});
    }

    public boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }


    /**
     * Object 转 String, 支持设置默认值
     *
     * @param o
     * @param defVal
     * @return
     */
    public String objectToString(Object o, String... defVal) {
        return isEmpty(o) ? (isEmpty(defVal) ? "" : defVal[0]) : o.toString();
    }

    /**
     * Object 转 Integer, 支持设置默认值
     *
     * @param o
     * @param defVal
     * @return
     */
    public Integer objectToInteger(Object o, Integer... defVal) {
        try {
            return isEmpty(o) ? (isEmpty(defVal) ? -1 : defVal[0]) : Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return isEmpty(defVal) ? -1 : defVal[0];
        }
    }

    /**
     * 删除 s 开始 len 长度的字符串
     *
     * @param s
     * @param len
     * @return
     */
    public String deleteStartString(String s, int len) {
        return s.length() > len ? s.substring(len, s.length()) : "";
    }

    /**
     * 删除 s 结尾 len 长度的字符串
     *
     * @param s
     * @param len
     * @return
     */
    public String deleteEndString(String s, int len) {
        return s.length() > len ? s.substring(0, s.length() - len) : "";
    }

    /**
     * String[] 加入 splitString 转 String
     *
     * @param ss
     * @param splitString
     * @return
     */
    public String join(String[] ss, String splitString) {
        String s = "";
        if (ss != null) {
            StringBuffer sBuffer = new StringBuffer("");
            for (int i = 0; i < ss.length; i++) {
                sBuffer.append(ss[i]).append(splitString);
            }
            s = deleteEndString(sBuffer.toString(), splitString.length());   // 去掉最后的分隔符 splitString
        }
        return s;
    }

    /**
     * 分割字符串,支持正则特殊符号
     */
    public String[] split(String s, String splitString) {
        int i = 0;
        StringTokenizer st = new StringTokenizer(s, splitString);
        String tokens[] = new String[st.countTokens()];
        while (st.hasMoreElements()) {
            tokens[i] = st.nextToken();
            i++;
        }
        return tokens;
    }

    /**
     * unicode 转 String
     */
    public String unicodeToString(String s) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(s);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            s = s.replace(matcher.group(1), ch + "");
        }
        return s;
    }

    /**
     * URL编码，将 s 转换成URL编码
     *
     * @param s
     * @return
     */
    public String urlEncoder(String s, String charsetName) {
        String temp = "";
        try {
            temp = URLEncoder.encode(s, charsetName);
            temp = temp.replaceAll("%2F", "/").replaceAll("%3A", ":").replaceAll("%3F", "?").replaceAll("%3D", "=").replaceAll("%26", "&").replaceAll("%23", "#");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * URL解码，将URL编码转换成中文
     *
     * @param s
     * @return
     */
    public String urlDecoder(String s, String charsetName) {
        String temp = "";
        try {
            temp = URLDecoder.decode(s, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 组装 Map
     *
     * @param collectionKey
     * @param collectionValue
     * @return
     */
    public Map collectionsToMap(Collection collectionKey, Collection collectionValue) {
        Map map = new LinkedHashMap();
        if (!isEmpty(new Object[]{collectionKey, collectionValue}) && collectionKey.size() == collectionValue.size()) {
            Iterator keyIterator = collectionKey.iterator();
            Iterator valueIterator = collectionValue.iterator();
            while (keyIterator.hasNext()) {
                map.put(keyIterator.next(), valueIterator.next());
            }
        }
        return map;
    }

    /**
     * 获取默认的解析时间类型
     *
     * @param formatStyle
     * @return
     */
    private SimpleDateFormat getDateFormat(String formatStyle) {
        return !isEmpty(formatStyle) ? new SimpleDateFormat(formatStyle) : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Date 转 String
     *
     * @param date   时期
     * @param format 格式   yyyy-MM-dd HH:mm:ss
     * @return
     */
    public String formatDate(Date date, String... format) {
        return !isEmpty(format) ? getDateFormat(format[0]).format(date) : getDateFormat("").format(date);
    }

    /**
     * StringDate 转 Date
     * parse:yyyy-MM-dd HH:mm:ss<br>
     *
     * @param StringDate 2018-05-11 10:10:10
     * @param format     格式   yyyy-MM-dd HH:mm:ss
     * @return
     */
    public Date parseData(String StringDate, String... format) {
        SimpleDateFormat dateFormat = !isEmpty(format) ? getDateFormat(format[0]) : getDateFormat("");
        try {
            return dateFormat.parse(StringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * 获取系统时间   yyyy-MM-dd HH:mm:ss
     *
     * @param formatStyle
     * @return
     */
    public String getSysTime(String... formatStyle) {
        return formatDate(new Date());
    }

    /**
     * List 反向选择,
     * -1 >> 倒数第一个
     * 0  >> 第一个
     * 1  >> 第二个
     *
     * @param list  list
     * @param index 位置索引
     * @return
     */
    public <E> E listReverse(List<E> list, int index) {
        return index < 0 ? list.get(list.size() + index) : list.get(index);
    }

    /**
     * valueLists旋转,行变成列,列变成行<br>
     * 注意的是,valueLists必须是矩形,行数、列数固定的。
     *
     * @param valueLists
     */
    public List<List> listsRotate(List<List> valueLists) {
        List<List> bigList = new ArrayList<>();
        int outSize = valueLists.size();
        int inSize = valueLists.get(0).size();
        for (int i = 0; i < inSize; i++) {
            List minList = new ArrayList<>();
            for (int j = 0; j < outSize; j++) {
                minList.add(valueLists.get(j).get(i));
            }
            bigList.add(minList);
        }
        return bigList;
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param regexWord
     * @return
     */
    private volatile String[] fbsArr = new String[]{"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};

    public String transferenceRegexWord(String regexWord) {
        for (String key : fbsArr) {
            if (regexWord.contains(key)) {
                regexWord = regexWord.replaceAll(key, "\\" + key);
            }
        }
        return regexWord;
    }

    /**
     * 获取 start 和 end 之间的String,组成一个List
     */
    public List<String> subStringsBetween(String s, String start, String end) {
        Matcher matcher = Pattern.compile(transferenceRegexWord(start) + "(.*?)" + transferenceRegexWord(end)).matcher(s);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }

    /**
     * 获取 start 和 end 之间的String
     */
    public String subStringBetween(String s, String start, String end) {
        int startIndex = s.indexOf(start);
        int endIndex = s.lastIndexOf(end);
        return s.substring(startIndex == -1 ? 0 : startIndex + start.length(), endIndex == -1 ? 0 : endIndex);
    }

    /**
     * 文本匹配正则
     *
     * @param text  文本
     * @param regex 正则表达式
     * @return
     */
    public String regexMatcher(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        matcher.find();
        return regex.contains("(") && regex.contains(")") ? matcher.group(1) : matcher.group();
    }

    /**
     * 生成MD5
     *
     * @param bytes
     * @return
     */
    public String md5(byte[] bytes) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * 获取 list 中的重复出现的个数
     *
     * @param list
     * @return Map
     */
    public Map<String, Integer> getAloneCountInList(List<String> list) {

        if (list == null)
            return new HashMap(); // return null;

        // -- 获取单独的数量
        final Map<String, Integer> aloneValueHashMap = new HashMap<>();
        for (String s : list) {
            if (aloneValueHashMap.containsKey(s)) {
                aloneValueHashMap.put(s, aloneValueHashMap.get(s).intValue() + 1);
            } else {
                aloneValueHashMap.put(s, 1);
            }
        }
        return getSortMapByValueDesc(aloneValueHashMap);
    }

    /**
     * 获取 通过value 降序的Map
     *
     * @param map
     * @return
     */
    public Map<String, Integer> getSortMapByValueDesc(final Map<String, Integer> map) {
        // -- 根据value的数量排序
        Map<String, Integer> orderByValueTreeMap = new TreeMap<String, Integer>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        if (map.get(obj1) != map.get(obj2)) {
                            return map.get(obj2).compareTo(map.get(obj1));
                        } else {
                            return map.get(obj1);
                        }
                    }
                }
        );
        orderByValueTreeMap.putAll(map);
        return orderByValueTreeMap;
    }

    /**
     * --------------------------------------------------------------------------------------
     */
    private static class SingletonHolder {
        private static final CommonUtil INSTANCE = new CommonUtil();
    }

    public static CommonUtil get() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * --------------------------------------------------------------------------------------
     */

    // TODO DELETE
    public static void main(String[] args) {

    }

}
