package com.ht.admin.mbeans.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.ht.shared.models.Employee;

/**
 * This For The Application Logged in and LoggedOut User's Details Maintainance
 * 
 * @author MUTHU G.K
 *
 */
public class ApplicationUsers {

	private static ApplicationUsers INSTANCE;

	public static ApplicationUsers getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ApplicationUsers();
		return INSTANCE;
	}

	private Map<String, HttpSession> sessions;
	private List<Employee> systemUsers;

	public Map<String, HttpSession> getSessions() {
		if (sessions == null)
			sessions = new HashMap<>();
		return sessions;
	}

	public void setSessions(Map<String, HttpSession> sessions) {
		this.sessions = sessions;
	}

	/** To get the all active user list */
	public List<Employee> getSystemUsers() {
		if (systemUsers == null)
			systemUsers = new ArrayList<>();
		return systemUsers;
	}

	public void setSystemUsers(List<Employee> systemUsers) {
		this.systemUsers = systemUsers;
	}

	/** To add the active user into the ActiveUser List */
	public boolean addActiveSystemUser(Employee systeUser) {
		boolean flage = false;
		if (systeUser != null && systeUser.getId() != null) {
			flage = getSystemUsers().add(systeUser);
		}
		return flage;
	}

	/**
	 * To remove the active user from the ActiveUser List and Kill their
	 * existing Session, for restrict the multiple login
	 */
	public boolean removeActiveSystemUser(Employee systeUser) {
		boolean flage = false;
		if (systeUser != null && systeUser.getId() != null && getSystemUsers().contains(systeUser)) {
			flage = getSystemUsers().remove(systeUser);
			if (flage) {
				HttpSession session = getSessions().get(systeUser.getUserName());
				removeSessionsBasedOnUserName(systeUser.getUserName());
				session.invalidate();
			}
		}
		return flage;
	}

	/**
	 * To add the session of the logged in users
	 */
	public void addSessionsBasedOnUserName(String userName, HttpSession session) {
		if (session != null && userName != null && userName.trim().length() > 0) {
			getSessions().put(userName, session);
		}
	}

	/**
	 * To add the session of the logged in users
	 */
	private void removeSessionsBasedOnUserName(String userName) {
		if (userName != null && userName.trim().length() > 0) {
			getSessions().remove(userName);
		}
	}

}
