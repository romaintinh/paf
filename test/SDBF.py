#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Mon Jun 25 10:48:06 2018

@author: rtinh
"""

import matplotlib.pyplot as plt
import math as m

x = [i/100 for i in range(2000)]
sbft =[]
dbft =[]

Ts = 5
Cs = 2
Clo = 2.4
Tlo = 12

def sbf(x):
    temp = m.floor((x-(Ts-Cs))/Ts)
    temp2 = x-2*(Ts-Cs) -Ts*temp
    epsilon = max(temp2,0)
    return max(Cs*temp + epsilon,0)

def dbf(x):
    return m.floor(x/Tlo)*Clo

for elt in x:
    sbft.append(sbf(elt))
    dbft.append(dbf(elt))


plt.plot(x,sbft)
plt.plot(x,dbft)
plt.show()
