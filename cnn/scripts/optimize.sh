python -m tensorflow.python.tools.optimize_for_inference \
  --input="../pb/retrained_graph.pb" \
  --output="../pb/optimized_graph.pb" \
  --input_names="Mul" \
  --output_names="final_result"