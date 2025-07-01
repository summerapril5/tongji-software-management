package com.seme.wiseinvest.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seme.wiseinvest.api.NetValue;
import com.seme.wiseinvest.api.OurSystem;
import com.seme.wiseinvest.api.client.SettleClient;
import com.seme.wiseinvest.common.domain.Result;
import com.seme.wiseinvest.product.domain.Product;
import com.seme.wiseinvest.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private SettleClient settleClient; // Mock SettleClient as it's autowired in controller

    @Autowired
    private ObjectMapper objectMapper;

    private Product product1;
    private Product product2;
    private NetValue netValue1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setProductId(1);
        product1.setProductName("Product A");
        product1.setRiskLevel(3);

        product2 = new Product();
        product2.setProductId(2);
        product2.setProductName("Product B");
        product2.setRiskLevel(5);

        netValue1 = new NetValue();
        netValue1.setProductId(1);
        netValue1.setDate(new Date());
        netValue1.setNetValue(1.05);
    }

    @Test
    void getLevel_shouldReturnRiskLevel() throws Exception {
        when(productService.getById(1)).thenReturn(product1);

        mockMvc.perform(post("/product/level").param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void getAllProducts_shouldReturnProductList() throws Exception {
        List<Product> products = Arrays.asList(product1, product2);
        when(productService.list()).thenReturn(products);

        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].productName").value("Product A"));
    }

    @Test
    void searchProducts_shouldReturnFilteredProductList() throws Exception {
        List<Product> products = Arrays.asList(product1);
        when(productService.searchByKeyword("Product A")).thenReturn(products);

        mockMvc.perform(get("/product/search/Product A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].productName").value("Product A"));
    }

    @Test
    void getNetValueByProductIdAndDate_shouldReturnNetValue() throws Exception {
        Date testDate = dateFormat.parse("2023-10-26");
        when(productService.getNetValueByProductIdAndDate(1, testDate)).thenReturn(1.05);

        mockMvc.perform(get("/product/netvalue/1/2023-10-26"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(1.05));
    }

    @Test
    void getNetValueByProductIdAndDate_whenNotFound_shouldReturnError() throws Exception {
        Date testDate = dateFormat.parse("2023-10-27");
        when(productService.getNetValueByProductIdAndDate(1, testDate)).thenReturn(null);

        mockMvc.perform(get("/product/netvalue/1/2023-10-27"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("No net value found for the given product and date."));
    }

    @Test
    void addProduct_shouldReturnSuccess() throws Exception {
        when(productService.saveProduct(any(Product.class))).thenReturn(true);

        mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void addProduct_shouldReturnErrorOnFailure() throws Exception {
        when(productService.saveProduct(any(Product.class))).thenReturn(false);

        mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("添加产品失败"));
    }

    @Test
    void updateProduct_shouldReturnSuccess() throws Exception {
        // productService.updateProduct is void, so no 'when' needed for its return if it doesn't throw exceptions
        mockMvc.perform(put("/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.productId").value(1));
    }

    @Test
    void getLatestNetValues_shouldReturnNetValueList() throws Exception {
        List<NetValue> netValues = Arrays.asList(netValue1);
        when(productService.getLatestNetValues()).thenReturn(netValues);

        mockMvc.perform(post("/product/net_values"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].netValue").value(1.05));
    }

    @Test
    void insertNetValue_shouldReturnTrue() throws Exception {
        when(productService.insertNetValue(any(NetValue.class))).thenReturn(true);

        mockMvc.perform(post("/product/net_value/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(netValue1)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getNetValue_shouldReturnDoubleValue() throws Exception {
        Date testDate = new Date();
        String dateString = dateFormat.format(testDate);
        when(productService.getNetValueByProductIdAndDate(anyInt(), any(Date.class))).thenReturn(1.05);

        mockMvc.perform(post("/product/net_value/1/" + dateString))
                .andExpect(status().isOk())
                .andExpect(content().string("1.05"));
    }
}