import tensorflow as tf 
import os
os.environ['KMP_DUPLICATE_LIB_OK']='True'

# sample data set of handwritten digits
mnist = tf.keras.datasets.mnist

# partition the data set
(x_train, y_train), (x_test, y_test) = mnist.load_data()

# normalize - convert to one dimension (1 vector per image)
x_train = tf.keras.utils.normalize(x_train, axis=1)
x_test = tf.keras.utils.normalize(x_test, axis=1)

model = tf.keras.models.Sequential()
model.add(tf.keras.layers.Flatten())
model.add(tf.keras.layers.Dense(128, activation=tf.nn.relu))
model.add(tf.keras.layers.Dense(128, activation=tf.nn.relu))

# softmax is to get values from 0-1
model.add(tf.keras.layers.Dense(10, activation=tf.nn.softmax))

model.compile(optimizer='adam',
            loss='sparse_categorical_crossentropy',
            metrics=['accuracy'])

model.fit(x_train, y_train, epochs=3)

# value loss and value accuracy
val_loss, val_acc = model.evaluate(x_test, y_test)
print(val_loss, val_acc)

# save a model
# model.save('num_reader.model')

# load a model
# new_model = tf.keras.models.load_model('num_reader.model')

# predict using the method and test data
predictions = model.predict([x_test])

import matplotlib.pyplot as plt 

import numpy as np 

# show the maximum probability predicted
print(np.argmax(predictions[0]))

# plot one image from the test data
plt.imshow(x_test[0])
plt.show()