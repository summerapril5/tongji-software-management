package com.seme.wiseinvest.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seme.wiseinvest.api.NetValue;
import com.seme.wiseinvest.product.domain.Product;
import java.util.Date;
import java.util.List;



public interface ProductService extends IService<Product> {
    List<Product> list();
    List<Product> searchByKeyword(String keyword);

    Double getNetValueByProductIdAndDate(int productId, Date date);

    boolean saveProduct(Product product);

    void updateProduct(Product product);
    List<NetValue> getLatestNetValues();
    boolean insertNetValue(NetValue netValue);

}
