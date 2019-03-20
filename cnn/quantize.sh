python quantize.py \
  --input="optimized_graph.pb" \
  --output="rounded_graph.pb" \
  --output_node_names="final_result" \
  --mode="weights_rounded"