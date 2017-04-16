package com.ht.admin.mbeans;

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

  private boolean multiple;
  private Employee systemUser = new Employee();
  private boolean rememberMe;
  private String browserTimeZone;

  @PostConstruct
  public void init() {
    doGetUserDetailsFromCookies(getSystemUser());
  }

  public UserService getUserServiceImpl() {
    return userServiceImpl;
  }

  public void setUserServiceImpl(UserService userServiceImpl) {
    this.userServiceImpl = userServiceImpl;
  }

  public SystemUser getSystemUser() {
    if (systemUser == null) {
      systemUser = new SystemUser();
    }
    return systemUser;
  }

  public void setSystemUser(SystemUser systemUser) {
    this.systemUser = systemUser;
  }

  public SystemUser getForgotSystemUser() {
    if (forgotSystemUser == null)
      forgotSystemUser = new SystemUser();
    return forgotSystemUser;
  }

  public void setForgotSystemUser(SystemUser forgotSystemUser) {
    this.forgotSystemUser = forgotSystemUser;
  }

  public boolean isMultiple() {
    return multiple;
  }

  public void setMultiple(boolean multiple) {
    this.multiple = multiple;
  }

  public boolean isRememberMe() {
    return rememberMe;
  }

  public void setRememberMe(boolean rememberMe) {
    this.rememberMe = rememberMe;
  }

  public String getBrowserTimeZone() {
    return browserTimeZone;
  }

  public void setBrowserTimeZone(String browserTimeZone) {
    this.browserTimeZone = browserTimeZone;
  }

  public void signIn() {

    if (!multiple) {
      if (getSystemUser().getUserName() == null
          || getSystemUser().getUserName().trim().length() == 0) {
        MessageRender.addWarningMessage("msg.user.required");
      }
      if (getSystemUser().getPassword() == null
          || getSystemUser().getPassword().trim().length() == 0) {
        MessageRender.addWarningMessage("msg.password.required");
      }

      if (FacesContextUtil.getFacesContext().getMessageList().isEmpty()) {
        try {
          getSystemUser().setTimeZone(browserTimeZone);
          SystemUser systemUser = getUserServiceImpl().doLoginAthenticate(getSystemUser());

          // *** Do the Login Updation in Application File [START]
          // *****

          ApplicationUsers applicationusers = ApplicationUsers.getInstance();
          boolean activeUser = applicationusers.isActiveSystemUser(systemUser);
          if (activeUser) {
            applicationusers.removeActiveSystemUser(systemUser);
          }
          applicationusers.addActiveSystemUser(systemUser);
          applicationusers.addSessionsBasedOnUserName(systemUser.getUserName(),
              FacesContextUtil.getSession());

          // ----------- [END] --------------

          systemUser.setSessionId(FacesContextUtil.getSessionId());
          systemUser.setTimeZone(
              systemUser.isValidEmployee() ? systemUser.getEmployee().getTimeZone() : null);
          systemUser.setLocation(FacesContextUtil.getClientCountry());
          systemUser.setLocale(FacesContextUtil.getClientLocale());
          systemUser.setIp(FacesContextUtil.getCurrentClientIP());
          if (rememberMe) {
            saveUserDetailsInCookies(this.systemUser);
          }

          // sets user to session and redirect to page
          redirectToHome(systemUser);
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

  private void redirectToHome(SystemUser systemUser) throws Exception {
    String path;
    if (systemUser.getIsFirstLogin() != null && systemUser.getIsFirstLogin()) {
      path = "admin/pages/setPassword.xhtml";
    } else {
      path = LoginUtils.getPath(systemUser);
    }

    FacesContextUtil.setSessionAttribute("user", systemUser);
    FacesContextUtil.setSessionAttribute("organization",
        systemUser.getEmployee().getOrganization());
    FacesContextUtil.setSessionAttribute("employeeTimeZone",
        systemUser.getEmployee().getTimeZone());
    FacesContextUtil.getFacesContext().getExternalContext().redirect(path);
  }

  public void forgetPassword() {
    if (StringUtils.isBlank(getForgotSystemUser().getUserName())) {
      MessageRender.addWarningMessage("msg.user.required");
    }

    try {
      systemUser = userServiceImpl.doGetSystemUserByUserName(getForgotSystemUser().getUserName());
      if (systemUser == null) {
        MessageRender.addErrorMessage("msg.user.notexist");
      } else if (!systemUser.getIsActive()) {
        MessageRender.addWarningMessage("msg.user.status.inactive");
      } else {
        if (FacesContextUtil.getFacesContext().getMessageList().isEmpty()) {
          try {
            getUserServiceImpl().doForgetPassword(getForgotSystemUser(),
                FacesContextUtil.getServerName(), FacesContextUtil.getServerPort(),
                FacesContextUtil.getContextPath());
            MessageRender.addInfoMessage("msg.email.sent");
          } catch (BusinessServiceException e) {
            logger.error(e.getMessage(), e);
            if (!e.getMessage().contains("Invalid")) {
              MessageRender.addErrorMessage("msg.user.password.sent.fails");
            } else {
              MessageRender.addErrorMessage(e.getMessage());
            }

          }
        }
      }
    } catch (BusinessServiceException e) {
      logger.error(e.getMessage(), e);
    }
  }

  public void resetFields() {
    setSystemUser(new SystemUser());
    setForgotSystemUser(new SystemUser());
  }

  public String switchActualUser() {

    String path;
    SystemUser currentUser = FacesContextUtil.getValueFromSession("actualUser");
    FacesContextUtil.setSessionAttribute("user", currentUser);
    path = "home" + BaseConstants.REDIRECT;
    FacesContextUtil.removeAttributeFromSession("menuBean");
    FacesContextUtil.removeAttributeFromSession("actualUser");
    return path;
  }

  /**
   * This for Save The User Name/Password Into The Cookies
   * 
   * @author [MUTHU G.K]
   */
  public void saveUserDetailsInCookies(SystemUser systemUser) {
    if (systemUser != null) {
      FacesContext.getCurrentInstance().getExternalContext().addResponseCookie("eInUN",
          systemUser.getUserName(), null);
      FacesContext.getCurrentInstance().getExternalContext().addResponseCookie("eInUP",
          systemUser.getPassword(), null);
    }
  }

  /**
   * This for Get The User Name/Password From The Cookies
   * 
   * @author [MUTHU G.K]
   */
  public void doGetUserDetailsFromCookies(SystemUser systemUser) {
    if (systemUser != null) {
      Map<String, Object> cookiMap =
          FacesContext.getCurrentInstance().getExternalContext().getRequestCookieMap();

      if (cookiMap != null) {
        Cookie nameCookie = (Cookie) cookiMap.get("eInUN");
        Cookie pswCookie = (Cookie) cookiMap.get("eInUP");

        systemUser.setUserName((nameCookie == null || nameCookie.getValue() == null
            || nameCookie.getValue().trim().length() == 0) ? null : nameCookie.getValue());
        systemUser.setPassword((pswCookie == null || pswCookie.getValue() == null
            || pswCookie.getValue().trim().length() == 0) ? null : pswCookie.getValue());
      }

    }
  }

  public void reSetForgetPsw() {
    forgotSystemUser = new SystemUser();
  }
}
