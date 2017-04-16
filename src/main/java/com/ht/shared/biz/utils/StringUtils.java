package com.ht.shared.biz.utils;

import java.util.Base64;
import java.util.StringTokenizer;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * This for the Only String Return method Class
 * 
 * @author MUTHU G.K
 *
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

  private StringUtils() {}

  /**
   * @author MUTHU G.K
   * 
   * @param atleastOneNumberRequired
   * @param atleastOneSmallLetterRequired
   * @param atleastOneCapsLetterRequired
   * @param atleastOneSpecialCharRequired
   * @param noSpaceRequired
   * @param minLength
   * @param maxLength
   * @return REGEX pattern for Validation
   */
  public static String validationRegularExpression(boolean atleastOneNumberRequired,
      boolean atleastOneSmallLetterRequired, boolean atleastOneCapsLetterRequired,
      boolean atleastOneSpecialCharRequired, boolean noSpaceRequired, int minLength,
      int maxLength) {

    StringBuilder regex = new StringBuilder("(");
    if (atleastOneNumberRequired) {
      regex.append("(?=.*\\d)");
    }
    if (atleastOneSmallLetterRequired) {
      regex.append("(?=.*[a-z])");
    }
    if (atleastOneCapsLetterRequired) {
      regex.append("(?=.*[A-Z])");
    }
    if (atleastOneSpecialCharRequired) {
      regex.append("(?=.*[\\p{Punct}])");
    }
    if (noSpaceRequired) {
      regex.append("(?=\\S+$)");
    }

    regex.append(".{" + minLength + "," + maxLength + "}");
    regex.append(")");

    return regex.toString();
  }

  /**
   * @author MUTHU G.K
   * 
   * @param value - <b>Input for Encryption</b>
   * @return <b>Encrypted String</b>
   */
  public static String basic64Encryption(byte[] value) {
    return (value != null && value.length != 0) ? Base64.getEncoder().encodeToString(value) : null;
  }

  /**
   * @author MUTHU G.K
   * 
   * @param value - <b>Input for Decryption</b>
   * @return <b>Decrypted String</b>
   */
  public static String basic64Decryption(String value) {
    return isNotBlank(value) ? new String(Base64.getDecoder().decode(value)) : null;
  }

  /**
   * @author MUTHU G.K
   * 
   * @param value - <b>Input for CamelCase String</b>
   * @return <b>Camel Case String</b>
   */
  public static String converToCamelCase(String strdata) {

    if (!isValidString(strdata)) {
      return null;
    }

    StringBuilder strbufCamelCase = new StringBuilder();
    StringTokenizer st = new StringTokenizer(strdata);

    st.countTokens();
    while (st.hasMoreTokens()) {
      String strWord = st.nextToken();
      strbufCamelCase.append(strWord.substring(0, 1).toUpperCase());
      if (strWord.length() > 1) {
        strbufCamelCase.append(strWord.substring(1).toLowerCase());
      }
      if (st.hasMoreTokens()) {
        strbufCamelCase.append(" ");
      }
    }
    return strbufCamelCase.toString();
  }

  /**
   * @author MUTHU G.K
   * 
   * @param value - <b>Input for remove unwanted Space</b>
   * @return <b>Single Spaced String</b>
   */
  public static String removeMoreThanOneSpaceFromString(String strdata) {

    if (!isValidString(strdata)) {
      return null;
    }

    StringBuilder value = new StringBuilder();
    StringTokenizer st = new StringTokenizer(strdata);

    st.countTokens();
    while (st.hasMoreTokens()) {
      value.append(st.nextToken());
      if (st.hasMoreTokens()) {
        value.append(" ");
      }
    }
    return value.toString();
  }

  public static boolean isNumericString(String str) {
    return NumberUtils.isParsable(str);
  }

  public static boolean isValidString(String str) {
    return isNotBlank(str);
  }
}
