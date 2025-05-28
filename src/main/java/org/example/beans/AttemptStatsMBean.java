package org.example.beans;

public interface AttemptStatsMBean {
  int getTotalAttempts();
  int getTotalHits();
  void checkForConsecutiveMisses();

}
