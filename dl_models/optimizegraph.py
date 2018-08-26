import sys
import tensorflow as tf
from tensorflow.python.tools import freeze_graph
from tensorflow.python.tools import optimize_for_inference_lib

MODEL_NAME = 'tfdroid'

# Freeze the graph

input_graph_path = './graphs/'+MODEL_NAME+'.pbtxt'
checkpoint_path = './graphs/'+MODEL_NAME+'.ckpt'
input_saver_def_path = ""
input_binary = False
output_node_names = "prediction"
restore_op_name = "save/restore_all"
filename_tensor_name = "save/Const:0"
output_frozen_graph_name = './graphs/frozen_'+MODEL_NAME+'.pb'
output_optimized_graph_name = './graphs/optimized_'+MODEL_NAME+'.pb'
clear_devices = True


input_graph_def = tf.GraphDef()
with tf.gfile.Open(output_frozen_graph_name, "r") as f:
    data = f.read()
    input_graph_def.ParseFromString(data)

output_graph_def = optimize_for_inference_lib.optimize_for_inference(
        input_graph_def,
        ["x"], # an array of the input node(s)
        ["prediction"], # an array of output nodes
        tf.float32.as_datatype_enum)

# Save the optimized graph

f = tf.gfile.FastGFile(output_optimized_graph_name, "w")
f.write(output_graph_def.SerializeToString())