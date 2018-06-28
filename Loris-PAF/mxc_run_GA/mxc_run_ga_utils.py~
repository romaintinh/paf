import random
import copy
import packingUtils
import schedulabilityFunctions
import fileUtils
import global_variables

def Print_individual(individual):
	size = len(individual)
	for i in range(0,size):
		print individual[i]
		
		
def Correct_individual_matrix(individual,list_aggregated_servers,list_task_to_schedule_for_each_aggregated_server,nb_hi_task):

	nb_aggregate_modal_servers = len(list_aggregated_servers)

	for i in range(0,nb_aggregate_modal_servers):
		nb_task_to_schedule = len(list_task_to_schedule_for_each_aggregated_server[i])
		nb_modal_servers = len(list_aggregated_servers[i])
		for k in range(0,nb_modal_servers):
			index_task_hi = int(list_aggregated_servers[i][k][1:len(list_aggregated_servers[i][k])])
			for j in range(0,nb_task_to_schedule):
				index_task_lo = int(list_task_to_schedule_for_each_aggregated_server[i][j][1:len(list_task_to_schedule_for_each_aggregated_server[i][j])])- nb_hi_task
				individual[index_task_hi][index_task_lo] = 1
				

def init_Individual(icls,hi_task_list, nb_hi_tasks, lo_tasks_name, nb_lo_tasks):

	#print
	#print "init_ind"
	individual = []
	lo_tasks_name_copy = copy.deepcopy(lo_tasks_name)
	previous_step_allocated = False
	probability_of_allocation = 0.5
	for i in range(0,nb_hi_tasks):
	#	print "lo_tasks_name_copy"
	#	print lo_tasks_name_copy
		modal_server_line = [0 for k in range(0,nb_lo_tasks)]
		nb_task_schedulable_by_modal_server_utilization = len(hi_task_list[i].schedulable_with_utilization)
		nb_task_schedulable_by_modal_server_sbf = len(hi_task_list[i].schedulable_with_sbf)
		allocate_to_this_modal_server = random.random()
		if (nb_task_schedulable_by_modal_server_utilization != 0 or nb_task_schedulable_by_modal_server_sbf != 0) and len(lo_tasks_name_copy) > 0 and allocate_to_this_modal_server >= probability_of_allocation:
			task_schedulable = []
			task_schedulable.extend(hi_task_list[i].schedulable_with_utilization)
			task_schedulable.extend(hi_task_list[i].schedulable_with_sbf)
			nb_task_schedulable = len(task_schedulable)
			
			index = random.randint(0, nb_task_schedulable-1)
			count_try = 0
			while task_schedulable[index].name not in lo_tasks_name_copy and count_try < len(lo_tasks_name_copy):
				index = random.randint(0, nb_task_schedulable-1)
				count_try += 1
			if task_schedulable[index].name in lo_tasks_name_copy:
				lo_task_number = lo_tasks_name_copy.index(task_schedulable[index].name)
				lo_task_position = int(task_schedulable[index].name[1:len(task_schedulable[index].name)])-nb_hi_tasks
				modal_server_line[lo_task_position] = 1
				if task_schedulable[index].name not in hi_task_list[i].schedulable_with_sbf:
					lo_tasks_name_copy.pop(lo_task_number)
				previous_step_allocated = True
			if previous_step_allocated == False:
				probability_of_allocation -= 0.1
			else:
				probability_of_allocation = 0.5
				
		individual.append(copy.deepcopy(modal_server_line))
		
	return icls(individual)
	
def init_Individual_with_part_randomness(icls,hi_task_list, nb_hi_tasks, lo_tasks_name, nb_lo_tasks):

	individual = []
	lo_tasks_name_copy = copy.deepcopy(lo_tasks_name)
	previous_step_allocated = False
	probability_of_allocation = 0.2
	for i in range(0,nb_hi_tasks):
		modal_server_line = [0 for k in range(0,nb_lo_tasks)]
		nb_task_schedulable_by_modal_server_utilization = len(hi_task_list[i].schedulable_with_utilization)
		nb_task_schedulable_by_modal_server_sbf = len(hi_task_list[i].schedulable_with_sbf)
		allocate_to_this_modal_server = random.random()
		if (nb_task_schedulable_by_modal_server_utilization != 0 or nb_task_schedulable_by_modal_server_sbf != 0) and len(lo_tasks_name_copy) > 0 and allocate_to_this_modal_server >= probability_of_allocation:
			task_schedulable = []
			task_schedulable.extend(hi_task_list[i].schedulable_with_utilization)
			task_schedulable.extend(hi_task_list[i].schedulable_with_sbf)
			nb_task_schedulable = len(task_schedulable)
			
			index = random.randint(0, nb_task_schedulable-1)
			count_try = 0
			while task_schedulable[index].name not in lo_tasks_name_copy and count_try < len(lo_tasks_name_copy):
				index = random.randint(0, nb_task_schedulable-1)
				count_try += 1
			if task_schedulable[index].name in lo_tasks_name_copy:
				lo_task_number = lo_tasks_name_copy.index(task_schedulable[index].name)
				lo_task_position = int(task_schedulable[index].name[1:len(task_schedulable[index].name)])-nb_hi_tasks
				modal_server_line[lo_task_position] = 1
				if task_schedulable[index].name not in hi_task_list[i].schedulable_with_sbf:
					lo_tasks_name_copy.pop(lo_task_number)
				previous_step_allocated = True
			if previous_step_allocated == False:
				probability_of_allocation -= 0.1
			else:
				probability_of_allocation = 0.5
				
		individual.append(copy.deepcopy(modal_server_line))
		
	return icls(individual)
	
def init_Individual_with_sbf_first(icls,hi_task_list, nb_hi_tasks, lo_tasks_name, nb_lo_tasks):

	individual = []
	lo_tasks_name_copy = copy.deepcopy(lo_tasks_name)
	previous_step_allocated = False
	probability_of_allocation = 0.2
	for i in range(0,nb_hi_tasks):
		modal_server_line = [0 for k in range(0,nb_lo_tasks)]
		nb_task_schedulable_by_modal_server_utilization = len(hi_task_list[i].schedulable_with_utilization)
		nb_task_schedulable_by_modal_server_sbf = len(hi_task_list[i].schedulable_with_sbf)
		allocate_to_this_modal_server = random.random()
		if (nb_task_schedulable_by_modal_server_utilization != 0 or nb_task_schedulable_by_modal_server_sbf != 0) and len(lo_tasks_name_copy) > 0 and allocate_to_this_modal_server >= probability_of_allocation:
			task_schedulable = []
			task_schedulable.extend(hi_task_list[i].schedulable_with_utilization)
			task_schedulable.extend(hi_task_list[i].schedulable_with_sbf)
			nb_task_schedulable = len(task_schedulable)
			
			if len(hi_task_list[i].schedulable_with_sbf) != 0:
				index = random.randint(0, len(hi_task_list[i].schedulable_with_sbf)-1)
				count_try = 0
				while task_schedulable[index].name not in lo_tasks_name_copy and count_try < len(lo_tasks_name_copy):
					index = random.randint(0, len(hi_task_list[i].schedulable_with_sbf)-1)
					count_try += 1
				if task_schedulable[index].name in lo_tasks_name_copy:
					lo_task_number = lo_tasks_name_copy.index(task_schedulable[index].name)
					lo_task_position = int(task_schedulable[index].name[1:len(task_schedulable[index].name)])-nb_hi_tasks
					modal_server_line[lo_task_position] = 1
					previous_step_allocated = True
					
			if previous_step_allocated == False:
				index = random.randint(0, len(hi_task_list[i].schedulable_with_utilization)-1)
				count_try = 0
				while task_schedulable[index].name not in lo_tasks_name_copy and count_try < len(lo_tasks_name_copy):
					index = random.randint(0, len(hi_task_list[i].schedulable_with_utilization)-1)
					count_try += 1
				if task_schedulable[index].name in lo_tasks_name_copy:
					lo_task_number = lo_tasks_name_copy.index(task_schedulable[index].name)
					lo_task_position = int(task_schedulable[index].name[1:len(task_schedulable[index].name)])-nb_hi_tasks
					modal_server_line[lo_task_position] = 1
					previous_step_allocated = True
				if task_schedulable[index].name not in hi_task_list[i].schedulable_with_sbf:
					#lo_tasks_name_copy.pop(lo_task_number)
					previous_step_allocated = True
				
		individual.append(copy.deepcopy(modal_server_line))
		
	return icls(individual)
		

def init_Population(pcls,ind_init,nb_individus):#,hi_task_parameters, nb_task_hi, copy_lo_tasks_name, nb_lo_task, nb_possible_allocation):

	population = []

	for i in range(0,nb_individus):
		string ="IND"+str(i)
		individual = ind_init()
		individual.generation_of_creation = -1
		population.append(individual)
	return pcls(population)
	
	
def evalAlloc(individual,hi_task_list,nb_hi_tasks,lo_task_list,nb_lo_tasks,task_parameters,hyperperiod):

	print "#######################"
	print "Eval"
	Print_individual(individual)

	evaluation = 0.	
	list_aggregated_name,list_task_to_schedule_for_each_aggregated_server_name = schedulabilityFunctions.Identify_aggregated_servers_and_their_tasks(individual, nb_hi_tasks,nb_lo_tasks,task_parameters)
	Correct_individual_matrix(individual,list_aggregated_name,list_task_to_schedule_for_each_aggregated_server_name,nb_hi_tasks)

	print "Corrected ind"
	Print_individual(individual)

	nb_aggregate_modal_servers = len(list_aggregated_name)
	schedulability = False
	for i in range(0,nb_aggregate_modal_servers):
		period_schedulability = False
		list_aggregated_modal_server = packingUtils.Convert_task_name_list_into_task_list(list_aggregated_name[i],hi_task_list)
		list_task = packingUtils.Convert_task_name_list_into_task_list(list_task_to_schedule_for_each_aggregated_server_name[i],lo_task_list)	
		print "list_aggregated_modal_server"
		packingUtils.showTaskList(list_aggregated_modal_server)
		print
		print "list_task"
		packingUtils.showTaskList(list_task)		
		print
		if len(list_aggregated_modal_server) == 1:
			schedulability,task_utilization = schedulabilityFunctions.Check_schedulability_utilization(list_aggregated_modal_server, list_task)
			if schedulability == True:
				evaluation += task_utilization
				period_schedulability = True
			else: 
				evaluation -= task_utilization*10000
		print "utilization"
		print schedulability
		if period_schedulability == False:
			task_utilization = schedulabilityFunctions.Compute_task_list_utilization_from_task_name_list(list_task)
			schedulability = schedulabilityFunctions.Check_schedulability_sbf_dbf(list_task,list_aggregated_modal_server, hyperperiod)
			if schedulability == True:
				evaluation += task_utilization
			else: 
				evaluation -= task_utilization*10000
		if 	len(list_aggregated_name[i]) > 1:
			global_variables.global_nb_aggregated_servers += len(list_aggregated_name[i])
			print("AGGREGATED MODAL SERVERS \n%s\t%s\t%s" % (individual, evaluation, individual.generation_of_creation)	)
		print "sbf/dbf"
		print schedulability
		print list_aggregated_name[i]
		print list_task_to_schedule_for_each_aggregated_server_name[i]
		print 
	print
	print
	return evaluation, 
	
def Process_results(individual,hi_task_list,lo_task_list,task_parameters,task_file_path,file_path_result):

	nb_hi_tasks=len(hi_task_list)
	nb_lo_tasks=len(lo_task_list)
	nb_tasks = len(task_parameters)
	
	list_aggregated_name,list_task_to_schedule_for_each_aggregated_server_name = schedulabilityFunctions.Identify_aggregated_servers_and_their_tasks(individual, nb_hi_tasks,nb_lo_tasks,task_parameters)
	Correct_individual_matrix(individual,list_aggregated_name,list_task_to_schedule_for_each_aggregated_server_name,nb_hi_tasks)
	modal_servers_task_list = []
	allocated_tasks_list = []
	for i in range(0,len(list_aggregated_name)):
		modal_servers_task_list.append(packingUtils.Convert_task_name_list_into_task_list(list_aggregated_name[i],task_parameters))
		allocated_tasks_list.append(packingUtils.Convert_task_name_list_into_task_list(list_task_to_schedule_for_each_aggregated_server_name[i],task_parameters))
	
	unallocated_lo_tasks = packingUtils.FindLoTaskNotAllocated(allocated_tasks_list,lo_task_list)
	unused_modal_server = packingUtils.FindLoTaskNotAllocated(modal_servers_task_list,hi_task_list)
	
	nb_aggregate_modal_server = len(modal_servers_task_list)
	
	utilization_each_packing = []
	
	final_packing = []
	
	for i in range(0,nb_aggregate_modal_server):
		nb_modal_server = len(modal_servers_task_list[i])
		server = copy.deepcopy(copy.deepcopy(modal_servers_task_list[i][0]))
		for j in range(1,nb_modal_server):
			server = packingUtils.pack(server,modal_servers_task_list[i][j],2,1)
		final_packing.append(copy.deepcopy(server))	
		
	final_packing.extend(copy.deepcopy(unallocated_lo_tasks))
	final_packing.extend(copy.deepcopy(unused_modal_server))
			
	final_packing = packingUtils.findOnePacking(final_packing,2)	
	task_file_path_modal_server = task_file_path+"_modal_server_allocation.txt"
	fileUtils.WriteTaskSetAndModalServers(task_file_path_modal_server,task_parameters,lo_task_list,hi_task_list,final_packing,modal_servers_task_list,allocated_tasks_list)
	
	utilizationLoTask = packingUtils.ComputeUtilizationOfTaskList(lo_task_list, 1)
	utilizationHiTasksInHiMode = packingUtils.ComputeUtilizationOfTaskList(hi_task_list, 2)
	utilizationHiTasksInLoMode = packingUtils.ComputeUtilizationOfTaskList(hi_task_list, 1)
	utilizationHiMinusLoHi = utilizationHiTasksInHiMode - utilizationHiTasksInLoMode
	Ulimit = utilizationLoTask + utilizationHiTasksInHiMode	
	
	utilization_mxc_run = 0.
	
	for i in range(0,len(allocated_tasks_list)):
		nb_tasks_allocated = len(allocated_tasks_list[i])
		for j in range(0,nb_tasks_allocated):
			utilization_mxc_run -= allocated_tasks_list[i][j].utilization[0]
		
	utilization_mxc_run += Ulimit
			
	task_file_path_result = file_path_result	
	
	fileUtils.WriteExperimentsValueInFile(task_file_path_result,nb_tasks,Ulimit,utilizationHiTasksInHiMode,utilizationLoTask,utilization_mxc_run, task_file_path,task_file_path_modal_server,len(hi_task_list),utilizationHiMinusLoHi)
	
	
def Check_best_ind_fitness(fitness):
	nb_parameters = len(fitness)
	
	for i in range(0,nb_parameters):
		if fitness[i] < 0:
			return False
	return True
	
def mut_flip_bit_aggregation(individual):
	choice_modal_server = random.randint(0, len(individual)-1)
	choice_task = random.randint(0, len(individual[choice_modal_server])-1)
	individual[choice_modal_server][choice_task] = 1
	return individual,
	
def mut_flip_bit_desaggregation(individual):
	choice_modal_server = random.randint(0, len(individual)-1)
	choice_task = random.randint(0, len(individual[choice_modal_server])-1)
	individual[choice_modal_server][choice_task] = 0

	return individual,
	
def Compute_limit_for_agg_desagg(ratio_aggregation):
	
	limit = 0.
	
	if ratio_aggregation <= 0.3:
		limit = 0.9 - ratio_aggregation * (0.9-0.7)/0.3
	elif 0.3 < ratio_aggregation <= 0.7:
		limit = 1 - ratio_aggregation
	else:
		limit = 1 - 2*ratio_aggregation
	if limit < 0:
		limit = 0.
	return limit
	
def Round_proper(float_number,digit_precision):

	float_number_str = str(float_number)
	for i in range(0, len(float_number_str)):
		if float_number_str[i] == ".":
			digit_position = copy.deepcopy(i)
			break
	float_number_str_approx = float_number_str[:digit_position+digit_precision+1]
	print 
	print "Round_proper"
	print 
	
	print "int(float_number_str[digit_position+digit_precision+2])"
	print int(float_number_str[digit_position+digit_precision+1])
	print float_number_str_approx 
	carry = True
	if int(float_number_str[digit_position+digit_precision+1]) >= 5:
		carry = True
		for i in range(len(float_number_str_approx)-1,-1,-1):
			print float_number_str_approx[i]
			if carry == True and i != digit_position:
				int_digit = int(float_number_str_approx[i])+1
				print str(int_digit%10)[0]
				float_number_str_approx = float_number_str_approx[:i]+str(int_digit%10)
				if int_digit >= 10 and i != 0:
					carry = True
				elif int_digit >= 10 and i == 0:
					print "Aggrandissement"
					print float_number_str_approx
					float_number_str_approx = str(1)+float_number_str_approx
					print float_number_str_approx
					
					carry = True
				else:
					carry = False
			
	
	return float(float_number_str_approx)
	

	

