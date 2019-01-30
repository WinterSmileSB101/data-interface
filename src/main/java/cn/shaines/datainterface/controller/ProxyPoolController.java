package cn.shaines.datainterface.controller;

import cn.shaines.datainterface.model.Result;
import cn.shaines.datainterface.util.ResultHandle;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: data-interface
 * @description:
 * @author: houyu
 * @create: 2019-01-30 17:41
 */

@RestController
@RequestMapping("/data-interface")
public class ProxyPoolController {

    @GetMapping("/proxypool")
    public Result biJia(@RequestParam(value = "key", defaultValue = "") String key, @RequestParam(value = "page", defaultValue = "1") int pageNum) {
        return ResultHandle.getSuccessResult().setData("");
    }
}
