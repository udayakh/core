package com.ht.admin.mbeans.utils;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.revature.shared.biz.utils.BusinessConstants;
import com.revature.shared.models.SeedSystemRole;
import com.revature.shared.models.SystemUser;

/**
 * Assister class to the LoginBean and SetPasswordManagedBean. It helps determine the right redirect
 * path and role for a user once their login or password reset is complete.
 * 
 * @author Denny Ayard denny.ayard@revature.com
 * 
 */
public class LoginUtils {

  private static final int HIGHEST_TICKET_ROLE_RANK = 4;

  private LoginUtils() {
    super();
  }

  /**
   * Produces a redirect path for successful logins and password resets
   * 
   * @param systemUser The user for this session.
   * @return A path to redirect the systemUser to.
   */
  public static String getPath(SystemUser systemUser) {
    // configure user role and home page
    String path = getPathFromExpiredPageCookie(systemUser);

    String requestedView = FacesContextUtil.getValueFromCookie("requestedView");
    if (requestedView != null) {
      if (switchRole(requestedView, systemUser)) {
        path = requestedView;
      }
      Cookie cookie = new Cookie("requestedView", "");
      cookie.setPath("/");
      cookie.setMaxAge(0);
      ((HttpServletResponse) FacesContextUtil.getExternalContext().getResponse()).addCookie(cookie);
    }

    if (path == null) {
      path = FacesContextUtil.getContextPath() + "/admin/pages/home.xhtml";
    }

    return path;
  }

  /**
   * Switches the user's role if it is allowed and necessary for their targeted view.
   * 
   * @param requestedView The view the user is trying to visit.
   * @param systemUser The user for this session.
   * @return true if a role switch succeeds or is not necessary; false if a role switch is illegal
   */
  public static boolean switchRole(String requestedView, SystemUser systemUser) {
    if (StringUtils.equalsAnyIgnoreCase(requestedView, "tickets")) {
      return switchToTicketRole(systemUser);
    }

    return true;
  }

  /**
   * Method for determining a user's view based on their cookies.
   * 
   * @param systemUser The user whose session has expired.
   * @return A path to visit after login success or null.
   */
  private static String getPathFromExpiredPageCookie(SystemUser systemUser) {
    Cookie beExpirePageCook =
        (Cookie) FacesContextUtil.getExternalContext().getRequestCookieMap().get("beExpirePage");
    if (beExpirePageCook != null) {
      String[] cookValues = beExpirePageCook.getValue().split("cookieslash");
      if (cookValues != null && cookValues.length == 3) {
        return processCookieValues(cookValues, systemUser);
      }
    }
    return null;
  }

  private static String processCookieValues(String[] cookValues, SystemUser systemUser) {
    String userName = cookValues[0];
    String role = cookValues[1];
    SeedSystemRole roleFromCookie = null;
    if (systemUser.getId().toString().equals(userName)) {
      for (SeedSystemRole ssr : systemUser.getSystemUserRoles()) {
        if (ssr.getCode().equals(role)) {
          roleFromCookie = ssr;
          break;
        }
      }
      if (roleFromCookie != null) {
        systemUser.setCurrentSystemRole(roleFromCookie);
        return cookValues[2];
      }
    }

    return null;
  }

  /**
   * Attempts to switch the user to a role with a tickets view.
   * 
   * @param systemUser The user for this session.
   * @return true if acceptable role found; false otherwise.
   */
  private static boolean switchToTicketRole(SystemUser systemUser) {
    SeedSystemRole currentRole = systemUser.getCurrentSystemRole();
    // if currentRole is an appropriate role, no change to systemUser needed
    if (findRoleWithTickets(currentRole.getCode()) > 0) {
      return true;
    }

    int highestFound = -1;
    List<SeedSystemRole> availableRoles = systemUser.getSystemUserRoles();
    // for available roles, try to find the highest ones that can visit the tickets view.
    for (SeedSystemRole role : availableRoles) {
      int foundRole = findRoleWithTickets(role.getCode());
      if (foundRole > highestFound) {
        highestFound = foundRole;
        currentRole = role;
        if (highestFound == HIGHEST_TICKET_ROLE_RANK) {
          break;
        }
      }
    }

    if (highestFound > 0) {
      systemUser.setCurrentSystemRole(currentRole);
      return true;
    }

    return false;
  }

  private static int findRoleWithTickets(String role) {
    int found;
    // higher roles cause higher return value
    switch (role) {
      // training admin
      case BusinessConstants.TRAINING_ADMIN_ROLE:
        found = HIGHEST_TICKET_ROLE_RANK;
        break;
      // mentor manager
      case BusinessConstants.MENTOR_MANAGER_ROLE:
        found = HIGHEST_TICKET_ROLE_RANK - 1;
        break;
      // trainer
      case BusinessConstants.TRAINER_ROLE:
        found = HIGHEST_TICKET_ROLE_RANK - 2;
        break;
      // mentor
      case BusinessConstants.MENTOR_ROLE:
        found = HIGHEST_TICKET_ROLE_RANK - 3;
        break;
      default:
        found = -1;
    }

    return found;
  }

}
