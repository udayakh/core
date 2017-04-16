package com.ht.shared.data;

import com.ht.shared.data.exception.DataServiceException;
import com.ht.shared.models.Employee;

public interface EmployeeDAO {

	Employee getUserByUserName(String userName) throws DataServiceException;

}
