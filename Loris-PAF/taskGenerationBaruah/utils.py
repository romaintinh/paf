

def sum_list_items(list_with_number):

	if type(list_with_number[0]) == type(1):
		result = 0
	elif type(list_with_number[0]) == type(1.):
		result = 0.
	else:
		return "FAILED"
	
	for i in range(len(list_with_number)):
		result += list_with_number[i]
		
	return result

