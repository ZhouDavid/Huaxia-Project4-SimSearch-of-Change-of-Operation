#-*-coding:utf-8-*-
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import os
import math
result_path = 'D:\人智实验室项目\华夏银行项目\华夏银行运维变更合规性查验项目\project\\result\\'
result_path = result_path.decode('utf-8').encode('gbk')
resultFileNames = os.listdir(result_path)
i = 0
for file in resultFileNames:
	file = result_path+file
	tmp = pd.read_csv(file)
	if i == 0:
		data = tmp
		i=i+1
		continue
	data = data.append(tmp,ignore_index = True)
	i=i+1
mmax = data['rcid'].max()
x = data.iloc[:,4]
# y = data.groupby('doc_name').count()
# dfreq = y.iloc[:,1]
# col_num = dfreq.count()
y = data.loc[:,'doc_name']
y = y.groupby(y.values).count()
y = pd.Series(y.values)
# plt.figure(1)
# n, bins, patches = plt.hist(x,mmax+1,facecolor='green')	
# plt.xlabel('Context')
# plt.ylabel('Freq')
# plt.title('Histogram of Contex Frequency')
# plt.axis([0, mmax+1, 0, 50])
# plt.grid(True)

# plt.figure(2)
# print docs

plt.bar(y.index,y.values,facecolor = 'g')
plt.xlabel('Context')
plt.ylabel('Freq')
plt.title('Histogram of Contex Frequency')
# plt.axis([0, 10, 0, 50])
plt.grid(True)
plt.show()
