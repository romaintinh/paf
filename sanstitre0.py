#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Jun 19 16:10:17 2018

@author: rtinh
"""

with open("prime.txt",mode="r") as f:
    lines = f.readlines()
    for line in lines:
        line = line.strip()
        