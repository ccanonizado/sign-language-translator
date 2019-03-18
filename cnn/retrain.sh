python retrain.py \
    --image_dir="data" \
    --how_many_training_step="500" \
    --output_graph="retrained_graph.pb" \
    --output_labels="labels.txt" \
    --final_tensor_name="final_result"