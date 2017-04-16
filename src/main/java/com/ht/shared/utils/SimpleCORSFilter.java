package com.ht.shared.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/**
 * This Filter is used to restrict the access if the request is not comming from front ent.
 * 
 * @author Venkateswarlu
 *
 */
@Component
public class SimpleCORSFilter implements Filter {

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse response = (HttpServletResponse) res;
    HttpServletRequest request = (HttpServletRequest) req;
    if (request.getHeader("Access-Control-Request-Method") != null
        && "OPTIONS".equals(request.getMethod())) {
      response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
      response.setHeader("Access-Control-Allow-Credentials", "true");
      response.setHeader("Access-Control-Max-Age", "3600");
      response.setHeader("Access-Control-Allow-Headers",
          "Accept, Authorization, Content-Type, token, adminToken, univUserToken, univUserAdminToken");
      response.setHeader("token", "*");
      response.setHeader("adminToken", "*");
      response.setHeader("univUserToken", "*");
      response.setHeader("univUserAdminToken", "*");

    }
    chain.doFilter(req, res);
  }

  @Override
  public void init(FilterConfig filterConfig) {
    // Nothing for initialization.
  }

  @Override
  public void destroy() {
    // Nothing for destroy.
  }

}
