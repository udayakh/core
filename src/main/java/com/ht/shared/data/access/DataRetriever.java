package com.ht.shared.data.access;

import java.util.Collection;
import java.util.List;

import com.ht.shared.data.access.exception.DataAccessException;
import com.ht.shared.data.access.exception.DataSourceOperationFailedException;

public interface DataRetriever {

  /**
   * Retrieve a persisted object based on its id and type.
   * 
   * @param type The entity class of the object.
   * @param keyValue Value for the object's primary key.
   * @return null or an instance of T
   * @throws DataAccessException
   */
  <T> T retrieveById(Class<T> type, Object keyValue) throws DataAccessException;

  <E> List<E> retrieveByHQL(String queryString) throws DataAccessException;

  <E> List<E> retrieveByHQL(String queryString, List<QueryParameter<?>> queryParameters)
      throws DataAccessException;

  <E> List<E> retrieveByHQL(String queryString, QueryProperties queryProperties)
      throws DataAccessException;

  <E> List<E> retrieveByHQL(String queryString, List<QueryParameter<?>> queryParameters,
      QueryProperties queryProperties) throws DataAccessException;

  <E> List<E> retrieveBySQL(String queryString) throws DataAccessException;

  <E> List<E> retrieveBySQL(String queryString, List<QueryParameter<?>> queryParameters)
      throws DataAccessException;

  <T> T retrieveObjectBySQL(String string, List<QueryParameter<?>> queryParameters, Class<?> t)
      throws DataSourceOperationFailedException;

  <E> List<E> retrieveBySQL(String queryString, QueryProperties queryProperties)
      throws DataAccessException;

  <E> List<E> retrieveBySQL(String queryString, List<QueryParameter<?>> queryParameters,
      QueryProperties queryProperties) throws DataAccessException;

  <T> T retrieveObjectByHQL(String queryString) throws DataAccessException;

  <T> T retrieveObjectByHQL(String queryString, List<QueryParameter<?>> queryParameters)
      throws DataAccessException;

  <E> List<E> retrieveByHQLWithLimits(String queryString, Integer startLimit, Integer endLimit)
      throws DataAccessException;

  <E> List<E> retrieveByHQLWithLimits(String queryString, Integer startLimit, Integer endLimit,
      List<QueryParameter<?>> queryParameters) throws DataAccessException;

  /**
   * Retrieve data using HQL.
   * 
   * @param query Query to execute.
   * @param target The target class of the list entries.
   * @return {@link List}
   * @throws DataAccessException
   */
  <T> List<T> retrieveByHQLResultTransformer(String query, Class<T> target)
      throws DataAccessException;

  /**
   * Retrieve data using HQL and the aliasToBean ResultTransformer.
   * 
   * @param query Query to execute.
   * @param queryParameters Properties to inject into the query.
   * @param target The target class of the list entries.
   * @return {@link List}
   * @throws DataAccessException
   */
  <T> List<T> retrieveByHQLResultTransformer(String query,
      Collection<QueryParameter<?>> queryParameters, Class<T> target) throws DataAccessException;

  <T, E> List<T> retrieveBySQLWithLimitsAndResultTransformer(E e, String query, Integer startLimit,
      Integer endLimit) throws DataAccessException;

  <E> List<E> retrieveBySQLWithLimits(String queryString, Integer startLimit, Integer endLimit)
      throws DataAccessException;

  <E> List<E> retrieveBySQLResultTransformer(String query, Class<?> t) throws DataAccessException;

  <E> List<E> retrieveBySQLResultTransformer(String queryString,
      List<QueryParameter<?>> queryParameters, Class<?> t) throws DataAccessException;

  <T> T retrieveObjectBySQLWithResultTransformer(String queryString,
      List<QueryParameter<?>> queryParameters, Class<?> t) throws DataAccessException;

  <E> List<E> retrieveBySQLResultTransformer(String queryString, Class<?> t, Integer start,
      Integer size) throws DataAccessException;

  <T> T retrieveObjectBySQL(String queryString) throws DataSourceOperationFailedException;

  <T> T retrieveObjectBySQL(String queryString, List<QueryParameter<?>> queryParameters)
      throws DataAccessException;

  // ----------------- Root Unique Fetch -------------[START] MUTHU G.K

  <E> List<E> retrieveByHQLUniqueRoot(String queryString, List<QueryParameter<?>> queryParameters)
      throws DataAccessException;

  <T> T retrieveObjectByHQLUniqueRoot(String queryString, List<QueryParameter<?>> queryParameters)
      throws DataAccessException;
  // ----------------- Root Unique Fetch ------------------------[END]

}
