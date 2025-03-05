package com.sky.controller.admin;

import com.sky.config.RedisConfiguration;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")//名称相同需要定义一下Bean名称
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺营业状态
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setStatus(@PathVariable("status") Integer status){
        log.info("设置店铺营业状态为:{}",status == 1 ? "营业中":"打样中");
        //用redis的字符串存储店铺营业状态（通过上面的三元法提取字符串）
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }

    /**
     * 获取店铺的营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer> getStatus(){
        Integer shopStatus =(Integer) redisTemplate.opsForValue().get(KEY);//放进去什么类型取出来也是什么类型
        log.info("获取店铺的营业状态为:{}",shopStatus == 1 ? "营业中":"打样中");
        return Result.success(shopStatus);
    }

}
