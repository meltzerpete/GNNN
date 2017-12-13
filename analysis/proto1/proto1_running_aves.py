import os
import numpy as np
import matplotlib.pyplot as plt

path = "../data/proto1/"

val = 0
data = []


def running_mean(x, N):
    return np.convolve(x, np.ones((N,)) / N)[(N - 1):]


for file in os.listdir(path):
    if file == "means.csv":
        continue
    print(val)
    val += 1
    current = np.loadtxt(path + file, skiprows=1)
    smooth = running_mean(current, 100)
    data.append(smooth)

np.savetxt(path + "smooth_means.csv", np.transpose(data), delimiter=",")
