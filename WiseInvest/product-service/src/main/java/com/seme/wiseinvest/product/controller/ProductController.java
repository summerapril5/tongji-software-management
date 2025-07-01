package com.seme.wiseinvest.product.controller;

import com.seme.wiseinvest.api.OurSystem; // 引入系统信息对象（可能用于联调）
import com.seme.wiseinvest.common.domain.Result; // 通用返回结果封装类
import com.seme.wiseinvest.api.NetValue; // 净值对象
import com.seme.wiseinvest.product.service.ProductService; // 产品服务类
import com.seme.wiseinvest.api.client.SettleClient; // 结算客户端，用于与结算服务通信

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor; // Lombok 注解，用于自动注入构造函数参数
import com.seme.wiseinvest.product.domain.Product; // 产品实体类

import java.util.Date;
import java.util.List;

@RestController // 表示这是一个 REST 控制器，返回值为 JSON 格式
@RequestMapping("/product") // 为该控制器下所有接口统一设置路径前缀
@RequiredArgsConstructor // Lombok 自动注入 final 修饰的字段（ProductService）
public class ProductController {

    private final ProductService productService; // 产品服务层依赖注入

    @Autowired
    private SettleClient settleClient; // 注入结算服务客户端

    /**
     * 获取指定产品的风险等级
     * @param productId 产品ID
     * @return 风险等级
     */
    @PostMapping("/level")
    public int getLevel(Integer productId) {
        return productService.getById(productId).getRiskLevel();
    }

    /**
     * 获取所有产品列表
     * @return 产品列表封装结果
     */
    @GetMapping("/list")
    public Result getAllProducts() {
        return Result.success(productService.list());
    }

    /**
     * 根据关键词搜索产品
     * @param keyword 关键词
     * @return 匹配的产品列表
     */
    @GetMapping("/search/{keyword}")
    public Result searchProducts(@PathVariable String keyword) {
        List<Product> products = productService.searchByKeyword(keyword);
        return Result.success(products);
    }

    /**
     * 获取指定产品在指定日期的净值
     * @param productId 产品ID
     * @param date 日期字符串（yyyy-MM-dd 格式）
     * @return 净值或错误信息
     */
    @GetMapping("/netvalue/{productId}/{date}")
    public Result getNetValueByProductIdAndDate(
            @PathVariable int productId,
            @PathVariable String date) {
        // 将字符串格式的日期转为 java.util.Date
        java.util.Date parsedDate = java.sql.Date.valueOf(date);
        Double netValue = productService.getNetValueByProductIdAndDate(productId, parsedDate);
        if (netValue == null) {
            return Result.error("No net value found for the given product and date.");
        }
        return Result.success(netValue);
    }

    /**
     * 添加新产品
     * @param product 产品对象
     * @return 添加是否成功
     */
    @PostMapping("/add")
    public Result addProduct(@RequestBody Product product) {
        boolean res =  productService.saveProduct(product);
        return res ? Result.success() : Result.error("添加产品失败");
    }

    /**
     * 更新产品信息
     * @param productId 产品ID
     * @param product 产品对象
     * @return 更新后的产品信息
     */
    @PutMapping("/{productId}")
    public Result updateProduct(@PathVariable Integer productId, @RequestBody Product product) {
        product.setProductId(productId);
        productService.updateProduct(product);
        return Result.success(product);
    }

    /**
     * 获取所有产品的最新净值（批量）
     * @return 每个产品最新的净值列表
     */
    @PostMapping("/net_values")
    public List<NetValue> getLatestNetValues() {
        return productService.getLatestNetValues();
    }

    /**
     * 插入某个产品的净值记录
     * @param netValue 净值对象
     * @return 是否插入成功
     */
    @PostMapping("/net_value/insert")
    public boolean insertNetValue(@RequestBody NetValue netValue){
        return productService.insertNetValue(netValue);
    }

    /**
     * 获取某产品某日期的净值（与前面 netvalue 接口功能相同，但参数为 Date 类型）
     * @param productId 产品ID
     * @param date 日期对象
     * @return 对应净值
     */
    @PostMapping("/net_value/{productId}/{date}")
    public Double getNetValue(@PathVariable("productId") Integer productId,
                              @PathVariable("date") Date date){
        return productService.getNetValueByProductIdAndDate(productId,date);
    };

}
