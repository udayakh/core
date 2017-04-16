package com.ht.shared.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;

import com.ht.client.rest.utils.RequestWrapper;

public class SpringServlet extends DispatcherServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void doDispatch(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    RequestWrapper myRequestWrapper = new RequestWrapper(request);
    super.doDispatch(myRequestWrapper, response);

  }
}
