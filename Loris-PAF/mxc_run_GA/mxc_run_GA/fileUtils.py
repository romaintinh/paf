import os
import sys
import copy
import packingUtils
import subprocess
import schedulabilityFunctions
import mxc_run_ga_utils

# extract from a given file the timing parameters of the tasks and derive their utilizations hi and low and the number of criticality levels into a list 
def ExtractTaskParameter(nameFile):
	fichier = open(nameFile,"r")
	content = fichier.read()

	i = 0
	size=len(content)

	maxCriticality = 0

	# Class timing parameters on a task basis
	parametersTaskGrouping = []
	currentTask = packingUtils.Task()

	while i<size:
		j=i
		while content[j]!=" " and content[j]!="\t" and content[j]!="\n":
			if j+1<size:
				j+=1

		taskName = str(content[i:j])
		currentTask.name = taskName
		j += 1		
		i=j
		while content[j]!=" " and content[j]!="\t" and content[j]!="\n":
                        if j+1<size:
                                j+=1

		period=int(content[i:j])
		currentTask.period.append(copy.deepcopy(period))
                j += 1
		i=j
                while content[j]!=" " and content[j]!="\t" and content[j]!="\n":
                        if j+1<size:
                                j+=1


		deadline=int(content[i:j])
		currentTask.deadline.append(copy.deepcopy(deadline))
                j += 1
		i=j
                while content[j]!=" " and content[j]!="\t" and content[j]!="\n":
                        if j+1<size:
                                j+=1


		criticality=int(content[i:j])
		currentTask.criticality = criticality
		if int(criticality) > maxCriticality:
			maxCriticality = int(criticality)
                j += 1
		i=j
		k = 0
		while k < int(criticality):
		        while content[j]!=" " and content[j]!="\t" and content[j]!="\n":
		                if j+1<size:
		                        j+=1
			wcet=float(content[i:j])
			currentTask.utilization.append((float(wcet)/float(deadline)))
			currentTask.wcet.append(copy.deepcopy(wcet))
		        j += 1
			i=j
			k += 1 

                while content[j]==" " or content[j]=="\t" or content[j]=="\n":
                        if j+1<size:
                                j+=1
			else:
				break				
		i=j

		parametersTaskGrouping.append(copy.deepcopy(currentTask))
		currentTask = packingUtils.Task()
		if i+1 >= size:
			break

	fichier.close()
	return maxCriticality,parametersTaskGrouping



def RetrieveFileNameFromItsPath(filePath):
	i = len(filePath)-1
	fileName = ""
	while i >= 0 and filePath[i]!= "/":
		fileName += filePath[i]
		i -= 1
	return fileName[:3:-1]
	
# Check if the server's name is already written in the file containing all the possible packing
def StringExist(tasks,nameFile):
	taskList=[]
	nbTrue = 0
	sizetaskNames = len(tasks)
	
	try:
		fichier = open(nameFile,"r")
		content = fichier.read()
		i = 0
		j = 0
		k = 0
		sizeFile = len(content)

		while i < sizeFile:
			while content[i] != "\t":
				i += 1
			# retrieval of one of the previously found packed servers
			i += 1
			j = i
			while content[j]!="\n":
				while content[j] != " ":
					j+= 1	
				taskList.append(content[i:j])
				while content[j] != "\t":
					j+= 1	
				j+=1
				i =j
			k = 0
			# checking if the the new packing corresponds to the retrieve one
			while k < sizetaskNames:				
				for task in taskList:
					if tasks[k].name == task:
						nbTrue += 1
						break
				k += 1
			if nbTrue == sizetaskNames:
				return True
			else:	
				j += 1
				i = j
				nbTrue = 0
				taskList=[]

		fichier.close()
		return False
	except IOError:
		return False		
	

# Write the packed servers' parameters in the file 
def WriteFilePackedServer(taskParameters,criticality,nameFile):
	numberOfElements = len(taskParameters)
	i=0
	if StringExist(taskParameters,nameFile) == True:
		return False	
		
	fichier = open(nameFile,"a")
	string = str(numberOfElements)+"\t"
	fichier.write(string)

	while i < numberOfElements:	
		string = taskParameters[i].name+" ("+str(taskParameters[i].utilization[criticality-1])+") \t"
		fichier.write(string)
		i += 1
	fichier.write("\n")

	fichier.close()

	return nameFile

# Write the task set with the proper modal servers
def WriteTaskSetAndModalServers(pathFileName,task_parameters,lo_task_list,hi_task_list,final_packing,modal_servers_task_list,allocated_tasks_list):

	string_to_write = ""
	fichier = open(pathFileName,"w")
	nb_aggregate_modal_server = len(modal_servers_task_list)
	nb_primal_servers = len(final_packing[0].servers)
	for i in range(0,nb_primal_servers):
		tasks_in_primal_server = packingUtils.getTaskNamesFromServers(final_packing[0].servers[i].name)
		nb_tasks_in_primal_servers = len(tasks_in_primal_server)
		for j in range(0,nb_tasks_in_primal_servers):
			task_index = packingUtils.retrieveIndexTaskParametersFromItsName(tasks_in_primal_server[j],task_parameters)
			if task_parameters[task_index].criticality == 2:
					string_to_write +=  "S"+str(i)+"\t"+task_parameters[task_index].name+"\t"+str(task_parameters[task_index].period[0])+"\t"+str(task_parameters[task_index].period[0])+"\t"+str(task_parameters[task_index].criticality)+"\t"+str(task_parameters[task_index].wcet[0])+"\t"+str(task_parameters[task_index].wcet[1])+"\n"
					for k in range(0,nb_aggregate_modal_server):
						nb_modal_servers_in_aggregate = len(modal_servers_task_list[k])
						if task_parameters[task_index] in modal_servers_task_list[k]:
							nb_allocated_tasks = len(allocated_tasks_list[k])
							for l in range(0,nb_allocated_tasks):
								string_to_write +=  "S"+str(i)+"\t"+allocated_tasks_list[k][l].name+"MS"+str(task_index)+"\t"+str(allocated_tasks_list[k][l].period[0])+"\t"+str(allocated_tasks_list[k][l].period[0])+"\t"+str(allocated_tasks_list[k][l].criticality)+"\t"+str(allocated_tasks_list[k][l].wcet[0])+"\n"
							break							
			elif task_parameters[task_index].criticality == 1:
					string_to_write +=  "S"+str(i)+"\t"+task_parameters[task_index].name+"\t"+str(task_parameters[task_index].period[0])+"\t"+str(task_parameters[task_index].period[0])+"\t"+str(task_parameters[task_index].criticality)+"\t"+str(task_parameters[task_index].wcet[0])+"\n"

	fichier.write(string_to_write)

	fichier.close()
	

def WriteExperimentsValueInFile(filePath,nbTasks,Ulimit,utilizationHiHi,utilizationLoLo,overallUtilizationMCSystem, filePathTaskSet,filePathBestAllocation,nbTaskHi,utilizationHiMinusLoHi):

	stringToWrite = ""

	if os.path.isfile(filePath) == False:
		stringToWrite = "File task set path, File best allocation path, nb of tasks, Umltc, UHiHi, ULoLo, Umxcrun, UHi-LoHi, nb of Hi tasks\n"
	fichier =  open(filePath,"a")

	stringToWrite += filePathTaskSet+","+filePathBestAllocation+","+str(nbTasks)+","+str(Ulimit)+","+str(utilizationHiHi)+","+str(utilizationLoLo)+","+str(overallUtilizationMCSystem)+","+str(utilizationHiMinusLoHi)+","+str(nbTaskHi)+"\n"

	fichier.write(stringToWrite)

	fichier.close()


def RetrieveExperimentsSettings(filePathTaskSet):
	sizePath = len(filePathTaskSet)
	i = 0
	
	while filePathTaskSet[i] != "/":
		i += 1

	j = i+1
	i = j

	while filePathTaskSet[j] != "/":
		j += 1


	experimentSettings = RetrieveFileNameInItsPath(filePathTaskSet)

	j = len(experimentSettings)-1
	while j > 0 and experimentSettings[j] != "S":
		j -= 1

	print experimentSettings

	filePathExperimentFile = str(experimentSettings[:j])

	return filePathExperimentFile
	

def RetrieveFileNameInItsPath(filePath):
	sizeFilePath = len(filePath)
	i = sizeFilePath-1
	while i > 0 and filePath[i] != "/":
		i -= 1

	return filePath[i+1:sizeFilePath]


def Write_dictioanry_content(dictionary,filePath):

        fichier =  open(filePath,"a")
	string = ""
	for i in dictionary.keys():		
		string += str(i)+" "+str(dictionary[i])+"\n"

	fichier.write(string)
        fichier.close()
        
def Write_file_no_results(filePath):

	filePath_no_result = filePath + "_no_proper_result.txt"

	fichier =  open(filePath_no_result,"a")

	stringToWrite = filePath+"\n"

	fichier.write(stringToWrite)

	fichier.close()
	
def Retrieve_directory_hierarchy(file_path):

	print "Retrieve_directory_hierarchy"

	size = len(file_path)
	
	for i in range(size-1,0,-1):
		if file_path[i] == "/":
			break
	
	print file_path[:i+1]
	return file_path[:i+1]
	
def Write_best_ind_generation(task_file_path,individual,file_path,task_parameters,average_time_for_each_generation,init_pop_time):

	string = ""
	if os.path.isfile(file_path) == False:
		string += "task file path,nb of modal servers, nb of Lo tasks,total nb of possible allocation in simple modal servers, generation of best individual,average time processing for a generation, time of generation initial population\n"

	fichier = open(file_path,"a")
	
	nb_modal_servers = len(individual)
	nb_lo_tasks = len(individual[0])
		
	nb_of_allocation_possible_in_simple_modal_servers = 0
	for i in range(0,len(task_parameters)):
		nb_of_allocation_possible_in_simple_modal_servers += len(task_parameters[i].schedulable_with_utilization)
		nb_of_allocation_possible_in_simple_modal_servers += len(task_parameters[i].schedulable_with_sbf)
	
	string += task_file_path+","+str(nb_modal_servers)+","+str(nb_lo_tasks)+","+str(nb_of_allocation_possible_in_simple_modal_servers)+","+str(individual.generation_of_creation)+","+str(average_time_for_each_generation)+","+str(init_pop_time)+"\n"
	
	fichier.write(string)
	
	fichier.close()

	


		

