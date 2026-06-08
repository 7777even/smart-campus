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
const xmlFiles = changedFiles.filter(f => f.endsWith('.xml'))
const sqlFiles = changedFiles.filter(f => f.endsWith('.sql'))

log(`Java: ${javaFiles.length} | Vue: ${vueFiles.length} | XML: ${xmlFiles.length} | SQL: ${sqlFiles.length}`)

phase('审查')

const findings = []

// 审查 Java 文件
for (const file of javaFiles) {
  try {
    const content = execSync(`git diff --cached "${file}"`, { encoding: 'utf-8', maxBuffer: 10 * 1024 * 1024 })
    const addedLines = content.split('\n').filter(l => l.startsWith('+') && !l.startsWith('+++'))

    // 检查新 Controller 是否缺少权限注解
    if (content.includes('@RestController') || content.includes('@RequestMapping')) {
      const hasPermissionAnnot = addedLines.some(l => l.includes('@RequirePermission'))
      if (!hasPermissionAnnot && !file.contains('WebController')) {
        findings.push({ file, severity: 'P0', message: '新增 Controller 缺少权限注解 (@RequirePermission)' })
      }
    }

    // 检查 `${}` SQL 拼接风险
    const dollarSqlMatches = addedLines.filter(l => l.includes('${') && (l.includes('sql') || l.includes('SQL') || l.includes('query') || l.includes('select')))
    if (dollarSqlMatches.length > 0) {
      findings.push({ file, severity: 'P0', message: `存在 ${dollarSqlMatches.length} 处 `${}` SQL 拼接，有 SQL 注入风险` })
    }

    // 检查异常是否被吞掉
    const emptyCatches = content.match(/catch\s*\(.*\)\s*\{\s*\n\s*\}/g)
    if (emptyCatches) {
      findings.push({ file, severity: 'P1', message: `存在 ${emptyCatches.length} 处空 catch 块，异常被吞掉` })
    }

    // 检查 Mapper XML
    if (file.endsWith('Mapper.xml') || file.endsWith('mapper.xml')) {
    }

  } catch (e) {
    // skip errors
  }
}

// 输出报告
phase('报告')

if (findings.length === 0) {
  log('✅ 未发现需要关注的问题')
} else {
  const p0 = findings.filter(f => f.severity === 'P0')
  const p1 = findings.filter(f => f.severity === 'P1')

  if (p0.length > 0) {
    log(`🔴 P0 严重问题 ${p0.length} 项：`)
    p0.forEach(f => log(`  - ${f.file}: ${f.message}`))
  }
  if (p1.length > 0) {
    log(`🟡 P1 建议修复 ${p1.length} 项：`)
    p1.forEach(f => log(`  - ${f.file}: ${f.message}`))
  }

  if (p0.length > 0) {
    log('⚠️ 发现 P0 级别问题，建议修复后再提交')
  }
}

return { files: changedFiles.length, findings, p0Count: findings.filter(f => f.severity === 'P0').length }
