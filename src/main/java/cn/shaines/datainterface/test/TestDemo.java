package cn.shaines.datainterface.test;

import cn.shaines.datainterface.util.CommonUtil;
import cn.shaines.datainterface.util.HttpUtil;

import java.util.List;

/**
 * @program: data-interface
 * @description: 测试模块
 * @author: houyu
 * @create: 2018-12-07 13:28
 */
public class TestDemo {

    public static void main(String[] args) throws Exception {

        String html = HttpUtil.get().getHtml("http://ip.jiangxianli.com/?page=" + 1);
        System.out.println(html);
        List<String> list = CommonUtil.get().regexMatcher(html, "data-url=\"(.*)\"\\s*data-unique-id=", List.class);
        System.out.println(list);
        String s = CommonUtil.get().regexMatcher(html, "data-url=\"(.*)\"\\s*data-unique-id=", String.class);
        System.out.println(s);


    }
}

