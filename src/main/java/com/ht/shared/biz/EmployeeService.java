package com.ht.shared.biz;

import com.ht.shared.biz.exception.BusinessServiceException;
import com.ht.shared.models.Employee;

public interface EmployeeService {

	Employee doLoginAthenticate(Employee employeeUser) throws BusinessServiceException;

}
