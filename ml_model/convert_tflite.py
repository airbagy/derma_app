import tensorflow as tf

converter = tf.lite.TFLiteConverter.from_keras_model_file("keras_model/model.h5")
tflite_model = converter.convert()
open("tflite_model/model.tflite", "wb").write(tflite_model)
