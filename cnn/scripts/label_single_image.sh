python ../python/label_image.py \
  --graph="../pb/graph.pb" --labels="../labels.txt" \
  --output_layer="final_result" \
  --image="../test/m/s79.jpg" \
  --input_layer="Mul"