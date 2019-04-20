python ../python/quantize.py \
  --input="../pb/optimized_graph.pb" \
  --output="../pb/graph.pb" \
  --output_node_names="final_result" \
  --mode="weights_rounded"