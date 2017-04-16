package com.ht.admin.mbeans.utils;

import java.util.MissingResourceException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessageRender {

  public static FacesMessage addInfoMessage(String msgKey) {
    String message = getMessage(msgKey);
    FacesMessage result = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
    FacesContext.getCurrentInstance().addMessage(null, result);
    return result;
  }

  public static FacesMessage addInfoMessageDirectly(String msg) {
    FacesMessage result = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, "");
    FacesContext.getCurrentInstance().addMessage(null, result);
    return result;
  }

  public static FacesMessage addInfoMessageWithDetails(String msgKey, String details) {
    String message1 = getMessage(msgKey);
    String message2 = getMessage(details);
    FacesMessage result = new FacesMessage(FacesMessage.SEVERITY_INFO, message1, message2);
    FacesContext.getCurrentInstance().addMessage(null, result);
    return result;
  }

  public static FacesMessage addWarningMessage(String msgKey) {
    String message = getMessage(msgKey);
    FacesMessage result = new FacesMessage(FacesMessage.SEVERITY_WARN, message, "");
    FacesContext.getCurrentInstance().addMessage(null, result);
    return result;
  }

  public static FacesMessage addWarningMessageDirectly(String msg) {
    FacesMessage result = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, "");
    FacesContext.getCurrentInstance().addMessage(null, result);
    return result;
  }

  public static FacesMessage addWarningMessageWithDetails(String msgKey, String details) {
    String message = getMessage(msgKey);
    FacesMessage result = new FacesMessage(FacesMessage.SEVERITY_WARN, message, details);
    FacesContext.getCurrentInstance().addMessage(null, result);
    return result;
  }

  public static FacesMessage addErrorMessage(String msgKey) {
    String message = getMessage(msgKey);
    FacesMessage result = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "");
    FacesContext.getCurrentInstance().addMessage(null, result);
    return result;
  }

  public static FacesMessage getErrorMessage(String msgKey, String clientId) {
    String message = getMessage(msgKey);
    FacesMessage result = new FacesMessage(message);
    return result;
  }

  public static FacesMessage addErrorMessageDirectly(String msg) {

    FacesMessage result = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
    FacesContext.getCurrentInstance().addMessage(null, result);
    return result;
  }

  public static FacesMessage addErrorMessageWithDetails(String msgKey, String details) {
    String message1 = getMessage(msgKey);
    String message2 = getMessage(details);
    FacesMessage result = new FacesMessage(FacesMessage.SEVERITY_ERROR, message1, message2);
    FacesContext.getCurrentInstance().addMessage(null, result);
    return result;
  }

  public static FacesMessage addFatalMessage(String msgKey) {
    String message = getMessage(msgKey);
    FacesMessage result = new FacesMessage(FacesMessage.SEVERITY_FATAL, message, "");
    FacesContext.getCurrentInstance().addMessage(null, result);
    return result;
  }

  public static FacesMessage addFatalMessageWithDetails(String msgKey, String details) {
    String message = getMessage(msgKey);
    FacesMessage result = new FacesMessage(FacesMessage.SEVERITY_FATAL, message, details);
    FacesContext.getCurrentInstance().addMessage(null, result);
    return result;
  }

  private static String getMessage(String key) {
    String message = null;
    try {
      if (key != null) {
        message = FacesContextUtil.getResourceBundle("validation").getString(key);
      }
    } catch (MissingResourceException e) {
      message = key;
    }
    return message;
  }

}
