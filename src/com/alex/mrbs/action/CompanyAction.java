package com.alex.mrbs.action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.RequestAware;

import com.alex.mrbs.biz.ICompanyBiz;
import com.alex.mrbs.entity.Company;
import com.opensymphony.xwork2.ActionContext;

public class CompanyAction implements RequestAware {

	private Map<String, Object> request;
	private List<Company> companyList;
	private ICompanyBiz companyBiz;
	private Company company = new Company();;
	
	public List<Company> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<Company> companyList) {
		this.companyList = companyList;
	}

	

	public ICompanyBiz getCompanyBiz() {
		return companyBiz;
	}

	public void setCompanyBiz(ICompanyBiz companyBiz) {
		this.companyBiz = companyBiz;
	}




	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void setCompany(List<Company> companyList) {
		this.companyList = companyList;
	}

	public Map<String, Object> getRequest() {
		return request;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}
	
	public String list(){
		
		Map<String,Object> session = ActionContext.getContext().getSession();
		request.put("depart_id", (String)session.get("department_id"));
		request.put("company_id", (String)session.get("company_id"));
		companyList = companyBiz.findAll();
		request.put("list", companyList);
		return "list";
		
	}
	
	public String updator(){
		company = companyBiz.findCompanyById(company.getCompany_id());
		request.put("company", company);
		return "updator";
		
	}
	
	public String create(){
		companyBiz.create(company);
		return redirect();
	}
	public String delete(){
		companyBiz.delete(company);
		return redirect();
	}

	private String redirect() {
		// TODO Auto-generated method stub
		return "getCompanyList";
	}
	
	public String creator(){
		return "creator";
	}
	
	public String update(){
		return redirect();
	}
	

}
