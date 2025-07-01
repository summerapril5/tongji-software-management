<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { fetchProducts, searchProducts, fetchNetValue, fetchTransactionDate, apiAddProduct, apiUpdateProduct } from '@/api/product';
import type { Product } from '@/types/product';
import { useRouter } from 'vue-router';

const products = ref<Product[]>([]);
const totalProducts = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const searchKeyword = ref('');
const appliedSearchKeyword = ref('');
const dialogVisible = ref(false);
const selectedProduct = ref<Product | null>(null);
const selectedProductNetValue = ref<number | null>(null);
const transactionDate = ref<string | null>(null);
const addProductDialogVisible = ref(false);
const addProductForm = ref({
  productName: '默认',
  riskLevel: 0,
  productType: '默认',
  productStatus: 0,
});
const isEditMode = ref(false);
const router = useRouter();

const paginatedProducts = computed(() => {
  let filteredProducts = products.value;
  if (appliedSearchKeyword.value) {
    filteredProducts = products.value.filter(product =>
        product.productName.toLowerCase().includes(appliedSearchKeyword.value.toLowerCase())
    );
  }
  return filteredProducts.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});

const handleSearch = async () => {
  if (searchKeyword.value.trim()) {
    try {
      const res = await searchProducts(searchKeyword.value);
      products.value = res.data as Product[];
      totalProducts.value = products.value.length;
      appliedSearchKeyword.value = searchKeyword.value;
    } catch (error) {
      ElMessage.error('搜索失败');
    }
  } else {
    loadProducts();
    appliedSearchKeyword.value = '';
  }
};

const loadProducts = async () => {
  try {
    const res = await fetchProducts(currentPage.value, pageSize.value);
    products.value = res.data as Product[];
    totalProducts.value = products.value.length;
    appliedSearchKeyword.value = '';
  } catch (error) {
    ElMessage.error('加载产品失败');
  }
};

const showProductDetails = async (product: Product) => {
  selectedProduct.value = product;
  try {
    if (!transactionDate.value || !product.productId) return ElMessage.error('获取交易日期失败');
    const res = await fetchNetValue(product.productId, transactionDate.value);
    selectedProductNetValue.value = res.data;
  } catch (error) {
    ElMessage.error('获取净值失败');
    selectedProductNetValue.value = null;
  }
  dialogVisible.value = true;
};

const clearSelectedProduct = () => {
  selectedProduct.value = null;
};

const handlePageChange = (newPage: number) => {
  currentPage.value = newPage;
  loadProducts();
};

const getTransactionDate = async () => {
  try {
    const res = await fetchTransactionDate();
    transactionDate.value = res.data as string;
  } catch (error) {
    ElMessage.error('获取交易日期失败');
    transactionDate.value = null;
  }
};

const showAddProductDialog = () => {
  addProductDialogVisible.value = true;
};

const cancelAddProduct = () => {
  addProductDialogVisible.value = false;
  clearAddProductForm();
};

const confirmAddProduct = async () => {
  if (!addProductForm.value.productName || !addProductForm.value.productType) {
    ElMessage.error('请填写所有字段');
    return;
  }

  try {
    const newProduct = {
      productName: addProductForm.value.productName,
      riskLevel: addProductForm.value.riskLevel,
      productType: addProductForm.value.productType,
      productStatus: addProductForm.value.productStatus,
    };
    const res = await apiAddProduct(newProduct);
    ElMessage.success('新增产品成功');
    loadProducts();
    cancelAddProduct();
  } catch (error) {
    ElMessage.error('新增产品失败');
  }
};

const productStatusOptions = [
  { value: 0, label: '待售' },
  { value: 1, label: '销售中' },
  { value: 2, label: '已下架' },
];

const startEditProduct = () => {
  isEditMode.value = true;
};

const confirmEditProduct = async () => {
  if (!addProductForm.value.productName || !addProductForm.value.riskLevel || !addProductForm.value.productType) {
    ElMessage.error('请填写所有字段');
    return;
  }
  try {
    const updatedProduct = {
      productId: selectedProduct.value?.productId,
      productName: addProductForm.value.productName,
      riskLevel: addProductForm.value.riskLevel,
      productType: addProductForm.value.productType,
      productStatus: addProductForm.value.productStatus,
    };
    await apiUpdateProduct(updatedProduct);
    ElMessage.success('修改产品成功');
    loadProducts();
    dialogVisible.value = false;
  } catch (error) {
    ElMessage.error('修改产品失败');
  }
};

const clearAddProductForm = () => {
  addProductForm.value = {
    productName: '',
    riskLevel: 0,
    productType: '',
    productStatus: 0,
  };
};

onMounted(() => {
  loadProducts();
  getTransactionDate();
});

const state = (n : number | undefined) => {
  if(n === undefined){
    router.push('/login');
    return ElMessage.error('系统重大异常');
  }
  if (n === 0) {
    return '待售';
  } else if (n === 1) {
    return '销售中';
  } else if (n === 2) {
    return '已下架';
  } else {
    return '未知状态';
  }
};
</script>

<template>
  <div class="product-list">
    <h2 style="text-align: center; margin-bottom: 20px; color: #0b407ce0;">产品列表</h2>
    <el-button @click="showAddProductDialog" style="margin-block: 30px; margin-left: 50px;" type="success">新增产品</el-button>
    <!-- 搜索框 -->
    <div class="search-bar" style="display: flex; justify-content: center;">
      <el-input v-model="searchKeyword" placeholder="请输入关键词" style="width: 90%;" />
      <el-button class="search-btn" @click="handleSearch" type="primary" style="margin-left: 20px;">搜索</el-button>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <el-table :data="paginatedProducts" style="width: 100%;">
        <el-table-column prop="productName" label="产品名称" width="400" align="center"/>
        <el-table-column prop="productType" label="产品类型" width="400" align="center"/>
        <el-table-column prop="riskLevel" label="风险等级" width="300" align="center"/>
        <el-table-column label="操作" width="300" align="center">
          <template #default="scope">
            <el-button @click="showProductDetails(scope.row)" type="text">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
          layout="prev, pager, next"
          :total="totalProducts"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
      />
    </div>

    <!-- 产品详情对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="selectedProduct ? selectedProduct.productName : '产品详情'"
        custom-class="custom-dialog"
        width="50%"
        top="15vh"
        @close="clearSelectedProduct"
    >
      <el-form v-if="!isEditMode && selectedProduct" :model="selectedProduct" label-width="80px">
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="selectedProduct.productName" readonly />
        </el-form-item>
        <el-form-item label="产品类型" prop="productType">
          <el-input v-model="selectedProduct.productType" readonly />
        </el-form-item>
        <el-form-item label="风险等级" prop="riskLevel">
          <el-input-number v-model="selectedProduct.riskLevel" :min="0" :max="4" readonly />
        </el-form-item>
        <el-form-item label="产品状态" prop="productStatus">
          <el-input :value="state(selectedProduct.productStatus)" readonly />
        </el-form-item>
        <el-form-item label="净值" prop="netValue">
          <el-input :value="selectedProductNetValue !== null ? selectedProductNetValue.toFixed(4) : ''" readonly />
        </el-form-item>
      </el-form>
      <el-form v-else ref="editProductFormRef" :model="addProductForm" label-width="80px">
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="addProductForm.productName" />
        </el-form-item>
        <el-form-item label="风险等级" prop="riskLevel">
          <el-input-number v-model="addProductForm.riskLevel" controls-position="right" :min="0" :max="4" />
        </el-form-item>
        <el-form-item label="产品类型" prop="productType">
          <el-input v-model="addProductForm.productType" />
        </el-form-item>
        <el-form-item label="产品状态" prop="productStatus">
          <el-select v-model="addProductForm.productStatus" placeholder="请选择">
            <el-option
                v-for="item in productStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button v-if="!isEditMode" @click="startEditProduct">修改产品</el-button>
          <template v-else>
            <el-button type="primary" @click="confirmEditProduct">确定</el-button>
            <el-button type="danger" @click="isEditMode = !isEditMode" style="margin-left: 20px;">取消</el-button>
          </template>
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增产品对话框 -->
    <el-dialog
        v-model="addProductDialogVisible"
        title="新增产品"
        custom-class="custom-dialog"
        width="50%"
        top="15vh"
        @close="clearAddProductForm"
    >
      <el-form ref="addProductFormRef" :model="addProductForm" label-width="80px">
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="addProductForm.productName" />
        </el-form-item>
        <el-form-item label="风险等级" prop="riskLevel">
          <el-input-number v-model="addProductForm.riskLevel" controls-position="right" :min="0" :max="4" />
        </el-form-item>
        <el-form-item label="产品类型" prop="productType">
          <el-input v-model="addProductForm.productType" />
        </el-form-item>
        <el-form-item label="产品状态" prop="productStatus">
          <el-select v-model="addProductForm.productStatus" placeholder="请选择">
            <el-option
                v-for="item in productStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="cancelAddProduct">取消</el-button>
          <el-button type="primary" @click="confirmAddProduct">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
<style scoped>
.product-item {
  padding: 10px;
  border-bottom: 1px solid #ddd;
  cursor: pointer;
}

.product-item:hover {
  background-color: #f5f5f5;
}

.product-details {
  display: flex;
  flex-direction: column;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
}

.table-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
