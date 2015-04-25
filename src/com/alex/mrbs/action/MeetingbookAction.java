package com.alex.mrbs.action;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.bytecode.Descriptor.Iterator;

import net.sf.json.JSONObject;
import org.apache.struts2.interceptor.RequestAware;
import org.hibernate.type.CalendarType;

import com.alex.mrbs.biz.IMeetingbookBiz;
import com.alex.mrbs.biz.IMeetingroomBiz;
import com.alex.mrbs.biz.IRoomDaysBiz;
import com.alex.mrbs.biz.IUserBiz;
import com.alex.mrbs.biz.impl.RoomDaysBiz;
import com.alex.mrbs.entity.DepartmentRoomAssigns;
import com.alex.mrbs.entity.Meetingbook;
import com.alex.mrbs.entity.Meetingroom;
import com.alex.mrbs.entity.RoomDays;
import com.alex.mrbs.entity.User;
import com.alex.mrbs.util.ExcelUtil;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class MeetingbookAction extends ActionSupport implements RequestAware {

	private static final long serialVersionUID = 7868676799552952109L;

	private File file;
	private Meetingbook meetingbook;
	private IMeetingbookBiz meetingbookBiz;
	private IMeetingroomBiz meetingroomBiz;
	private IUserBiz userBiz;
	private Map<String, Object> request;
	private String result;
	private IRoomDaysBiz roomDaysBiz;



	private String roomid;
	private String startdate;
	private String enddate;

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public IUserBiz getUserBiz() {
		return userBiz;
	}

	public void setUserBiz(IUserBiz userBiz) {
		this.userBiz = userBiz;
	}
	public IRoomDaysBiz getRoomDaysBiz() {
		return roomDaysBiz;
	}

	public void setRoomDaysBiz(IRoomDaysBiz roomDaysBiz) {
		this.roomDaysBiz = roomDaysBiz;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getRoomid() {
		return roomid;
	}

	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}

	public Map<String, Object> getRequest() {
		return request;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public MeetingbookAction() {
		System.out.println("MeetingBookAction Created");
	}

	/**
	 * �½��A��
	 * 
	 */
	public String create() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			
			Map<String, Object> session = ActionContext.getContext()
					.getSession();
			if (meetingbook != null) {
				RoomDays roomDay = roomDaysBiz.findByCompanyId(Integer.parseInt((String)session.get("company_id")));
				String startimestr = roomDay.getBookStartTime();
				String endtimestr = roomDay.getBookEndTime();
				//判断时间是否在预订时间内
				if(!timeValidate(startimestr, endtimestr)){
					map.put("result", true);
					map.put("reason", "timeValidate");
					JSONObject js = JSONObject.fromObject(map);
					result = js.toString();
					return "succeed";
				}
			//	systime = df.parse(systimestr);
				
				//if(systime.before(youDate)){
				/*
				 * Meetingroom room = meetingroomBiz.findRoom_2(meetingbook
				 * .getBookmeeting()); Integer approve = room.getApprove();
				 */
				meetingbook.setState("1");

				
				String username = (String) session.get("username");
				if (null != username && !username.equals("")) {
					meetingbook.setBookername(username);
				}
				boolean isExist = meetingbookBiz.isExistfindOverlapping(meetingbook);
				if (!isExist == true) {
					meetingbookBiz.create(meetingbook);
					map.put("result", true);
					
				}else
				{
					map.put("result", true);
					map.put("reason", "timeError");
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map.put("result", false);
		}
		JSONObject js = JSONObject.fromObject(map);
		result = js.toString();
		return "succeed";

	}
	
	public String CreateWithList() throws Exception{
		ExcelUtil excelUtil = new ExcelUtil(file.getPath());
		List<Meetingbook> list = excelUtil.loadExcel();
		meetingbookBiz.createWithList(list);
		System.gc();
		return "back";
	}

	/*
	 * 判断时间是否在预订时间内
	 */
	private boolean timeValidate(String startimestr, String endtimestr)
			throws ParseException {
		DateFormat df = new SimpleDateFormat("HH:mm:ss");//设置显示格式
		Date systime = new Date();
		Date starttime = df.parse(startimestr);
		Date endtime = df.parse(endtimestr);
		String systimestr =df.format(systime);
		systime = df.parse(systimestr);
		if(systime.before(starttime)||systime.after(endtime)){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 审批预订流程
	 * 
	 * @return
	 */
	public String createFromApprove() {
		meetingbook.setState("0");
		Calendar calendar = Calendar.getInstance();
		Map<String, Object> session = ActionContext.getContext().getSession();
		String username = (String) session.get("username");
		meetingbook.setBookername(username);
		Date date = calendar.getTime();
		meetingbook.setBooktime(date.toString());
		meetingbook.setState("0");
		boolean isExist = meetingbookBiz.isExistfindOverlapping(meetingbook);
		if (!isExist) {
			meetingbookBiz.create(meetingbook);
			return "getFormList";
		} else {
			return "getApproveFormList";
		}

	}

	/**
	 *
	 * 
	 */
	public String delete() {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		Meetingbook meetingItem = meetingbookBiz.findById(meetingbook.getId());
		String bookername = meetingItem.getBookername();
		String username = (String)ActionContext.getContext().getSession().get("username");
		if(!bookername.equals(username)){
			map.put("result", false);
			result = JSONObject.fromObject(map).toString();
			return "succeed";
		}
		Boolean bool = meetingbookBiz.delete(meetingbookBiz
				.findById(meetingbook.getId()));
		
		map.put("result", bool);
		result = JSONObject.fromObject(map).toString();
		return "succeed";
	}

	public String updator() {
		return "updator";
	}

	/**
	 * ���»���Ԥ��
	 * 
	 */
	public String update() {

		return redirect();
	}

	/**
	 * ����Ԥ���б�
	 * 
	 */
	public String list() {
		List<Meetingbook> booklist = meetingbookBiz.findAll();
		// List<Meetingbook> booklist = meetingbookBiz.findByState("1");
		if (null != booklist) {
			request.put("booklist", booklist);
			return SUCCESS;
		}
		return NONE;
	}

	/**
	 * 获取预定会议信息和会议室信息
	 * 
	 * @return json数据格式
	 */
	public String maplist() {
		// System.out.println(roomid);
		List<Meetingbook> booklist = null;
		if (roomid != null && !("".equals(roomid))) {
			// booklist = meetingbookBiz.findByRoomIDAndState(
			// Integer.valueOf(roomid), "1");
			booklist = meetingbookBiz.findByRoomID(Integer.valueOf(roomid));

		}

		Map<String, Object> session = ActionContext.getContext().getSession();
		request.put("department_id", (String) session.get("department_id"));
		request.put("company_id", (String) session.get("company_id"));
		String userid = (String) session.get("login_id");
		List<Meetingroom> roomlist = new ArrayList<Meetingroom>();

		User user = userBiz.findById(Integer.parseInt(userid));
		Set<DepartmentRoomAssigns> departmentRoomAssigns = user.getDepartment()
				.getDepartmentRoomAssigns();

		java.util.Iterator<DepartmentRoomAssigns> ite = departmentRoomAssigns
				.iterator();
		while (ite.hasNext()) {
			Meetingroom room = ite.next().getRoom();
			if(room.getStatus()==0)//非冻结
				roomlist.add(room);
		}

		// roomlist = meetingroomBiz.findAll();
		Integer bookDay = null;
		if (roomlist != null && roomlist.size() > 0) {
			Integer companyId = roomlist.get(0).getCompany_id();
			bookDay = roomDaysBiz.getDayByCompanyId(companyId);

		}

		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject js = null;

		// booklist.toArray(meetingbook)
		try {

			map.put("book", booklist);
			map.put("room", roomlist);
			map.put("bookDay", bookDay);

			js = JSONObject.fromObject(map);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		result = js.toString();
		return "succeed";

	}

	/**
	 * ������ϸ
	 */
	public String detail() {
		// user = userBiz.findById(user.getId());
		// Iterator<RoleAssign> ras = user.getRoleAssigns().iterator();
		// if (roles != null) {
		// roles.clear();
		// }
		// roles = roleBiz.findAll();
		// chkRoles = new ArrayList<Integer>();
		// while (ras.hasNext()) {
		// chkRoles.add(ras.next().getRole().getId());
		// }
		// Iterator<Privilege> ps = user.getPrivileges().iterator();
		// if (privileges != null) {
		// privileges.clear();
		// }
		// privileges = privilegeBiz.findAll();
		// chkPrivileges = new ArrayList<Integer>();
		// while (ps.hasNext()) {
		// chkPrivileges.add(ps.next().getPermission().getId());
		// }
		return "detail";
	}

	/**
	 * �ض����б�ҳ���ֹ�ظ��ύ
	 * 
	 */
	private String redirect() {
		request.put("type", "Meetingbook");
		return "operation_success";
	}

	public Meetingbook getMeetingbook() {
		return meetingbook;
	}

	public void setMeetingbook(Meetingbook meetingbook) {
		this.meetingbook = meetingbook;
	}

	public IMeetingbookBiz getMeetingbookBiz() {
		return meetingbookBiz;
	}

	public void setMeetingbookBiz(IMeetingbookBiz meetingbookBiz) {
		this.meetingbookBiz = meetingbookBiz;
	}

	public IMeetingroomBiz getMeetingroomBiz() {
		return meetingroomBiz;
	}

	public void setMeetingroomBiz(IMeetingroomBiz meetingroomBiz) {
		this.meetingroomBiz = meetingroomBiz;
	}

}
