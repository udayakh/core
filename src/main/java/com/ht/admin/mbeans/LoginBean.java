package com.ht.admin.mbeans;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ht.shared.models.Employee;
import com.ht.admin.mbeans.utils.ApplicationUsers;
import com.ht.admin.mbeans.utils.FacesContextUtil;
import com.ht.admin.mbeans.utils.LoginUtils;
import com.ht.admin.mbeans.utils.MessageRender;
import com.ht.shared.biz.EmployeeService;
import com.ht.shared.biz.exception.BusinessServiceException;
import com.ht.shared.models.Employee;
import com.ht.shared.utils.BaseConstants;

@ManagedBean
@RequestScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final transient Logger logger = Logger.getLogger(LoginBean.class);

	@ManagedProperty("#{employeeServiceImpl}")
	private transient EmployeeService employeeService;
	private Employee employee = new Employee();
	private boolean multiple;

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@PostConstruct
	public void init() {
		doGetUserDetailsFromCookies(getEmployeeUser());
	}

	private void doGetUserDetailsFromCookies(Employee employeeUser) {

		if (employee == null) {

			Map<String, Object> cookieMap = FacesContextUtil.getExternalContext().getRequestCookieMap();
			if (cookieMap != null) {
				Cookie nameCookie = (Cookie) cookieMap.get("unername");
				Cookie pswCookie = (Cookie) cookieMap.get("password");

				employee.setUserName((nameCookie == null || nameCookie.getValue() == null
						|| nameCookie.getValue().trim().length() == 0) ? null : nameCookie.getValue());
				employee.setPassword(
						(pswCookie == null || pswCookie.getValue() == null || pswCookie.getValue().trim().length() == 0)
								? null : pswCookie.getValue());
			}
		}
	}

	public Employee getEmployeeUser() {
		if (employee == null) {
			employee = new Employee();
		}
		return employee;
	}

	public void signIn() {

		if (!multiple) {
			if (getEmployee().getUserName() == null || getEmployee().getUserName().trim().length() == 0) {
				MessageRender.addWarningMessage("msg.user.required");
			}
			if (getEmployee().getPassword() == null || getEmployee().getPassword().trim().length() == 0) {
				MessageRender.addWarningMessage("msg.password.required");
			}

			if (FacesContextUtil.getFacesContext().getMessageList().isEmpty()) {
				try {
					Employee employee = employeeService.doLoginAthenticate(getEmployeeUser());

					// *** Do the Login Updation in Application File [START]
					// *****

					ApplicationUsers applicationusers = ApplicationUsers.getInstance();
					applicationusers.addSessionsBasedOnUserName(employee.getUserName(), FacesContextUtil.getSession());

					// ----------- [END] --------------

					employee.setSessionId(FacesContextUtil.getSessionId());

					// sets user to session and redirect to page
					redirectToHome(employee);
					multiple = true;
				} catch (BusinessServiceException e) {
					logger.error(e.getMessage(), e);
					MessageRender.addErrorMessageDirectly(e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					MessageRender.addErrorMessage("msg.system.error");
				}
			}
		}
	}

	private void redirectToHome(Employee employee) throws IOException {
		String path;
		path = LoginUtils.getPath(employee);
		FacesContextUtil.setSessionAttribute("user", employee);
		FacesContextUtil.getFacesContext().getExternalContext().redirect(path);

	}

}