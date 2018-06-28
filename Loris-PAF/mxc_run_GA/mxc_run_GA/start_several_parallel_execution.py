import sys
import subprocess
import sys
import os 

target_folder = str(sys.argv[1])



if target_folder[-1] != "/":
	target_folder = target_folder+"/"

folders = os.listdir(target_folder)

nb_elements = len(folders)

i = 0

while i < nb_elements:
	path = target_folder+folders[i]
	if os.path.isdir(path) == False:
		folders.pop(i)
		nb_elements = len(folders)
		i -= 1
	i += 1


nb_folder = len(folders)

for i in range(0,nb_folder):
	path = target_folder+folders[i]
	command = ["nohup", "python", "script_process_folder_mxc_run_ga_ea_mu_plus_lambda.py",path]
	print command
	subprocess.Popen(command)

print
		


