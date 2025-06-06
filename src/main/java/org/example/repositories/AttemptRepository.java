package org.example.repositories;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.event.Observes;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.beans.AttemptStats;
import org.example.beans.HitRatio;
import org.example.model.Attempt;
import org.example.services.AreaCheck;
import org.example.services.AreaCheckQualifier;
import org.example.utils.MBeanRegistry;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Named("attemptRepository")
@SessionScoped
@Startup
public class AttemptRepository implements Serializable {
  private static final int LATEST_ATTEMPTS_COUNT = 10;

  private final AttemptStats statsMBean = new AttemptStats();
  private final HitRatio hitRatioMBean = new HitRatio();

  @PersistenceContext
  private EntityManager entityManager;

  @Inject
  @AreaCheckQualifier
  private AreaCheck areaCheck;

  @PostConstruct
  public void init(@Observes @Initialized(SessionScoped.class) Object unused) {
    MBeanRegistry.registerBean(statsMBean, "attemptStats");
    MBeanRegistry.registerBean(hitRatioMBean, "hitRatio");
  }

  public void destroy(@Observes @Destroyed(SessionScoped.class) Object unused) {
    MBeanRegistry.unregisterBean(statsMBean);
    MBeanRegistry.unregisterBean(hitRatioMBean);
  }

  public List<Attempt> getAttemptsList(int start, int count) {
    return entityManager.createQuery("select attempt from Attempt attempt", Attempt.class)
      .setFirstResult(start).setMaxResults(count).getResultList();
  }

  public List<Attempt> getLatestAttemptsList() {
    int attemptsCount = getAttemptsCount();
    int firstResultIndex = Math.max(attemptsCount - LATEST_ATTEMPTS_COUNT, 0);
    return  entityManager.createQuery("select attempt From Attempt attempt", Attempt.class)
      .setFirstResult(firstResultIndex).setMaxResults(LATEST_ATTEMPTS_COUNT).getResultList();
  }

  @Transactional
  public Attempt addAttempt(Attempt attempt) {
    areaCheck.checkHit(attempt);
    entityManager.merge(attempt);
    entityManager.flush();
    statsMBean.updateAttempt(attempt.isResult());
    hitRatioMBean.updateStats(attempt.isResult());
    return attempt;
  }

  public int getAttemptsCount() {
    return entityManager.createQuery("select count(*) from Attempt", Number.class).getSingleResult().intValue();
  }

  @Transactional
  public void clearAttempts() {
    entityManager.createQuery("delete from Attempt").executeUpdate();
  }

  @Transactional
  public void addAttemptFromJsParams(int currentR) {
    final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

    try {
      var x = Double.parseDouble(params.get("x"));
      var y = Double.parseDouble(params.get("y"));
      var graphR = Double.parseDouble(params.get("r"));

      final Attempt attemptBean = new Attempt(
        x / graphR * currentR,
        y / graphR * currentR,
        currentR
      );
      addAttempt(attemptBean);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  public String collectToJson(Function<? super Attempt, Double> getter) {
    return new Gson().toJson(getLatestAttemptsList().stream().map(getter).collect(Collectors.toList()));
  }

  public String getX() {
    return collectToJson(Attempt::getX);
  }

  public String getY() {
    return collectToJson(Attempt::getY);
  }

  public String getR() {
    return collectToJson(Attempt::getR);
  }

  public String getHit() {
    return new Gson().toJson(getLatestAttemptsList().stream().map(Attempt::isResult).collect(Collectors.toList()));
  }

  public String getPointsCoordinates() {
    return new Gson().toJson(
      getLatestAttemptsList().stream().map(Attempt::getCoordinates).collect(Collectors.toList())
    );
  }
}
