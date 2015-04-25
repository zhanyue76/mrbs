package com.alex.mrbs.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;

import com.alex.mrbs.biz.IRoomDaysBiz;
import com.alex.mrbs.biz.impl.CompanyBiz;
import com.alex.mrbs.entity.Company;
import com.alex.mrbs.entity.RoomDays;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class RoomDaysAction extends ActionSupport implements RequestAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2975651879421557667L;

	Map<String,Object> request;
	
	private IRoomDaysBiz roomDaysBiz;
	
	private RoomDays roomDays;
	
	private CompanyBiz companyBiz;
	
	private String result;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public CompanyBiz getCompanyBiz() {
		return companyBiz;
	}

	public void setCompanyBiz(CompanyBiz companyBiz) {
		this.companyBiz = companyBiz;
	}

	public RoomDays getRoomDays() {
		return roomDays;
	}

	public void setRoomDays(RoomDays roomDays) {
		this.roomDays = roomDays;
	}

	public IRoomDaysBiz getRoomDaysBiz() {
		return roomDaysBiz;
	}

	public void setRoomDaysBiz(IRoomDaysBiz roomDaysBiz) {
		this.roomDaysBiz = roomDaysBiz;
	}

	public Map<String, Object> getRequest() {
		return request;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}
	
	public String roomDayList(){
		
		Map<String,Object> session = ActionContext.getContext().getSession();
		
		String company_id_temp = (String)session.get("company_id");
		Company company = companyBiz.findCompanyById(Integer.parseInt(company_id_temp));
		request.put("department_id", (String)session.get("department_id"));
		request.put("company_id", company_id_temp);
		request.put("company_name", company.getCompany_name());
		RoomDays roomDay = null;
		try{
			 roomDay =  roomDaysBiz.findByCompanyId(Integer.parseInt(company_id_temp));
		}catch(Exception e){
			return "roomDayCreator";
		}
		if(roomDay!=null){
			List<RoomDays> list = new ArrayList<RoomDays>();
			list.add(roomDay);
			request.put("roomDaysList", list);
			return "roomDayList";
		}else{
			return "roomDayCreator";
		}

	}
	
	public String createDays() throws Exception{
		Integer company_id = roomDays.getCompany_id();
		roomDaysBiz.create(roomDays);
		HttpServletResponse response = ServletActionContext.getResponse();
		//response.sendRedirect("GetroomDayList");
		//response.getWriter("GetroomDayList");
		return null;
		
	}
	
	public String updateDays(){
		Map<String, Object> map = new HashMap<String, Object>();
		try{
			roomDaysBiz.update(roomDays);
			map.put("result", true);
		}catch(RuntimeException e){
			map.put("result", false);
		}
		
		JSONObject js = JSONObject.fromObject(map);
		result = js.toString();
		return "succeed";
	
	}

}
