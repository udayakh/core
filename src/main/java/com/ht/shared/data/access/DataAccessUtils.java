package com.ht.shared.data.access;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataAccessUtils {

  private static DataAccessUtils INSTANCE;

  /**
   * This method used to getInstance.
   * 
   * @return INSTANCE
   */
  public static DataAccessUtils getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DataAccessUtils();
    }
    return INSTANCE;
  }

  private String dataAccessPropFile =
      "com/revature/shared/data/access/dataAccessMessages.properties";

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
          DataAccessUtils.class.getClassLoader().getResourceAsStream(dataAccessPropFile);
      if (propertiesFile != null) {
        prop.load(propertiesFile);
        String tempString = prop.getProperty(keyName);
        if (tempString != null && !tempString.equalsIgnoreCase("")) {
          value = tempString;
        } else {
          value = keyName;
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return value;
  }

}
