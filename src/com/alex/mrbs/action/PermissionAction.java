package com.alex.mrbs.action;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.interceptor.RequestAware;

import com.alex.mrbs.biz.IPermissionBiz;
import com.alex.mrbs.entity.Permission;
import com.alex.mrbs.entity.Privilege;
import com.alex.mrbs.entity.User;
import com.opensymphony.xwork2.ActionSupport;

public class PermissionAction extends ActionSupport implements RequestAware {
	private static final long serialVersionUID = 2956361101633982123L;
	private static final String OPERATION_SUCCESS = "operation_success";
	private Permission permission;
	private IPermissionBiz permissionBiz;
	private Map<String, Object> request;
	private Boolean privilege;

	/**
	 * 新建权限准备及跳转
	 * @return /Permission_creator.jsp
	 */
	public String creator() {
		return "creator";
	}

	/**
	 * 新建权限
	 * @return /commons/operation_success.jsp >> /Permission_list
	 */
	public String create() {
		// 设置为特殊权限
		setToPrivilete();
		// 保存权限
		permissionBiz.create(permission);
		return redirect();
	}

	/**
	 * 删除权限
	 * @return /commons/operation_success.jsp >> /Permission_list
	 */
	public String delete() {
		permissionBiz.delete(permissionBiz.findById(permission.getPer_id()));
		return redirect();
	}
	
	/**
	 * 更新权限准备及跳转
	 * @return /Permission_updator.jsp
	 */
	public String updator(){
		permission = permissionBiz.findById(permission.getPer_id());
		privilege = permission.getPrivileges().size() != 0 ? true : false;
		return "updator";
	}
	
	/**
	 * 权限信息
	 * @return /Permission_detail.jsp
	 */
	public String detail(){
		permission = permissionBiz.findById(permission.getPer_id());
		privilege = permission.getPrivileges().size() != 0 ? true : false;
		return "detail";
	}
	
	public String update(){
		setToPrivilete();
		permissionBiz.update(permission);
		return redirect();
	}

	/**
	 * 权限列表
	 * @return /Permission_list.jsp
	 */
	public String list() {
		List<Permission> list = permissionBiz.findAll();
		if (null != list) {
			request.put("list", list);
			return "list";
		}
		return NONE;
	}

	/**
	 * 根据页面已选项设置特殊权限
	 * 
	 */
	private void setToPrivilete(){
		if (privilege==true) {
			Set<Privilege> privileges = new HashSet<Privilege>();
			User user = new User();
			user.setUser_id(1);
			privileges.add(new Privilege(permission, user));
			permission.setPrivileges(privileges);
		}
	}

	/**
	 * 重定向到列表页面防止重复提交
	 * @return /commons/operation_success.jsp >> /Permission_list
	 */
	private String redirect() {
		request.put("type", "Permission");
		return OPERATION_SUCCESS;
	}
	
	// Getters and Setters

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public void setPermissionBiz(IPermissionBiz permissionBiz) {
		this.permissionBiz = permissionBiz;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}

	public Boolean isPrivilege() {
		return privilege;
	}
	
	public void setPrivilege(Boolean privilege) {
		this.privilege = privilege;
	}

}
