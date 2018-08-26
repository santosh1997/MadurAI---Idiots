import os
import csv
import cv2
import random

nude_files = os.listdir('nude')
nonnude_files = os.listdir('non')
with open('dict.csv', 'w') as csvFile:
	writer = csv.writer(csvFile)
	for file in nude_files:
		writer.writerow([1,0,'nude/'+file])
	for file in nonnude_files:
		writer.writerow([0,1,'non/'+file])

with open('dict.csv', 'r') as csvFile:
	li = csvFile.readlines()
	random.shuffle(li)
with open('dict.csv', 'w') as csvFile:
	csvFile.writelines(li)