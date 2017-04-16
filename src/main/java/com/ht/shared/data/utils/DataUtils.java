package com.ht.shared.data.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.Query;

import com.ht.shared.biz.utils.StringUtils;
import com.ht.shared.data.access.QueryParameter;

public class DataUtils {
  private final Logger logger = Logger.getLogger(DataUtils.class);
  private static DataUtils instance;
  private String dataPropFile = "com/revature/shared/data/utils/dataMessages.properties";

  /**
   * This method used to getInstance.
   * 
   * @return INSTANCE
   */
  public static DataUtils getInstance() {
    if (instance == null) {
      instance = new DataUtils();
    }
    return instance;
  }

  /**
   * This method used to getPropertyFileValue.
   * 
   * @return value
   */
  public String getPropertyFileValue(String keyName) {
    String value = null;
    Properties prop = new Properties();
    try {
      InputStream propertiesFile =
          DataUtils.class.getClassLoader().getResourceAsStream(dataPropFile);
      if (propertiesFile != null) {
        prop.load(propertiesFile);
        String tempString = prop.getProperty(keyName);
        if (StringUtils.isNotBlank(tempString)) {
          value = tempString;
        } else {
          value = keyName;
        }
      }
    } catch (IOException ex) {
      logger.error(ex.getMessage(), ex);
    }
    return value;
  }

  /**
   * Convenience method for generating parameter lists for our DataRetriever API.
   * 
   * @param params One or more {@link QueryParameter}
   * @return List of {@link QueryParameter}
   */
  public static List<QueryParameter<?>> constructParamList(QueryParameter<?>... params) {
    List<QueryParameter<?>> list = new ArrayList<>(params.length);
    for (QueryParameter<?> param : params) {
      list.add(param);
    }
    return list;
  }

  /**
   * Assister method to DataRetriever and DataModifier implementations. This method handles adding
   * the values of many {@link QueryParameter}s to the Hibernate {@link Query}.
   * 
   * @param query
   * @param parameter
   */
  public static void setQueryParameters(Query query, Collection<QueryParameter<?>> parameters) {
    for (QueryParameter<?> parameter : parameters) {
      String name = parameter.getName();
      Object value = parameter.getValue();
      if (value instanceof Collection) {
        query.setParameterList(name, (Collection<?>) value);
      } else {
        query.setParameter(name, value);
      }
    }
  }

}
