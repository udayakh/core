package com.ht.shared.data.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ht.shared.data.EmployeeDAO;
import com.ht.shared.data.access.DataModifier;
import com.ht.shared.data.access.DataRetriever;
import com.ht.shared.data.access.QueryParameter;
import com.ht.shared.data.access.exception.DataAccessException;
import com.ht.shared.data.exception.DataRetrievalFailedException;
import com.ht.shared.data.exception.DataServiceException;
import com.ht.shared.data.utils.DataUtils;
import com.ht.shared.models.Employee;

public class EmployeeDAOImpl implements EmployeeDAO {

	@Autowired
	private DataRetriever dataRetriever;

	@Autowired
	private DataModifier dataModifier;

	@Override
	public Employee getUserByUserName(String userName) throws DataServiceException {
		Employee employee = null;
		try {
			StringBuilder query = new StringBuilder("From Employee su where su.userName=:userName ");
			List<QueryParameter<?>> queryParameters = new ArrayList<>();
			queryParameters.add(new QueryParameter<>(userName, userName));
			employee = dataRetriever.retrieveObjectByHQL(query.toString(), queryParameters);
		} catch (DataAccessException dataAccessException) {
			throw new DataRetrievalFailedException(
					DataUtils.getInstance().getPropertyFileValue("Data Retrieval Failed"), dataAccessException);
		}
		return employee;
	}

}
