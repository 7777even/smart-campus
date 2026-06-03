<template>
  <div class="page-login">
    <div class="login-card">
      <h2>🎓 智慧校园</h2>
      <p class="login-desc">学生 / 教师登录</p>
      <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
      <p class="login-tip">演示: student1 / 123456</p>
      <p class="login-tip teacher-tip">教师: teacher1 / 123456</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: 'student1', password: '123456' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }

  loading.value = true
  try {
    const res = await request.post('/auth/login', { username: form.username, password: form.password })
    const { token, user } = res.data
    localStorage.setItem('portal_token', token)
    localStorage.setItem('portal_user', JSON.stringify(user))
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.page-login {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.login-card {
  width: 400px;
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);

  h2 { text-align: center; font-size: 24px; margin-bottom: 4px; color: #303133; }
  .login-desc { text-align: center; color: #909399; font-size: 13px; margin-bottom: 28px; }
  .login-btn { width: 100%; height: 44px; font-size: 16px; letter-spacing: 4px; }
  .login-tip { text-align: center; font-size: 12px; color: #c0c4cc; margin-top: 12px; }
  .teacher-tip { margin-top: 4px; }
}
</style>
