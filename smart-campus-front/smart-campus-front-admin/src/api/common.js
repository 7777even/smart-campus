/**
 * 通用字典/下拉选项 API
 */
import request from './request'

export function getDepartments() {
  return request.get('/common/departments')
}

export function getMajorsByDepartment(departmentId) {
  return request.get('/common/majors', { params: { departmentId } })
}

export function getTeachers() {
  return request.get('/common/teachers')
}

export function getClasses() {
  return request.get('/common/classes')
}

export function getCourses() {
  return request.get('/common/courses')
}
