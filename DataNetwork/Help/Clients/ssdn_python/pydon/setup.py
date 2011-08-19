# -*- coding: utf-8 -*-
from setuptools import setup, find_packages

setup(name='Pydon',
      version='0.6',
      description='Python packages for the Sense/Stage DataNetwork',
      long_description="""The Sense/Stage DataNetwork can be used to communicate between programs,
      such as SuperCollider, Max/MSP, PureData, Processing, C++ and Python, as well as to communicate
      to the wireless Sense/Stage MiniBees. This package provides the python client for the datanetwork,
      as well as the bridge between the datanetwork and the MiniBee network, as well as an osc sending
      application as a simple bridge to the MiniBee network""",
      author='Marije Baalman',
      author_email='sensestage@nescivi.nl',
      url='http://www.sensestage.eu',
      license='GNU Lesser General Public License',
      keywords=['XBee', 'OpenSoundControl', 'OSC', 'SenseStage', 'DataNetwork', 'MiniBee'],
      packages=find_packages(),
      install_requires=[
         'pyOSC>=0.3',
         'pyserial>=2.5'
    ]
)