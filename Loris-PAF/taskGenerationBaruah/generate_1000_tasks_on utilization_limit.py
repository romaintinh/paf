import sys
import parameter_generating_function
import fileUtils
import utils
import os

for i in (0,1000):
    s=python /cal/homes/lmillet/Projet-PAF/taskGenerationBaruah/generate_task.py 10 100 0.05 0.9 0.5 4 0.6 3 ./ 4
    join(/home/lmillet/Projet-PAF/taskGenerationBaruah/Tasks_on_variation_utilization,s)
    
    
