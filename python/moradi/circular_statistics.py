# MIT License
#
# Copyright (c) 2019 Idan Morad
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

"""Circular Statitics  methods for performing statistics operations such as the conversation between ranges, distance
between angles, average and pearson correlation."""
from typing import Union, Tuple

import math
import numpy

_radian_vectorized = numpy.vectorize(math.radians)


def _s_dist(circ_val1: float, circ_val2: float, r: float) -> float:
    """
    Returns the length of the directed walk from circ_val1 to circ_val2, with the lowest
    value length as distance. based on
    https://www.codeproject.com/Articles/190833/Circular-Values-Math-and-Statistics-with-Cplusplus

    :param circ_val1: the starting circular value.
    :param circ_val2: the ending circular value.
    :param r: max angle.
    :return: the directed walk.
    """
    distance = circ_val2 - circ_val1
    if distance < -r / 2:
        return distance + r
    if distance >= r / 2:
        return distance - r
    return distance


def angle_distance_degree(deg1: float, deg2: float) -> float:
    """
    Return the distance between angles.

    :param deg1: the starting angle, in degrees.
    :param deg2: the ending angle, in degrees.
    :return: the distance in range [-180, 180]
    """
    return _s_dist(deg1, deg2, 360)


def angle_distance_radians(rad1: float, rad2: float) -> float:
    """
    Return the distance between angles.

    :param rad1: the starting angle, in radians.
    :param rad2: the ending angle, in radians.
    :return: the distance in range [:math:`-\pi` , :math:`\pi`]
    """
    return _s_dist(rad1, rad2, 2 * math.pi)


def _covert_360_to_180(degrees: float) -> float:
    """
    converts from range (0, 360) to (-180, 180)

    :param degrees: the angle to convert from in degrees
    :return: the converted value in range (-180, 180)
    """
    if degrees > 180:
        return degrees - 360
    return degrees


def _covert_180_to_360(degrees: float) -> float:
    """
    converts from range (-180, 180) to (0, 360)

    :param degrees: the angle to convert from in degrees
    :return: the converted value in range (0, 360)
    """
    if degrees < 0:
        return degrees + 360
    return degrees


def _covert_2pi_to_pi(radians: float) -> float:
    """
    converts from range (0, :math:`2\pi`) to (:math:`-\pi`, :math:`\pi`)

    :param radians: the angle to convert from in radians
    :return: the converted value in range (:math:`-\pi`, :math:`\pi`)
    """
    if radians > math.pi:
        return radians - 2 * math.pi
    return radians


def _covert_pi_to_2pi(radians: float) -> float:
    """
    converts from range (:math:`-\pi`, :math:`\pi`) to (0, :math:`2\pi`)

    :param radians: the angle to convert from in radians
    :return: the converted value in range (0, :math:`2\pi`)
    """
    if radians < 0:
        return radians + 2 * math.pi
    return radians


CONVERSION_TABLE = {
    (0, 360):
        {(0, 360): lambda x: x, (-180, 180): _covert_360_to_180, (0, 2 * math.pi): math.radians,
         (-math.pi, math.pi): lambda x: _covert_2pi_to_pi(math.radians(x))},
    (-180, 180):
        {(0, 360): _covert_180_to_360, (-180, 180): lambda x: x,
         (0, 2 * math.pi): lambda x: math.radians(_covert_180_to_360(x)),
         (-math.pi, math.pi): math.radians},
    (0, 2 * math.pi):
        {(0, 360): math.degrees, (-180, 180): lambda x: _covert_360_to_180(math.degrees(x)),
         (0, 2 * math.pi): lambda x: x, (-math.pi, math.pi): _covert_2pi_to_pi},
    (-math.pi, math.pi):
        {(0, 360): lambda x: math.degrees(_covert_pi_to_2pi(x)), (-180, 180): math.degrees,
         (0, 2 * math.pi): _covert_pi_to_2pi, (-math.pi, math.pi): lambda x: x}}
"""
Table implements as dictionary of dictionaries that receives two tuples of representations of angles, and converts the 
given value from the range of the first tuple to second one.

For example: CONVERSION_TABLE[(0,360)][(-180,180)](270) will output -90.

available ranges are:
    * (0, 360)
    * (-180, 180)
    * (0, :math:`2\pi`)
    * (:math:`-\pi`, :math:`\pi`)
"""


def radians_angles_average(x: numpy.ndarray) -> float:
    """
    Calculates the average between radian angles.

    :param x: numpy array of degree angles in range (0, :math:`2\pi`) or (:math:`-\pi`, :math:`\pi`).
    :return: the average.
    """
    n = x.size
    return numpy.arctan2((1 / n) * numpy.sum(numpy.sin(x)), (1 / n) * numpy.sum(numpy.cos(x)))


def degrees_angles_average(x: numpy.ndarray) -> float:
    """
    Calculates the average between degree angles.

    :param x: numpy array of degree angles in range (0, 360) or (-180, 180).
    :return: the average.
    """
    x_rad = _radian_vectorized(x)
    avg_rad = radians_angles_average(x_rad)
    return math.degrees(avg_rad)


def compute_pearson_circular_correlation(x: numpy.ndarray, input_range_x: Union[Tuple[int, int], Tuple[float, float]],
                                         y: numpy.ndarray,
                                         input_range_y: Union[Tuple[int, int], Tuple[float, float]]) -> float:
    """
    Computes Pearson's circular product-moment correlation coefficients for pairs of collections.

    :param x: first array of angles
    :param input_range_x: input range of x
    :param y: second array of angles
    :param input_range_y: input range of y
    :return: Pearson's circular correlation coefficient for the two collections.
    """
    x_converted = numpy.vectorize(CONVERSION_TABLE[input_range_x][(0, 2 * math.pi)])(x)
    y_converted = numpy.vectorize(CONVERSION_TABLE[input_range_y][(0, 2 * math.pi)])(y)
    x_converted_mean = radians_angles_average(x_converted)
    y_converted_mean = radians_angles_average(y_converted)

    x_converted = numpy.sin(numpy.vectorize(lambda rad: angle_distance_radians(x_converted_mean, rad))(x_converted))
    y_converted = numpy.sin(numpy.vectorize(lambda rad: angle_distance_radians(y_converted_mean, rad))(y_converted))
    return numpy.sum(numpy.multiply(x_converted, y_converted)) / math.sqrt(
        numpy.sum(numpy.power(x_converted, 2)).astype(numpy.float64) * numpy.sum(numpy.power(y_converted, 2)).astype(
            numpy.float64))
