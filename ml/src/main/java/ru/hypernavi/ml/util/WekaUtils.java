package ru.hypernavi.ml.util;

import org.jetbrains.annotations.NotNull;
import weka.classifiers.evaluation.Evaluation;

/**
 * Created by amosov-f on 07.01.17
 */
public enum WekaUtils {
  ;

  public static double correlationCoefficient(@NotNull final Evaluation eval) {
    try {
      return eval.correlationCoefficient();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
