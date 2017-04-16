package com.ht.admin.mbeans.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.ht.shared.remote.resources.RemoteResourceService;
import com.ht.shared.utils.ApplicationContextUtils;

/**
 * FacesContextUtil for jsf components.
 *
 */

public class FacesContextUtil {

  private static final Logger LOGGER = Logger.getLogger(FacesContextUtil.class);

  private FacesContextUtil() {}

  public static FacesContext getFacesContext() {
    return FacesContext.getCurrentInstance();
  }

  public static boolean isNoMessageInFacesContext() {
    return FacesContext.getCurrentInstance().getMessageList().isEmpty();
  }

  /**
   * getResourceBundle.
   * 
   * @param contxtName contxtName
   * @return null
   */
  public static ResourceBundle getResourceBundle(String contxtName) {
    ResourceBundle custom =
        getFacesContext().getApplication().getResourceBundle(getFacesContext(), contxtName);
    return custom;
  }

  private static Map<String, Object> getSessionMap() {
    if (getFacesContext() != null && getFacesContext().getExternalContext() != null
        && getFacesContext().getExternalContext().getSessionMap() != null) {
      return getFacesContext().getExternalContext().getSessionMap();
    }
    return null;
  }

  public static Map<String, Object> getRequestMap() {
    return getFacesContext().getExternalContext().getRequestMap();
  }

  public static <T> void setSessionAttribute(String attributName, T value) {
    Map<String, Object> session = getSessionMap();
    session.put(attributName, value);
  }

  public static <T> void setRequestAttribute(String attributName, T value) {
    Map<String, Object> request = getRequestMap();
    request.put(attributName, value);
  }

  /**
   * getValueFromSession.
   */
  public static <T> T getValueFromSession(String attributName) {
    Map<String, Object> session = getSessionMap();
    T genT = (T) session.get(attributName);
    return genT;
  }

  /**
   * getValueFromRequest.
   */
  public static <T> T getValueFromRequest(String attributName) {
    Map<String, Object> session = getRequestMap();
    T genT = (T) session.get(attributName);
    return genT;
  }

  /**
   * removeAttributeFromSession.
   */
  public static void removeAttributeFromSession(String attributeName) {
    Map<String, Object> session = getSessionMap();
    if (session != null) {
      session.remove(attributeName);
    }
  }

  /**
   * removeAttributeFromRequest.
   */
  public static void removeAttributeFromRequest(String attributeName) {
    Map<String, Object> request = getSessionMap();
    if (request != null) {
      request.remove(attributeName);
    }
  }

  public static HttpServletRequest getRequest() {
    return (HttpServletRequest) getFacesContext().getExternalContext().getRequest();
  }

  public static HttpSession getSession() {
    return getRequest().getSession();
  }

  public static String getContextPath() {
    return getFacesContext().getExternalContext().getRequestContextPath();
  }

  public static String getServerName() {
    return getRequest().getServerName();
  }

  public static String getServerPort() {
    return String.valueOf(getRequest().getServerPort());
  }

  public static String getServerScheme() {
    return getRequest().getScheme();
  }

  public static String getCurrentClientIP() {
    return getRequest().getRemoteAddr();
  }

  public static String getClientCountry() {
    return getFacesContext().getViewRoot().getLocale().getDisplayCountry();
  }

  public static Locale getClientLocale() {
    return getFacesContext().getViewRoot().getLocale();
  }

  public static String getClientTimeZone() {
    Calendar calendar = Calendar.getInstance(getFacesContext().getViewRoot().getLocale());
    return calendar.getTimeZone().getDisplayName();
  }

  public static String getSessionId() {
    return getRequest().getSession().getId();
  }

  /**
   * fileDownload.
   */
  public static FacesContext fileDownload(String filePath, String originalFileName, String fileType,
      boolean isDelete) {
    RemoteResourceService remoteResourceService =
        ApplicationContextUtils.getBean("s3RemoteResourceServiceImpl");
    try {
      if (isDelete) {
        remoteResourceService.deleteResource(filePath);
      }
    } catch (Exception e1) {
      LOGGER.error(e1.getMessage(), e1);
    }
    return null;
  }

  /**
   * getYesOrNoOption.
   */
  public static SelectItem[] getYesOrNoOption() {
    SelectItem[] options = new SelectItem[3];
    options[0] = new SelectItem("", "Select");
    options[1] = new SelectItem("Y", "Yes");
    options[2] = new SelectItem("N", "No");
    return options;
  }

  public static String getDocumentTemplatePath() {
    String path = getRequest().getServletContext().getRealPath("/documentTemplates");
    return path;
  }

  /**
   * makeMandatoryFieldHighlight.
   */
  public static void makeMandatoryFieldHighlight(String componentId) {
    UIComponent component = getFacesContext().getViewRoot().findComponent(componentId);
    if (component != null) {
      component.getAttributes().put("styleClass", "mandatoryFieldHighlight");
    }
  }

  /**
   * unMakeMandatoryFieldHighlight.
   */
  public static void unMakeMandatoryFieldHighlight(String componentId) {
    UIComponent component = getFacesContext().getViewRoot().findComponent(componentId);
    if (component != null) {
      component.getAttributes().put("styleClass", "");
    }
  }

  /**
   * emailValidationCheck.
   */
  public static boolean emailValidationCheck(String emailComponentId) {
    boolean result = false;
    Pattern pattern = Pattern.compile(
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    UIComponent component = getFacesContext().getViewRoot().findComponent(emailComponentId);
    if (component != null) {
      String value = (String) component.getAttributes().get("value");
      Matcher matcher = pattern.matcher(value);
      boolean resultTemp = matcher.matches();
      if (!resultTemp) {
        component.getAttributes().put("styleClass", "mandatoryFieldHighlight");
        result = true;
      } else {
        component.getAttributes().put("styleClass", "");
      }
    }
    return result;
  }

  /**
   * emailValidation.
   */
  public static boolean emailValidation(String email) {
    Pattern pattern = Pattern.compile(
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  /**
   * passwordValidationCheck.
   */
  public static boolean passwordValidationCheck(String passwordComponentId) {
    boolean result = false;
    Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$");
    UIComponent component = getFacesContext().getViewRoot().findComponent(passwordComponentId);
    if (component != null) {
      String value = (String) component.getAttributes().get("value");
      Matcher matcher = pattern.matcher(value);
      boolean resultTemp = matcher.matches();
      if (!resultTemp) {
        component.getAttributes().put("styleClass", "mandatoryFieldHighlight");
        result = true;
      } else {
        component.getAttributes().put("styleClass", "");
      }
    }
    return result;
  }

  /**
   * mandatoryFieldCheck.
   */
  public static boolean mandatoryFieldCheck(List<String> componentIds) {
    boolean result = false;
    UIComponent component = null;
    Object value = null;
    for (String componentId : componentIds) {
      component = getFacesContext().getViewRoot().findComponent(componentId);
      if (component != null) {
        value = component.getAttributes().get("value");
        if (value == null || value.toString().trim().length() <= 0
            || value.toString().equalsIgnoreCase("0")) {
          component.getAttributes().put("styleClass", "mandatoryFieldHighlight");
          result = true;
        } else {
          component.getAttributes().put("styleClass", "");
        }
      }
    }
    return result;
  }

  /**
   * unMakeAllMandatoryFieldHighlight.
   */
  public static void unMakeAllMandatoryFieldHighlight(List<String> componentIds) {
    UIComponent component = null;
    for (String componentId : componentIds) {
      component = getFacesContext().getViewRoot().findComponent(componentId);
      if (component != null) {
        component.getAttributes().put("styleClass", "");
      }
    }
  }

  /**
   * fileDownloader.
   */
  public static void fileDownloader(String filePath, String fileName, String fileType) {
    RemoteResourceService remoteResourceService =
        ApplicationContextUtils.getBean("s3RemoteResourceServiceImpl");
    try {
      getResponse().sendRedirect(remoteResourceService.getDownloadUrl(filePath, fileName));
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    getFacesContext().responseComplete();
  }

  /**
   * fileDownloaderAsZip.
   */
  public static void fileDownloaderAsZip(String zipFile, String[] sourceFiles,
      String[] originalFileNames) {
    try {
      byte[] buffer = new byte[1024];
      RemoteResourceService remoteResourceService =
          ApplicationContextUtils.getBean("s3RemoteResourceServiceImpl");
      ByteArrayOutputStream fout = new ByteArrayOutputStream();
      ZipOutputStream zout = new ZipOutputStream(fout);
      for (int i = 0; i < sourceFiles.length; i++) {
        ByteArrayInputStream fin =
            new ByteArrayInputStream(remoteResourceService.getResourceAsByteArray(sourceFiles[i]));
        zout.putNextEntry(new ZipEntry(originalFileNames[i]));
        int len;
        while ((len = fin.read(buffer)) > 0) {
          zout.write(buffer, 0, len);
        }
        zout.closeEntry();
        fin.close();
      }
      zout.close();
      remoteResourceService.saveResource(zipFile, fout.toByteArray());
      String fileName = zipFile.substring(zipFile.lastIndexOf('/') + 1);
      fileDownloader(zipFile, fileName, "application/zip");
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);;
    }
  }

  public static HttpServletResponse getResponse() {
    HttpServletResponse response =
        (HttpServletResponse) getFacesContext().getExternalContext().getResponse();
    return response;
  }

  /**
   * ifFilePresent.
   */
  public static boolean ifFilePresent(String filePath) {
    RemoteResourceService remoteResourceService =
        ApplicationContextUtils.getBean("s3RemoteResourceServiceImpl");
    boolean result = false;
    try {
      result = remoteResourceService.exists(filePath);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * fileDownloaderFromURL.
   */
  public static void fileDownloaderFromURL(String fileURL, String fileName, String fileType) {
    try {
      getResponse().sendRedirect(fileURL);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    getFacesContext().responseComplete();
  }

  /**
   * getValueFromRequestParameter.
   */
  public static <T> T getValueFromRequestParameter(String attributName) {
    Map<String, String> request = getFacesContext().getExternalContext().getRequestParameterMap();
    T genT = (T) request.get(attributName);
    return genT;
  }

  // ------------------ Newly add for the JSF Client -----------------[START]

  /**
   * getExternalContext.
   */
  public static ExternalContext getExternalContext() {
    if (getFacesContext() != null) {
      return getFacesContext().getExternalContext();
    } else
      return null;
  }

  /**
   * add Cookie to the Client.
   */
  public static boolean addCookie(String key, String value) {
    if (getExternalContext() != null) {
      try {
        Cookie cookie = new Cookie(key, value);
        ((HttpServletResponse) getExternalContext().getResponse()).addCookie(cookie);
        return true;
      } catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }
    } else
      return false;
  }

  /**
   * add Cookie to the Client with age.
   */
  public static boolean addCookie(String key, String value, int age) {
    if (getExternalContext() != null) {
      try {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(age);
        ((HttpServletResponse) getExternalContext().getResponse()).addCookie(cookie);
        return true;
      } catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }
    } else
      return false;
  }

  /**
   * remove Cookie to the Client with age.
   */
  public static boolean removeCookie(String key) {
    if (getExternalContext() != null) {
      try {
        Cookie cookie = new Cookie(key, "");
        cookie.setMaxAge(0);
        ((HttpServletResponse) getExternalContext().getResponse()).addCookie(cookie);
        return true;
      } catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }
    } else
      return false;
  }

  /**
   * get value from Cookie.
   */
  public static String getValueFromCookie(String key) {
    String value = null;
    if (getExternalContext() == null || key == null)
      return null;
    try {

      Map<String, Object> cookies = getExternalContext().getRequestCookieMap();
      if (cookies != null && cookies.containsKey(key)) {
        Cookie cookie = (Cookie) cookies.get(key);
        value = cookie.getValue();
      }
      return value;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }

  }

  /**
   * To get the partial render ids list from the View
   * 
   * @return
   */
  public static Collection<String> getRenderViewIds() {
    return getFacesContext().getPartialViewContext().getRenderIds();
  }

  /**
   * To add the partial render ids to the View
   * 
   * @param ids
   */
  public static void addRenderViewIds(String ids) {
    if (ids == null || ids.trim().length() == 0)
      return;

    getRenderViewIds().add(ids);
  }
  // ------------------ Newly add for the JSF Client -----------------[END]

}
