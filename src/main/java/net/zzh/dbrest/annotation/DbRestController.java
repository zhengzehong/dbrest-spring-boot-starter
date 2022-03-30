package net.zzh.dbrest.annotation;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

/**
 * @Description: 继承@Controller，效果也类似，标记Dbrest接口类
 * @author Zeo Zheng
 * @date 2022/1/20 11:58
 * @version 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@ResponseBody
public @interface DbRestController {

}
