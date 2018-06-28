import copy
import fileUtils
import math
import fractions


class Task:	
	def __init__(self):
		self.name = ""
		self.period = []
		self.deadline = []
		self.criticality = 0
		self.wcet = []
		self.utilization = []
		self.dbf = [] # For the case we consider the task as a task use of the dbf formula
		self.sbf = [] # For the case we consider the task as a Modal Server use of the sbf function of Insup Lee and Insik Shin
		self.schedulable_with_utilization = []
		self.schedulable_with_sbf = []

	def Show(self):
		string ="TASK "+self.name+"\t"+str(self.period)+"\t"+str(self.criticality)+"\t"+str(self.wcet)+"\t"+str(self.utilization)#+"\t"+str(self.dbf)+"\t"+str(self.sbf)
		print string
	def Show_schedulable(self):
		string ="TASK "+self.name+" can schedule utilization"
		print string	
		showTaskList(self.schedulable_with_utilization)
		print "sbf:"
		showTaskList(self.schedulable_with_sbf)
		print


class Packing:
	def __init__(self):
		self.servers = []
		self.dual = []
		self.childPacking = []

	def Show(self):
		print "servers"
		print self.servers

	def GetChildPacking(self):
		return self.childPacking



# Extract the name of all tasks that have a criticality level greater than criticalityLevel given in parameter to the function.
def extractAllTasksOfCriticalityGreater(taskParameters,criticalityLevel):
	taskToReturn=[]
	i = 0
	numberOfTasks = len(taskParameters)
	while i < numberOfTasks:
		if int(taskParameters[i].criticality) >= criticalityLevel:
			taskToReturn.append(copy.deepcopy(taskParameters[i]))
		i += 1 
	return taskToReturn

# Extract and return tasks with their criticality-level parameters
def extractTaskWithTheirOwnCriticalityTimingParameters(taskParameters):
	taskToReturn = []
	nbTasks = len(taskParameters)
	i = 0
	while i < nbTasks:
		newTask = Task()
		newTask.name = taskParameters[i].name
		newTask.period.append(copy.deepcopy(taskParameters[i].period[0]))
		newTask.wcet.append(copy.deepcopy(taskParameters[i].wcet[taskParameters[i].criticality-1]))
		newTask.utilization.append(copy.deepcopy(taskParameters[i].utilization[taskParameters[i].criticality-1]))
		newTask.criticality = 1
	
		taskToReturn.append(copy.deepcopy(newTask))
		
		i += 1

	return taskToReturn

# Extract the name of all tasks that have a criticality level lower than criticalityLevel given in parameter to the function.
def extractAllTasksOfCriticalityLower(taskParameters,criticalityLevel):
	taskToReturn=[]
	i = 0
	numberOfTasks = len(taskParameters)
	while i < numberOfTasks:
		if int(taskParameters[i].criticality) < criticalityLevel:
			taskToReturn.append(copy.deepcopy(taskParameters[i]))
		i += 1 
	return taskToReturn
	
# Find all packing from the given taskParameters and for the criticality level criticality
def findAllPacking(taskParameters,criticality,fileName,utilizationLimit):
	newTaskParameter=[]
	allPackingList = []
	packing = Packing()
	lonelyTask = 0
#	showTaskList(taskParameters)
	i = 0
	j = 0
	taskToPack = extractAllTasksOfCriticalityGreater(taskParameters,criticality)
	numberOfTasks = len(taskToPack)
	
	while i < numberOfTasks:			
		while j < numberOfTasks:
			# if i=j the same task will be considered by both increments i and j.
			if i == j and j+1 < numberOfTasks:
				j+=1
			elif j+1 >= numberOfTasks:
				break;
			# try to pack the two tasks.
			server = pack(taskToPack[i],taskToPack[j],criticality,utilizationLimit)

			#if server != False:
				#server.Show()
			# In case of failure consider next task 
			if server == False and verifyTermination(taskToPack,criticality,utilizationLimit) == False:
				j += 1
			elif server == False and verifyTermination(taskToPack,criticality,utilizationLimit) == True:
				if fileUtils.StringExist(taskToPack,fileName) == False:
					packing.servers = copy.deepcopy(taskToPack)
					allPackingList.append(copy.deepcopy(packing))
					fileUtils.WriteFilePackedServer(taskToPack,criticality,fileName)
				newTaskParameter=[]
				packing = Packing()	
				j += 1
			# Otherwise create a task set with the packed tasks and the unchanged tasks.
			else:	
				newTaskParameter = []	
				newTaskParameter.append(copy.deepcopy(taskToPack[i]))	
				newTaskParameter[0].name = server.name
				newTaskParameter[0].utilization[criticality-1] = server.utilization[0]
				newTaskParameter[0].period = copy.deepcopy(server.period)
				newTaskParameter[0].period.sort()
				oldTaskParameter = copy.deepcopy(taskToPack)

				# removing of the tasks which have been packed.
				oldTaskParameter.pop(i)
				if i < j :
					oldTaskParameter.pop(j-1)
				else:
					oldTaskParameter.pop(j)	

				# new set of tasks with the packed server and the other tasks.
				newTaskParameter.extend(oldTaskParameter)
 
				if verifyTermination(newTaskParameter,criticality,utilizationLimit) == False:
					res = findAllPacking(newTaskParameter,criticality,fileName,utilizationLimit)
					if res != []:
						allPackingList.extend(res)
				else:		
					if fileUtils.StringExist(newTaskParameter,fileName) == False:
						fileUtils.WriteFilePackedServer(newTaskParameter,criticality,fileName)			
						packing.servers = copy.deepcopy(newTaskParameter)
						allPackingList.append(Packing())
						allPackingList[-1].servers = copy.deepcopy(packing.servers)
					newTaskParameter=[]
					packing = Packing()					
				j += 1
		i += 1
		j = 0
	return allPackingList
# allPackingList = [ Packing1,Packing2,...,Packingk ]


	

# task1 and task2 are lists whose first elements are a string containing their names and the second is a type float storing their respective utilizations.
def pack(task1,task2,criticality,utilizationLimit):
	server = Task()

	if task1.utilization[task1.criticality-1]+task2.utilization[task2.criticality-1] <= utilizationLimit:
		sizeTask1 = len(task1.name)
		i = 0
		compterT1 = []
		while i < sizeTask1: 
			if task1.name[i] == "T":
				compterT1.append(copy.deepcopy(i))
			i += 1
		compterT1.append(copy.deepcopy(i))

		i = 0
		sizeTask2 = len(task2.name)
		compterT2 = []
		while i < sizeTask2: 
			if task2.name[i] == "T":
				compterT2.append(copy.deepcopy(i))
			i += 1
		compterT2.append(copy.deepcopy(i))
 
		symbol=[]

		i = 0
		while i < len(compterT1)-1:
			symbol.append(copy.deepcopy(str(task1.name[compterT1[i]+1:compterT1[i+1]])))
			i += 1

		i = 0
		while i < len(compterT2)-1:
			symbol.append(copy.deepcopy(str(task2.name[compterT2[i]+1:compterT2[i+1]])))
			i += 1

		symbol.sort()

		i = 0
		while i < len(symbol):
			server.name = server.name +"T"+str(symbol[i])
			i += 1

		server.utilization.append(round(task1.utilization[task1.criticality-1]+task2.utilization[task2.criticality-1],3))
		nbPeriod = len(task1.period)
		l = 0
		if nbPeriod > 1:
			while l < nbPeriod:
				try:
					server.period.index(task1.period[l])
				except ValueError:
					server.period.append(copy.deepcopy(task1.period[l]))
				l += 1
		else:
			try:
				server.period.index(task1.period[l])
			except ValueError:
					server.period.append(copy.deepcopy(task1.period[l]))
		nbPeriod = len(task2.period)
		l = 0

		if nbPeriod > 1:
			while l < nbPeriod:
				try:
					server.period.index(task2.period[l])
				except ValueError:
					server.period.append(copy.deepcopy(task2.period[l]))
				l += 1
		else:
			try:
				server.period.index(task2.period[l])
			except ValueError:
					server.period.append(copy.deepcopy(task2.period[l]))


		server.period.sort()
		server.deadline.extend(copy.deepcopy(server.period))
		server.criticality = 1
		return server
	else:
		return False

# function used to verify if there are no servers with spare capacity to integrate another task.
#	True: packing done
#	False: packing still possible
def verifyTermination(taskParameters,criticality,utilizationLimit):
	i = 0
	minUtilization = [10.,""]
	sizeTaskSet = len(taskParameters)

	if sizeTaskSet == 1:
		return True

	while i < sizeTaskSet:	
		if taskParameters[i].utilization[criticality-1] < minUtilization[0]:
			minUtilization = [taskParameters[i].utilization[criticality-1],taskParameters[i].name]
		i += 1
	i = 0 
	toReturn = True

	while i < sizeTaskSet:
		#print
		#print utilizationLimit
		#print taskParameters[i].utilization[criticality-1]
		if round((utilizationLimit-taskParameters[i].utilization[criticality-1]),3) >= round(minUtilization[0],3) and taskParameters[i].name!= minUtilization[1]:
			toReturn = False
		i += 1
#	showTaskList(taskParameters)
	return toReturn




def retrieveTaskNamesFromTaskGroupName(groupTaskNames):
	sizeGroupTask = len(groupTaskNames)
	tasks = []
	j = 0
	i = 0
	while i < sizeGroupTask:
		if i+1 < sizeGroupTask:	
			j = i+1
		else:
			break
		while groupTaskNames[j]!="T":			
			if j+1 < sizeGroupTask:
				j += 1
			else:
				j += 1
				break
		name = groupTaskNames[i:j]
		tasks.append(copy.deepcopy(name))
		i = j

	return tasks


def computeTotalUtilizationOfListOfTaskNames(listOfTaskNames,taskList):
	sizeList = len(listOfTaskNames)
	i = 0
	utilization = 0.
	decrement = 0
	# decrement is used to "realignate" the index in the table and the numbering used for tasks.
	# i.e if the first task is named T1 the decrement is set to 1 so that its index is set to 0 when accessing the T1 parameters
	if taskList[0].name[1] !="0":
		decrement = 1

	while i < sizeList:
		index = int(listOfTaskNames[i][1:len(listOfTaskNames[i])])-decrement
		utilization += taskList[index].utilization[0]
		i += 1

	return utilization
	

# Retrieve the tasks' names from a server.
def getTaskNamesFromServers(String):
	tasks = []
	sizeString = len(String)
	i = 0
	j = 0	
	while i < sizeString:
		j = i+1
		while j < sizeString and String[j] != "T":
			j += 1
		tasks.append(String[i:j])
		i = j

	return tasks

# Retrieve the servers' names from a file 
def getServerNames(String):
	server = []
	server.append(0)
	server[0] = String[0]

	i = 0
	sizeString = len(String)

	while i < sizeString:
		j = i 
		while String[j]!= "T":
			j += 1
		i = j
		while String[i]!=" ":
			i += 1

		tasks = getTaskNamesFromServers(String[j:i])
		server.append(copy.deepcopy(tasks))
		i += 1
		if len(server)-1 == int(server[0]):
			break
	return server


# Select the packing in which the number of the servers is the most important
def selectPackingMaxServer(filePath):
	fichier = open(filePath,"r")

	content = fichier.read()
	serverMax = []
	serverMax.append(-1)
	i = 0
	sizeFile = len(content)

	while i < sizeFile:
		j = i
		while content[j] != "\n":
			j += 1
		server = getServerNames(content[i:j])
		if serverMax[0] < server[0]:
			serverMax = copy.deepcopy(server)
		i = j
		i += 1

	fichier.close()

	return serverMax

# Retrive all the possible RUN primal servers from a file
def RetrieveAllPossibleRUNPrimalServers(filePath,taskListParameters):
	
	fichier = open(filePath,"r")

	content = fichier.read()
	allServerPossibilities = []
	i = 0
	sizeFile = len(content)

	while i < sizeFile:
		j = i
		while content[j] != "\n":
			j += 1
		server = getServerNames(content[i:j])	
		server = server[1:len(server)]
		nbServers = len(server)
		k = 0
		while k < nbServers:
			nbTasksInServer = len(server[k])
			
			if len(server[k]) == 1 and IsTheElementPresentInTheList(server[k],allServerPossibilities) == False and retrieveIndexTaskParametersFromItsName(server[k][0],taskListParameters) == -1:
					allServerPossibilities.append(copy.deepcopy(server[k]))
			if len(server[k]) > 1 and IsTheElementPresentInTheList(server[k],allServerPossibilities) == False:
					allServerPossibilities.append(copy.deepcopy(server[k]))
			k += 1
		i = j
		i += 1

	fichier.close()

	return allServerPossibilities



def AssociateLoTaskSetWithModalServers(packingWithMaxServer,taskParameters,allocationOfLoTasksToModalServers):

	i = 0
	nbServer = len(packingWithMaxServer)
	associationLoTaskToServer = [ [] for i in range(0,nbServer) ]
	i = 0
	indexServerHiTask = 0
	j = 0
	k = 0
	while i < nbServer:
		j = 0
		nbTaskHiInServer = len(packingWithMaxServer[i])
		while j < nbTaskHiInServer:
			associationLoTaskToServer[i].append(packingWithMaxServer[i][j])
			if packingWithMaxServer[i][j] != "TF":
				indexTask = retrieveIndexTaskParametersFromItsName(packingWithMaxServer[i][j],taskParameters)
				nbLowTaskAssociated = len(allocationOfLoTasksToModalServers[indexTask])
				k = 0
				while k < nbLowTaskAssociated:
					associationLoTaskToServer[i].append(taskParameters[allocationOfLoTasksToModalServers[indexTask][k]].name)
					k += 1
			else:
				nbLowTaskAssociated = len(allocationOfLoTasksToModalServers[-1])
				k = 0
				while k < nbLowTaskAssociated:
					associationLoTaskToServer[i].append(taskParameters[allocationOfLoTasksToModalServers[-1][k]].name)
					k += 1
			j += 1
		i += 1

	return associationLoTaskToServer

def retrieveIndexTaskParametersFromItsName(taskName,taskParameters):
	
	index = -1
	i = 0
	nbTask = len(taskParameters)

	#print
	#print "retrieveIndexTaskParametersFromItsName"
	#print taskName

	while i < nbTask:
		if taskName == taskParameters[i].name:
			index = copy.deepcopy(i)
			break
		i += 1

	#print index
	#print 

	return index

def FindLoTaskNotAllocated(allocationOfLoTasksToModalServers,loTaskList):

	nbTaskHi = len(allocationOfLoTasksToModalServers)
	nbTaskLo = len(loTaskList)
	loTaskListUnallocated = copy.deepcopy(loTaskList)

	indexTask = nbTaskLo+1
	i = 0
	j = 0

	while i < nbTaskHi:
		nbTaskLoAllocatedToTaskHi = len(allocationOfLoTasksToModalServers[i])
		j = 0
		while j < nbTaskLoAllocatedToTaskHi:
			indexTask = retrieveIndexTaskParametersFromItsName(allocationOfLoTasksToModalServers[i][j].name,loTaskListUnallocated)
			if indexTask != -1:
				loTaskListUnallocated.pop(indexTask)
			j += 1
		i += 1

	return loTaskListUnallocated


def showTaskList(taskList):

	size = len(taskList)
	i = 0
		
	while i < size: 
		if type(taskList[i]) ==  type(Task()):
			taskList[i].Show()
		else:
			print taskList[i]
		i += 1

def FillHiMode(utilizationHiTasksInHiMode, unallocatedLoTask, period):

	taskForFilling = Task()

	utilizationToFill = math.ceil(utilizationHiTasksInHiMode) - utilizationHiTasksInHiMode
	utilizationToFill = str(utilizationToFill)
	utilizationToFill = utilizationToFill[:6]
	utilizationToFill = float(utilizationToFill)

	taskForFilling.name = "TF"
	taskForFilling.utilization = [0,utilizationToFill]
	taskForFilling.criticality = 2	

	taskForFilling.period.append(period)

	taskForFilling.wcet = [0,float(int(taskForFilling.period[0]*utilizationToFill*100))/100]

	return taskForFilling

def AllPossiblePeriodsForFillingTask(unallocatedLoTask):

	nbUnallocatedTaskLo = len(unallocatedLoTask)
	periodList = []
	i = 0

	while i < nbUnallocatedTaskLo:
		try:
			periodList.index(unallocatedLoTask[i].period[0])
		except ValueError: 
			periodList.append(unallocatedLoTask[i].period[0])
		i += 1	

 	allPeriodGroupPossible = AllPossibleGroupPeriodsFromPeriodList(periodList,[])

	gcdList = []
	i = 0
	nbPossiblePeriodGroups = len(allPeriodGroupPossible)
	while i < nbPossiblePeriodGroups:
		nbPeriodInGroup = len(allPeriodGroupPossible[i])		
		j = 0
		while j < nbPeriodInGroup-1 and nbPeriodInGroup > 1:
			allPeriodGroupPossible[i].append(fractions.gcd(allPeriodGroupPossible[i].pop(0),allPeriodGroupPossible[i].pop(0)))
			j += 1
		try:
			gcdList.index(allPeriodGroupPossible[i][0])
		except ValueError:
			gcdList.extend(allPeriodGroupPossible[i])
		i += 1	
		
	gcdList.sort()
	return gcdList

def AllPossibleGroupPeriodsFromPeriodList(periodList,init):
	
	nbLoTask = len(periodList)
	possibilities = []
	onePossibleGroup = init
	i = 0

	if nbLoTask > 1:
		while i < nbLoTask:
			try:
				possibilities.index([periodList[i]])
			except ValueError:
				onePossibleGroup.append(periodList[i])
				possibilities.append(copy.deepcopy(onePossibleGroup))

			temp = AllPossibleGroupPeriodsFromPeriodList(periodList[i+1:nbLoTask],onePossibleGroup)
			
			nbInTemp = len(temp)
			j = 0
			while j < nbInTemp:
				try:
					possibilities.index(temp[j])
				except ValueError:
					possibilities.append(temp[j])
				j += 1
			del onePossibleGroup[:]
			onePossibleGroup = init
			i += 1
	elif nbLoTask == 1:
		onePossibleGroup.append(periodList[i])
		possibilities.append(copy.deepcopy(onePossibleGroup))
		return possibilities

	return possibilities


def SeparateHiFromLoTasks(taskParameters):

	HiTaskList = []
	LoTaskList = []
	utilizationLoTask = 0.
	utilizationHiTasksInHiMode = 0.
	i = 0
	while i < len(taskParameters):
		if taskParameters[i].criticality == 1:
			LoTaskList.append(taskParameters[i])
		elif taskParameters[i].criticality == 2:
			HiTaskList.append(taskParameters[i])
		i += 1

	return [LoTaskList,HiTaskList]


def ComputeUtilizationOfTaskList(taskParametersList, criticalityLevel):
	i = 0
	nbTasks = len(taskParametersList)

	utilization = 0.

	while i < nbTasks:
		utilization += taskParametersList[i].utilization[criticalityLevel-1]
		i += 1

	return utilization

def IsTheElementPresentInTheList(element, listOfElements):
	sizeList = len(listOfElements)
	rep = False
	try:
		listOfElements.index(element)
		rep = True
	except ValueError:
		rep = False

	return rep


def ReturnTaskWithMinOrMaxElement(taskList, minOrMaxString, wantedElementStr):

	nbTasks = len(taskList)
	minOrMax = 0
	i = 0
	attribute = 0
	returnTask = Task()

	if minOrMaxString == "max" or minOrMaxString == "Max":
		while i < nbTasks:
			attribute = getattr(taskList[i],wantedElementStr)[0]
			if attribute >= minOrMax or i == 0:
				returnTask = copy.deepcopy(taskList[i])
				minOrMax = copy.deepcopy(attribute)
			i += 1	
	elif minOrMaxString == "min" or minOrMaxString == "Min":
		while i < nbTasks:
			attribute = getattr(taskList[i],wantedElementStr)[0]
			if attribute <= minOrMax or i == 0:
				returnTask = copy.deepcopy(taskList[i])	
				minOrMax = copy.deepcopy(attribute)
			i += 1	

	return returnTask


def ReturnNumberOfTasksInServer(serverList):
	nbTasks = len(serverList)
	i = 0
	j = 0
	counter = 0
	while i < nbTasks :
		sizeTask = len(serverList[i])
		j = 0
		while j < sizeTask:		
			if serverList[i][j] == "T":
				counter += 1
			j += 1
		i += 1

	return counter

def RetrievePackedServersFromARUNPacking(packingRUN):
	nbPackedServers = len(packingRUN.servers)
	aggregatedServers = []
	i = 0
	while i < nbPackedServers:
		if len(packingRUN.servers[i].name) > 2:
			aggregatedServers.append(getTaskNamesFromServers(packingRUN.servers[i].name))
		i += 1

	return aggregatedServers
	
		
def findOnePacking(taskParameters,criticality):

	print 
	print "findOnePacking"
	print 
	newTaskParameter=[]
	allPackingList = []
	packing = Packing()
	lonelyTask = 0
#	showTaskList(taskParameters)
	i = 0
	j = 0
#	taskToPack = extractAllTasksOfCriticalityGreater(taskParameters,criticality)
	taskToPack = copy.deepcopy(taskParameters)

	numberOfTasks = len(taskToPack)
	
	utilization_list = [copy.deepcopy(taskToPack[0].utilization[taskToPack[0].criticality-1])]
	packed_tasks = [ ]
	
	packed_tasks.append(copy.deepcopy(taskToPack[0]))
	current_bin = 0

	for i in range(1,numberOfTasks):
		nb_bin = len(utilization_list)
		print "nb_bin"
		print nb_bin
		showTaskList(packed_tasks)
		task_packed = False
		for j in range(0,nb_bin):
			if utilization_list[j] + taskToPack[i].utilization[taskToPack[i].criticality-1] <= 1:
				utilization_list[j] = utilization_list[j] + taskToPack[i].utilization[taskToPack[i].criticality-1]
				packed = pack(packed_tasks[j],taskToPack[i],criticality,1)
				packed_tasks[j] = copy.deepcopy(packed)
				task_packed = True
				break
		if task_packed == False:
			packed_tasks.append(copy.deepcopy(taskToPack[i]))
			utilization_list.append(copy.deepcopy(taskToPack[i].utilization[taskToPack[i].criticality-1]))


	packing = Packing()
	packing.servers = packed_tasks
	print
	showTaskList(packed_tasks)
	print
	return [packing]

def retrieve_task_name_in_modal_server(task_name_in_modal_server):
	task_name_size = len(task_name_in_modal_server)

	for i in range(0,task_name_size):
		if task_name_in_modal_server[i] == "M":
			break

	return task_name_in_modal_server[:i]
	
def Convert_task_name_list_into_task_list(task_name_list,task_parameters):

	nb_tasks = len(task_name_list)
	task_list = []
	
	for i in range(0,nb_tasks):
		task_index = retrieveIndexTaskParametersFromItsName(task_name_list[i],task_parameters)
		task_list.append(task_parameters[task_index])
		
	return task_list
	











