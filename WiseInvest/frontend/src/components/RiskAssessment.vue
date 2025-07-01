<script lang="ts" setup>
import { ref } from 'vue';
import { ElForm, ElFormItem, ElRadioGroup, ElRadio, ElButton } from 'element-plus';

const emit = defineEmits();

interface Option {
  label: string;
  score: number;
}

interface Question {
  id: string;
  question: string;
  options: Option[];
}

const formRef = ref();
const submitted = ref(false);
const level = ref(0);

const questions: Question[] = [
  {
    id: 'riskUnderstanding',
    question: '您对投资风险的理解程度如何？',
    options: [
      { label: '非常保守', score: 0 },
      { label: '稍微保守', score: 1 },
      { label: '中等', score: 2 },
      { label: '稍微进取', score: 3 },
      { label: '非常进取', score: 4 },
    ],
  },
  {
    id: 'investmentPurpose',
    question: '您投资的主要目的是什么？',
    options: [
      { label: '长期财富增值', score: 4 },
      { label: '中期资金增长', score: 3 },
      { label: '短期保本', score: 2 },
      { label: '短期现金流需求', score: 1 },
    ],
  },
  {
    id: 'lossTolerance',
    question: '您能接受的最大年度投资损失是多少？',
    options: [
      { label: '不超过5%的损失', score: 0 },
      { label: '5%-10%的损失', score: 1 },
      { label: '10%-20%的损失', score: 2 },
      { label: '超过20%的损失', score: 3 },
    ],
  },
  {
    id: 'investmentExperience',
    question: '您的投资经验和知识水平如何？',
    options: [
      { label: '完全没有投资经验', score: 0 },
      { label: '了解一些基础投资知识', score: 2 },
      { label: '有一定的投资经验', score: 3 },
      { label: '经验丰富', score: 4 },
    ],
  },
  {
    id: 'financialStatus',
    question: '您的家庭收入和财务状况如何？',
    options: [
      { label: '家庭收入较低', score: 0 },
      { label: '家庭收入一般', score: 1 },
      { label: '家庭收入较高', score: 3 },
      { label: '其他（如退休金等）', score: 2 },
    ],
  },
];

const responses = ref<{ [key: string]: number }>({});
const score = ref<number | null>(null);
const riskLevel = ref<string>('');

// 计算风险等级
const calculateRiskLevel = (totalScore: number): string => {
  level.value = Math.floor(totalScore / 4);
  switch (level.value) {
    case 0:
      return '低风险的保守型';
    case 1:
      return '中低风险的较稳健型';
    case 2:
      return '中风险的稳健型';
    case 3:
      return '中高风险的进取型';
    default:
      return '高风险的激进型';
  }
};

// 提交表单并展示结果
const submitForm = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      score.value = Object.values(responses.value).reduce((acc, score) => acc + score, 0);
      submitted.value = true;
      riskLevel.value = calculateRiskLevel(score.value!);
      emit('update:level', level.value);
    } else {
      return false;
    }
  });
};
</script>

<template>
  <div class="risk-assessment">
    <h2 style="color:#4CAF50">WiseInvest风险承受能力评估</h2>
    <el-form :model="responses" ref="formRef" label-width="200px" class="assessment-form">
      <div v-for="question in questions" :key="question.id" class="question">
        <el-form-item :label="question.question" :prop="question.id"
                      :rules="[{ required: true, message: '请选择一个选项', trigger: 'change' }]" label-position="top">
          <el-radio-group v-model="responses[question.id]">
            <el-radio v-for="option in question.options" :key="option.score" :label="option.score">
              {{ option.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </div>
      <!-- 提交按钮 -->
      <el-button type="success" @click="submitForm" style="margin-left:10px ;width: 10%;">提交</el-button>
    </el-form>
    <div v-if="submitted" class="result">
      <p>您的风险承受能力评分是：{{ score }}</p>
      <p>根据评分，您的风险偏好为：{{ riskLevel }}</p>
    </div>
  </div>
</template>

<style scoped>
.question  {
  margin-bottom: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.result {
  margin-top: 20px;
  font-size: 18px;
  color: #4CAF50;
}
</style>
