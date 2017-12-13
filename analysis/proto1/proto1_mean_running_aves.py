import matplotlib as mpl
import numpy as np
import matplotlib.pyplot as plt

mpl.rcParams['text.usetex'] = True

path = "../data/proto1/"
file = "smooth_means.csv"

val = 0

data = np.loadtxt(path + "smooth_means.csv", delimiter=",")

ave = np.mean(data, 1)

plt.figure(1)
plt1 = plt.subplot()

plt.title("Running mean (100 window) over 100 trials.")
plt1.set_ylabel("Mean error")
plt1.set_xlabel("Trial")

plt1.plot(ave[:99900])

plt.savefig('proto1-running-mean.pdf')
plt.show()
