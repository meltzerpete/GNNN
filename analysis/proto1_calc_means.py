import os
import numpy as np

path = "../data/proto1/"

data = []
val = 0

for file in os.listdir(path):
    print(val)
    val += 1
    current = np.loadtxt(path + file, skiprows=1)
    # print(current)
    data.append(current)

means = np.mean(data, 0)

np.savetxt(path + "means.csv", means, delimiter=",")


