package com.ht.admin.mbeans.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.ht.shared.models.Employee;

/**
 * Assister class to the LoginBean and SetPasswordManagedBean. It helps
 * determine the right redirect path and role for a user once their login or
 * password reset is complete.
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
	 * @param systemUser
	 *            The user for this session.
	 * @return A path to redirect the systemUser to.
	 */
	public static String getPath(Employee systemUser) {
		// configure user role and home page
		String path = getPathFromExpiredPageCookie(systemUser);

		String requestedView = FacesContextUtil.getValueFromCookie("requestedView");
		if (requestedView != null) {
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

	private static String getPathFromExpiredPageCookie(Employee systemUser) {
		Cookie beExpirePageCook = (Cookie) FacesContextUtil.getExternalContext().getRequestCookieMap()
				.get("beExpirePage");
		if (beExpirePageCook != null) {
			String[] cookValues = beExpirePageCook.getValue().split("cookieslash");
			if (cookValues != null && cookValues.length == 3) {
				return processCookieValues(cookValues, systemUser);
			}
		}
		return null;
	}

	private static String processCookieValues(String[] cookValues, Employee systemUser) {
		String userName = cookValues[0];
		String role = cookValues[1];
		return null;
	}

}
