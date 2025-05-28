package org.example.beans;

import com.google.common.util.concurrent.AtomicDouble;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

@Named("hitRatio")
@SessionScoped
public class HitRatio implements HitRatioMBean, Serializable {
  private final AtomicDouble clickInterval = new AtomicDouble();
  private  final Queue<Long> clickTimes = new LinkedList<>();
  private static final int MAX_HISTORY = 100;



  @Override
  public double getClickInterval() {
    return clickInterval.get();
  }

  public void recordClick() {
    long now = System.currentTimeMillis();
    clickTimes.add(now);
    if (clickTimes.size() > MAX_HISTORY) {
      clickTimes.poll();
    }
    if (clickTimes.size() < 2) clickInterval.set(0.0);

    long sum = 0;
    Long prev = null;
    for (Long time : clickTimes) {
      if (prev != null) {
        sum += time - prev;
      }
      prev = time;
    }

    int count = clickTimes.size() - 1;
    clickInterval.set( count > 0 ? (double) sum / count / 1000.0 : 0.0);
  }



  public void updateStats(boolean hit) {
    recordClick();
  }
}
