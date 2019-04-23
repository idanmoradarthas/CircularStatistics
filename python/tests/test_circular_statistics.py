from pathlib import Path

import pandas

from moradi.circular_statistics import *


def test_distance_degrees_inside():
    assert angle_distance_degree(10, 90) == 80


def test_distance_degrees_outside():
    assert angle_distance_degree(350, 10) == 20


def test_distance_degrees_outside_2():
    assert angle_distance_degree(10, 350) == -20


def test_distance_radians_inside():
    assert angle_distance_radians(math.radians(10), math.radians(90)) == math.radians(80)


def test_distance_radians_outside():
    assert math.isclose(angle_distance_radians(math.radians(350), math.radians(10)), math.radians(20),
                        rel_tol=0.0000001)


def test_distance_radians_outside_2():
    assert math.isclose(angle_distance_radians(math.radians(10), math.radians(350)), math.radians(-20),
                        rel_tol=0.0000001)


def test_conversions():
    assert CONVERSION_TABLE[(0, 360)][(0, 360)](270) == 270
    assert CONVERSION_TABLE[(0, 360)][(-180, 180)](270) == -90
    assert CONVERSION_TABLE[(0, 360)][(0, 2 * math.pi)](270) == 1.5 * math.pi
    assert CONVERSION_TABLE[(0, 360)][(-math.pi, math.pi)](270) == -0.5 * math.pi

    assert CONVERSION_TABLE[(-180, 180)][(0, 360)](-90) == 270
    assert CONVERSION_TABLE[(-180, 180)][(-180, 180)](-90) == -90
    assert CONVERSION_TABLE[(-180, 180)][(0, 2 * math.pi)](-90) == 1.5 * math.pi
    assert CONVERSION_TABLE[(-180, 180)][(-math.pi, math.pi)](-90) == -0.5 * math.pi

    assert CONVERSION_TABLE[(0, 2 * math.pi)][(0, 360)](1.5 * math.pi) == 270
    assert CONVERSION_TABLE[(0, 2 * math.pi)][(-180, 180)](1.5 * math.pi) == -90
    assert CONVERSION_TABLE[(0, 2 * math.pi)][(0, 2 * math.pi)](1.5 * math.pi) == 1.5 * math.pi
    assert CONVERSION_TABLE[(0, 2 * math.pi)][(-math.pi, math.pi)](1.5 * math.pi) == -0.5 * math.pi

    assert CONVERSION_TABLE[(-math.pi, math.pi)][(0, 360)](-0.5 * math.pi) == 270
    assert CONVERSION_TABLE[(-math.pi, math.pi)][(-180, 180)](-0.5 * math.pi) == -90
    assert CONVERSION_TABLE[(-math.pi, math.pi)][(0, 2 * math.pi)](-0.5 * math.pi) == 1.5 * math.pi
    assert CONVERSION_TABLE[(-math.pi, math.pi)][(-math.pi, math.pi)](-0.5 * math.pi) == -0.5 * math.pi


def test_radian_avg():
    radians = numpy.array([0, 0.25 * math.pi, 0.5 * math.pi, 0.75 * math.pi, math.pi])
    assert radians_angles_average(radians) == 0.5 * math.pi


def test_degree_avg():
    degrees = numpy.array([0, 45, 90, 135, 180])
    assert degrees_angles_average(degrees) == 90


def test_pearson_correlation():
    bearing_frame = pandas.read_csv(Path(__file__).parent.joinpath("resources").joinpath("azi1_calc.csv"),
                                    encoding="latin1")
    assert math.isclose(compute_pearson_circular_correlation(bearing_frame["heading_calc"], (-180, 180),
                                                             bearing_frame["heading (0, 360)"], (0, 360)), 0.8402,
                        rel_tol=0.0001)
