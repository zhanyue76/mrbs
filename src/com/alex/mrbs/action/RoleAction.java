package com.alex.mrbs.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.struts2.interceptor.RequestAware;

import com.alex.mrbs.biz.IPermissionAssignBiz;
import com.alex.mrbs.biz.IPermissionBiz;
import com.alex.mrbs.biz.IRoleBiz;
import com.alex.mrbs.entity.Permission;
import com.alex.mrbs.entity.PermissionAssign;
import com.alex.mrbs.entity.Role;
import com.opensymphony.xwork2.ActionSupport;

public class RoleAction extends ActionSupport implements RequestAware {
	private static final long serialVersionUID = 2223273712398100938L;
	private static final String OPERATION_SUCCESS = "operation_success";
	private Role role;
	private IRoleBiz roleBiz;
	private IPermissionBiz permissionBiz;
	private IPermissionAssignBiz permissionAssignBiz;
	private Map<String, Object> request;
	private List<Permission> permissions;
	private List<Integer> chkPermissions;
	private Boolean role_locked;

	/**
	 * �½���ɫ׼������ת
	 * 
	 * @return /User_creator.jsp
	 */
	public String creator() {
		if (permissions != null) {
			permissions.clear();
		}
		permissions = permissionBiz.findAll();
		return "creator";
	}

	/**
	 * �½���ɫ
	 * 
	 * @return /commons/operation_success.jsp >> /Role_list
	 */
	public String create() {

		if (chkPermissions == null || chkPermissions.size() == 0) {
			role.setRole_locked(role_locked);
			roleBiz.update(role);
		} else {

			setRolePermissions();
		}

		// roleBiz.create(role);
		return redirect();
	}

	/**
	 * ɾ���ɫ
	 * 
	 * @return /commons/operation_success.jsp >> /Role_list
	 */
	public String delete() {
		roleBiz.delete(roleBiz.findById(role.getRole_id()));
		return redirect();
	}

	/**
	 * ��ɫ�б�
	 * 
	 * @return /Role_list.jsp
	 */
	public String list() {
		List<Role> list = roleBiz.findAll();
		if (null != list) {
			request.put("list", list);
			return "list";
		}
		return NONE;
	}

	/**
	 * ���½�ɫ׼������ת
	 * 
	 * @return /User_updator.jsp
	 */
	public String updator() {
		role = roleBiz.findById(role.getRole_id());
		Iterator<PermissionAssign> pas = role.getPermissionAssigns().iterator();
		if (permissions != null) {
			permissions.clear();
		}
		permissions = permissionBiz.findAll();
		chkPermissions = new ArrayList<Integer>();
		while (pas.hasNext()) {
			Permission permssion = pas.next().getPermission();
			chkPermissions.add(permssion.getPer_id());
		}
		return "updator";
	}

	/**
	 * ��ɫ��ϸ
	 * 
	 * @return /User_updator.jsp
	 */
	public String detail() {
		role = roleBiz.findById(role.getRole_id());
		Iterator<PermissionAssign> pas = role.getPermissionAssigns().iterator();
		if (permissions != null) {
			permissions.clear();
		}
		permissions = permissionBiz.findAll();
		chkPermissions = new ArrayList<Integer>();
		while (pas.hasNext()) {
			chkPermissions.add(pas.next().getPermission().getPer_id());
		}
		return "detail";
	}

	/**
	 *
	 * 
	 * @return /commons/operation_success.jsp >> /User_list
	 */
	public String update() {

	
		updateRolePermissions();
		
		role.setRole_locked(role_locked);
		
		roleBiz.update(role);
		

		return redirect();
	}

	private void updateRolePermissions() {

		Set<PermissionAssign> ps = new HashSet<PermissionAssign>();
		PermissionAssign pa = null;
		Permission p = null;
		for (Integer id : chkPermissions) {
			p = new Permission();
			p.setPer_id(id);
			pa = new PermissionAssign(role, p);
			ps.add(pa);

			role.setPermissionAssigns(ps);
		}
	}

	/**
	 * ���ҳ����ѡ�����ý�ɫȨ��
	 */
	private void setRolePermissions() {
		/*
		 * if (chkPermissions != null && chkPermissions.size() != 0) {
		 * Set<PermissionAssign> ps = new HashSet<PermissionAssign>();
		 * PermissionAssign pa = null; Permission p = null; for (Integer id :
		 * chkPermissions) { p = new Permission(); p.setPer_id(id); pa = new
		 * PermissionAssign(role, p); ps.add(pa); }
		 * 
		 * 
		 * role.setPermissionAssigns(ps); }
		 */
		if (chkPermissions != null || chkPermissions.size() != 0) {
			role.setRole_locked(role_locked);
			permissionAssignBiz.create(role, chkPermissions
					.toArray(new Integer[chkPermissions.size()]));
		}
	}

	/**
	 * �ض����б�ҳ���ֹ�ظ��ύ
	 * 
	 * @return /commons/operation_success.jsp >> /Role_list
	 */
	private String redirect() {
		request.put("type", "Role");
		return OPERATION_SUCCESS;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setRoleBiz(IRoleBiz roleBiz) {
		this.roleBiz = roleBiz;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}

	public List<Integer> getChkPermissions() {
		return chkPermissions;
	}

	public void setChkPermissions(List<Integer> chkPermissions) {
		this.chkPermissions = chkPermissions;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissionBiz(IPermissionBiz permissionBiz) {
		this.permissionBiz = permissionBiz;
	}

	public Boolean isLocked() {
		return role_locked;
	}

	public Boolean getLocked() {
		return role_locked;
	}

	public Boolean getRole_locked() {
		return role_locked;
	}

	public void setRole_locked(Boolean roleLocked) {
		role_locked = roleLocked;
	}

	public void setLocked(Boolean locked) {
		this.role_locked = locked;
	}

	public IPermissionAssignBiz getPermissionAssignBiz() {
		return permissionAssignBiz;
	}

	public void setPermissionAssignBiz(IPermissionAssignBiz permissionAssignBiz) {
		this.permissionAssignBiz = permissionAssignBiz;
	}

}
