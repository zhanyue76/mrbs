package com.alex.mrbs.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.interceptor.RequestAware;

import com.alex.mrbs.biz.impl.UserBiz;
import com.alex.mrbs.entity.PermissionAssign;
import com.alex.mrbs.entity.Role;
import com.alex.mrbs.entity.RoleAssign;
import com.alex.mrbs.entity.User;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport implements RequestAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7573240901837179122L;

	Map<String, Object> request;
	private User user;
	private UserBiz userBiz;

	public UserBiz getUserBiz() {
		return userBiz;
	}

	public void setUserBiz(UserBiz userBiz) {
		this.userBiz = userBiz;
	}

	public void setRequest(Map<String, Object> request) {
		// TODO Auto-generated method stub
		this.request = request;

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String login() {
		
		
		Map<String, Object> ses = ActionContext.getContext().getSession();
		if(ses.get("login_id")!=null){
			ses.clear();
			return "login";
			
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		int date = calendar.get(Calendar.DATE);
		String dateStr = date + "";
		System.out.println("当前日期:" + date);
		
		if(user==null){
			return "login";
		}
		User userBean = userBiz.GetUserByName(user.getUser_account());
		if(userBean==null){
			return "login";
		}
		if (userBean.getUser_password().equals(user.getUser_password())) {
			Map<String, Object> session = ActionContext.getContext()
					.getSession();
			session.put("login_id",userBean.getUser_id().toString());
			session.put("username", userBean.getUser_account().toString());
			session.put("department_id", userBean.getUser_departmentId().toString());
			session.put("company_id", userBean.getUser_companyId().toString());
			System.out.println(" 登陆成功 ！");
			return "admin";
		} else
			System.out.println("登录失败!");
		return "login";

	}

	public String logout() {

		Map<String, Object> session = ActionContext.getContext().getSession();
		session.clear();
		System.out.println(" 注销成功 ！");
		return "login";

	}

	/**
	 * 
	 * @return 返回做导航页数据
	 */
	public String getLeftPage() {
		Map<String, Object> session = ActionContext.getContext().getSession();
		try {

			String username = (String) session.get("username");

			User user = userBiz.GetUserByName(username);
			Map<String, Object> list = userBiz.GetUserPermissionList(user);
			request.put("list", list);
			return "LeftPage";

		} catch (RuntimeException e) {
			return "login";
		}

	}

}
