package com.ht.shared.data.access;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.Transformers;
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
import com.ht.shared.data.utils.DataUtils;

/**
 * This implementation is based on hibernate and this is used for the Select DML operation This uses
 * hibernate session which is injected through spring container.Transactions managed by spring
 * container.
 * 
 * @author Selva
 * 
 */

@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
@SuppressWarnings("unchecked")
public class DataRetrieverHibernateImpl implements DataRetriever, Serializable {

  private static final Function<List<?>, Integer> GET_SIZE = list -> list != null ? list.size() : 0;

  private static final long serialVersionUID = 1L;
  private static final String QUERY_RETRIEVED_RESULT = "Query retrieved the result from database.";
  private static final String RETRIEVAL_FAILS = "msg.datasource.retrieval.fail";

  private static Logger logger = Logger.getLogger(DataRetrieverHibernateImpl.class);

  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public <T> T retrieveById(Class<T> type, Object keyValue) throws DataAccessException {
    T object = null;
    try {
      logger.debug(String.format("Retrieving the data, based on the key value : %s ", keyValue));

      Session session = getSessionFactory().getCurrentSession();
      ClassMetadata classMetadata = getSessionFactory().getClassMetadata(type);
      String keyName = classMetadata.getIdentifierPropertyName();
      Criteria criteria = session.createCriteria(type);
      criteria.add(Restrictions.eq(keyName, keyValue));
      object = type.cast(criteria.uniqueResult());

      logger.debug(
          String.format("Data retrieved successfully, based on the key value : %s ", keyValue));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return object;
  }



  @Override
  public <T> T retrieveObjectByHQL(String queryString) throws DataAccessException {
    T object = null;
    try {
      logger.debug(String.format("Retrieving the data, based on the query: %s", queryString));
      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        object = (T) query.uniqueResult();
      }
      if (object != null) {
        logger.debug(
            String.format("Data retrieved successfully, based on the query: %s ", queryString));
      } else {
        logger.debug(String.format("Retrieved empty data for given query: %s ", queryString));
      }
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return object;
  }

  @Override
  public <T> T retrieveObjectByHQL(String queryString, List<QueryParameter<?>> queryParameters)
      throws DataAccessException {
    T object = null;
    try {
      logger.debug(
          String.format("Retrieving the object based on the query : %s , with the parameters : %s",
              queryString, getJsonString(queryParameters)));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        for (QueryParameter<?> queryParameter : queryParameters) {
          if (queryParameter.getValue().getClass().equals(List.class)
              || queryParameter.getValue().getClass().equals(ArrayList.class)) {
            List<?> parameter = (List<?>) queryParameter.getValue();
            query.setParameterList(queryParameter.getName(), parameter);
          } else {
            query.setParameter(queryParameter.getName(), queryParameter.getValue());
          }
        }
        object = (T) query.uniqueResult();
      }
      if (object != null) {
        logger.debug(String.format(
            "Data retrieved successfully, based on the query : %s , with the parameters : %s",
            queryString, getJsonString(queryParameters)));
      } else {
        logger.debug(String.format(
            "Retrieved empty data, based on the query : %s , with the parameters : %s", queryString,
            getJsonString(queryParameters)));
      }
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return object;
  }

  @Override
  public <E> List<E> retrieveByHQL(String queryString) throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(String.format("Retrieving the result based on the query : %s", queryString));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        objects = query.list();
      }

      logger
          .debug(String.format("Retrieved the result based on the query : %s and size of data : %d",
              queryString, GET_SIZE.apply(objects)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <E> List<E> retrieveByHQL(String queryString, List<QueryParameter<?>> queryParameters)
      throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(
          String.format("Retrieving the result based on the query : %s , with the parameters : %s",
              queryString, getJsonString(queryParameters)));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        DataUtils.setQueryParameters(query, queryParameters);
        objects = query.list();
      }

      logger.debug(String.format(
          "Retrieved the result based on the query : %s , "
              + "with the parameters : %s, and the size of data : %d",
          queryString, getJsonString(queryParameters), GET_SIZE.apply(objects)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <E> List<E> retrieveByHQL(String queryString, QueryProperties queryProperties)
      throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(String.format(
          "Retrieving the result based on the query : %s , with the query properties : %s",
          queryString, getJsonString(queryProperties)));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        if (queryProperties != null) {
          if (queryProperties.getFirstResult() != null) {
            query.setFirstResult(queryProperties.getFirstResult());
          }
          if (queryProperties.getMaxResults() != null) {
            query.setMaxResults(queryProperties.getMaxResults());
          }
          if (queryProperties.getFetchSize() != null) {
            query.setFetchSize(queryProperties.getFetchSize());
          }
        }
        objects = query.list();
      }

      logger.debug(String.format(
          "Retrieved the result based on the query : %s , "
              + "	with the query properties : %s, size of data : %d",
          queryString, getJsonString(queryProperties), GET_SIZE.apply(objects)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <E> List<E> retrieveByHQL(String queryString, List<QueryParameter<?>> queryParameters,
      QueryProperties queryProperties) throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(String.format(
          "Retrieving the result based on the query : %s ,"
              + " with the parameters : %s ,properties : %s",
          queryString, getJsonString(queryParameters), getJsonString(queryProperties)));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        for (QueryParameter<?> queryParameter : queryParameters) {
          if (queryParameter.getValue().getClass().equals(List.class)
              || queryParameter.getValue().getClass().equals(ArrayList.class)) {
            List<?> parameter = (List<?>) queryParameter.getValue();
            query.setParameterList(queryParameter.getName(), parameter);
          } else {
            query.setParameter(queryParameter.getName(), queryParameter.getValue());
          }
        }
        if (queryProperties != null) {
          if (queryProperties.getFirstResult() != null) {
            query.setFirstResult(queryProperties.getFirstResult());
          }
          if (queryProperties.getMaxResults() != null) {
            query.setMaxResults(queryProperties.getMaxResults());
          }
          if (queryProperties.getFetchSize() != null) {
            query.setFetchSize(queryProperties.getFetchSize());
          }
        }
        objects = query.list();
      }

      logger.debug(String.format(
          "Retrieved the result based on the query : %s , "
              + "with the parameters : %s ,properties : %s, size of data : %d",
          queryString, getJsonString(queryParameters), getJsonString(queryProperties),
          GET_SIZE.apply(objects)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <E> List<E> retrieveBySQL(String queryString) throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(String.format("Retrieving the result based on the sql query : %s", queryString));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createSQLQuery(queryString);
        objects = query.list();
      }

      logger.debug(
          String.format("Retrieved the result based on the sql query : %s, size of data : %d",
              queryString, GET_SIZE.apply(objects)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <E> List<E> retrieveBySQL(String queryString, List<QueryParameter<?>> queryParameters)
      throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(String.format(
          "Retrieving the result based on the sql query : %s , with the parameters : %s",
          queryString, getJsonString(queryParameters)));

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
        objects = query.list();
      }

      logger.debug(String.format(
          "Retrieved the result based on the sql query : %s ,"
              + " with the parameters : %s, size of data : %d",
          queryString, getJsonString(queryParameters), GET_SIZE.apply(objects)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <E> List<E> retrieveBySQL(String queryString, QueryProperties queryProperties)
      throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(String.format(
          "Retrieving the result based on the sql query : %s , with the query properties : %s",
          queryString, getJsonString(queryProperties)));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createSQLQuery(queryString);
        if (queryProperties != null) {
          if (queryProperties.getFirstResult() != null) {
            query.setFirstResult(queryProperties.getFirstResult());
          }
          if (queryProperties.getMaxResults() != null) {
            query.setMaxResults(queryProperties.getMaxResults());
          }
          if (queryProperties.getFetchSize() != null) {
            query.setFetchSize(queryProperties.getFetchSize());
          }
        }
        objects = query.list();
      }

      logger.debug(String.format(
          "Retrieved the result based on the sql query : %s ,"
              + " with the query properties : %s, size of data : %d",
          queryString, getJsonString(queryProperties), GET_SIZE.apply(objects)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <E> List<E> retrieveBySQL(String queryString, List<QueryParameter<?>> queryParameters,
      QueryProperties queryProperties) throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(String.format(
          "Retrieving the result based on the sql query : %s ,"
              + " with the parameters : %s ,properties : %s",
          queryString, getJsonString(queryParameters), getJsonString(queryProperties)));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createSQLQuery(queryString);
        for (QueryParameter<?> queryParameter : queryParameters) {
          if (queryParameter.getValue().getClass().equals(List.class)
              || queryParameter.getValue().getClass().equals(ArrayList.class)) {
            List<?> parameter = (List<?>) queryParameter.getValue();
            query.setParameterList(queryParameter.getName(), parameter);
          } else {
            query.setParameter(queryParameter.getName(), queryParameter.getValue());
          }
        }
        if (queryProperties != null) {
          if (queryProperties.getFirstResult() != null) {
            query.setFirstResult(queryProperties.getFirstResult());
          }
          if (queryProperties.getMaxResults() != null) {
            query.setMaxResults(queryProperties.getMaxResults());
          }
          if (queryProperties.getFetchSize() != null) {
            query.setFetchSize(queryProperties.getFetchSize());
          }
        }
        objects = query.list();
      }

      logger.debug(String.format(
          "Retrieved the result based on the sql query : %s ,"
              + " with the parameters : %s ,properties : %s," + " size of data : %d",
          queryString, getJsonString(queryParameters), getJsonString(queryProperties),
          GET_SIZE.apply(objects)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <E> List<E> retrieveByHQLWithLimits(String queryString, Integer startLimit,
      Integer endLimit) throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(String.format("Retrieving the result based on the query : %s ,"
          + " with start limit : %d ,no of records : %d", queryString, startLimit, endLimit));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        query.setFirstResult(startLimit);
        query.setMaxResults(endLimit);
        objects = query.list();
      }

      logger.debug(String.format(
          "Retrieved the result based on the query : %s ,"
              + " with start limit : %d ,no of records : %d," + " size of data : %d",
          queryString, startLimit, endLimit, GET_SIZE.apply(objects)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <E> List<E> retrieveByHQLWithLimits(String queryString, Integer startLimit,
      Integer endLimit, List<QueryParameter<?>> queryParameters) throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(String.format(
          "Retrieving the result based on the query : %s ,"
              + "with parameters : %s, start limit : %d ,no of records : %d",
          queryString, getJsonString(queryParameters), startLimit, endLimit));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        query.setFirstResult(startLimit);
        query.setMaxResults(endLimit);
        for (QueryParameter<?> queryParameter : queryParameters) {
          if (queryParameter.getValue().getClass().equals(List.class)
              || queryParameter.getValue().getClass().equals(ArrayList.class)) {
            List<?> parameter = (List<?>) queryParameter.getValue();
            query.setParameterList(queryParameter.getName(), parameter);
          } else {
            query.setParameter(queryParameter.getName(), queryParameter.getValue());
          }
        }
        objects = query.list();
      }

      logger.debug(String.format("Retrieved the result based on the query : %s ,"
          + "with parameters : %s, start limit : %d ," + "no of records : %d, size of data : %d",
          queryString, getJsonString(queryParameters), startLimit, endLimit,
          GET_SIZE.apply(objects)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  /**
   * 
   * This for set the Native SQL Query's column values into given DTO class
   * 
   * @author MUTHU G.K
   * @param e - DTO Class Object
   * @param query - Native SQL Query
   * @exception if query column name Setter is not there in the class means,
   * 
   *            it will throw the could not find the setter Exception <br>
   *            or miss match data type means it will throw not correct data type or convert the
   *            data type for the columns
   */
  @Override
  public <T> List<T> retrieveByHQLResultTransformer(String query, Class<T> target)
      throws DataAccessException {
    try {
      logger.debug(String.format("Retrieving the result based on the query : %s", query));

      List<T> list = getSessionFactory().getCurrentSession().createQuery(query)
          .setResultTransformer(Transformers.aliasToBean(target)).list();

      logger.debug(String.format("Retrieved the result based on the query : %s, size of data : %d",
          query, GET_SIZE.apply(list)));
      logger.info(QUERY_RETRIEVED_RESULT);

      return list;

    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
  }

  @Override
  public <T> List<T> retrieveByHQLResultTransformer(String queryString,
      Collection<QueryParameter<?>> queryParameters, Class<T> target) throws DataAccessException {
    List<T> objects = null;
    try {
      logger.debug(
          String.format("Retrieving the result based on the sql query : %s , parameters : %s",
              queryString, getJsonString(queryParameters)));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        DataUtils.setQueryParameters(query, queryParameters);
        query.setResultTransformer(Transformers.aliasToBean(target));
        objects = query.list();
        logger.debug(String.format(
            "Retrieved the result based on the sql query : %s ,"
                + " parameters : %s, size of data : %d ",
            queryString, getJsonString(queryParameters), GET_SIZE.apply(objects)));
      }
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <T, E> List<T> retrieveBySQLWithLimitsAndResultTransformer(E e, String query,
      Integer startLimit, Integer endLimit) throws DataAccessException {
    List<T> list = null;
    try {
      logger.debug(String.format("Retrieving the result based on the sql query : %s ,"
          + "with limits start : %d ,no of records : %d", query, startLimit, endLimit));

      Session session = getSessionFactory().getCurrentSession();
      if (query != null) {
        list = session.createSQLQuery(query).setFirstResult(startLimit).setMaxResults(endLimit)
            .setResultTransformer(Transformers.aliasToBean(e.getClass())).list();
      }

      logger.debug(String.format(
          "Retrieved the result based on the sql query : %s ,"
              + "with limits start : %d ,no of records : %d," + " size of data : %d",
          query, startLimit, endLimit, GET_SIZE.apply(list)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return list;
  }

  @Override
  public <E> List<E> retrieveBySQLWithLimits(String queryString, Integer startLimit,
      Integer endLimit) throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(String.format("Retrieving the result based on the sql query : %s ,"
          + "with limits start : %d ,no of records : %d", queryString, startLimit, endLimit));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query =
            session.createSQLQuery(queryString).setFirstResult(startLimit).setMaxResults(endLimit);
        objects = query.list();
      }
      logger.debug(String.format(
          "Retrieved the result based on the sql query : %s ,"
              + "with limits start : %d ,no of records : %d, size of data : %d",
          queryString, startLimit, endLimit, GET_SIZE.apply(objects)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <E> List<E> retrieveBySQLResultTransformer(String query, Class<?> t)
      throws DataAccessException {
    List<E> list = null;
    try {
      logger.debug(String.format("Retrieving the result based on the sql query : %s ", query));
      Session session = getSessionFactory().getCurrentSession();
      if (query != null) {
        list =
            session.createSQLQuery(query).setResultTransformer(Transformers.aliasToBean(t)).list();
      }
      logger.debug(
          String.format("Retrieved the result based on the sql query : %s , size of data : %d",
              query, GET_SIZE.apply(list)));
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return list;
  }

  @Override
  public <E> List<E> retrieveBySQLResultTransformer(String queryString,
      List<QueryParameter<?>> queryParameters, Class<?> t) throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(
          String.format("Retrieving the result based on the sql query : %s , parameters : %s",
              queryString, getJsonString(queryParameters)));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createSQLQuery(queryString);
        for (QueryParameter<?> queryParameter : queryParameters) {
          if (queryParameter.getValue().getClass().equals(List.class)
              || queryParameter.getValue().getClass().equals(ArrayList.class)) {
            List<?> parameter = (List<?>) queryParameter.getValue();
            query.setParameterList(queryParameter.getName(), parameter);
          } else {
            query.setParameter(queryParameter.getName(), queryParameter.getValue());
          }
        }
        if (t != null) {
          query.setResultTransformer(Transformers.aliasToBean(t));
        }
        objects = query.list();
        logger.debug(String.format(
            "Retrieved the result based on the sql query : %s ,"
                + " parameters : %s, size of data : %d ",
            queryString, getJsonString(queryParameters), GET_SIZE.apply(objects)));
      }
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <T> T retrieveObjectBySQLWithResultTransformer(String queryString,
      List<QueryParameter<?>> queryParameters, Class<?> t) throws DataAccessException {
    T object = null;
    try {
      logger.debug(
          String.format("Retrieving the Object based on the sql query : %s , parameters : %s",
              queryString, getJsonString(queryParameters)));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createSQLQuery(queryString);

        for (QueryParameter<?> queryParameter : queryParameters) {
          if (queryParameter.getValue().getClass().equals(List.class)
              || queryParameter.getValue().getClass().equals(ArrayList.class)) {
            List<?> parameter = (List<?>) queryParameter.getValue();
            query.setParameterList(queryParameter.getName(), parameter);
          } else {
            query.setParameter(queryParameter.getName(), queryParameter.getValue());
          }
        }
        if (t != null) {
          query.setResultTransformer(Transformers.aliasToBean(t));
        }
        object = (T) query.uniqueResult();
        logger.debug(
            String.format("Retrieved the Object based on the sql query : %s , parameters : %s",
                queryString, getJsonString(queryParameters)));
      }
    } catch (ConstraintViolationException cvException) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue("duplicate.record.exception");
      logger.error(cvException.getMessage(), cvException);
      throw new DuplicateRecordException(msg, cvException);
    } catch (HibernateException hibernateException) {
      String msg = hibernateException.getMessage();
      logger.error(hibernateException.getMessage(), hibernateException);
      throw new DataSourceOperationFailedException(msg);
    } catch (Exception exception) {
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(exception.getMessage());
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return object;
  }

  @Override
  public <E> List<E> retrieveBySQLResultTransformer(String queryString, Class<?> t, Integer start,
      Integer size) throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(String.format("Retrieving the result based on the sql query : %s  ,"
          + " start limit : %d, no of records : %d", queryString, start, size));

      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createSQLQuery(queryString);
        if (start != null) {
          query.setFirstResult(start);
        }
        if (size != null) {
          query.setMaxResults(size);
        }
        query.setResultTransformer(Transformers.aliasToBean(t));
        objects = query.list();
        logger.debug(String.format(
            "Retrieved the result based on the sql query : %s , start limit : %d,"
                + " no of records : %d, size of data : %d ",
            queryString, start, size, GET_SIZE.apply(objects)));
      }
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <T> T retrieveObjectBySQL(String queryString) throws DataSourceOperationFailedException {
    T object = null;
    try {
      logger
          .debug(String.format("Retrieving the Object based on the sql query : %s ", queryString));
      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createSQLQuery(queryString);
        object = (T) query.uniqueResult();
        if (object != null) {
          logger.debug(
              String.format("Retrieved the Object based on the sql query : %s", queryString));
        } else {
          logger.debug(String.format("Retrieved empty data for the sql query : %s ", queryString));
        }
      }
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return object;
  }

  @Override
  public <T> T retrieveObjectBySQL(String queryString, List<QueryParameter<?>> queryParameters)
      throws DataAccessException {
    T object = null;
    try {
      logger.debug(
          String.format("Retrieving the Object based on the sql query : %s , with parameters : %s",
              queryString, getJsonString(queryParameters)));
      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createSQLQuery(queryString);

        for (QueryParameter<?> queryParameter : queryParameters) {
          if (queryParameter.getValue().getClass().equals(List.class)
              || queryParameter.getValue().getClass().equals(ArrayList.class)) {
            List<?> parameter = (List<?>) queryParameter.getValue();
            query.setParameterList(queryParameter.getName(), parameter);
          } else {
            query.setParameter(queryParameter.getName(), queryParameter.getValue());
          }
        }

        object = (T) query.uniqueResult();
        logger.debug(
            String.format("Retrieved the Object based on the sql query : %s , with parameters : %s",
                queryString, getJsonString(queryParameters)));
      }
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return object;
  }

  @Override
  public <T> T retrieveObjectBySQL(String queryString, List<QueryParameter<?>> queryParameters,
      Class<?> t) throws DataSourceOperationFailedException {
    T object = null;
    try {
      logger.debug(
          String.format("Retrieving the Object based on the sql query : %s , with parameters : %s ",
              queryString, getJsonString(queryParameters)));
      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createSQLQuery(queryString);

        for (QueryParameter<?> queryParameter : queryParameters) {
          if (queryParameter.getValue().getClass().equals(List.class)
              || queryParameter.getValue().getClass().equals(ArrayList.class)) {
            List<?> parameter = (List<?>) queryParameter.getValue();
            query.setParameterList(queryParameter.getName(), parameter);
          } else {
            query.setParameter(queryParameter.getName(), queryParameter.getValue());
          }
        }
        if (t != null) {
          query.setResultTransformer(Transformers.aliasToBean(t));
        }
        object = (T) query.uniqueResult();
        logger.debug(String.format(
            "Retrieved the Object based on the sql query : %s , with parameters : %s ", queryString,
            getJsonString(queryParameters)));
      }
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return object;
  }

  // ----------------- Root Unique Fetch -------------[START] MUTHU G.K
  @Override
  public <E> List<E> retrieveByHQLUniqueRoot(String queryString,
      List<QueryParameter<?>> queryParameters) throws DataAccessException {
    List<E> objects = null;
    try {
      logger.debug(
          String.format("Retrieving the result based on the query : %s , with parameters : %s",
              queryString, getJsonString(queryParameters)));
      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        for (QueryParameter<?> queryParameter : queryParameters) {
          if (queryParameter.getValue().getClass().equals(List.class)
              || queryParameter.getValue().getClass().equals(ArrayList.class)) {
            List<?> parameter = (List<?>) queryParameter.getValue();
            query.setParameterList(queryParameter.getName(), parameter);
          } else {
            query.setParameter(queryParameter.getName(), queryParameter.getValue());
          }
        }
        objects = query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        logger.debug(String.format(
            "Retrieved the result based on the query : %s ,"
                + " with parameters : %s, size of data : %d ",
            queryString, getJsonString(queryParameters), GET_SIZE.apply(objects)));
      }
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return objects;
  }

  @Override
  public <T> T retrieveObjectByHQLUniqueRoot(String queryString,
      List<QueryParameter<?>> queryParameters) throws DataAccessException {

    T object = null;
    try {
      logger.debug(
          String.format("Retrieving the object based on the query : %s , with parameters : %s",
              queryString, getJsonString(queryParameters)));
      Session session = getSessionFactory().getCurrentSession();
      if (queryString != null) {
        Query query = session.createQuery(queryString);
        for (QueryParameter<?> queryParameter : queryParameters) {
          if (queryParameter.getValue().getClass().equals(List.class)
              || queryParameter.getValue().getClass().equals(ArrayList.class)) {
            List<?> parameter = (List<?>) queryParameter.getValue();
            query.setParameterList(queryParameter.getName(), parameter);
          } else {
            query.setParameter(queryParameter.getName(), queryParameter.getValue());
          }
        }
        object = (T) query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
        logger.debug(
            String.format("Retrieved the object based on the query : %s , with parameters : %s",
                queryString, getJsonString(queryParameters)));
      }
    } catch (Exception exception) {
      String msg = DataAccessUtils.getInstance().getPropertyFileValue(RETRIEVAL_FAILS);
      logger.error(exception.getMessage(), exception);
      throw new DataSourceOperationFailedException(msg, exception);
    }
    logger.info(QUERY_RETRIEVED_RESULT);
    return object;

  }

  // ----------------- Root Unique Fetch ------------------------[END]
  private String getJsonString(Object obj) throws JsonProcessingException {
    if (!logger.isDebugEnabled())
      return null;
    ObjectWriter ow = new ObjectMapper().writer();
    ow.withDefaultPrettyPrinter();
    return ow.writeValueAsString(obj);
  }
}
