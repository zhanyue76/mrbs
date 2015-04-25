package com.alex.mrbs.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.struts2.interceptor.RequestAware;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.alex.mrbs.biz.ICompanyBiz;
import com.alex.mrbs.biz.IDepartmentBiz;
import com.alex.mrbs.biz.IPrivilegeBiz;
import com.alex.mrbs.biz.IRoleBiz;
import com.alex.mrbs.biz.IUserBiz;
import com.alex.mrbs.biz.impl.CompanyBiz;
import com.alex.mrbs.entity.Company;
import com.alex.mrbs.entity.Department;
import com.alex.mrbs.entity.Permission;
import com.alex.mrbs.entity.Privilege;
import com.alex.mrbs.entity.Role;
import com.alex.mrbs.entity.RoleAssign;
import com.alex.mrbs.entity.User;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport implements RequestAware {

	private static final long serialVersionUID = 7868676799552952109L;
	private User user;
	private IUserBiz userBiz;
	private IRoleBiz roleBiz;
	private ICompanyBiz companyBiz;
	private IDepartmentBiz departmentBiz;
	private IPrivilegeBiz privilegeBiz;
	private Map<String, Object> request;
	private List<Role> roles;
	private List<Privilege> privileges;
	private List<Integer> chkRoles;
	private List<Integer> chkPrivileges;
	

	public UserAction() {
		System.out.println("UserAction Created");
	}

	/**
	 * �½��û�׼������ת
	 * 
	 * @return /User_creator.jsp
	 */
	public String creator() {
		
		Map<String,Object> session = ActionContext.getContext().getSession();
		List<Company> companylist = companyBiz.findAll();
		List<Department> departmentList = departmentBiz.findAll();
		request.put("department_id", (String)session.get("department_id"));
		request.put("company_id", (String)session.get("company_id"));
		request.put("departmentList", departmentList);
		request.put("companylist", companylist);
		if (roles != null) {
			roles.clear();
		}
		roles = roleBiz.findAll();
		if (privileges != null) {
			privileges.clear();
		}
		privileges = privilegeBiz.findAll();
		return "creator";
	}
	


	/**
	 * �½��û�
	 * 
	 * @return /commons/operation_success.jsp >> /User_list
	 */
	public String create() {

		if (chkRoles != null && chkRoles.size() > 0) {
			createWithUserRoleAssigns();
		} else {
			// �����û���ɫ
			// setUserRoleAssigns();
			// �����û�����Ȩ��
			setUserPrivileges();
			// �����û�
			userBiz.create(user);
		}
		return redirect();
	}

	/**
	 * ɾ���û�
	 * 
	 * @return /commons/operation_success.jsp >> /User_list
	 */
	public String delete() {
		// ͨ��ҳ�洫���ID���ҳ����û� , ִ��ɾ��
		userBiz.delete(userBiz.findById(user.getUser_id()));
		return redirect();
	}

	/**
	 * �����û�׼������ת
	 * 
	 * @return /User_updator.jsp
	 */
	public String updator() {
		
		List<Company> companylist = companyBiz.findAll();
		List<Department> departmentList = departmentBiz.findAll();
		request.put("departmentList", departmentList);
		request.put("companylist", companylist);
		user = userBiz.findById(user.getUser_id());
		request.put("department_id", user.getUser_departmentId());
		request.put("company_id", user.getUser_companyId());
		Iterator<RoleAssign> ras = user.getRoleAssigns().iterator();
		if (roles != null) {
			roles.clear();
		}
		roles = roleBiz.findAll();
		chkRoles = new ArrayList<Integer>();
		while (ras.hasNext()) {
			chkRoles.add(ras.next().getRole().getRole_id());
		}
		Iterator<Privilege> ps = user.getPrivileges().iterator();
		if (privileges != null) {
			privileges.clear();
		}
		privileges = privilegeBiz.findAll();
		chkPrivileges = new ArrayList<Integer>();
		while (ps.hasNext()) {
			chkPrivileges.add(ps.next().getPermission().getPer_id());
		}
		return "updator";
	}

	/**
	 * �����û�
	 * 
	 * @return /commons/operation_success.jsp >> /User_list
	 */
	public String update() {
		// �ж��û��Ƿ�ѡ���˽�ɫ
		if (chkRoles == null || chkRoles.size() == 0) {
			// �����û�
			userBiz.update(user);
		} else {

			//createWithUserRoleAssigns();
			updateWithUserRoleAssigns();

		}

		// �����ɹ���ʾ���ض����ֹ�ظ��ύ
		return redirect();
	}

	/**
	 * �û��б�
	 * 
	 * @return /User_list.jsp
	 */
	public String list() {
		
		Map<String,Object> session = ActionContext.getContext().getSession();
		request.put("department_id", (String)session.get("department_id"));
		request.put("company_id", (String)session.get("company_id"));
		List<User> list = userBiz.findAll();
		if (null != list) {
			request.put("list", list);
			return "list";
		}
		return NONE;
	}

	/**
	 * �û���ϸ
	 * 
	 * @return /User_list.jsp
	 */
	public String detail() {
		user = userBiz.findById(user.getUser_id());
		Iterator<RoleAssign> ras = user.getRoleAssigns().iterator();
		if (roles != null) {
			roles.clear();
		}
		roles = roleBiz.findAll();
		chkRoles = new ArrayList<Integer>();
		while (ras.hasNext()) {
			chkRoles.add(ras.next().getRole().getRole_id());
		}
		Iterator<Privilege> ps = user.getPrivileges().iterator();
		if (privileges != null) {
			privileges.clear();
		}
		privileges = privilegeBiz.findAll();
		chkPrivileges = new ArrayList<Integer>();
		while (ps.hasNext()) {
			chkPrivileges.add(ps.next().getPermission().getPer_id());
		}
		return "detail";
	}

	
	private void createWithUserRoleAssigns() {
		if (chkRoles != null && chkRoles.size() != 0) {
			/*
			 * Set<RoleAssign> ras = new HashSet<RoleAssign>(); RoleAssign ra =
			 * null; Role role = null; for (Integer id : chkRoles) { role = new
			 * Role(); role.setRole_id(id); ra = new RoleAssign(role, user);
			 * ras.add(ra); } user.setRoleAssigns(ras);
			 */
			userBiz
					.create(user, chkRoles
							.toArray(new Integer[chkRoles.size()]));
		}
	}
	
	private void updateWithUserRoleAssigns() {
		if (chkRoles != null && chkRoles.size() != 0) {
			/*
			 * Set<RoleAssign> ras = new HashSet<RoleAssign>(); RoleAssign ra =
			 * null; Role role = null; for (Integer id : chkRoles) { role = new
			 * Role(); role.setRole_id(id); ra = new RoleAssign(role, user);
			 * ras.add(ra); } user.setRoleAssigns(ras);
			 */
			userBiz
			.update(user, chkRoles
					.toArray(new Integer[chkRoles.size()]));
		}
	}
	
	

	
	private void setUserPrivileges() {
		if (chkPrivileges != null && chkPrivileges.size() != 0) {
			Set<Privilege> ps = new HashSet<Privilege>();
			Privilege privilege = null;
			Permission permission = null;
			for (Integer id : chkPrivileges) {
				permission = new Permission();
				permission.setPer_id(id);
				privilege = new Privilege(permission, user);
				ps.add(privilege);
			}
			user.setPrivileges(ps);
		}
	}

	/**
	 * �ض����б�ҳ���ֹ�ظ��ύ
	 * 
	 * @return /commons/operation_success.jsp >> /User_list
	 */
	private String redirect() {
		request.put("type", "User");
		return "operation_success";
	}

	// Getters and Setters
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUserBiz(IUserBiz userBiz) {
		this.userBiz = userBiz;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setChkRoles(List<Integer> chkRoles) {
		this.chkRoles = chkRoles;
	}

	public List<Privilege> getPrivileges() {
		return privileges;
	}

	public void setChkPrivileges(List<Integer> chkPrivileges) {
		this.chkPrivileges = chkPrivileges;
	}

	public void setRoleBiz(IRoleBiz roleBiz) {
		this.roleBiz = roleBiz;
	}

	public void setPrivilegeBiz(IPrivilegeBiz privilegeBiz) {
		this.privilegeBiz = privilegeBiz;
	}

	public List<Integer> getChkRoles() {
		return chkRoles;
	}

	public List<Integer> getChkPrivileges() {
		return chkPrivileges;
	}

	public ICompanyBiz getCompanyBiz() {
		return companyBiz;
	}

	public void setCompanyBiz(ICompanyBiz companyBiz) {
		this.companyBiz = companyBiz;
	}

	public IDepartmentBiz getDepartmentBiz() {
		return departmentBiz;
	}

	public void setDepartmentBiz(IDepartmentBiz departmentBiz) {
		this.departmentBiz = departmentBiz;
	}

	public Map<String, Object> getRequest() {
		return request;
	}
	
}
