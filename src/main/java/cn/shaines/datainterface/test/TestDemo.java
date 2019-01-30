package cn.shaines.datainterface.test;

import cn.shaines.datainterface.util.CommonUtil;
import cn.shaines.datainterface.util.FixedCache;
import cn.shaines.datainterface.util.HttpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: data-interface
 * @description: 测试模块
 * @author: houyu
 * @create: 2018-12-07 13:28
 */
public class TestDemo {

    public static void main(String[] args) throws Exception {

        CommonUtil commonUtil = CommonUtil.get();
        FixedCache<String> fixedCache = new FixedCache(50);
        for (int i = 1; i < 5; i++){
            String html = HttpUtil.get().getHtml("http://ip.jiangxianli.com/?page=" + i);
            List<String> list = commonUtil.subStringsBetween(html, "data-url=\"", "\"");
            if (list.size() == 0){
                break;
            }
            fixedCache.addAll(list);
        }
        System.out.println(fixedCache.toList());



    }
}

