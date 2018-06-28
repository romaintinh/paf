import copy
import math
import packingUtils
import mxc_run_ga_utils


def getTheLeastCommonMultipleV2(initialIntegersTable):
	
	nbIntegers = len(initialIntegersTable)
	newIntegersTable = copy.deepcopy(initialIntegersTable)

	while checkTermination(newIntegersTable) != True:
		minimum = getLeastElementAndItsIndex(newIntegersTable)
		newIntegersTable[minimum[0]] = newIntegersTable[minimum[0]] + initialIntegersTable[minimum[0]]

	return newIntegersTable[0]


def getTheLeastCommonMultiple(initialIntegersTable,integersTable):
	
	nbIntegers = len(integersTable)
	newIntegersTable = copy.deepcopy(integersTable)

	if checkTermination(integersTable) == True:
		return integersTable[0]
	else:
		minimum = getLeastElementAndItsIndex(integersTable)
		newIntegersTable[minimum[0]] = integersTable[minimum[0]] + initialIntegersTable[minimum[0]]
		return getTheLeastCommonMultiple(initialIntegersTable,newIntegersTable)

def checkTermination(integers):
	nbIntegers = len(integers)
	i = 0
	nbEquality = 0
	string = ""
	while i < nbIntegers:
		if integers[0] == integers[i]:
			string = str(integers[0])+" "+str(integers[i])
			nbEquality += 1	
		i += 1
	if nbEquality == i:
		return True	
	else: 
		return False

def getLeastElementAndItsIndex(integersTable):
	nbIntegers = len(integersTable)
	i = 0
	minimum = [0,integersTable[0]]

	while i < nbIntegers:
		if minimum[1] > integersTable[i]:
			minimum[1] = integersTable[i]
			minimum[0] = i
		i += 1

	return minimum



# Derive the sbf function on the given time interval. The definition of the sbf is taken from the paper Periodic Resource Model for Compositional Real-Time Guarantees of
# Insik Shin and Insup Lee.
# tBegin and tEnd are used to indicate the time of beginning and of end of the interval
# taskParameters corresponds to the parameters of one task 
# typeTask i sused to indicate whether it is a modal server or a low task that is a normal tasks
def SbfFunctionForModalServer(tBegin, tEnd, oneTaskParameters):	
	timeCurrent = tBegin
	epsilon = 0
	sbfValues = []
	sbfvalue = ""

	budgetServer = (oneTaskParameters.wcet[oneTaskParameters.criticality-1]-oneTaskParameters.wcet[0])
	while	timeCurrent <= tEnd:
		epsilon = max(0,timeCurrent-2*(oneTaskParameters.period[0]-budgetServer)-oneTaskParameters.period[0]*math.floor((timeCurrent - (oneTaskParameters.period[0]- budgetServer))/oneTaskParameters.period[0]))
		sbfValue = (math.floor((timeCurrent - (oneTaskParameters.period[0] - budgetServer))/oneTaskParameters.period[0])*budgetServer+epsilon)*1000
		if sbfValue < 0:
			sbfValue = 0
		sbfValues.append(int(sbfValue))
		timeCurrent += 1

	oneTaskParameters.sbf.extend(copy.deepcopy(sbfValues))
	

def ComputeSbfFunctionForListOfTasks(listOfTask,tBegin,tEnd):
	sizeList = len(listOfTask)
	i = 0
	
	while i < sizeList:
		SbfFunctionForModalServer(tBegin, tEnd, listOfTask[i])
		i += 1

	

def DbfFunction(tBegin, tEnd, oneTaskParameters,criticalityLevel):
	nbTaskActivation = int(float(tEnd-tBegin)/float(oneTaskParameters.period[0]))+1
	dbfValues = []
	dbfValue = 0.
	i = tBegin
	hyperperiod = tEnd-tBegin

	if criticalityLevel >= 0:
		for i in range(0,hyperperiod+1):
			dbfValue = max(0,math.floor((float(i))/oneTaskParameters.period[0]))*oneTaskParameters.wcet[criticalityLevel-1]*1000
			dbfValues.append(int(math.ceil(dbfValue)))
			i += 1
	# Case for modal servers
	else:
		budget = oneTaskParameters.wcet[1]-oneTaskParameters.wcet[0]
		while i <= tEnd:
			dbfValue = max(0,math.floor((float(i))/oneTaskParameters.period[0]))*budget*1000
			dbfValues.append(int(math.ceil(dbfValue)))
			i += 1

	oneTaskParameters.dbf.extend(copy.deepcopy(dbfValues))


def ComputeDbfFunctionForListOfTasks(listOfTask,tBegin,tEnd,criticalityLevel):
	sizeList = len(listOfTask)
	i = 0
	
	while i < sizeList:
		DbfFunction(tBegin, tEnd, listOfTask[i],criticalityLevel)
		i += 1
		
		
def Compute_dbf_accumulated_tasks(listOfTask):

	sizeList = len(listOfTask)
	summed_dbf = copy.deepcopy(listOfTask[0].dbf)
	hyperperiod = len(listOfTask[0].dbf)
	for i in range(1,sizeList):
		for j in range(0,hyperperiod):
			summed_dbf[j] += listOfTask[i].dbf[j]
			
	return summed_dbf

def TruncateValue(floatNumber, digitWanted):
	floatStr = str(floatNumber)
	sizeFloatStr = len(floatStr)

	i = 0
	while i < sizeFloatStr:
		if floatStr[i] == ".":
			break
		i += 1

	return float(floatStr[:i+digitWanted+1])
	


def UtilizationPRM(taskParameters, ResourceParameters):
	PRMUtilization = 0.
	PRMBudget = ResourceParameters.wcet[ResourceParameters.criticality-1]-ResourceParameters.wcet[0]

	PRMUtilization = PRMBudget / ResourceParameters.period[0]*(1-(2*(ResourceParameters.period[0]-PRMBudget))/taskParameters.period[0])

	return PRMUtilization


def UtilizationPRMPeriod(periodLoTask, ResourceParameters):
	PRMUtilization = 0.
	PRMBudget = ResourceParameters.wcet[ResourceParameters.criticality-1]-ResourceParameters.wcet[0]

	PRMUtilization = PRMBudget / ResourceParameters.period[0]*(1-(2*(ResourceParameters.period[0]-PRMBudget))/periodLoTask)

	return PRMUtilization


def ComputeSbfOfAggregatedModalServers(oneAggregatedModalServer):

	nbOfModalServerAggregated = len(oneAggregatedModalServer)
	aggregatedSbf = []
	aggregatedSbf.extend(copy.deepcopy(oneAggregatedModalServer[0].sbf))
	time = len(oneAggregatedModalServer[0].sbf)
	utilization = oneAggregatedModalServer[0].utilization[1]
	for i in range(1,nbOfModalServerAggregated):
		for j in range(0,time):
			aggregatedSbf[j] +=  oneAggregatedModalServer[i].sbf[j]
		utilization += oneAggregatedModalServer[i].utilization[1]

	if utilization > 1:
		return False

	return aggregatedSbf
	

def Find_aggregated_server(list_aggregated_servers,current_aggregate):

	nb_aggregated_servers = len(list_aggregated_servers)
	nb_modal_servers = len(current_aggregate)
	
	for i in range(0,nb_aggregated_servers):
		for k in range(0,nb_modal_servers):
			if current_aggregate[k] in list_aggregated_servers[i]:
				return i			
	return -1
	
def Merge_aggregated_modal_servers(aggregate_one, aggregate_two):
	size_aggregate_two = len(aggregate_two)
	
	print
	print "Merge"
	print aggregate_one
	print aggregate_two

	for i in range(0,size_aggregate_two):
		print aggregate_two[i] 
		print aggregate_two[i] not in aggregate_one
		if aggregate_two[i] not in aggregate_one:
			aggregate_one.append(copy.deepcopy(aggregate_two[i]))
			
	#return aggregate_one

def Count_time_of_appearance_tasks(list_group_task_name, task_name):
	nb_group = len(list_group_task_name)
	counter_for_task = 0

	for i in range(0,nb_group):
		if task_name in list_group_task_name[i]:
			counter_for_task += 1

	return counter_for_task

def Retrieve_index_modal_server_where_a_task_appear(list_agg_modal_server,task_name):

	index_agg_modal_server = []
	nb_agg_modal_server = len(list_agg_modal_server)
	for i in range(0,nb_agg_modal_server):
		if task_name in list_agg_modal_server[i]:
			index_agg_modal_server.append(copy.deepcopy(i))

	return index_agg_modal_server
	
def Identify_aggregated_servers_and_their_tasks(individual, nb_hi_tasks,nb_lo_tasks, task_parameters):

	list_aggregated_servers = []
	list_task_to_schedule_for_each_aggregated_server = []

	print
	print "Identify_aggregated_servers_and_their_tasks"
	mxc_run_ga_utils.Print_individual(individual)	

	for i in range(0,nb_lo_tasks):
		count = 0
		current = []
		print ("Lo Task:%s" % (task_parameters[nb_hi_tasks+i].name))
		for j in range(0,nb_hi_tasks):
			if individual[j][i] == 1:
				current.append(task_parameters[j].name) # gather name of MS executing the LO tasks
		print current
		if len(current) != 0:
			aggregate_number = Find_aggregated_server(list_aggregated_servers, current)
			print aggregate_number
			if aggregate_number == -1:
				list_aggregated_servers.append(copy.deepcopy(current))
				list_task_to_schedule_for_each_aggregated_server.append([task_parameters[nb_hi_tasks+i].name])
			else:
				print("To aggregated with %s" % (list_aggregated_servers[aggregate_number]))	
				print "Avant merge"
				print list_aggregated_servers
				print list_task_to_schedule_for_each_aggregated_server	
				print "current"
				print list_aggregated_servers[aggregate_number]
				print current
				print "task"
				print list_task_to_schedule_for_each_aggregated_server[aggregate_number]
				print task_parameters[nb_hi_tasks+i].name
				Merge_aggregated_modal_servers(list_aggregated_servers[aggregate_number], current)
				Merge_aggregated_modal_servers(list_task_to_schedule_for_each_aggregated_server[aggregate_number], [task_parameters[nb_hi_tasks+i].name])
				print "Apres merge"
				print list_aggregated_servers[aggregate_number]
				print current
				print "task"
				print list_task_to_schedule_for_each_aggregated_server[aggregate_number]
				print task_parameters[nb_hi_tasks+i].name
				print 
	print "before returned"
	print list_aggregated_servers
	print list_task_to_schedule_for_each_aggregated_server	
	print 

	for i in range(0, nb_hi_tasks):
		print task_parameters[i].name
		counter_appearances = Count_time_of_appearance_tasks(list_aggregated_servers, task_parameters[i].name)
		print "counter_appearances"
		print counter_appearances
		if counter_appearances > 1:
			index_agg_modal_server = Retrieve_index_modal_server_where_a_task_appear(list_aggregated_servers,task_parameters[i].name)
			print "index_agg_modal_server"
			print index_agg_modal_server
			nb_merge_to_do = len(index_agg_modal_server)
			print "nb_merge_to_do"
			print nb_merge_to_do
			print list_aggregated_servers
			print list_task_to_schedule_for_each_aggregated_server	
			for j in range(1,nb_merge_to_do):
				Merge_aggregated_modal_servers(list_aggregated_servers[index_agg_modal_server[0]], list_aggregated_servers[index_agg_modal_server[j]])
				Merge_aggregated_modal_servers(list_task_to_schedule_for_each_aggregated_server[index_agg_modal_server[0]], list_task_to_schedule_for_each_aggregated_server[index_agg_modal_server[j]])
			print list_aggregated_servers
			print list_task_to_schedule_for_each_aggregated_server
			print "pop"	
			print index_agg_modal_server
			print len(list_task_to_schedule_for_each_aggregated_server)
			print index_agg_modal_server
			index_agg_modal_server.sort(reverse=True)
			print index_agg_modal_server
			for j in range(0,nb_merge_to_do-1):
				list_task_to_schedule_for_each_aggregated_server.pop(index_agg_modal_server[j])
				list_aggregated_servers.pop(index_agg_modal_server[j])
				

	print		
	print "returned"
	print list_aggregated_servers
	print list_task_to_schedule_for_each_aggregated_server	
	print 
	return list_aggregated_servers,list_task_to_schedule_for_each_aggregated_server
	
def Compute_task_list_utilization_from_task_name_list(task_list):
	nb_task = len(task_list)
	utilization = 0.
	for i in range(0,nb_task):
		utilization += task_list[i].utilization[0]
		
	return utilization	
	
	
def Check_schedulability_utilization(list_modal_servers, list_tasks):
	
	nb_modal_server = len(list_modal_servers)
	#if nb_modal_server > 1:
	#	return False
	modal_server_utilization = list_modal_servers[0].utilization[1]-list_modal_servers[0].utilization[0]
	nb_tasks = len(list_tasks)
	utilization = 0.
	period_constraint = True
	for i in range(0,nb_tasks):
		utilization += list_tasks[i].utilization[0]
		if list_tasks[i].period[0]%list_modal_servers[0].period[0]!=0:
			period_constraint = False
	
	if utilization > modal_server_utilization or period_constraint == False:
		return False, utilization
	else: 		
		return True, utilization

def Check_schedulability_sbf_dbf(list_tasks,list_modal_server, hyperperiod):

	aggregate_sbf = ComputeSbfOfAggregatedModalServers(list_modal_server)
	print "aggregate_sbf"
	#print aggregate_sbf
	if aggregate_sbf != False:
		tasks_dbf = Compute_dbf_accumulated_tasks(list_tasks)
		print "tasks_dbf"
		#print tasks_dbf
		for i in range(0,hyperperiod+1):
#			print("sbf %s dbf %s" % (aggregate_sbf[i],tasks_dbf[i]))
			if aggregate_sbf[i] < tasks_dbf[i]:
				return False
		return True
	else:
		return False

# Compute for each modal server a list of tasks that it can schedule
def Compute_task_list_for_modal_server(modal_server,lo_task_list):

	nb_tasks = len(lo_task_list)
	
	schedulable_tasks = []
	
	modal_server_utilization = modal_server.utilization[1]-modal_server.utilization[0]
	
	for i in range(0,nb_tasks):
		print("modal server: %s\t Task:%s" % (modal_server.name,lo_task_list[i].name))
		print lo_task_list[i].period[0] < modal_server.period[0]
		if lo_task_list[i].period[0] >= modal_server.period[0]:
			print "lo_task_list[i].period[0] % modal_server.period[0] == 0 and modal_server_utilization >= lo_task_list[i].utilization[0]"
			print lo_task_list[i].period[0] % modal_server.period[0] == 0 and modal_server_utilization >= lo_task_list[i].utilization[0]
			if lo_task_list[i].period[0] % modal_server.period[0] == 0 and modal_server_utilization >= lo_task_list[i].utilization[0]:
				schedulable_tasks.append(lo_task_list[i])
				modal_server.schedulable_with_utilization.append(lo_task_list[i])			
			schedulability = True
			for j in range(0,len(modal_server.sbf)):
#				print("Modal server %s and task %s: sbf %s \t dbf %s" % (modal_server.name,lo_task_list[i].name,modal_server.sbf[j],lo_task_list[i].dbf[j]))
				if modal_server.sbf[j] < lo_task_list[i].dbf[j]:
					schedulability = False
			print "schedulability"
			print schedulability
			if schedulability == True:
 				schedulable_tasks.append(lo_task_list[i])
 				modal_server.schedulable_with_sbf.append(lo_task_list[i])
 #			print("schedulability: %s" % (schedulability))
	print 
 	return schedulable_tasks
			
	















