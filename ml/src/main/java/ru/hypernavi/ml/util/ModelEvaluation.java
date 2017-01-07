package ru.hypernavi.ml.util;

import org.jetbrains.annotations.NotNull;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

import java.util.Random;

/**
 * Created by amosov-f on 07.01.17
 */
public final class ModelEvaluation<T> {
  @NotNull
  private final T classifier;
  @NotNull
  private final Evaluation evaluation;

  public ModelEvaluation(@NotNull final T classifier, @NotNull final Evaluation evaluation) {
    this.classifier = classifier;
    this.evaluation = evaluation;
  }

  @NotNull
  public T getClassifier() {
    return classifier;
  }

  @NotNull
  public Evaluation getEvaluation() {
    return evaluation;
  }

  public double getCorrelationCoefficient() {
    return WekaUtils.correlationCoefficient(evaluation);
  }

  @NotNull
  public static <T extends Classifier> ModelEvaluation<T> leaveOneOut(@NotNull final T classifier, @NotNull final Instances data) {
    try {
      final Evaluation eval = new Evaluation(data);
      eval.crossValidateModel(classifier, data, data.size(), new Random(0));
      return new ModelEvaluation<>(classifier, eval);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
