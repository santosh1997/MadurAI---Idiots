import tensorflow as tf
import numpy as np
import csv
import cv2
x_data=[]
y_data=[]
with open('dict.csv', 'rU') as f:
    reader = csv.reader(f)
    for row in reader:
        y_data.append(np.array([int(row[0]),int(row[1])]))
        img=cv2.imread(row[2])
        x_data.append(np.resize(np.dot(img[...,:3], [0.299, 0.587, 0.114]), (4096)))
x_data=np.array(x_data)
y_data=np.array(y_data)

n_classes = 2
batch_size = 90

x = tf.placeholder('float', [None, 4096])
x = tf.identity(x, name="x")
y = tf.placeholder('float')

keep_rate = 0.8
keep_prob = tf.placeholder(tf.float32)

def conv2d(x, W):
    return tf.nn.conv2d(x, W, strides=[1,1,1,1], padding='SAME')

def maxpool2d(x):
    #                        size of window         movement of window
    return tf.nn.max_pool(x, ksize=[1,2,2,1], strides=[1,2,2,1], padding='SAME')



def convolutional_neural_network(x):
    weights = {'W_conv0':tf.Variable(tf.random_normal([6,6,1,32])),
               'W_conv1':tf.Variable(tf.random_normal([6,6,32,64])),
               'W_conv2':tf.Variable(tf.random_normal([1,6,64,128])),
               'W_fc':tf.Variable(tf.random_normal([8*8*128,8192])),
               'W_fc1':tf.Variable(tf.random_normal([8192,1024])),
               'out':tf.Variable(tf.random_normal([1024, n_classes]))}

    biases = {'b_conv0':tf.Variable(tf.random_normal([32])),
              'b_conv1':tf.Variable(tf.random_normal([64])),
              'b_conv2':tf.Variable(tf.random_normal([128])),
              'b_fc':tf.Variable(tf.random_normal([8192])),
              'b_fc1':tf.Variable(tf.random_normal([1024])),
              'out':tf.Variable(tf.random_normal([n_classes]))}

    x = tf.reshape(x, shape=[-1, 64, 64, 1])

    conv0 = tf.nn.relu(conv2d(x, weights['W_conv0']) + biases['b_conv0'])
    conv0 = maxpool2d(conv0)

    conv1 = tf.nn.relu(conv2d(conv0, weights['W_conv1']) + biases['b_conv1'])
    conv1 = maxpool2d(conv1)
    
    conv2 = tf.nn.relu(conv2d(conv1, weights['W_conv2']) + biases['b_conv2'])
    conv2 = maxpool2d(conv2)

    fc = tf.reshape(conv2,[-1, 8*8*128])
    fc = tf.nn.relu(tf.matmul(fc, weights['W_fc'])+biases['b_fc'])
    fc = tf.nn.relu(tf.matmul(fc, weights['W_fc1'])+biases['b_fc1'])
    fc = tf.nn.dropout(fc, keep_rate)

    output = tf.matmul(fc, weights['out'])+biases['out']

    return output

def train_neural_network(x):
    prediction = convolutional_neural_network(x)
    prediction = tf.identity(prediction, name="prediction")
    cost = tf.reduce_mean( tf.nn.softmax_cross_entropy_with_logits(logits=prediction, labels=y) )
    optimizer = tf.train.AdamOptimizer().minimize(cost)
    
    hm_epochs = 11
    saver = tf.train.Saver()
    with tf.Session() as sess:
        sess.run(tf.initialize_all_variables())

        for epoch in range(hm_epochs):
            epoch_loss = 0
            for i in range(2):
                epoch_x = x_data[(i*128):(i*128)+128] 
                epoch_y = y_data[(i*128):(i*128)+128] 
                _, c = sess.run([optimizer, cost], feed_dict={x: epoch_x, y: epoch_y})
                epoch_loss += c

            print('Epoch', epoch, 'completed out of',hm_epochs,'loss:',epoch_loss)

        correct = tf.equal(tf.argmax(prediction, 1), tf.argmax(y, 1))
        accuracy = tf.reduce_mean(tf.cast(correct, 'float'))
        print('Accuracy:',accuracy.eval({x:x_data, y:y_data}))
        tf.train.write_graph(sess.graph_def, '.', 'graphs/tfdroid.pbtxt')  
        saver.save(sess, 'graphs/tfdroid.ckpt')
train_neural_network(x)