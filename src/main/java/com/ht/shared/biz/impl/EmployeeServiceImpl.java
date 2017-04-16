package com.ht.shared.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.shared.biz.EmployeeService;
import com.ht.shared.biz.exception.AuthenticationFailException;
import com.ht.shared.biz.exception.BusinessServiceException;
import com.ht.shared.biz.utils.BusinessUtils;
import com.ht.shared.data.EmployeeDAO;
import com.ht.shared.data.exception.DataServiceException;
import com.ht.shared.models.Employee;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeDAO employeeDAO;

	@Override
	public Employee doLoginAthenticate(Employee employeeUser) throws BusinessServiceException {
		Employee athenticateUser;
		try {
			athenticateUser = employeeDAO.getUserByUserName(employeeUser.getUserName());
			if (athenticateUser == null) {
				throw new AuthenticationFailException(
						BusinessUtils.getInstance().getPropertyFileValue("msg.invalid.user"));
			}
		} catch (DataServiceException e) {
			throw new BusinessServiceException(e.getMessage(), e);
		}
		return athenticateUser;
	}

}
