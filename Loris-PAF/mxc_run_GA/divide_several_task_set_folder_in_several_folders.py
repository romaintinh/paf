import os
import sys



folder_task = str(sys.argv[1])
number_of_directories = int(sys.argv[2])

if folder_task[-1] != "/":
	string = "./"+folder_task
	folder_task = folder_task+"/"
else:
	string = "./"+folder_task

all_folder_content = os.listdir(string)
folder_tasks = []

nb_files = len(all_folder_content)

for i in range(0,nb_files):
	path = folder_task+all_folder_content[i]
	print 
	print path
	if "Utilization" in all_folder_content[i] and os.path.isdir(path) == True:
		folder_tasks.append(all_folder_content[i])


nb_files = len(folder_tasks)
print 
print folder_tasks
print nb_files
print

number_of_task_per_folders = nb_files / number_of_directories

for i in range(0,number_of_directories):
	string = "mkdir "+folder_task+"Lot"+str(i)
	print string
	print
	os.system(string)
	for j in range(0,number_of_task_per_folders):
		file_name = folder_tasks.pop(0)
		file_name = folder_task+file_name
		string = "cp -r "+file_name+" "+folder_task+"Lot"+str(i)
		print string
		os.system(string)
