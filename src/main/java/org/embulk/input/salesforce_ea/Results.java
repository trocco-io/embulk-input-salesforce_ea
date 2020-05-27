package org.embulk.input.salesforce_ea;

import java.util.List;
import java.util.Map;

public class Results {
  private List<Map<String, String>> records;

  public List<Map<String, String>> getRecords() {
    return records;
  }

  public void setRecords(List<Map<String, String>> records) {
    this.records = records;
  }
}