import sys
import parameter_generating_function
import fileUtils
import utils

period_min = int(sys.argv[1])
period_max = int(sys.argv[2])
utilization_min = float(sys.argv[3])
utilization_max = float(sys.argv[4])
ratio_min = float(sys.argv[5])
ratio_max = float(sys.argv[6])
probability_hi_task = float(sys.argv[7])
utilization_limit = float(sys.argv[8])
path_to_write_task_files = str(sys.argv[9])
nb_processor = int(sys.argv[10])
period_granularity = int(sys.argv[11])

utilization_lo = 0.
utilization_hi_hi = 0.
utilization_task_set = 0.
ratio_hi_lo = 0.
utilization_lo_list = []
period_list = []
task_hi_index = []
utilization_hi_list = []

while utilization_lo < utilization_limit and utilization_hi_hi < utilization_limit:
	
	utilization_value = parameter_generating_function.generate_utilization(1,utilization_min, utilization_max)[0]
	ratio_hi_lo = parameter_generating_function.generate_ratio_utilization_hi_lo(ratio_min,ratio_max)	
	utilization_value_lo = utilization_value / ratio_hi_lo
	period_list.extend(parameter_generating_function.taskPeriodGenerator(period_min,period_max,period_granularity,1))
	print period_list[-1]
	
	if parameter_generating_function.task_hi_or_lo(probability_hi_task) == True:
		utilization_hi_list.append(min(utilization_value,utilization_limit-utilization_hi_hi))	
		utilization_hi_hi += utilization_hi_list[-1]
		utilization_lo_list.append(min(utilization_hi_list[-1],utilization_value_lo,utilization_limit-utilization_lo))
		task_hi_index.append(len(utilization_lo_list)-1)
		last_hi = True
	else:
		utilization_lo_list.append(min(utilization_value_lo,utilization_limit-utilization_lo))
		last_hi = False				
	utilization_lo += utilization_lo_list[-1]
			
fileUtils.Write_Task_Parameter(period_list, utilization_lo_list, utilization_hi_list, task_hi_index, path_to_write_task_files,nb_processor)
	
	
		

		




