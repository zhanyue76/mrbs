package com.alex.mrbs.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;

import com.alex.mrbs.biz.ICompanyBiz;
import com.alex.mrbs.biz.IDepartmentAssignBiz;
import com.alex.mrbs.biz.IDepartmentBiz;
import com.alex.mrbs.biz.IMeetingroomBiz;
import com.alex.mrbs.biz.IUserBiz;
import com.alex.mrbs.entity.Company;
import com.alex.mrbs.entity.Department;
import com.alex.mrbs.entity.DepartmentAssign;
import com.alex.mrbs.entity.DepartmentRoomAssigns;
import com.alex.mrbs.entity.Meetingroom;
import com.alex.mrbs.entity.User;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class MeetingroomAction extends ActionSupport implements RequestAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Meetingroom> roomList;
	private IMeetingroomBiz meetingroomBiz;
	private IUserBiz userBiz;
	private IDepartmentAssignBiz departmentAssignBiz;
	
	private IDepartmentBiz departmentBiz;
	
	private ICompanyBiz companyBiz;

	//HttpServletRequest request = ServletActionContext.getRequest();
	private Map<String, Object> request;
	
	private Meetingroom meetingroom;
	
	private String result;
	
	private String roomid;
	

	public String getRoomid() {
		return roomid;
	}

	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Map<String, Object> getRequest() {
		return request;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
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

	public IDepartmentAssignBiz getDepartmentAssignBiz() {
		return departmentAssignBiz;
	}

	public void setDepartmentAssignBiz(IDepartmentAssignBiz departmentAssignBiz) {
		this.departmentAssignBiz = departmentAssignBiz;
	}

	public IUserBiz getUserBiz() {
		return userBiz;
	}

	public void setUserBiz(IUserBiz userBiz) {
		this.userBiz = userBiz;
	}


	
	
	
	/**
	 * at booking time,get roomList , user , departmentAssignList
	 */
	public String list() {
		
		List<DepartmentAssign> departmentAssignList = departmentAssignBiz.findByCompanyid(1);
		List<User> userlist =  userBiz.findAllNoPass();
		List<Department> deparmentList = departmentBiz.findAll();
		
		
		Map<String, Object> session = ActionContext.getContext().getSession();
		String userid = (String) session.get("login_id");
		List<Meetingroom> roomList = new ArrayList<Meetingroom>();

		User user = userBiz.findById(Integer.parseInt(userid));
		Set<DepartmentRoomAssigns> departmentRoomAssigns = user.getDepartment()
				.getDepartmentRoomAssigns();

		java.util.Iterator<DepartmentRoomAssigns> ite = departmentRoomAssigns
				.iterator();
		while (ite.hasNext()) {
			roomList.add(ite.next().getRoom());
		}
		
		if (null != roomList && null !=departmentAssignList && null != deparmentList) {
			request.put("roomList", roomList);
			request.put("userlist", userlist);
			request.put("departmentAssignList", departmentAssignList);
			request.put("departmentList", deparmentList);
			return SUCCESS;
		}		
		return NONE;
	}
	
	public String roomList(){
		try{
		Map<String,Object> session = ActionContext.getContext().getSession();
		request.put("department_id", (String)session.get("department_id"));
		request.put("company_id", (String)session.get("company_id"));
		request.put("login_id", (String)session.get("login_id"));
		List<Meetingroom> roomList = meetingroomBiz.findAll_2();
		Map<String, Object> ses = ActionContext.getContext().getSession();
		Integer companyId = (Integer)ses.get("company_id");
		departmentBiz.findAllByCompanyId(companyId);
		request.put("roomList", roomList);
		}catch(RuntimeException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return "roommanage";
		
	}
	
	public String creator(){
		List<Company> companyList = companyBiz.findAll();
		request.put("companyList", companyList);
		return "creator";
		
	}
	
	public String create(){
		meetingroomBiz.create(meetingroom);
		return "getRoomList";
	}
	public String updator(){
		Meetingroom room = meetingroomBiz.findRoom_2(meetingroom.getId());
		List<Company> companyList = companyBiz.findAll();
		request.put("companyList", companyList);
		request.put("room", room);
		return "updator";
	}
	
	public String update(){
		Meetingroom room = meetingroom;
		meetingroomBiz.update(room);
		return "getRoomList";
	}

	
	public String roomDayList(){

		Meetingroom room = meetingroomBiz.findById(Integer.parseInt(roomid));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("room", room);
		JSONObject js = JSONObject.fromObject(map);
		result = js.toString();
		return "succeed";
		
		
	}
	// public String execute() {
	// List<Meetingroom> roomList = meetingroomBiz.findAll();
	// HttpServletRequest request = ServletActionContext.getRequest();
	// if (null != roomList) {
	// request.setAttribute("roomList", roomList);
	// return SUCCESS;
	// }
	// return "error";
	// }

//	public HttpServletRequest getRequest() {
//		return request;
//	}

	

	public IMeetingroomBiz getMeetingroomBiz() {
		return meetingroomBiz;
	}

	public void setMeetingroomBiz(IMeetingroomBiz meetingroomBiz) {
		this.meetingroomBiz = meetingroomBiz;
	}

	public Meetingroom getMeetingroom() {
		return meetingroom;
	}

	public void setMeetingroom(Meetingroom meetingroom) {
		this.meetingroom = meetingroom;
	}

	public List<Meetingroom> getRoomList() {
		return roomList;
	}

	public void setRoomList(List<Meetingroom> roomList) {
		this.roomList = roomList;
	}
	
	

	
}
