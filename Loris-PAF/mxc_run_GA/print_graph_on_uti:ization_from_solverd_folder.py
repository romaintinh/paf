import numpy as np
import os
import fileUtils
import matplotlib.pyplot as plt
import sys
task_direction=str(sys.argv[1])
u=np.array([])
ubound=np.array([])

for roots,dirs,files in os.walk(task_direction):
    for names in files:
        if (str(names)[0:12]=='Utilization_') and str(names)[15:]=='result.csv':
            print str(names)
            result_path=task_direction+str(names)
            print result_path
            ubound=np.append(ubound,float(str(names)[12:15]))
            moy=0
            compteur=0
            with open(result_path, mode='r') as f:
                for line in f.readlines()[1:]:
                    content=line.split(',')
                    if content!=[]:
                        compteur+=1
                        moy+=float(content[3])-float(content[5])
                moy=moy/compteur
                u=np.append(u,moy)
                      
print u
print ubound
