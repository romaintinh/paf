import numpy 
import math
import random

def draw_number_from_uniform_distribution(interval_start=0, interval_end=1):
	number = numpy.random.uniform(interval_start, interval_end)
	return number

def generate_period(nbTask, min_period, max_period):	
	i = 0
	period = []	
	for i in range(0,nbTask):
		period.append(int(draw_number_from_uniform_distribution(min_period, max_period)))
	
	return period
	
# Generate tasks' periods in the interval [tmin;tmax] with a granularity of tgranualrity. This algorithm comes from the article Techniques For The Synthesis Of Multiprocessor Tasksets of Paul EMBERSON, Roger STAFFORD and Robert I. DAVIS.
def taskPeriodGenerator(tmin,tmax,tgranularity,nbTask):
	param = nbTask * [1.]	
	periods = nbTask *[1.]
	tgranularity = float(tgranularity)
	tmin = float(tmin)
	tmax = float(tmax)
	
	for i in range(0,nbTask):
		param[i] = draw_number_from_uniform_distribution(math.log(tmin), math.log(tmax+tgranularity))
		periods[i] = math.floor(math.exp(param[i])/tgranularity)*tgranularity

	return periods
	
def task_period_uniform_with_granularity(tmin,tmax,tgranularity,nbTask):
	
	possible_period_values = range(tmin,tmax+tgranularity,tgranularity)
	print 
	print possible_period_values
	print
	
	periods = []	
	probability_per_period_value = 1.0 / len(possible_period_values)
	
	print 
	print probability_per_period_value
	print 
	
	for i in range(0,nbTask):
		random_number = draw_number_from_uniform_distribution()
		print random_number
		print 
		j = 0
		while (j+1)*probability_per_period_value < random_number and j < len(possible_period_values)-1:
			j += 1
		periods.append(possible_period_values[j])
	print periods
	return periods

def generate_utilization(nbTask, utilization_min, utilization_max):
	i = 0
	utilization = []	
	for i in range(0,nbTask):
		utilization.append(draw_number_from_uniform_distribution(utilization_min, utilization_max))
	
	return utilization

def generate_ratio_utilization_hi_lo(ratio_min, ratio_max):
	return draw_number_from_uniform_distribution(ratio_min, ratio_max)	
	
def task_hi_or_lo(probability_hi_task):
	test = draw_number_from_uniform_distribution()
	if test >= probability_hi_task:
		return False
	else:
		return True




