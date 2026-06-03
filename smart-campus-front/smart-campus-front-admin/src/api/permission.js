/**
 * 权限管理 API
 */
import request from './request'

// ---------- 用户管理 ----------
export function getUsers(params) {
  return request.get('/permissions/users', { params })
}

export function updateUser(id, data) {
  return request.put(`/permissions/users/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/permissions/users/${id}`)
}

export function toggleUserStatus(id) {
  return request.put(`/permissions/users/${id}/toggle-status`)
}

// ---------- 角色管理 ----------
export function getRoles() {
  return request.get('/permissions/roles')
}

// ---------- 权限/菜单 ----------
export function getPermissionTree() {
  return request.get('/permissions/menus')
}

export function assignPermissions(roleId, permissionIds) {
  return request.put(`/permissions/roles/${roleId}/permissions`, { permissionIds })
}
