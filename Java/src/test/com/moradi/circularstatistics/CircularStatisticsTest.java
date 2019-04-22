package com.moradi.circularstatistics;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class CircularStatisticsTest {

  @Test
  public void testDistanceAnglesInside() {
    assertEquals(80, CircularStatistics.angleDistanceDegree(10, 90), 0.0);
  }

  @Test
  public void testDistanceAnglesOutside() {
    assertEquals(20, CircularStatistics.angleDistanceDegree(350, 10), 0.0);
  }

  @Test
  public void testDistanceAnglesOutside2() {
    assertEquals(-20, CircularStatistics.angleDistanceDegree(10, 350), 0.0);
  }

  @Test
  public void testDistanceRadiansInside() {
    assertEquals(
        Math.toRadians(80),
        CircularStatistics.angleDistanceRadians(Math.toRadians(10), Math.toRadians(90)),
        0.00001);
  }

  @Test
  public void testDistanceRadiansOutside() {
    assertEquals(
        Math.toRadians(20),
        CircularStatistics.angleDistanceRadians(Math.toRadians(350), Math.toRadians(10)),
        0.00001);
  }

  @Test
  public void testDistanceRadiansOutside2() {
    assertEquals(
        Math.toRadians(-20),
        CircularStatistics.angleDistanceRadians(Math.toRadians(10), Math.toRadians(350)),
        0.00001);
  }

  @Test
  public void conversions() {
    assertEquals(
        270,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(0.0, 360.0))
            .get(Pair.of(0.0, 360.0))
            .apply(270.0),
        0.1);
    assertEquals(
        -90,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(0.0, 360.0))
            .get(Pair.of(-180.0, 180.0))
            .apply(270.0),
        0.1);
    assertEquals(
        1.5 * Math.PI,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(0.0, 360.0))
            .get(Pair.of(0.0, 2 * Math.PI))
            .apply(270.0),
        0.1);
    assertEquals(
        -0.5 * Math.PI,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(0.0, 360.0))
            .get(Pair.of(-Math.PI, Math.PI))
            .apply(270.0),
        0.1);

    assertEquals(
        270,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(-180.0, 180.0))
            .get(Pair.of(0.0, 360.0))
            .apply(-90.0),
        0.1);
    assertEquals(
        -90,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(-180.0, 180.0))
            .get(Pair.of(-180.0, 180.0))
            .apply(-90.0),
        0.1);
    assertEquals(
        1.5 * Math.PI,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(-180.0, 180.0))
            .get(Pair.of(0.0, 2 * Math.PI))
            .apply(-90.0),
        0.1);
    assertEquals(
        -0.5 * Math.PI,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(-180.0, 180.0))
            .get(Pair.of(-Math.PI, Math.PI))
            .apply(-90.0),
        0.1);

    assertEquals(
        270,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(0.0, 2 * Math.PI))
            .get(Pair.of(0.0, 360.0))
            .apply(1.5 * Math.PI),
        0.1);
    assertEquals(
        -90,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(0.0, 2 * Math.PI))
            .get(Pair.of(-180.0, 180.0))
            .apply(1.5 * Math.PI),
        0.1);
    assertEquals(
        1.5 * Math.PI,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(0.0, 2 * Math.PI))
            .get(Pair.of(0.0, 2 * Math.PI))
            .apply(1.5 * Math.PI),
        0.1);
    assertEquals(
        -0.5 * Math.PI,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(0.0, 2 * Math.PI))
            .get(Pair.of(-Math.PI, Math.PI))
            .apply(1.5 * Math.PI),
        0.1);

    assertEquals(
        270,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(-Math.PI, Math.PI))
            .get(Pair.of(0.0, 360.0))
            .apply(-0.5 * Math.PI),
        0.1);
    assertEquals(
        -90,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(-Math.PI, Math.PI))
            .get(Pair.of(-180.0, 180.0))
            .apply(-0.5 * Math.PI),
        0.1);
    assertEquals(
        1.5 * Math.PI,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(-Math.PI, Math.PI))
            .get(Pair.of(0.0, 2 * Math.PI))
            .apply(-0.5 * Math.PI),
        0.1);
    assertEquals(
        -0.5 * Math.PI,
        CircularStatistics.CONVERSION_TABLE
            .get(Pair.of(-Math.PI, Math.PI))
            .get(Pair.of(-Math.PI, Math.PI))
            .apply(-0.5 * Math.PI),
        0.1);
  }

  @Test
  public void computePearsonCircularCorrelation() throws Exception {
    BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(
                this.getClass().getClassLoader().getResourceAsStream("azi1_calc.csv")));
    CSVParser csvFileParser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());

    List<Double> x =
        csvFileParser.getRecords().stream()
            .map(rec -> Double.parseDouble(rec.get("heading_calc")))
            .collect(Collectors.toList());

    csvFileParser.close();

    reader =
        new BufferedReader(
            new InputStreamReader(
                this.getClass().getClassLoader().getResourceAsStream("azi1_calc.csv")));

    csvFileParser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());

    List<Double> y =
        csvFileParser.getRecords().stream()
            .map(rec -> Double.parseDouble(rec.get("heading (0, 360)")))
            .collect(Collectors.toList());

    csvFileParser.close();

    assertEquals(
        0.8402,
        CircularStatistics.computePearsonCircularCorrelation(
            x, Pair.of(-180.0, 180.0), y, Pair.of(0.0, 360.0)),
        0.0001);
  }
}
