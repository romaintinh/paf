import os
import numpy 

nb_processor = [2]
probability_hi_task = [0.3]

step_utilization = 0.05
period_min = 10
period_max = 100
period_granularity = 1
task_utilization_min = 0.02
task_utilization_max = 0.7
ratio_utilization_hi_lo_min = 1
ratio_utilization_hi_lo_max = 4

path_to_write_task_files = "./"
nb_task_set_per_configuration = 500


for h in range(0,len(nb_processor)):
	utilization_limit = numpy.arange(0.6*(h+1),nb_processor[h]+step_utilization,step_utilization)
	for i in range(0,len(utilization_limit)):
		for j in range(0,len(probability_hi_task)):
			directory_name = "nbProc_"+str(nb_processor[h])+"_prob_hi_"+str(probability_hi_task[j])+"_period_min_"+str(period_min)+"_max_"+str(period_max)+"_utilization_min_"+str(task_utilization_min)+"_max_"+str(task_utilization_max)+"_ratio_min_"+str(ratio_utilization_hi_lo_min)+"_max_"+str(ratio_utilization_hi_lo_max)+"_uniform_with_granularity_distribution"+str(nb_task_set_per_configuration)+"_taskset"
			path_to_write_task_files = "./"+directory_name
			command_create_dir = "mkdir "+ path_to_write_task_files
			os.system(command_create_dir)
			path_to_write_task_files = "./"+directory_name+"/Utilization_"+str(utilization_limit[i])
			command_create_dir = "mkdir "+ path_to_write_task_files
                        os.system(command_create_dir)
			#print command_create_dir
			for k in range(0,nb_task_set_per_configuration):
				command = "python generate_task_period_uniform_with_granularity.py "+str(period_min)+" "+str(period_max)+" "+str(task_utilization_min)+" "+str(task_utilization_max)+" "+str(ratio_utilization_hi_lo_min)+" "+str(ratio_utilization_hi_lo_max)+" "+str(probability_hi_task[j])+" "+str(utilization_limit[i])+" "+path_to_write_task_files+" "+str(nb_processor[h])+" "+str(period_granularity)
				os.system(command)			


