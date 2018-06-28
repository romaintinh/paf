#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Mon Jun 25 10:48:06 2018

@author: rtinh
"""

import matplotlib.pyplot as plt
import math as m

x = [i/10 for i in range(3653)]
sbft =[]
dbft =[]

Ts = []
Cs = []
Tlo = []
Clo = []

with open("testsbfdbf",mode='r') as f:
    for line in f.readlines():
        content = line.split("\t")
        if int(content[5])==2:
            Ts.append(int(content[1]))
            Cs.append(float(content[3]))
        else:
            Tlo.append(int(content[1]))
            Clo.append(float(content[3]))
print(Ts)
print(Cs)
print(Tlo)
print(Clo)



Tlo = [10]
Clo = [2.4]

def sbf(x,i):
    temp = m.floor((x-(Ts[i]-Cs[i]))/Ts[i])
    temp2 = x-2*(Ts[i]-Cs[i]) -Ts[i]*temp
    epsilon = max(temp2,0)
    return max(Cs[i]*temp + epsilon,0)

def dbf(x,i):
    return m.floor(x/Tlo[i])*Clo[i]

for elt in x:
    tempHi =0
    tempLo =0
    for i in range(len(Ts)):
        tempHi+=sbf(elt,i)
    sbft.append(tempHi)
    for i in range(len(Clo)):
        tempLo+=dbf(elt,i)
    dbft.append(tempLo)


plt.plot(x,sbft)
plt.plot(x,dbft)
plt.show()
