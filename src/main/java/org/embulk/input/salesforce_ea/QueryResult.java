package org.embulk.input.salesforce_ea;

public class QueryResult {
  private String responseId;
  private Results results;
  private String query;
  private long responseTime;
  private int limit;
  private int offset;
  private String resultVariable;
  private boolean done;

  public String getResponseId() {
    return responseId;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public Results getResults() {
    return results;
  }

  public void setResults(Results results) {
    this.results = results;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public long getResponseTime() {
    return responseTime;
  }

  public void setResponseTime(long responseTime) {
    this.responseTime = responseTime;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public String getResultVariable() {
    return resultVariable;
  }

  public void setResultVariable(String resultVariable) {
    this.resultVariable = resultVariable;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }
}