import os
import numpy 

nb_processor = [4]
probability_hi_task = [0.7]

step_utilization = 0.1
period_min = 10
period_max = 100
period_granularity = 10
task_utilization_min = 0.05
task_utilization_max = 0.9
ratio_utilization_hi_lo_min = 2
ratio_utilization_hi_lo_max = 8

path_to_write_task_files = "./manuscrit_thesis_taskset"
nb_task_set_per_configuration = 100


for h in range(0,len(nb_processor)):
	utilization_limit = numpy.arange(0.6*(h+1),nb_processor[h]+step_utilization,step_utilization)
	for i in range(0,len(utilization_limit)):
		for j in range(0,len(probability_hi_task)):
			directory_name = "Log_nbProc_"+str(nb_processor[h])+"_prob_hi_"+str(probability_hi_task[j])+"_period_"+str(period_min)+"_"+str(period_max)+"_util_"+str(task_utilization_min)+"_"+str(task_utilization_max)+"_ratio_"+str(ratio_utilization_hi_lo_min)+"_"+str(ratio_utilization_hi_lo_max)+"_log_uniform_"+str(nb_task_set_per_configuration)+"sets"
			path_to_write_task_files = "./"+directory_name
			command_create_dir = "mkdir "+ path_to_write_task_files
			os.system(command_create_dir)
			path_to_write_task_files = "./"+directory_name+"/Utilization_"+str(utilization_limit[i])
			command_create_dir = "mkdir "+ path_to_write_task_files
                        os.system(command_create_dir)
			#print command_create_dir
			for k in range(0,nb_task_set_per_configuration):
				command = "python generate_task_period_log.py "+str(period_min)+" "+str(period_max)+" "+str(task_utilization_min)+" "+str(task_utilization_max)+" "+str(ratio_utilization_hi_lo_min)+" "+str(ratio_utilization_hi_lo_max)+" "+str(probability_hi_task[j])+" "+str(utilization_limit[i])+" "+path_to_write_task_files+" "+str(nb_processor[h])+" "+str(period_granularity)
				os.system(command)			


