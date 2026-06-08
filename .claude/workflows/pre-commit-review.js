export const meta = {
  name: 'pre-commit-review',
  description: '在提交前自动执行代码审查，扫描正确性、安全、规范问题',
  phases: [
    { title: '收集变更', detail: 'git diff --cached 获取暂存区变更' },
    { title: '审查', detail: '对变更文件做多维审查扫描' },
    { title: '报告', detail: '输出审查结论与修复建议' },
  ],
}

import { execSync } from 'child_process'

phase('收集变更')

// 获取暂存区变更
const diffOutput = execSync('git diff --cached --name-only', { encoding: 'utf-8' })
const changedFiles = diffOutput.split('\n').filter(Boolean)

if (changedFiles.length === 0) {
  log('没有暂存的变更，跳过审查')
  process.exit(0)
}

log(`检测到 ${changedFiles.length} 个变更文件`)

// 按类型分类
const javaFiles = changedFiles.filter(f => f.endsWith('.java'))
const vueFiles = changedFiles.filter(f => f.endsWith('.vue'))
const xmlFiles = changedFiles.filter(f => f.endsWith('.xml') || f.endsWith('.mapper'))
const sqlFiles = changedFiles.filter(f => f.endsWith('.sql'))
const jsFiles = changedFiles.filter(f => f.endsWith('.js') && !f.includes('node_modules'))

log(`Java: ${javaFiles.length} | Vue: ${vueFiles.length} | XML: ${xmlFiles.length} | SQL: ${sqlFiles.length} | JS: ${jsFiles.length}`)

phase('审查')

const findings = []

// ============================================================
// 审查 Java 文件
// ============================================================
for (const file of javaFiles) {
  try {
    const content = execSync(`git diff --cached "${file}"`, { encoding: 'utf-8', maxBuffer: 10 * 1024 * 1024 })
    const addedLines = content.split('\n').filter(l => l.startsWith('+') && !l.startsWith('+++'))

    // 1. 新增 Controller 是否缺少权限注解
    if (content.includes('@RestController') || content.includes('@RequestMapping')) {
      const hasPermissionAnnot = addedLines.some(l => l.includes('@RequirePermission') || l.includes('@PreAuthorize'))
      if (!hasPermissionAnnot && !file.includes('WebController') && !file.includes('/web/')) {
        findings.push({ file, line: '?', severity: 'P0', message: '新增 Controller 缺少权限注解 (@RequirePermission)' })
      }
    }

    // 2. `${}` SQL 拼接风险（SQL 注入）
    const dollarSqlMatches = addedLines.filter(l =>
      l.includes('${') && (l.toLowerCase().includes('sql') || l.toLowerCase().includes('select') || l.toLowerCase().includes('from') || l.toLowerCase().includes('where'))
    )
    if (dollarSqlMatches.length > 0) {
      findings.push({ file, line: '?', severity: 'P0', message: `存在 ${dollarSqlMatches.length} 处 "\${}" SQL 拼接，有 SQL 注入风险` })
    }

    // 3. 空 catch 块
    const emptyCatches = content.match(/catch\s*\(.*?\)\s*\{\s*\n?\s*\}/g)
    if (emptyCatches && emptyCatches.length > 0) {
      findings.push({ file, line: '?', severity: 'P1', message: `存在 ${emptyCatches.length} 处空 catch 块，异常被吞掉` })
    }

    // 4. System.out.println 泄漏
    const printlnMatches = addedLines.filter(l => l.includes('System.out.println') || l.includes('System.err.println'))
    if (printlnMatches.length > 0) {
      findings.push({ file, line: '?', severity: 'P2', message: `${printlnMatches.length} 处 System.out.println 残留，建议改用日志` })
    }

    // 5. TODO 残留
    const todoMatches = addedLines.filter(l => l.includes('TODO') || l.includes('FIXME'))
    if (todoMatches.length > 0) {
      findings.push({ file, line: '?', severity: 'P2', message: `${todoMatches.length} 处 TODO/FIXME 标记未处理` })
    }

    // 6. Controller 直接注入 Mapper 风险
    if (file.includes('Controller')) {
      const mapperInjections = addedLines.filter(l => l.includes('Mapper'))
      if (mapperInjections.length > 0) {
        findings.push({ file, line: '?', severity: 'P1', message: `Controller 中注入 Mapper（${mapperInjections.length} 处），应通过 Service/Biz 层访问` })
      }
    }

    // 7. @Transactional 缺失（Service 中涉及写操作的方法）
    if (file.includes('Service') && !file.includes('Test')) {
      const hasInsertUpdate = addedLines.some(l => l.includes('insert') || l.includes('update') || l.includes('delete') || l.includes('save'))
      const hasTransactionAnnot = addedLines.some(l => l.includes('@Transactional'))
      if (hasInsertUpdate && !hasTransactionAnnot) {
        findings.push({ file, line: '?', severity: 'P1', message: 'Service 包含写操作但未标注 @Transactional' })
      }
    }

  } catch (e) {
    // skip binary or unreadable files
  }
}

// ============================================================
// 审查 Vue 文件
// ============================================================
for (const file of vueFiles) {
  try {
    const content = execSync(`git diff --cached "${file}"`, { encoding: 'utf-8', maxBuffer: 10 * 1024 * 1024 })
    const addedLines = content.split('\n').filter(l => l.startsWith('+') && !l.startsWith('+++'))

    // 1. 直接 import axios
    const axiosImports = addedLines.filter(l => l.includes("import axios") || l.includes("from 'axios'") || l.includes('from "axios"'))
    if (axiosImports.length > 0) {
      findings.push({ file, line: '?', severity: 'P0', message: '直接 import axios，应使用统一的 request 实例' })
    }

    // 2. 内联样式
    const inlineStyles = addedLines.filter(l => l.includes('style="') || l.includes("style='"))
    if (inlineStyles.length > 0) {
      findings.push({ file, line: '?', severity: 'P1', message: `${inlineStyles.length} 处内联 style，建议使用 SCSS scoped 样式` })
    }

    // 3. TODO 残留
    const todoMatches = addedLines.filter(l => l.includes('TODO') || l.includes('FIXME'))
    if (todoMatches.length > 0) {
      findings.push({ file, line: '?', severity: 'P2', message: `${todoMatches.length} 处 TODO/FIXME 标记未处理` })
    }

    // 4. el-table 直接使用（admin 需用 BaseDataTable 替代）
    if (addedLines.some(l => l.includes('<el-table'))) {
      findings.push({ file, line: '?', severity: 'P2', message: '直接使用 el-table，建议使用项目封装的 BaseDataTable 组件' })
    }

  } catch (e) {
    // skip
  }
}

// ============================================================
// 审查 SQL 文件
// ============================================================
for (const file of sqlFiles) {
  try {
    const content = execSync(`git diff --cached "${file}"`, { encoding: 'utf-8', maxBuffer: 10 * 1024 * 1024 })

    // 1. DROP TABLE 危险操作
    if (content.includes('DROP TABLE') || content.includes('drop table')) {
      findings.push({ file, line: '?', severity: 'P0', message: '包含 DROP TABLE 操作，请确认是否预期' })
    }

    // 2. 缺少 IF NOT EXISTS
    const createTableLines = content.split('\n').filter(l => l.toUpperCase().includes('CREATE TABLE'))
    const missingIfNotExists = createTableLines.filter(l => !l.toUpperCase().includes('IF NOT EXISTS'))
    if (missingIfNotExists.length > 0) {
      findings.push({ file, line: '?', severity: 'P1', message: `${missingIfNotExists.length} 处 CREATE TABLE 缺少 IF NOT EXISTS` })
    }

  } catch (e) {
    // skip
  }
}

// ============================================================
// 审查 XML Mapper 文件
// ============================================================
for (const file of xmlFiles) {
  try {
    const content = execSync(`git diff --cached "${file}"`, { encoding: 'utf-8', maxBuffer: 10 * 1024 * 1024 })
    const addedLines = content.split('\n').filter(l => l.startsWith('+') && !l.startsWith('+++'))

    // 1. ${} SQL 拼接
    const dollarMatches = addedLines.filter(l => l.includes('${') && !l.includes('jdbcType'))
    if (dollarMatches.length > 0) {
      findings.push({ file, line: '?', severity: 'P0', message: `Mapper XML 中存在 ${dollarMatches.length} 处 "\${}" 拼接，应使用 #{} 防止 SQL 注入` })
    }

  } catch (e) {
    // skip
  }
}

// ============================================================
// 输出报告
// ============================================================
phase('报告')

if (findings.length === 0) {
  log('✅ 未发现需要关注的问题')
} else {
  const p0 = findings.filter(f => f.severity === 'P0')
  const p1 = findings.filter(f => f.severity === 'P1')
  const p2 = findings.filter(f => f.severity === 'P2')

  if (p0.length > 0) {
    log(`🔴 P0 严重问题 ${p0.length} 项：`)
    p0.forEach(f => log(`  - ${f.file}: ${f.message}`))
  }
  if (p1.length > 0) {
    log(`🟡 P1 建议修复 ${p1.length} 项：`)
    p1.forEach(f => log(`  - ${f.file}: ${f.message}`))
  }
  if (p2.length > 0) {
    log(`🔵 P2 值得关注 ${p2.length} 项：`)
    p2.forEach(f => log(`  - ${f.file}: ${f.message}`))
  }

  if (p0.length > 0) {
    log('⚠️ 发现 P0 级别问题，建议修复后再提交')
  }

  log(`\n📊 汇总：P0=${p0.length}  P1=${p1.length}  P2=${p2.length}  共 ${findings.length} 项`)
}

return { files: changedFiles.length, findings, p0Count: findings.filter(f => f.severity === 'P0').length }
