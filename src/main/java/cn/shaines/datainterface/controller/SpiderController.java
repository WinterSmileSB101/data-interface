package cn.shaines.datainterface.controller;

import cn.shaines.datainterface.spider.BiBiJing;
import cn.shaines.datainterface.util.MvcUtil;
import cn.shaines.datainterface.util.ResultHandle;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @program: data-interface
 * @description: 比比鲸控制层
 * @author: houyu
 * @create: 2018-12-10 09:25
 */
@RestController
@RequestMapping("/data-interface")
public class SpiderController {

    private MvcUtil mvcUtil = MvcUtil.get();

    @Autowired
    BiBiJing biBiJing;

    @GetMapping("/bijia")
    public String biJia(@RequestParam(value = "key", defaultValue = "") String key, @RequestParam(value = "page", defaultValue = "1")  int pageNum) {
        // System.out.println("---------->>biJia" + key + "==>>" + pageNum);
        if ("".equals(key)) return JSONObject.toJSONString(ResultHandle.getFailResult("key cannot be empty!"));
        Map<String, Object> process = biBiJing.process(key, pageNum);
        return JSONObject.toJSONString(ResultHandle.getSuccessResult().setData(process));
    }
}
