import os

path = 'non'
files = os.listdir(path)
i = 1

for file in files:
    os.rename(os.path.join(path, file), os.path.join(path,'resized'+str(i)+'.jpg'))
    i = i+1