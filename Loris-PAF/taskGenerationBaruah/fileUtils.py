import math
import datetime

# Write the generated task parameters into a file.
def Write_Task_Parameter(period,utilization_lo, utilization_hi, hi_task_index, path_to_write_task_files,nb_processor):
	now = datetime.datetime.now()
	if path_to_write_task_files[-1] == "/":
		filepath = path_to_write_task_files+"taskParameters_processor_"+str(nb_processor)+"_"+str(now.day)+str(now.hour)+str(now.minute)+str(now.second)+str(now.microsecond)+".txt"
	else:	
		filepath = path_to_write_task_files+"/taskParameters_processor_"+str(nb_processor)+"_"+str(now.day)+str(now.hour)+str(now.minute)+str(now.second)+str(now.microsecond)+".txt"

	fichier = open(filepath,"a")
	string =""
	i = 0
	j = 0
	nb_task = len(period)
	i = 0
	task_index = [i for i in range(0,nb_task)]

	for i in range(0,len(hi_task_index)):
		string += "T"+str(i)+"\t"+str(int(period[hi_task_index[i]]))+"\t"+str(int(period[hi_task_index[i]]))+"\t"+str(2)+"\t"+str(utilization_lo[hi_task_index[i]]*period[hi_task_index[i]])+"\t"+str(utilization_hi[i]*period[hi_task_index[i]])+"\n"
		task_index.remove(hi_task_index[i])
		
	for j in range(0,len(task_index)):
		i += 1
		string += "T"+str(i)+"\t"+str(int(period[task_index[j]]))+"\t"+str(int(period[task_index[j]]))+"\t"+str(1)+"\t"+str(utilization_lo[task_index[j]]*period[task_index[j]])+"\n"
		
	string += "\n"
	fichier.write(string)

	fichier.close()

	return 0
