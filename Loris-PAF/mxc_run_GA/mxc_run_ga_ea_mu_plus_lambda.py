import copy
import random
from deap import creator
from deap import tools
from deap import base
from deap import algorithms
import fileUtils
import sys
import packingUtils
import mxc_run_ga_utils
import schedulabilityFunctions
#from scoop import futures
import datetime 


task_file_path = str(sys.argv[1])
if len(sys.argv) > 2:
	file_path_result = str(sys.argv[2])
else:
	file_path_result = task_file_path+"_result_ea_mu_plus_lambda.txt"
	
[criticality_level,task_parameters] = fileUtils.ExtractTaskParameter(task_file_path)


nb_tasks = len(task_parameters)

periods_table = []
for i in range(0,nb_tasks):
	periods_table.extend(task_parameters[i].period)
hyperperiod = schedulabilityFunctions.getTheLeastCommonMultipleV2(periods_table)

[lo_task_list,hi_task_list] = packingUtils.SeparateHiFromLoTasks(task_parameters)

nb_hi_tasks = len(hi_task_list)
nb_lo_tasks = len(lo_task_list)

if nb_hi_tasks == 0 or nb_lo_tasks == 0:
	task_file_path_modal_server = task_file_path+"_modal_server_allocation_ea_mu_plus_lambda.txt"
	final_packing = packingUtils.findOnePacking(task_parameters,2)	
	fileUtils.WriteTaskSetAndModalServers(task_file_path_modal_server,task_parameters,lo_task_list,hi_task_list,final_packing,[],[])
	sys.exit("No task Hi or Lo")
	
	
schedulabilityFunctions.ComputeSbfFunctionForListOfTasks(hi_task_list,0,hyperperiod)
schedulabilityFunctions.ComputeDbfFunctionForListOfTasks(lo_task_list,0,hyperperiod,0)

task_schedulable_for_each_modal_server = []
nb_possible_allocation = 0
for i in range(0,nb_hi_tasks):
	task_schedulable_for_each_modal_server.append(schedulabilityFunctions.Compute_task_list_for_modal_server(hi_task_list[i],lo_task_list))
	nb_possible_allocation += len(hi_task_list[i].schedulable_with_utilization) + len(hi_task_list[i].schedulable_with_sbf)
	hi_task_list[i].Show_schedulable()

lo_tasks_name = [lo_task_list[i].name for i in range(0,nb_lo_tasks)]


nb_individus = nb_tasks
nb_generation = 100

toolbox = base.Toolbox()
#toolbox.register("map", futures.map)

creator.create("FitnessMax", base.Fitness, weights=(1.0,))
creator.create("Individual", list, generation_of_creation = -1,fitness=creator.FitnessMax)

# Attribute generator
toolbox.register("attr_bool", random.randint, 0, 1)
toolbox.register("modal_server",tools.initRepeat,list,toolbox.attr_bool,nb_lo_tasks)	
toolbox.register("individual", mxc_run_ga_utils.init_Individual,creator.Individual, hi_task_list, nb_hi_tasks, lo_tasks_name, nb_lo_tasks)
toolbox.register("population", mxc_run_ga_utils.init_Population,list,toolbox.individual,nb_individus)
toolbox.register("mutate", tools.mutFlipBit, indpb=0.1)
toolbox.register("select", tools.selBest, k=nb_individus)	
toolbox.register("mate", tools.cxTwoPoint)  
toolbox.register("evaluate", mxc_run_ga_utils.evalAlloc,hi_task_list=hi_task_list,nb_hi_tasks=nb_hi_tasks,lo_task_list=lo_task_list,nb_lo_tasks=nb_lo_tasks,task_parameters=task_parameters,hyperperiod=hyperperiod)	
   
def main():
	start_pop = datetime.datetime.now()
	pop = toolbox.population()
	end_pop = datetime.datetime.now()
	time_pop_init_gen = end_pop.microsecond - start_pop.microsecond
	hof = tools.HallOfFame(1)
	# Evaluate the entire population
	fitnesses = list(map(toolbox.evaluate, pop))
	for ind, fit in zip(pop, fitnesses):
		ind.fitness.values = fit
		
	CXPB,MUTPB,NGEN,nb_offspring,mu = 0.4,0.4,nb_generation,nb_individus,nb_individus

	print("  Evaluated %i individuals" % len(pop))
	hof.update(pop)
	
	average_time_for_each_generation = 0
		
	# Begin the evolution
	for g in range(NGEN):
		print("-- Generation %i --" % g)
		start_gen = datetime.datetime.now()
		# Vary the population
		offspring = []
		for x_offspring in xrange(nb_offspring):
			op_choice = random.random()
			if op_choice < CXPB:			# Apply crossover
				ind1, ind2 = map(toolbox.clone, random.sample(pop, 2))
				ind1, ind2 = toolbox.mate(ind1, ind2)
				del ind1.fitness.values
				offspring.append(ind1)
				offspring.append(ind2)
				x_offspring += 1
			elif op_choice < CXPB + MUTPB:  # Apply mutation
				ind = toolbox.clone(random.choice(pop))
				for modal_server in ind:
					toolbox.mutate(modal_server)
				del ind.fitness.values
				offspring.append(ind)
			else:						   # Apply reproduction
				offspring.append(random.choice(pop))
	
		# Evaluate the individuals with an invalid fitness
		invalid_ind = [ind for ind in offspring if not ind.fitness.valid]
		fitnesses = map(toolbox.evaluate, invalid_ind)
		for ind, fit in zip(invalid_ind, fitnesses):
			ind.generation_of_creation = copy.deepcopy(g)
			ind.fitness.values = fit
			#mxc_run_ga_utils.Print_individual(ind)
			
			
		print("  Evaluated %i individuals" % len(invalid_ind))
		
		# The population is entirely replaced by the offspring
		pop[:] = toolbox.select(pop + offspring)
		hof.update(pop)
		
		# Gather all the fitnesses in one list and print the stats
		fits = [ind.fitness.values[0] for ind in pop]
		end_gen = datetime.datetime.now()
		
		average_time_for_each_generation = end_gen.microsecond - start_gen.microsecond
		
		
		length = len(pop)
		mean = sum(fits) / length
		sum2 = sum(x*x for x in fits)
		std = abs(sum2 / length - mean**2)**0.5
		
		print("  Min %s" % min(fits))
		print("  Max %s" % max(fits))
		print("  Avg %s" % mean)
		print("  Std %s" % std)
	
	print("-- End of (successful) evolution --")
	
	best_ind = hof[0]
	mxc_run_ga_utils.Print_individual(best_ind)
	print("Best individual is %s, %s, from generation %s" % (best_ind, best_ind.fitness.values,best_ind.generation_of_creation))
	print 
	if mxc_run_ga_utils.Check_best_ind_fitness(best_ind.fitness.values) == True:
		average_time_for_each_generation = float(average_time_for_each_generation)/float(NGEN)
		init_pop_time = end_pop.microsecond - start_pop.microsecond
		mxc_run_ga_utils.Process_results(best_ind,hi_task_list,lo_task_list,task_parameters,task_file_path,file_path_result)
		file_task_path_directory_hierarchy = fileUtils.Retrieve_directory_hierarchy(task_file_path)
		best_individual_generation_file_statistics = file_task_path_directory_hierarchy[:-1]+"_best_individual_statistics_ea_mu_plus_lambda.csv"
		fileUtils.Write_best_ind_generation(task_file_path,best_ind, best_individual_generation_file_statistics,task_parameters,average_time_for_each_generation,init_pop_time)
	else:
		print "No correct Individuals"
		fileUtils.Write_file_no_results(task_file_path)

if __name__ == "__main__":
	main()

