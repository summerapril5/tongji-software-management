package com.seme.wiseinvest.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seme.wiseinvest.api.NetValue;
import com.seme.wiseinvest.api.OurSystem;
import com.seme.wiseinvest.api.client.SettleClient;
import com.seme.wiseinvest.product.domain.Product;
import com.seme.wiseinvest.product.mapper.NetValueMapper;
import com.seme.wiseinvest.product.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private NetValueMapper netValueMapper;

    @Mock
    private SettleClient settleClient;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;
    private NetValue netValue1;
    private OurSystem ourSystem;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setProductId(1);
        product1.setProductName("Product A");
        product1.setRiskLevel(3);

        product2 = new Product();
        product2.setProductId(2);
        product2.setProductName("Product B");

        netValue1 = new NetValue();
        netValue1.setProductId(1);
        netValue1.setDate(new Date());
        netValue1.setNetValue(1.05);

        ourSystem = new OurSystem();
        ourSystem.setTransactionDate(new Date());
    }

    @Test
    void list_shouldReturnAllProducts() {
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productMapper.selectList(null)).thenReturn(expectedProducts);

        List<Product> actualProducts = productService.list();

        assertEquals(expectedProducts, actualProducts);
        verify(productMapper, times(1)).selectList(null);
    }

    @Test
    void searchByKeyword_shouldReturnMatchingProducts() {
        String keyword = "Product A";
        List<Product> expectedProducts = Collections.singletonList(product1);
        when(productMapper.selectList(any(QueryWrapper.class))).thenReturn(expectedProducts);

        List<Product> actualProducts = productService.searchByKeyword(keyword);

        assertEquals(expectedProducts, actualProducts);
        verify(productMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    void getNetValueByProductIdAndDate_shouldReturnNetValue() {
        Date testDate = new Date();
        when(netValueMapper.selectOne(any(QueryWrapper.class))).thenReturn(netValue1);

        Double actualNetValue = productService.getNetValueByProductIdAndDate(1, testDate);

        assertEquals(netValue1.getNetValue(), actualNetValue);
        verify(netValueMapper, times(1)).selectOne(any(QueryWrapper.class));
    }

    @Test
    void getNetValueByProductIdAndDate_whenNotFound_shouldReturnNull() {
        Date testDate = new Date();
        when(netValueMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        Double actualNetValue = productService.getNetValueByProductIdAndDate(1, testDate);

        assertNull(actualNetValue);
        verify(netValueMapper, times(1)).selectOne(any(QueryWrapper.class));
    }

    @Test
    void saveProduct_shouldSaveProductAndInitialNetValue() {
        when(settleClient.getSystem()).thenReturn(ourSystem);
        when(netValueMapper.insert(any(NetValue.class))).thenReturn(1);
        // Mock the behavior of productMapper.insert to set the productId
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setProductId(1); // Simulate ID generation
            return 1; // Simulate successful insert
        }).when(productMapper).insert(any(Product.class));


        boolean result = productService.saveProduct(product1);

        assertTrue(result);
        verify(productMapper, times(1)).insert(product1);
        verify(settleClient, times(1)).getSystem();
        verify(netValueMapper, times(1)).insert(any(NetValue.class));
    }

    @Test
    void saveProduct_whenNetValueInsertFails_shouldReturnFalse() {
        when(settleClient.getSystem()).thenReturn(ourSystem);
        when(netValueMapper.insert(any(NetValue.class))).thenReturn(0); // Simulate insert failure
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setProductId(1); // Simulate ID generation
            return 1; // Simulate successful insert
        }).when(productMapper).insert(any(Product.class));

        boolean result = productService.saveProduct(product1);

        assertFalse(result);
        verify(productMapper, times(1)).insert(product1);
        verify(settleClient, times(1)).getSystem();
        verify(netValueMapper, times(1)).insert(any(NetValue.class));
    }

    @Test
    void updateProduct_shouldCallMapperUpdateById() {
        productService.updateProduct(product1);
        verify(productMapper, times(1)).updateById(product1);
    }

    @Test
    void getLatestNetValues_shouldReturnLatestNetValues() {
        NetValue latestDateNetValue = new NetValue();
        latestDateNetValue.setDate(new Date()); // Assume this is the latest date

        List<NetValue> expectedNetValues = Collections.singletonList(netValue1);

        when(netValueMapper.selectOne(any(QueryWrapper.class))).thenReturn(latestDateNetValue);
        when(netValueMapper.selectList(any(QueryWrapper.class))).thenReturn(expectedNetValues);

        List<NetValue> actualNetValues = productService.getLatestNetValues();

        assertEquals(expectedNetValues, actualNetValues);
        verify(netValueMapper, times(1)).selectOne(any(QueryWrapper.class)); // For max date
        verify(netValueMapper, times(1)).selectList(any(QueryWrapper.class)); // For values with max date
    }

    @Test
    void getLatestNetValues_whenNoNetValues_shouldReturnEmptyList() {
        when(netValueMapper.selectOne(any(QueryWrapper.class))).thenReturn(null); // No max date found

        List<NetValue> actualNetValues = productService.getLatestNetValues();

        assertTrue(actualNetValues.isEmpty());
        verify(netValueMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(netValueMapper, never()).selectList(any(QueryWrapper.class));
    }

    @Test
    void insertNetValue_shouldReturnTrueOnSuccess() {
        when(netValueMapper.insert(any(NetValue.class))).thenReturn(1);
        boolean result = productService.insertNetValue(netValue1);
        assertTrue(result);
        verify(netValueMapper, times(1)).insert(netValue1);
    }

    @Test
    void insertNetValue_shouldReturnFalseOnFailure() {
        when(netValueMapper.insert(any(NetValue.class))).thenReturn(0);
        boolean result = productService.insertNetValue(netValue1);
        assertFalse(result);
        verify(netValueMapper, times(1)).insert(netValue1);
    }
}