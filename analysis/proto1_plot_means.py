import matplotlib as mpl
import matplotlib.pyplot as plt
import numpy as np

path = "../data/proto1/"
file = "means.csv"

means = np.loadtxt(path + file)
print(means)

plt.subplot()

plt.plot(means)
plt.show()
