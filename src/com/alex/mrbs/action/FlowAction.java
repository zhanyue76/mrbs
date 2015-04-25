package com.alex.mrbs.action;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;

import com.alex.mrbs.biz.IMeetingroomBiz;
import com.alex.mrbs.biz.impl.MeetingbookBiz;
import com.alex.mrbs.biz.impl.WorkflowBiz;
import com.alex.mrbs.entity.Meetingbook;
import com.alex.mrbs.entity.Meetingroom;
import com.alex.mrbs.entity.WorkflowBean;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class FlowAction extends ActionSupport implements RequestAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6872082211832743277L;

	private WorkflowBiz workflowBiz;

	private WorkflowBean workflowBean;

	private MeetingbookBiz meetingbookBiz;

	private String bookstatus;

	private String taskId;// taskId流程任务ID

	private String bookId;// 会议申请ID

	private String comment;

	private String deploymentId;// 流程资源定义

	private IMeetingroomBiz meetingroomBiz;

	public IMeetingroomBiz getMeetingroomBiz() {
		return meetingroomBiz;
	}

	public void setMeetingroomBiz(IMeetingroomBiz meetingroomBiz) {
		this.meetingroomBiz = meetingroomBiz;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getBookstatus() {
		return bookstatus;
	}

	public void setBookstatus(String bookstatus) {
		this.bookstatus = bookstatus;
	}

	public WorkflowBiz getWorkflowBiz() {
		return workflowBiz;
	}

	public void setWorkflowBiz(WorkflowBiz workflowBiz) {
		this.workflowBiz = workflowBiz;
	}

	public WorkflowBean getWorkflowBean() {
		return workflowBean;
	}

	public void setWorkflowBean(WorkflowBean workflowBean) {
		this.workflowBean = workflowBean;
	}

	private Map<String, Object> request;

	public void setRequest(Map<String, Object> request) {

		this.request = request;

	}

	public String newploy() {
		return "newploy";

	}

	public String deploySubmit() {
		String filename = workflowBean.getFilename();
		File file = workflowBean.getFile();
		try {
			workflowBiz.saveNewDeploye(file, filename);
			System.out.println("发布成功");

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return "getMyDeployList";

	}

	public String deployList() {
		// 1:查询部署对象信息，对应表（act_re_deployment）
		List<Deployment> delpoyList = workflowBiz.getDeployList();
		// 2：查询流程定义信息，对应表 act_re_procdef
		List<ProcessDefinition> pdList = workflowBiz
				.findProcessDefinitionList();
		request.put("delpoyList", delpoyList);
		request.put("pdList", pdList);
		return "myDeployList";
	}

	public String deployDelete() {
		workflowBiz.deleteProcessDefinitionByDeploymentId(deploymentId);
		return "getDeployList";
	}

	public String redirect() {
		request.put("type", "User");
		return "operation_success";
	}

	public String startProcess() {

		// Map<String, Object> session =
		// ActionContext.getContext().getSession();

		// workflowBiz.saveStartProcess(bookId);
		workflowBiz.saveStartProcessWithUsername(bookId);
		return "getFormList";
	}

	public String taskList() {
		Map<String, Object> session = ActionContext.getContext().getSession();
		String assignName = (String) session.get("username");
		List<Task> mytaskList = workflowBiz.queryTask(assignName);
		request.put("list", mytaskList);
		return "taskList";
	}

	public String completeTask() {
		Meetingbook book = meetingbookBiz.findById(Integer.parseInt(bookId));
		boolean isExist = meetingbookBiz.isExistfindOverlapping(book);

		if (isExist) {
			book.setState("0");
			meetingbookBiz.update(book);
			workflowBiz.DeleteTaskById(Integer.parseInt(taskId));
			return "taskList";
		} else {

			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("agree", true);

			if (comment != null && !comment.equals(""))
				workflowBiz.completeTask(taskId, bookId, comment, variables);
			else
				workflowBiz.completeTask(taskId, bookId, variables);
			return "taskList";
		}
	}

	/**
	 * 
	 * @return 当前登陆者的已接受，但未审批的流程
	 */
	public String getTaskList() {

		Map<String, Object> session = ActionContext.getContext().getSession();
		List<Task> taskList = workflowBiz.queryTask((String) session
				.get("username"));
		// request.put("list", taskList);
		Map<String, Meetingbook> listMap = new HashMap<String, Meetingbook>();

		for (Task task : taskList) {
			String taskId = task.getId();
			String processInstanceId = task.getProcessInstanceId();// 获取流程实例ID
			Meetingbook meetingBook = workflowBiz
					.getMeetingbook(processInstanceId);// 获取预定ID
			listMap.put(taskId, meetingBook);

		}
		request.put("list", listMap);
		for (Task task : taskList) {
			System.out.println(task.getId() + "-----------" + task.getName());
		}

		return "taskList";

	}

	/**
	 * 领取任务
	 * 
	 * @return
	 */
	public String ClaimTask() {
		Map<String, Object> session = ActionContext.getContext().getSession();
		// HttpServletRequest request =
		// (HttpServletRequest)ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
		// String taskId = (String)request.getAttribute("id");

		String username = (String) session.get("username");
		workflowBiz.claimTask(taskId, username);
		return "GettaskList";
	}

	/*
	 * 获取科可领取流程单
	 */
	/*
	 * public String getCandidatGroup() { List<Task> list =
	 * workflowBiz.getCandidatGroup("user1"); if (list != null && list.size() >
	 * 0) { request.put("Candidat", list); } for (Task task : list) {
	 * System.out.println(task.getAssignee() + "-" + task.getId()); } return
	 * "candidatGroup"; }
	 */

	public String viewTaskForm() {

		workflowBiz.saveStartProcess(workflowBean);
		return "taskFomr";
	}

	public MeetingbookBiz getMeetingbookBiz() {
		return meetingbookBiz;
	}

	public void setMeetingbookBiz(MeetingbookBiz meetingbookBiz) {
		this.meetingbookBiz = meetingbookBiz;
	}

	/**
	 * 获取不同状态的预订列表 status=0->表示未提交，2表示审核中，1表示审核成功
	 * 
	 * @return 获取申请表单
	 */
	public String formList() {

		Map<String, Object> session = ActionContext.getContext().getSession();
		String username = (String) session.get("username");
		List<Meetingbook> list = new ArrayList<Meetingbook>();
		List<Meetingbook> list0 = workflowBiz
				.findHis_meetingbookIdList_ByName(username);

		List<Meetingbook> list1 = meetingbookBiz.findByStatusAndBookUser(
				username, "0");
		if (list0 != null && list0.size() > 0) {
			for (Meetingbook m : list0) {
				list.add(m);
			}
		}

		if (list1 != null && list1.size() > 0) {
			for (Meetingbook m : list1) {
				list.add(m);
			}
		}
		request.put("list", list);
		return "formList";
	}

	public String manageList() {
		return "manage";

	}

	/**
	 * 
	 * @return 可领取审批单
	 */
	public String candidatGroup() {
		Map<String, Object> session = ActionContext.getContext().getSession();
		String username = (String) session.get("username");
		List<Task> list = workflowBiz.getCandidatGroup(username);
		Map<String, Meetingbook> listMap = new HashMap<String, Meetingbook>();
		for (Task task : list) {
			String taskId = task.getId();// 任务ID
			String processInstanceId = task.getProcessInstanceId();// 获取流程实例ID
			Meetingbook meetingBook = workflowBiz
					.getMeetingbook(processInstanceId);
			listMap.put(taskId, meetingBook);
		}
		request.put("list", listMap);
		return "getCandidatGroup";
	}

	/**
	 * 查看审批单详情
	 * 
	 * @return 审批单form
	 */
	public String approveForm() {

		Meetingbook meetingbook = meetingbookBiz.findById(Integer
				.parseInt(bookId));
		request.put("taskId", taskId);
		request.put("meetingbook", meetingbook);
		List<Comment> commentList = workflowBiz.findCommentByTaskId(taskId);

		if (commentList != null) {
			request.put("commentList", commentList);
		}
		return "approveForm";

	}

	public String finishedList() {
		System.out.println("123");
		return "123";
	}

	public String viewHisComment() {

		Meetingbook meetingbook = meetingbookBiz.findById(Integer
				.parseInt(bookId));
		request.put("list", meetingbook);
		List<Comment> commentList = workflowBiz
				.findCommentByBookMeetingId(Integer.parseInt(bookId));
		request.put("commentList", commentList);
		return "flowList";

	}

	public String viewImage() throws Exception {
		String deploymentId = ServletActionContext.getRequest().getParameter(
				"deploymentId");
		String imageName = ServletActionContext.getRequest().getParameter(
				"imageName");

		// 2：获取资源文件表（act_ge_bytearray）中资源图片输入流InputStream
		InputStream in = workflowBiz.findImageInputStream(deploymentId,
				imageName);
		// 3：从response对象获取输出流
		OutputStream out = ServletActionContext.getResponse().getOutputStream();
		for (int b = -1; (b = in.read()) != -1;) {
			out.write(b);
		}
		out.close();
		in.close();
		return null;

	}

	/**
	 * 查看当前流程图（查看当前活动节点，并使用红色的框标注）
	 */
	public String viewCurrentImageByBookId() {
		// 任务ID
		Task task = workflowBiz.getTaskBybookId(Integer.parseInt(bookId));
		ProcessDefinition pd = workflowBiz.findProcessDefinitionByTask(task);// 获取流程定义
		request.put("deploymentId", pd.getDeploymentId());
		request.put("imageName", pd.getDiagramResourceName());
		Map<String, Object> map = workflowBiz
				.findCoordingByTaskId(task.getId());
		request.put("acs", map);
		return "image";
	}

	public String changeBookForm() {
		Meetingbook meetbook = meetingbookBiz
				.findById(Integer.parseInt(bookId));
		request.put("list", meetbook);
		return "changeBookForm";
	}

	/**
	 * 
	 * @return 需审批task列表
	 */
	public String approveTaskForm() {

		Map<String, Object> session = ActionContext.getContext().getSession();
		request.put("department_id", (String) session.get("department_id"));
		request.put("company_id", (String) session.get("company_id"));
		request.put("login_id", (String) session.get("login_id"));
		List<Meetingroom> meetingroom = meetingroomBiz
				.findRoomListNeedToApprove();
		request.put("approveRoomList", meetingroom);
		return "approveTaskFormList";

	}

}
