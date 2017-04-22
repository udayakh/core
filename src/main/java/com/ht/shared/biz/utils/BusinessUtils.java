package com.ht.shared.biz.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.ht.client.rest.utils.FileUtils;
import com.ht.shared.utils.ApplicationContextUtils;

public class BusinessUtils {

	private static BusinessUtils INSTANCE;

	private final Logger logger = Logger.getLogger(BusinessUtils.class);

	/**
	 * This method used to getInstance.
	 * 
	 * @return INSTANCE
	 */
	public static BusinessUtils getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BusinessUtils();
		}
		return INSTANCE;
	}

	public static final String JOOMLA_URL = "http://27.251.35.202/lms/login.php";
	public static final String JOOMLA_URL_COURSES = "http://27.251.35.202/lms/courses.php";

	private String businessPropertiesFile = "com/revature/shared/biz/utils/businessMessages.properties";

	/**
	 * This method used to getPropertyFileValue.
	 * 
	 * @return value
	 */
	public String getPropertyFileValue(String keyName) {
		String value = null;
		Properties prop = new Properties();
		try {
			InputStream propertiesFile = BusinessUtils.class.getClassLoader()
					.getResourceAsStream(businessPropertiesFile);
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

	/**
	 * This method used to getJSONResponseUsingPost.
	 * 
	 * @return json
	 */
	public JSONObject getJSONResponseUsingPost(String httpURL, List<NameValuePair> nvps) {
		HttpPost httpPost = null;
		JSONObject json = null;
		try (DefaultHttpClient httpclient = new DefaultHttpClient()) {
			httpPost = new HttpPost(httpURL);
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			HttpResponse response = httpclient.execute(httpPost);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			json = new JSONObject(builder.toString());
			EntityUtils.consume(response.getEntity());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}

		return json;
	}

	/**
	 * This method used to getResponse.
	 */
	public String getResponse(String url, String params) {
		String reponse = null;
		HttpURLConnection pageRequest = null;
		try {
			pageRequest = (HttpURLConnection) new URL(url).openConnection();
			pageRequest.setRequestMethod("POST");
			pageRequest.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			String paramsTemp = params;
			pageRequest.setRequestProperty("Content-Length", "" + Integer.toString(paramsTemp.getBytes().length));
			pageRequest.setDoOutput(true);
			pageRequest.setInstanceFollowRedirects(true);
			DataOutputStream wr = new DataOutputStream(pageRequest.getOutputStream());
			wr.writeBytes(paramsTemp);
			wr.flush();
			wr.close();
			if (pageRequest.getResponseCode() == 200) {
				reponse = getResponse(pageRequest).toString();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			pageRequest.disconnect();
		}
		return reponse;
	}

	private StringBuffer getResponse(HttpURLConnection request) {
		StringBuffer response = null;
		try {
			String temp;
			BufferedReader ld = new BufferedReader(new InputStreamReader(request.getInputStream()));
			response = new StringBuffer();
			while ((temp = ld.readLine()) != null) {
				response.append(temp);
			}
			ld.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
