labels=(a b c d e f g h hellohi i j k l m n no o okaygoodjob p q r s t that u v w x y yes z)

for i in ${labels[@]}
do
    for j in 76 77 78 79 80
    do
        python ../python/label_image.py \
            --graph="../pb/graph.pb" --labels="../labels.txt" \
            --output_layer="final_result" \
            --image="../test/$i/s$j.jpg" \
            --input_layer="Mul" \
            --label="$i"
    done
done