package com.ht.shared.data.access;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ht.shared.data.access.exception.DataAccessException;
import com.ht.shared.data.access.exception.DataSourceOperationFailedException;
import com.ht.shared.data.access.exception.DuplicateRecordException;
import com.ht.shared.data.access.exception.ReferentialIntegrityException;
import com.ht.shared.data.utils.DataUtils;

/**
 * This implementation is based on hibernate and this is used for the following DML operation Insert
 * , Update , Delete This uses hibernate session which is injected through spring container and also
 * the transaction managed by spring container.
 * 
 * @author Selva
 * 
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = false,
    rollbackFor = DataAccessException.class)
public class DataModifierHibernateImpl implements DataModifier, Serializable {

  private static final long serialVersionUID = 1L;
  private static Logger logger = Logger.getLogger(DataModifierHibernateImpl.class);
  private static final String OBJECT_SAVE_FAILS = "Object save failed";
  private static final String OBJECT_UPDATE_FAILS = "Object updation failed.";
  private static final String OBJECT_SAVE_FAILED_RECORDS = "Object save failed. No of records : %d";
  private static final String OBJECT_UPDATE_FAILED_RECORDS =
      "Object updation failed. No of records : %d";
  private static final String QUERY_EXECUTION_FAILS = "Query execution failed. query : %s ";
  private static final String DATA_DELETION_FAILS = "Data deletion failed,with no of records : %d ";
  private static final String OBJECTS_SAVE_FAILED_RECORDS =
      "Objects save failed. No of records : %d";
  private static final String DUPLICATE_RECORD_EXCEPTION = "duplicate.record.exception";
  private static final String FOREIGN_KEY_EXCEPTION = "foreign.key.exception";
  private static final String QUERY_EXECUTION_FAILED = "Query execution failed. query : %s";
  private static final String DATA_UPDATION_FAILS = "Data updation failed. No of records : %d";
  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public <T> void insert(T type) throws DataAccessException {
    try {
      logger.debug("Preparing to save object.");
      Session session = getSessionFactory().getCurrentSession();
      Object id = session.save(type);
      logger.debug(String.format("Object saved successfully. with id : %s ", id));
      session.flush();
      logger.info("Data saved successfully.");
    } catch (ConstraintViolationException cvException) {
      logger.error("", cvException);
      throw new DuplicateRecordException(
          DataAccessUtils.getInstance().getPropertyFileValue(DUPLICATE_RECORD_EXCEPTION),
          cvException);
    } catch (HibernateException hibernateException) {
      logger.error(OBJECT_SAVE_FAILS, hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage(),
          hibernateException);
    } catch (Exception exception) {
      logger.error(OBJECT_SAVE_FAILS, exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
  }

  @Override
  public <E> boolean insertBulk(List<E> objects) throws DataAccessException {
    boolean result = false;
    try {
      logger.debug(String.format("Preparing to save objects. no of Objects : %d ", objects.size()));
      Session session = getSessionFactory().getCurrentSession();
      for (int i = 0; i < objects.size(); i++) {
        Object type = objects.get(i);
        session.saveOrUpdate(type);
        logger.debug(String.format("Object saved successfully. %d/%d", i + 1, objects.size()));
        if (i % 20 == 0) {
          session.flush();
          session.clear();
        }
      }
      session.flush();
      logger.debug(String.format("Objects saved successfully, no of objects : %d", objects.size()));
      logger.info(String.format("Data saved successfully. No of records : %d", objects.size()));
    } catch (ConstraintViolationException cvException) {
      logger.error(String.format(OBJECT_SAVE_FAILED_RECORDS, objects.size()), cvException);
      throw new DuplicateRecordException(
          DataAccessUtils.getInstance().getPropertyFileValue(DUPLICATE_RECORD_EXCEPTION),
          cvException);
    } catch (HibernateException hibernateException) {
      logger.error(String.format(OBJECT_SAVE_FAILED_RECORDS, objects.size()), hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage(),
          hibernateException);
    } catch (Exception exception) {
      logger.error(String.format(OBJECT_SAVE_FAILED_RECORDS, objects.size()), exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
    return result;
  }


  @Override
  public <E> boolean saveOrUpdate(List<E> objects) throws DataAccessException {
    boolean result = false;
    try {
      logger.debug(String.format("Preparing to save or update the objects. no of Objects : %d ",
          objects.size()));
      Session session = getSessionFactory().getCurrentSession();
      for (int i = 0; i < objects.size(); i++) {
        session.saveOrUpdate(objects.get(i));
        logger.debug(
            String.format("Object saved or updated successfully. %d/%d", i + 1, objects.size()));
        if (i % 20 == 0) {
          session.flush();
          session.clear();
        }
      }
      session.flush();
      logger.debug(String.format("Objects saved successfully, no of objects : %d", objects.size()));
      logger.info(String.format("Data saved successfully. No of records : %d", objects.size()));
    } catch (ConstraintViolationException cvException) {
      logger.error(String.format(OBJECTS_SAVE_FAILED_RECORDS, objects.size()), cvException);
      throw new DuplicateRecordException(
          DataAccessUtils.getInstance().getPropertyFileValue(DUPLICATE_RECORD_EXCEPTION),
          cvException);
    } catch (HibernateException hibernateException) {
      logger.error(String.format(OBJECTS_SAVE_FAILED_RECORDS, objects.size()), hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage(),
          hibernateException);
    } catch (Exception exception) {
      logger.error(String.format(OBJECTS_SAVE_FAILED_RECORDS, objects.size()), exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
    return result;
  }

  @Override
  public <T> void update(T type) throws DataAccessException {
    try {
      logger.debug("Preparing to update the object");
      Session session = getSessionFactory().getCurrentSession();
      session.clear();
      session.saveOrUpdate(type);
      logger.debug("Object updated successfully.");
      session.flush();
      logger.info("Data updated successfully.");
    } catch (ConstraintViolationException cvException) {
      logger.error(OBJECT_UPDATE_FAILS, cvException);
      throw new DuplicateRecordException(
          DataAccessUtils.getInstance().getPropertyFileValue(DUPLICATE_RECORD_EXCEPTION),
          cvException);
    } catch (HibernateException hibernateException) {
      logger.error(OBJECT_UPDATE_FAILS, hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage());
    } catch (Exception exception) {
      logger.error(OBJECT_UPDATE_FAILS, exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
  }

  @Override
  public <T> void delete(T type) throws DataAccessException {
    try {
      logger.debug("Preparing to delete the object.");
      Session session = getSessionFactory().getCurrentSession();
      session.delete(type);
      session.flush();
      logger.debug("Object deleted successfully.");
      logger.info("Data deleted successfully.");
    } catch (ConstraintViolationException cvException) {
      logger.error("Object deletion failed due to constraint violation.", cvException);
      throw new ReferentialIntegrityException(
          DataAccessUtils.getInstance().getPropertyFileValue(FOREIGN_KEY_EXCEPTION), cvException);
    } catch (HibernateException hibernateException) {
      logger.error("Object deletion failed.", hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage(),
          hibernateException);
    } catch (Exception exception) {
      logger.error("Object deletion failed.", exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
  }

  @Override
  public <E> boolean deleteBulk(List<E> objects) throws DataAccessException {
    boolean result = false;
    try {
      logger.debug(
          String.format("Preparing to delete the objects. no of objects : %d", objects.size()));
      Session session = getSessionFactory().getCurrentSession();
      for (int i = 0; i < objects.size(); i++) {
        Object type = objects.get(i);
        session.delete(type);
        logger.debug(String.format("Object deleted successfully. %d/%d", i + 1, objects.size()));
        if (i % 20 == 0) {
          session.flush();
          session.clear();
        }
      }
      session.flush();
      logger.debug("Object deleted successfully");
      logger.info("Data deleted successfully");
    } catch (ConstraintViolationException cvException) {
      logger.error(String.format(DATA_DELETION_FAILS, objects.size()), cvException);
      throw new ReferentialIntegrityException(
          DataAccessUtils.getInstance().getPropertyFileValue(FOREIGN_KEY_EXCEPTION), cvException);
    } catch (HibernateException hibernateException) {
      logger.error(String.format(DATA_DELETION_FAILS, objects.size()), hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage(),
          hibernateException);
    } catch (Exception exception) {
      logger.error(String.format(DATA_DELETION_FAILS, objects.size()), exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
    return result;
  }

  @Override
  public <E> boolean updateBulk(List<E> objects) throws DataAccessException {
    boolean result = false;
    try {
      logger.debug(
          String.format("Preparing to update the objects. no of objects : %d", objects.size()));
      Session session = getSessionFactory().getCurrentSession();
      for (int i = 0; i < objects.size(); i++) {
        Object type = objects.get(i);
        session.saveOrUpdate(type);
        logger.debug(String.format("Object updated successfully.%d/%d", i + 1, objects.size()));
        if (i % 20 == 0) {
          session.flush();
          session.clear();
        }
      }
      session.flush();
      logger
          .debug(String.format("Objects updated successfully, no of objects : %d", objects.size()));
      logger.info(String.format("Data updated successfully. No of records : %d", objects.size()));
    } catch (ConstraintViolationException cvException) {
      logger.error(String.format(DATA_UPDATION_FAILS, objects.size()), cvException);
      throw new DuplicateRecordException(
          DataAccessUtils.getInstance().getPropertyFileValue(DUPLICATE_RECORD_EXCEPTION),
          cvException);
    } catch (HibernateException hibernateException) {
      logger.error(String.format(DATA_UPDATION_FAILS, objects.size()), hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage(),
          hibernateException);
    } catch (Exception exception) {
      logger.error(String.format(DATA_UPDATION_FAILS, objects.size()), exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
    return result;
  }

  @Override
  public <T> void merge(T type) throws DataAccessException {
    try {
      logger.debug("Preparing to update the object");
      Session session = getSessionFactory().getCurrentSession();
      session.clear();
      session.merge(type);
      session.flush();
      logger.debug("Object updated successfully.");
      logger.info("Data updated successfully.");
    } catch (ConstraintViolationException cvException) {
      logger.error(OBJECT_UPDATE_FAILS, cvException);
      throw new DuplicateRecordException(
          DataAccessUtils.getInstance().getPropertyFileValue(DUPLICATE_RECORD_EXCEPTION),
          cvException);
    } catch (HibernateException hibernateException) {
      logger.error(OBJECT_UPDATE_FAILS, hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage(),
          hibernateException);
    } catch (Exception exception) {
      logger.error(OBJECT_UPDATE_FAILS, exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
  }

  @Override
  public <E> boolean mergeBulk(List<E> objects) throws DataAccessException {
    boolean result = false;
    try {
      logger.debug(
          String.format("Preparing to  update the objects. no of Objects : %d ", objects.size()));
      Session session = getSessionFactory().getCurrentSession();
      for (int i = 0; i < objects.size(); i++) {
        Object type = objects.get(i);
        session.merge(type);
        logger.debug(String.format("Object updated successfully. %d/%d", i + 1, objects.size()));
        if (i % 20 == 0) {
          session.flush();
          session.clear();
        }
      }
      session.flush();
      logger
          .debug(String.format("Objects updated successfully, no of objects : %d", objects.size()));
      logger.info(String.format("Data updated successfully. No of records : %d", objects.size()));
    } catch (ConstraintViolationException cvException) {
      logger.error(String.format(OBJECT_UPDATE_FAILED_RECORDS, objects.size()), cvException);
      throw new DuplicateRecordException(
          DataAccessUtils.getInstance().getPropertyFileValue(DUPLICATE_RECORD_EXCEPTION),
          cvException);
    } catch (HibernateException hibernateException) {
      logger.error(String.format(OBJECT_UPDATE_FAILED_RECORDS, objects.size()), hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage(),
          hibernateException);
    } catch (Exception exception) {
      logger.error(String.format(OBJECT_UPDATE_FAILED_RECORDS, objects.size()), exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
    return result;
  }

  @Override
  public Integer executeQuery(String queryString) throws DataAccessException {
    Integer noOfRowsUpdated = 0;
    try {
      logger.debug(String.format("Executing the query : %s", queryString));
      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        noOfRowsUpdated = query.executeUpdate();
        session.flush();
      }
      logger.debug(String.format("Executed the query : %s, no of rows affected : %d ", queryString,
          noOfRowsUpdated));
      logger.info(String.format("Query executed successfully. query : %s", queryString));
      return noOfRowsUpdated;
    } catch (ConstraintViolationException cvException) {
      logger.error(String.format(QUERY_EXECUTION_FAILED, queryString), cvException);
      throw new ReferentialIntegrityException(
          DataAccessUtils.getInstance().getPropertyFileValue(FOREIGN_KEY_EXCEPTION), cvException);
    } catch (HibernateException hibernateException) {
      logger.error(String.format(QUERY_EXECUTION_FAILED, queryString), hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage(),
          hibernateException);
    } catch (Exception exception) {
      logger.error(String.format(QUERY_EXECUTION_FAILED, queryString), exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
  }

  @Override
  public Integer executeQuery(String queryString, List<QueryParameter<?>> queryParameters)
      throws DataAccessException {
    Integer noOfRowsUpdated = 0;
    try {
      logger.debug(String.format("Executing the query : %s, with parameters :%s", queryString,
          getJsonString(queryParameters)));
      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        DataUtils.setQueryParameters(query, queryParameters);
        noOfRowsUpdated = query.executeUpdate();
      }
      logger
          .debug(String.format("Executed the query : %s,parameters : %s, no of rows affected : %d ",
              queryString, getJsonString(queryParameters), noOfRowsUpdated));
      logger.info(String.format("Query executed successfully. query : %s, with parameters : %s",
          queryString, getJsonString(queryParameters)));
      return noOfRowsUpdated;
    } catch (ConstraintViolationException cvException) {
      logger.error(String.format(QUERY_EXECUTION_FAILS, queryString), cvException);
      throw new ReferentialIntegrityException(
          DataAccessUtils.getInstance().getPropertyFileValue(FOREIGN_KEY_EXCEPTION), cvException);
    } catch (HibernateException hibernateException) {
      logger.error(String.format(QUERY_EXECUTION_FAILS, queryString), hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage(),
          hibernateException);
    } catch (Exception exception) {
      logger.error(String.format(QUERY_EXECUTION_FAILS, queryString), exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
  }

  @Override
  public void executeSQLQuery(String queryString) throws DataAccessException {
    try {
      Integer noOfRowsUpdated = 0;
      logger.debug(String.format("Executing the sql query : %s", queryString));
      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createSQLQuery(queryString);
        noOfRowsUpdated = query.executeUpdate();
      }
      logger.debug(String.format("Executed the sql query : %s, no of rows affected : %d ",
          queryString, noOfRowsUpdated));
      logger.info(String.format("SQL Query executed successfully. query : %s", queryString));
    } catch (HibernateException hibernateException) {
      logger.error(String.format("SQL Query executtion failed. query : %s", queryString),
          hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage(),
          hibernateException);
    } catch (Exception exception) {
      logger.error(String.format("SQL Query execution failed. query : %s", queryString), exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
  }

  @Override
  public int executeSQLQuery(String queryString, List<QueryParameter<?>> queryParameters)
      throws DataAccessException {
    try {
      int noOfRowsUpdated = 0;
      logger.debug(String.format("Executing the sql query : %s, parameters : %s ", queryString,
          getJsonString(queryParameters)));
      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createSQLQuery(queryString);
        for (QueryParameter<?> queryParameter : queryParameters) {
          if (queryParameter.getValue() != null
              && (queryParameter.getValue().getClass().equals(List.class)
                  || queryParameter.getValue().getClass().equals(ArrayList.class))) {
            List<?> parameter = (List<?>) queryParameter.getValue();
            query.setParameterList(queryParameter.getName(), parameter);
          } else {
            query.setParameter(queryParameter.getName(), queryParameter.getValue());
          }
        }
        noOfRowsUpdated = query.executeUpdate();
      }
      logger.debug(
          String.format("Executed the sql query : %s,parameters : %s, no of rows affected : %d ",
              queryString, getJsonString(queryParameters), noOfRowsUpdated));
      logger.info(String.format("SQL Query executed successfully. query : %s, with parameters : %s",
          queryString, getJsonString(queryParameters)));

      return noOfRowsUpdated;
    } catch (ConstraintViolationException cvException) {
      logger.error(String.format("SQL Query execution failed. query : %s ", queryString),
          cvException);
      throw new ReferentialIntegrityException(
          DataAccessUtils.getInstance().getPropertyFileValue(FOREIGN_KEY_EXCEPTION), cvException);
    } catch (HibernateException hibernateException) {
      logger.error(String.format("SQL Query executed successfully. query : %s ", queryString),
          hibernateException);
      throw new DataSourceOperationFailedException(hibernateException.getMessage());
    } catch (Exception exception) {
      logger.error(String.format("SQL Query executtion failed. query : %s ", queryString),
          exception);
      throw new DataSourceOperationFailedException(exception.getMessage());
    }
  }

  @Override
  public <T> T refreshObject(T type) throws DataSourceOperationFailedException {
    try {
      Session session = getSessionFactory().getCurrentSession();
      session.refresh(type);
      logger.debug("Object refreshed and full data retrieved successfully");
      logger.info("Data retrieved successfully");
    } catch (Exception exception) {
      logger.error("Data retrieval failed", exception);
      throw new DataSourceOperationFailedException(exception.getMessage(), exception);
    }
    return type;
  }

  private String getJsonString(Object obj) throws JsonProcessingException {
    if (!logger.isDebugEnabled())
      return null;
    ObjectWriter ow = new ObjectMapper().writer();
    ow.withDefaultPrettyPrinter();
    return ow.writeValueAsString(obj);
  }
}
