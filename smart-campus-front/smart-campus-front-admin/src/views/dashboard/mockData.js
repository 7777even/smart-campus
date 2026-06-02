export function getDashboardData() {
  return {
    overview: {
      totalStudents: 8642,
      totalTeachers: 523,
      totalDepartments: 12,
      totalMajors: 48,
      totalClasses: 186,
      totalCourses: 320
    },
    teaching: {
      semesterCourses: 156,
      completedExams: 28,
      pendingExams: 15,
      teacherWorkload: [
        { month: '3月', count: 125 },
        { month: '4月', count: 158 },
        { month: '5月', count: 186 },
        { month: '6月', count: 210 },
        { month: '7月', count: 175 },
        { month: '8月', count: 98 }
      ]
    },
    studentDistribution: {
      departments: [
        { name: '计算机科学与技术学院', value: 1860 },
        { name: '经济管理学院', value: 1250 },
        { name: '电子信息工程学院', value: 1100 },
        { name: '数学与统计学院', value: 950 },
        { name: '外国语学院', value: 820 },
        { name: '机械工程学院', value: 780 },
        { name: '建筑与土木工程学院', value: 720 },
        { name: '医学院', value: 690 },
        { name: '材料科学与工程学院', value: 650 },
        { name: '艺术学院', value: 540 },
        { name: '体育学院', value: 460 },
        { name: '马克思主义学院', value: 320 }
      ],
      grades: [
        { grade: '大一', count: 2350 },
        { grade: '大二', count: 2180 },
        { grade: '大三', count: 2010 },
        { grade: '大四', count: 1850 },
        { grade: '研究生', count: 1252 }
      ],
      gender: { male: 55, female: 45 },
      growth: [
        { year: '2019', count: 6200 },
        { year: '2020', count: 6580 },
        { year: '2021', count: 6980 },
        { year: '2022', count: 7450 },
        { year: '2023', count: 8020 },
        { year: '2024', count: 8642 }
      ]
    },
    resource: {
      total: 2340,
      totalDownloads: 45620,
      uploadTrend: [
        { month: '1月', count: 85 },
        { month: '2月', count: 62 },
        { month: '3月', count: 120 },
        { month: '4月', count: 98 },
        { month: '5月', count: 145 },
        { month: '6月', count: 132 }
      ],
      hotResources: [
        { name: '高等数学-期末考试试卷', downloads: 1240, dept: '数学学院' },
        { name: '大学英语四级词汇表', downloads: 980, dept: '外语学院' },
        { name: 'Python程序设计基础教程', downloads: 876, dept: '计算机学院' },
        { name: '数据结构与算法习题集', downloads: 765, dept: '计算机学院' },
        { name: '线性代数典型例题解析', downloads: 654, dept: '数学学院' },
        { name: '大学物理实验报告模板', downloads: 543, dept: '电信学院' },
        { name: '计算机组成原理复习提纲', downloads: 487, dept: '计算机学院' },
        { name: '马克思主义基本原理概论', downloads: 432, dept: '马克思学院' },
        { name: 'C语言程序设计实验指导', downloads: 398, dept: '计算机学院' },
        { name: '概率论与数理统计公式手册', downloads: 354, dept: '数学学院' }
      ]
    },
    exam: {
      countTrend: [
        { month: '3月', count: 8 },
        { month: '4月', count: 14 },
        { month: '5月', count: 22 },
        { month: '6月', count: 32 },
        { month: '7月', count: 26 },
        { month: '8月', count: 6 }
      ],
      avgScores: [
        { name: '数学学院', value: 82.3 },
        { name: '外语学院', value: 80.1 },
        { name: '计算机学院', value: 78.5 },
        { name: '经管学院', value: 76.2 },
        { name: '电信学院', value: 74.8 },
        { name: '机械学院', value: 72.6 }
      ],
      passRate: 92.5,
      excellenceRate: 28.3
    },
    system: {
      onlineUsers: 128,
      todayLogins: 567,
      weeklyActive: 3240,
      cpuUsage: 45,
      memoryUsage: 62,
      diskUsage: 55,
      status: 'normal',
      services: [
        { name: 'Web服务', status: 'normal' },
        { name: '数据库', status: 'normal' },
        { name: '缓存服务', status: 'normal' },
        { name: '文件存储', status: 'warning' }
      ]
    }
  }
}
