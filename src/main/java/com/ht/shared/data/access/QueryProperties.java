package com.ht.shared.data.access;

public class QueryProperties {

  private Integer firstResult;
  private Integer maxResults;
  private Integer fetchSize;

  public Integer getFirstResult() {
    return firstResult;
  }

  public void setFirstResult(Integer firstResult) {
    this.firstResult = firstResult;
  }

  public Integer getMaxResults() {
    return maxResults;
  }

  public void setMaxResults(Integer maxResults) {
    this.maxResults = maxResults;
  }

  public Integer getFetchSize() {
    return fetchSize;
  }

  public void setFetchSize(Integer fetchSize) {
    this.fetchSize = fetchSize;
  }

}
