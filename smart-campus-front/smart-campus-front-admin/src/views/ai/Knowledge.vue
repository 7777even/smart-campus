<template>
  <div class="knowledge-page">
    <div class="page-header">
      <h2>📚 AI 知识库管理</h2>
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增文档</el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="搜索标题/内容" clearable style="width: 200px" @clear="fetchData" />
      <el-select v-model="searchForm.category" placeholder="分类" clearable style="width: 140px" @change="fetchData">
        <el-option label="校园信息" value="校园信息" />
        <el-option label="管理规定" value="管理规定" />
        <el-option label="教务指南" value="教务指南" />
        <el-option label="校园服务" value="校园服务" />
      </el-select>
      <el-button type="primary" @click="fetchData">查询</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData.list" v-loading="loading" stripe style="width: 100%" border>
      <el-table-column prop="title" label="文档标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="120">
        <template #default="{ row }">
          <el-tag size="small" effect="plain">{{ row.category }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="tags" label="标签" width="200" show-overflow-tooltip />
      <el-table-column prop="uploader" label="上传者" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{ row }">{{ row.createTime?.substring(0, 16) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'"
                     @click="handleToggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-popconfirm title="确认删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="tableData.totalCount > 0">
      <el-pagination
        v-model:current-page="tableData.pageNo"
        v-model:page-size="tableData.pageSize"
        :total="tableData.totalCount"
        layout="total, sizes, prev, pager, next"
        background
        @change="fetchData" />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑文档' : '新增文档'" width="700px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="文档标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width: 100%">
            <el-option label="校园信息" value="校园信息" />
            <el-option label="管理规定" value="管理规定" />
            <el-option label="教务指南" value="教务指南" />
            <el-option label="校园服务" value="校园服务" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="标签(逗号分隔)" />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="文档内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getKnowledgePage, createKnowledgeDoc, updateKnowledgeDoc, deleteKnowledgeDoc } from '@/api/ai'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)

const searchForm = reactive({ keyword: '', category: '' })
const form = reactive({ id: null, title: '', category: '', tags: '', content: '' })
const tableData = reactive({ list: [], totalCount: 0, pageNo: 1, pageSize: 10 })

async function fetchData() {
  loading.value = true
  try {
    const res = await getKnowledgePage({
      pageNo: tableData.pageNo,
      pageSize: tableData.pageSize,
      ...searchForm,
    })
    const d = res.data || {}
    tableData.list = d.list || []
    tableData.totalCount = d.totalCount || 0
    tableData.pageNo = d.pageNo || 1
    tableData.pageSize = d.pageSize || 10
  } catch (e) { /* ignored */ }
  finally { loading.value = false }
}

function handleAdd() {
  isEdit.value = false
  form.id = null
  form.title = ''
  form.category = ''
  form.tags = ''
  form.content = ''
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, { id: row.id, title: row.title, category: row.category, tags: row.tags, content: row.content })
  dialogVisible.value = true
}

async function handleSave() {
  if (!form.title || !form.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await updateKnowledgeDoc({ ...form })
      ElMessage.success('修改成功')
    } else {
      await createKnowledgeDoc({ ...form })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await fetchData()
  } catch (e) { /* ignored */ }
  finally { saving.value = false }
}

async function handleDelete(id) {
  try {
    await deleteKnowledgeDoc(id)
    ElMessage.success('删除成功')
    await fetchData()
  } catch (e) { /* ignored */ }
}

async function handleToggleStatus(row) {
  try {
    await updateKnowledgeDoc({ id: row.id, status: row.status === 1 ? 0 : 1 })
    ElMessage.success(row.status === 1 ? '已禁用' : '已启用')
    await fetchData()
  } catch (e) { /* ignored */ }
}

onMounted(fetchData)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header h2 { margin: 0; font-size: 18px; color: #303133; }

.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
