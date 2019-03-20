python -m tensorflow.python.tools.optimize_for_inference \
  --input="retrained_graph.pb" \
  --output="optimized_graph.pb" \
  --input_names="Mul" \
  --output_names="final_result"