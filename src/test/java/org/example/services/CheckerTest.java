package org.example.services;

import org.example.dto.AttemptDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CheckerTest {

  private Checker checker;

  @BeforeEach
  void setUp() {
    checker = new Checker();
  }

  @Test
  void testHitInArea() {
    assertTrue(checker.attemptIsInArea(new AttemptDto(0.1, 0.1, 1.0)));
    assertTrue(checker.attemptIsInArea(new AttemptDto(0.25, 1, 2.0)));
    assertFalse(checker.attemptIsInArea(new AttemptDto(0.1, -0.2, 1.0)));
  }

  @Test
  void testHitInRectangle() {
    assertTrue(checker.attemptIsInRect(new AttemptDto(-0.5, 0.25, 1.0)));
    assertTrue(checker.attemptIsInRect(new AttemptDto(-1.0, 0.5, 2.0)));

    assertFalse(checker.attemptIsInRect(new AttemptDto(-1.1, 0.6, 1.0)));
    assertFalse(checker.attemptIsInRect(new AttemptDto(0.1, 0.1, 1.0)));
  }

  @Test
  void testHitInSector() {
    assertTrue(checker.attemptIsInSector(new AttemptDto(-0.5, -0.5, 2.0)));
    assertTrue(checker.attemptIsInSector(new AttemptDto(-0.1, -0.1, 0.5)));

    assertFalse(checker.attemptIsInSector(new AttemptDto(-1.1, -1.1, 3.0)));
    assertFalse(checker.attemptIsInSector(new AttemptDto(0.1, -0.2, 1.0)));
  }

  @Test
  void testHitInTriangle() {
    assertTrue(checker.attemptIsInTriangle(new AttemptDto(0.1, 0.1, 1.0)));
    assertTrue(checker.attemptIsInTriangle(new AttemptDto(0.25, 0.5, 1.0)));

    assertFalse(checker.attemptIsInTriangle(new AttemptDto(0.6, 0.6, 1.0)));
    assertFalse(checker.attemptIsInTriangle(new AttemptDto(-0.1, 0.2, 1.0)));
  }
}
