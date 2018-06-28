import os
import sys
import subprocess
import fileUtils
import time

taskdirectory = str(sys.argv[1])
resultExperimentFilePath = taskdirectory


dictionary_output_program = {}

dictionary_output_program["NO LO tasks or HI tasks"]=0
dictionary_output_program["DBF value greater than 32768"]=0
dictionary_output_program["SBF value greater than 32768"]=0
dictionary_output_program["Modal server capacity not big enough"]=0
dictionary_output_program["Execution greater than 30 minutes"]=0
dictionary_output_program["Execution greater than 30 minutes details"]=[]
dictionary_output_program["Unknown"]=[]
dictionary_output_program["Execution Time"]=[]
dictionary_output_program["Average Execution Time"]=0.
counter = 0
average_time = 0.

for root,dirs,files in os.walk(taskdirectory):
	if type(root ) != type([]):
		root = [root]
	for root_path in root:
			resultExperimentFilePath = root_path+"result.csv"
			for files_name in files:
				if root_path[-1] == "/":
					taskFilePath = root_path+files_name	
				else:
					taskFilePath = root_path+"/"+files_name	

				os.system("date")
				output = []

				begin = 0.
				end = 0.

				try :				
					begin = time.time()
					print ["doalarm","1800","python","mxc_run_ga_ea_mu_plus_lambda.py",taskFilePath,resultExperimentFilePath]
					output = subprocess.check_output(["doalarm","1800","python","mxc_run_ga_ea_mu_plus_lambda.py",taskFilePath,resultExperimentFilePath],stderr=subprocess.STDOUT)#,stderr=sys.stdout.fileno())
					end = time.time()
				except subprocess.CalledProcessError,e:
					if e.returncode == -14:
						dictionary_output_program["Execution greater than 30 minutes"] += 1
						dictionary_output_program["Execution greater than 30 minutes details"].append([taskFilePath,e.output])
					else:
						dictionary_output_program["Unknown"].append([taskFilePath,e.output])
	
				for i in dictionary_output_program.keys():
					if i in output:
						dictionary_output_program[i] += 1

				if end - begin > 0:
					counter = counter +1
					average_time = average_time + (end - begin)
					dictionary_output_program["Execution Time"].append(end-begin)
				os.system("date")


dictionary_output_program["Average Execution Time"]= average_time / float(counter)


resultExperimentFilePath += "ditionary.txt"
dictionary_output_program["Unknown number"]=len(dictionary_output_program["Unknown"])
fileUtils.Write_dictioanry_content(dictionary_output_program,resultExperimentFilePath)

