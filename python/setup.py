from distutils.core import setup

from setuptools import find_packages

with open(".version", "r") as file:
    version = file.readline().split("==")[1]

with open("requirements.txt", "r") as file:
    requirements = file.readlines()

setup(name="circular_statistics",
      version=version,
      description="Python Circular Statistics Package",
      author="Idan Morad",
      license="MIT",
      packages=find_packages(exclude=['contrib', 'docs', 'tests']),
      install_requires=requirements,
      classifiers=[
          'Development Status :: 4 - Beta',
          'License :: OSI Approved :: MIT License',
          'Programming Language :: Python :: 3.7',
          'Topic :: Scientific/Engineering :: Mathematics'
      ])
