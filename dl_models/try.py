import tensorflow as tf
from tensorflow.examples.tutorials.mnist import input_data
mnist = input_data.read_data_sets("/tmp/data/", one_hot = True)
epoch_x, epoch_y = mnist.train.next_batch(1)
print epoch_x.shape
print epoch_y.shape