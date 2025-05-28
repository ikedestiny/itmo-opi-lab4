package org.example.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AttemptDto implements Serializable {
  public AttemptDto(double x, double y, double r) {
    this.x = x;
    this.y = y;
    this.r = r;
  }

  private double x;

  private double y;

  private double r;
}
