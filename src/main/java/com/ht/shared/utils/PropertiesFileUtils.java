package com.ht.shared.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service
@DependsOn({"applicationProperties"})
public class PropertiesFileUtils {

  private static Logger logger = Logger.getLogger(PropertiesFileUtils.class);

  private static Properties applicationProperties;

  @Autowired
  private PropertiesFileUtils(Properties applicationProperties) {
    PropertiesFileUtils.applicationProperties = applicationProperties;
  }

  public static String getValue(String propertyName) {
    return getValue("application", propertyName);
  }

  /**
   * getValue method.
   *
   */
  public static String getValue(String fileName, String propertyName) {
    String value = null;
    Properties prop = null;
    if ("application".equals(fileName)) {
      prop = applicationProperties;
    } else {
      try (FileInputStream propertiesFile = new FileInputStream(fileName)) {
        prop = new Properties();
        prop.load(propertiesFile);

      } catch (IOException e) {
        logger.debug(e);
        prop = null;
      }
    }

    if (prop != null) {
      value = StringUtils.trimToEmpty(prop.getProperty(propertyName));
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Return state: fileName=\"" + fileName + "\" propertyName=\"" + propertyName
          + "\" value=\"" + value + '"');
    }
    return value;
  }

  public static String getKey(String fileName, String propertyName) {
    String value = null;
    Properties prop = null;
    InputStream propertiesFile = null;
    try {
      if ("pageLabels".equals(fileName)) {
        prop = ApplicationContextUtils.getBean("pageLabels");
      }
      if (prop == null) {
        prop = new Properties();
        propertiesFile = PropertiesFileUtils.class.getClassLoader()
            .getResourceAsStream(fileName + ".properties");
        prop.load(propertiesFile);
      }

      String tempString = prop.getProperty(propertyName);
      if (tempString != null && !"".equalsIgnoreCase(tempString)) {
        value = tempString;
      } else {
        value = propertyName;
      }

    } catch (IOException ex) {
      logger.debug(ex);
    } finally {
      if (propertiesFile != null) {
        try {
          propertiesFile.close();
        } catch (IOException e) {
          logger.debug(e);
        }
      }
    }
    return value;
  }

}
