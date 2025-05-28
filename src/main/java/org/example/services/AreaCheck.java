package org.example.services;


import org.example.model.Attempt;

import java.io.Serializable;

public interface AreaCheck extends Serializable {
  void checkHit(Attempt attemptBean);
}
