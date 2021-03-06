# CircularStatistics
Some few methods in Java and Python I picked along the way in my research on the subject.

[full code documentation](https://idanmoradarthas.github.io/CircularStatistics/)

# Introduction
"... 

In the many diverse scientific fields, the measurements are directions. For instance, a biologist may be measuring the direction of flight of a bird or the orientation of an animal, while a geologist may be interested in the direction of the earth's magnetic pole. Such directions may be in two dimensions as in the first two examples or in three dimensions like the last one. A set of such observations on directions is referred to as directional data.

...

Directional data have many unique and novel features both in terms of modeling and in their statistical treatment.

...

Such distinctive features make directional analysis substantially different from the standard "linear" statistical analysis of univariate or multivariate data that one finds in most statistics books. The need for "invariance" of statistical methods and measures with respect to the choice of this arbitrary zero-direction and sense of rotation, makes many of the usual linear techniques and measures often misleading, if not entirely meaningless. Commonly used summary measures on the real line, such as the sample mean and variance turn out to be inappropriate as do all the moments and cumulants. [...] Analytical tools such as the moment generating function and other generating functions are equally useless. Many notions such as correlation and regression as well as their statistical measures, need to be re-invented for directional data.

..."

Jammalamadaka, S. Rao, and Ambar Sengupta. Topics in circular statistics. Vol. 5. world scientific, 2001. p. 1-3.

Most of the theory and inspiration for this code was motivated by the quoted book above.

# Distance Between Angles
Inorder to calculate distance between angles I used the code presented in this work:

Lior Kogan, “Circular Values Math and Statistics with C++11”. CodeProject, CodeProject, 27 Apr 2013, https://www.codeproject.com/Articles/190833/Circular-Values-Math-and-Statistics-with-Cplusplus 

```C++
// the length of the directed walk from c1 to c2, with the lowest absolute-value length
// return value is in [-R/2, R/2)
static double Sdist(const CircVal& c1, const CircVal& c2)
{
    double d= c2.val-c1.val;
    if (d <  -Type::R_2) return d + Type::R;
    if (d >=  Type::R_2) return d - Type::R;
                         return d          ;
}
```

# Angles Mean
Since the arithmetic mean is not always appropriate for angles, I took the formula of mean circular quantities found in [Wikipedia](https://en.wikipedia.org/wiki/Mean_of_circular_quantities):

<img src="https://latex.codecogs.com/gif.latex?%5Cbar%7B%5Calpha%7D%3D%5Carctan2%5CBigg%28%5Ccfrac%7B1%7D%7BN%7D%5CSigma%5EN_%7Bj%3D1%7D%5Csin%28%5Calpha_j%29%2C%5Ccfrac%7B1%7D%7BN%7D%5CSigma%5EN_%7Bj%3D1%7D%5Ccos%28%5Calpha_j%29%5CBigg%29">

# Pearson's Correlation
"...
In many situations, one comes across bivariate or multivariate data where some or all of the component variables are angular. For example, in studying bird migration, one may be interested in observing the direction from which the birds come as well as the direction of their return, resulting in observations on a torus i.e., (circle x circle); or one may record both the wind direction and flight direction of a migratory bird; or wind directions and drift direction of clouds, orientations of pebbles lying on foresets and foreset azimuths; or in medical studies of vector cardiograms, several variables are measured in angles. In such cases, one might be interested in questions of correlation or association between such variables as well as regression with the goal of predicting one variable given the other. Sometimes, some of the component variables may be linear so that we axe interested in correlation and regression issues in such circular-linear cases as well.
..."

Jammalamadaka, S. Rao, and Ambar Sengupta. Topics in circular statistics. Vol. 5. world scientific, 2001. p. 175

the following formula was taken from pages 176-178 of the quoted book above.

<img src="https://latex.codecogs.com/gif.latex?PearsonCircular_%7B%5Calpha%2C%5Cbeta%7D%3D%5Ccfrac%7B%5CSigma%5EN_%7Bi%3D1%7D%5Csin%28%5Calpha_i-%5Cbar%7B%5Calpha%7D%29%5Csin%28%5Cbeta_i-%5Cbar%7B%5Cbeta%7D%29%7D%7B%5Csqrt%7B%5CSigma%5EN_%7Bi%3D1%7D%5Csin%5E2%28%5Calpha_i-%5Cbar%7B%5Calpha%7D%29%5CSigma%5EN_%7Bi%3D1%7D%5Csin%5E2%28%5Cbeta_i-%5Cbar%7B%5Cbeta%7D%29%7D%7D">
