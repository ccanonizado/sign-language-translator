## CNN Model

This model was produced by retraining the Inception-v3 model with a hand gestures data set.

___

### Content:

* **/python** for the training files and results solver
* **/results** for the final and raw results
* **/scripts** for the automation

___

### Retraining the Model:

If you want to make a model of your own or add gestures, create a **/data** folder in the root and make a folder for every classification. Label the folders accordingly.

After that, execute the following:

```
./scripts/retrain.sh
./scripts/optimize.sh
./scripts/quantize.sh
```

___

### Making Predictions:

Change the arguments of **./scripts/label_single_image.sh** as needed and execute the script. Make sure that lines 143, 144, 147, and 148 are commented out in **./python/label_image.py**.

___

### Calculating Results:

1. First, uncomment lines 143, 144, 147, and 148 from **./python/label_image.py**.
2. Next, execute **./scripts/label_all_images.sh**.
3. Lastly, execute **./python/solve_results.py**.

If everything runs smoothly, the *final results* can be found in the **results** folder.
