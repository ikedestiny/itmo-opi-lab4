package org.example.services;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import org.example.dto.AttemptDto;
import org.example.model.Attempt;


import java.io.Serializable;
import java.util.Date;

@Named("areaCheck")
@SessionScoped
@AreaCheckQualifier
public class AreaCheckImpl implements AreaCheck, Serializable {
  @Override
  public void checkHit(Attempt attemptBean) {
    long startTime = System.nanoTime();
    attemptBean.setCreatedAt(new Date(System.currentTimeMillis()));

    boolean hit = new Checker()
      .attemptIsInArea(
        new AttemptDto(attemptBean.getX(), attemptBean.getY(), attemptBean.getR()
      )
    );

    attemptBean.setResult(hit);
    attemptBean.setExecutionTime(System.nanoTime() - startTime);
  }
}
