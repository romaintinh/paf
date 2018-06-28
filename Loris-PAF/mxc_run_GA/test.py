import copy
import random
from deap import creator
from deap import tools
from deap import base
import fileUtils
import sys
import packingUtils
import mxc_run_ga_utils
import schedulabilityFunctions
from scoop import futures
import datetime 
import time
import os

task_file_path = str(sys.argv[1])
if len(sys.argv) > 2:
	file_path_result = str(sys.argv[2])
else:
	file_path_result = task_file_path+"_result.txt"
	
[criticality_level,task_parameters] = fileUtils.ExtractTaskParameter(task_file_path)


packingUtils.showTaskList(task_parameters)

print "rounding"
print 
for i in range(0,len(task_parameters)):
	print task_parameters[i].utilization[0]
	print mxc_run_ga_utils.Round_proper(task_parameters[i].utilization[0],3)
	print "*******"
	print mxc_run_ga_utils.Round_proper(99.9999,3)
	print
	
print 

nb_tasks = len(task_parameters)

periods_table = []
for i in range(0,nb_tasks):
	periods_table.extend(task_parameters[i].period)
hyperperiod = schedulabilityFunctions.getTheLeastCommonMultipleV2(periods_table)

[lo_task_list,hi_task_list] = packingUtils.SeparateHiFromLoTasks(task_parameters)

nb_hi_tasks = len(hi_task_list)
nb_lo_tasks = len(lo_task_list)
schedulabilityFunctions.ComputeSbfFunctionForListOfTasks(hi_task_list,0,hyperperiod)
schedulabilityFunctions.ComputeDbfFunctionForListOfTasks(lo_task_list,0,hyperperiod,0)

sbf = copy.deepcopy(hi_task_list[3].sbf)
dbf = copy.deepcopy(task_parameters[8].dbf)
ps = hi_task_list[3].utilization[1]

for i in [4]:
        for j in range(0,len(sbf)):
                sbf[j] += hi_task_list[i].sbf[j]
	ps += hi_task_list[i].utilization[1]
for i in [14,16]:
        for j in range(0,len(dbf)):
                dbf[j] += task_parameters[i].dbf[j]
for i in range(0,len(sbf)):
        if sbf[i] < dbf[i]:
                print "allocation incorrecte"
	if ps > 1:
		print "Primal server impossible"

sys.exit(0)


task_schedulable_for_each_modal_server = []
nb_possible_allocation = 0
for i in range(0,nb_hi_tasks):
	task_schedulable_for_each_modal_server.append(schedulabilityFunctions.Compute_task_list_for_modal_server(hi_task_list[i],lo_task_list))
	nb_possible_allocation += len(hi_task_list[i].schedulable_with_utilization) + len(hi_task_list[i].schedulable_with_sbf)
	hi_task_list[i].Show_schedulable()

lo_tasks_name = [lo_task_list[i].name for i in range(0,nb_lo_tasks)]


nb_individus = nb_tasks*5
nb_generation = 10

toolbox = base.Toolbox()
toolbox.register("map", futures.map)

creator.create("FitnessMax", base.Fitness, weights=(1.0,))
creator.create("Individual", list, fitness=creator.FitnessMax)

# Attribute generator
toolbox.register("attr_bool", random.randint, 0, 1)
#toolbox.register("modal_server",tools.initRepeat,list,toolbox.attr_bool,nb_lo_tasks)	
toolbox.register("individual", mxc_run_ga_utils.init_Individual_with_sbf_first,creator.Individual, hi_task_list, nb_hi_tasks, lo_tasks_name, nb_lo_tasks)
toolbox.register("population", mxc_run_ga_utils.init_Population,list,toolbox.individual,nb_individus)
toolbox.register("mutate", tools.mutFlipBit, indpb=0.1)
toolbox.register("select", tools.selTournament, tournsize=3)	
toolbox.register("mate", tools.cxTwoPoint)  
toolbox.register("evaluate", mxc_run_ga_utils.evalAlloc,hi_task_list=hi_task_list,nb_hi_tasks=nb_hi_tasks,lo_task_list=lo_task_list,nb_lo_tasks=nb_lo_tasks,task_parameters=task_parameters,hyperperiod=hyperperiod)    
   
def main():
    pop = toolbox.population()
    hof = tools.HallOfFame(1)
    # Evaluate the entire population
    fitnesses = list(map(toolbox.evaluate, pop))
    for ind, fit in zip(pop, fitnesses):
        ind.fitness.values = fit
        
    CXPB,MUTPB,NGEN = 0.4,0.4,nb_generation
    os.system("date")
    start = datetime.datetime.now()

    print("  Evaluated %i individuals" % len(pop))
    hof.update(pop)
    for i in range(0,len(pop)):
    	print pop[i]
    	time.sleep(1)
    end = datetime.datetime.now()
    os.system("date")
    difference =  end - datetime.timedelta(microseconds=start.microsecond)
    print difference.microsecond
    print difference.second
    print difference.minute
    print("%s" % (end.microsecond-start.microsecond))
	    
if __name__ == "__main__":
    main()
    
    
  # svn commit -m" fihcier resultat ac noms different pour chaque algo et arret de l'algo si aucune tache lo ou hi"
