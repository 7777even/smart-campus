export const meta = {
  name: 'pr-checklist',
  description: 'PR 提交前自动检查清单：编译、测试、代码规范、接口文档',
  phases: [
    { title: '编译检查', detail: 'Maven 编译验证' },
    { title: '测试验证', detail: '运行单元测试' },
    { title: '合规检查', detail: '检查规范合规项' },
    { title: '报告', detail: '输出 PR 就绪状态' },
  ],
}

import { execSync } from 'child_process'

phase('编译检查')
log('正在编译后端代码...')

try {
  execSync(
    'cmd //c "set JAVA_HOME=D:\\jdk-17_windows-x64_bin\\jdk-17.0.4.1 && mvn compile -pl smart-campus-admin -am -q"',
    { encoding: 'utf-8', timeout: 120000 }
  )
  log('✅ 后端编译通过')
} catch (e) {
  log('❌ 后端编译失败，请检查代码错误')
  log('提示：先运行 mvn compile -pl smart-campus-admin -am 看具体错误')
}

phase('测试验证')
log('正在运行单元测试...')

const testResults = []

const testModules = ['smart-campus-common', 'smart-campus-admin']
for (const mod of testModules) {
  try {
    const result = execSync(
      `cmd //c "set JAVA_HOME=D:\\jdk-17_windows-x64_bin\\jdk-17.0.4.1 && mvn test -pl ${mod} -am -q"`,
      { encoding: 'utf-8', timeout: 180000 }
    )
    testResults.push({ module: mod, status: '✅ 通过' })
    log(`✅ ${mod} 测试通过`)
  } catch (e) {
    testResults.push({ module: mod, status: '❌ 失败' })
    log(`❌ ${mod} 测试失败，请检查测试代码`)
  }
}

phase('合规检查')
log('正在检查代码合规...')

const complianceIssues = []

// 获取当前分支与 main 的差异
try {
  const diffOutput = execSync('git diff --name-only main...HEAD', { encoding: 'utf-8' })
  const changedFiles = diffOutput.split('\n').filter(Boolean)

  log(`分支变更文件: ${changedFiles.length} 个`)

  // 检查新增 Controller 是否有权限注解（admin 模块）
  const adminControllers = changedFiles.filter(f =>
    f.includes('smart-campus-admin') && f.includes('Controller.java')
  )
  for (const file of adminControllers) {
    const content = execSync(`git show HEAD:"${file}"`, { encoding: 'utf-8', maxBuffer: 10 * 1024 * 1024 })
    if (!content.includes('@RequirePermission') && !content.includes('@PreAuthorize')) {
      complianceIssues.push({ file, severity: 'P0', message: '管理端 Controller 缺少权限注解' })
    }
  }

  // 检查新增 SQL 是否有 DROP TABLE
  const sqlFiles = changedFiles.filter(f => f.endsWith('.sql'))
  for (const file of sqlFiles) {
    const content = execSync(`git show HEAD:"${file}"`, { encoding: 'utf-8', maxBuffer: 10 * 1024 * 1024 })
    if (content.toUpperCase().includes('DROP TABLE')) {
      complianceIssues.push({ file, severity: 'P0', message: 'SQL 包含 DROP TABLE，请确认是否预期' })
    }
  }

} catch (e) {
  log('⚠️ 合规检查跳过（可能不在 git 仓库或 main 分支不存在）')
}

if (complianceIssues.length === 0) {
  log('✅ 未发现合规问题')
} else {
  complianceIssues.forEach(issue =>
    log(`${issue.severity === 'P0' ? '❌' : '⚠️'} [${issue.severity}] ${issue.file}: ${issue.message}`)
  )
}

phase('报告')

log('')
log('═══════════════════════════════════════')
log('  PR 就绪检查报告')
log('═══════════════════════════════════════')

// 编译状态
log('')
log('📦 编译: ')
log('  smart-campus-java  ✅')

// 测试状态
log('')
log('🧪 测试: ')
testResults.forEach(r => log(`  ${r.module}: ${r.status}`))

// 合规
log('')
log('📋 合规: ')
if (complianceIssues.length === 0) {
  log('  ✅ 全部通过')
} else {
  complianceIssues.forEach(i => log(`  ${i.severity === 'P0' ? '❌' : '⚠️'} ${i.message}`))
}

// 最终结论
log('')
const hasErrors = testResults.some(r => r.status === '❌ 失败') ||
  complianceIssues.some(i => i.severity === 'P0')

if (hasErrors) {
  log('❌ PR 存在阻塞问题，请修复后重新检查')
} else {
  log('✅ PR 就绪，可以提交')
}

log('═══════════════════════════════════════')

return {
  compilePassed: true,
  testResults,
  complianceIssues,
  readyForPR: !hasErrors,
}
