/*
MIT License

Copyright (c) 2019 Idan Morad

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.moradi.circularstatistics;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class contains methods for performing statistics operations such as the conversation between
 * ranges, distance between angles, average and pearson correlation.
 *
 * @author Idan Morad
 * @version 1.0
 */
public class CircularStatistics {
  /**
   * Table implements as map of maps that receives two pair of ranges of representations of angles
   * and, converts the given value from the range of the first pair to second one.
   *
   * <p>For example: CONVERSION_TABLE.get(Pair.of(0,360)).get(Pair.of(-180,180)).apply(270) will
   * output -90
   *
   * <p>available ranges are:
   *
   * <ul>
   *   <li>(0, 360)
   *   <li>(-180, 180)
   *   <li>(0, 2&pi;)
   *   <li>(-&pi;, &pi;)
   * </ul>
   */
  public static final Map<Pair<Double, Double>, Map<Pair<Double, Double>, Function<Double, Double>>>
      CONVERSION_TABLE =
          ImmutableMap
              .<Pair<Double, Double>, Map<Pair<Double, Double>, Function<Double, Double>>>builder()
              .put(
                  Pair.of(0.0, 360.0),
                  ImmutableMap.<Pair<Double, Double>, Function<Double, Double>>builder()
                      .put(Pair.of(0.0, 360.0), Function.identity())
                      .put(Pair.of(-180.0, 180.0), CircularStatistics::convert360To180)
                      .put(Pair.of(0.0, 2 * Math.PI), Math::toRadians)
                      .put(Pair.of(-Math.PI, Math.PI), x -> convert2piToPi(Math.toRadians(x)))
                      .build())
              .put(
                  Pair.of(-180.0, 180.0),
                  ImmutableMap.<Pair<Double, Double>, Function<Double, Double>>builder()
                      .put(Pair.of(0.0, 360.0), CircularStatistics::convert180To360)
                      .put(Pair.of(-180.0, 180.0), Function.identity())
                      .put(Pair.of(0.0, 2 * Math.PI), x -> Math.toRadians(convert180To360(x)))
                      .put(Pair.of(-Math.PI, Math.PI), Math::toRadians)
                      .build())
              .put(
                  Pair.of(0.0, 2 * Math.PI),
                  ImmutableMap.<Pair<Double, Double>, Function<Double, Double>>builder()
                      .put(Pair.of(0.0, 360.0), Math::toDegrees)
                      .put(Pair.of(-180.0, 180.0), x -> convert360To180(Math.toDegrees(x)))
                      .put(Pair.of(0.0, 2 * Math.PI), Function.identity())
                      .put(Pair.of(-Math.PI, Math.PI), CircularStatistics::convert2piToPi)
                      .build())
              .put(
                  Pair.of(-Math.PI, Math.PI),
                  ImmutableMap.<Pair<Double, Double>, Function<Double, Double>>builder()
                      .put(Pair.of(0.0, 360.0), x -> Math.toDegrees(convertPiTo2pi(x)))
                      .put(Pair.of(-180.0, 180.0), Math::toDegrees)
                      .put(Pair.of(0.0, 2 * Math.PI), CircularStatistics::convertPiTo2pi)
                      .put(Pair.of(-Math.PI, Math.PI), Function.identity())
                      .build())
              .build();

  /**
   * Return the distance between angles.
   *
   * @param angdeg1 the starting angle, in degrees.
   * @param angdeg2 the ending angle, in degrees.
   * @return the distance in range [-180, 180]
   */
  public static double angleDistanceDegree(double angdeg1, double angdeg2) {
    return sDist(angdeg1, angdeg2, 360);
  }

  /**
   * Return the distance between angles.
   *
   * @param angrad1 the starting angle, in radians.
   * @param angrad2 the ending angle, in radians.
   * @return the distance in range [-&pi;, &pi;]
   */
  public static double angleDistanceRadians(double angrad1, double angrad2) {
    return sDist(angrad1, angrad2, 2 * Math.PI);
  }

  /**
   * Returns the length of the directed walk from circVal1 to circVal2, with the lowest value length as distance. based on
   * https://www.codeproject.com/Articles/190833/Circular-Values-Math-and-Statistics-with-Cplusplus
   *
   * @param circVal1 the starting circular value.
   * @param circVal2 the ending circular value.
   * @param r max angle.
   * @return the directed walk.
   */
  private static double sDist(double circVal1, double circVal2, double r) {
    double distance = circVal2 - circVal1;
    if (distance < -r / 2) {
      return distance + r;
    }
    if (distance >= r / 2) {
      return distance - r;
    }
    return distance;
  }

  /**
   * converts from range (0, 360) to (-180, 180).
   *
   * @param angdeg angle, in degrees.
   * @return the angle in range (-180, 180).
   */
  private static double convert360To180(double angdeg) {
    if (angdeg > 180) {
      return angdeg - 360;
    }
    return angdeg;
  }

  /**
   * converts from range (-180, 180) to (0, 360).
   *
   * @param angdeg angle, in degrees.
   * @return the angle in range (-180, 180).
   */
  private static double convert180To360(double angdeg) {
    if (angdeg < 0) {
      return angdeg + 360;
    }
    return angdeg;
  }

  /**
   * converts from range (0, 2&pi;) to (-&pi;, &pi;).
   *
   * @param angrad angle, in radians.
   * @return the angle in range (-&pi;, &pi;).
   */
  private static double convert2piToPi(double angrad) {
    if (angrad > Math.PI) {
      return angrad - 2 * Math.PI;
    }
    return angrad;
  }

  /**
   * converts from range (-&pi;, &pi;) to (0, 2&pi;).
   *
   * @param angrad angle, in radians.
   * @return the angle in range (0, 2&pi;).
   */
  private static double convertPiTo2pi(double angrad) {
    if (angrad < 0) {
      return angrad + 2 * Math.PI;
    }
    return angrad;
  }

  /**
   * Calculates the average between radian angles.
   *
   * @param angrads collection of degree angles in range (0, 2&pi;) or (-&pi;, &pi;).
   * @return the average.
   */
  public static double radianAnglesAverage(Collection<Double> angrads) {
    double n = angrads.size();
    return Math.atan2(
        (1 / n) * angrads.stream().mapToDouble(Math::sin).sum(),
        (1 / n) * angrads.stream().mapToDouble(Math::cos).sum());
  }

  /**
   * Calculates the average between degree angles.
   *
   * @param angdegs collection of degree angles in range (0, 360) or (-180, 180).
   * @return the average.
   */
  public static double degreeAnglesAverage(Collection<Double> angdegs) {
    List<Double> radianAngles = angdegs.stream().map(Math::toRadians).collect(Collectors.toList());
    double radianAverage = radianAnglesAverage(radianAngles);
    return Math.toDegrees(radianAverage);
  }

  /**
   * Computes Pearson's circular product-moment correlation coefficients for pairs of collections.
   *
   * @param x first data collection.
   * @param inputRangeX input range of the first collection.
   * @param y second data collection.
   * @param inputRangeY input range of the second collection.
   * @return Pearson's circular correlation coefficient for the two collections.
   */
  public static double computePearsonCircularCorrelation(
      Collection<Double> x,
      Pair<Double, Double> inputRangeX,
      Collection<Double> y,
      Pair<Double, Double> inputRangeY) {

    Preconditions.checkArgument(x.size() == y.size());

    List<Double> xConverted =
        x.stream()
            .map(CONVERSION_TABLE.get(inputRangeX).get(Pair.of(0.0, 2 * Math.PI)))
            .collect(Collectors.toList());
    List<Double> yConverted =
        y.stream()
            .map(CONVERSION_TABLE.get(inputRangeY).get(Pair.of(0.0, 2 * Math.PI)))
            .collect(Collectors.toList());

    double xMean = radianAnglesAverage(xConverted);
    double yMean = radianAnglesAverage(yConverted);

    List<Double> xSin =
        xConverted.stream()
            .map(rad -> angleDistanceRadians(xMean, rad))
            .map(Math::sin)
            .collect(Collectors.toList());
    List<Double> ySin =
        yConverted.stream()
            .map(rad -> angleDistanceRadians(yMean, rad))
            .map(Math::sin)
            .collect(Collectors.toList());

    double multiplicationSum = 0.0;
    for (int i = 0; i < xSin.size(); i++) {
      multiplicationSum += xSin.get(i) * ySin.get(i);
    }

    return multiplicationSum
        / Math.sqrt(
            xSin.stream().mapToDouble(r -> r * r).sum()
                * ySin.stream().mapToDouble(r -> r * r).sum());
  }
}
