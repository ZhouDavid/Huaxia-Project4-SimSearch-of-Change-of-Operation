time_new=[x[:2]+":"+x[2:] for x in time_cleaned]        
hour_list = [t[:2] for t in time_new]
print hour_list
numbers=[x for x in xrange(0,24)]
labels=map(lambda x: str(x), numbers)
plt.xticks(numbers, labels)
plt.xlim(0,24)
pdb.set_trace()
plt.hist(hour_list)
plt.show()