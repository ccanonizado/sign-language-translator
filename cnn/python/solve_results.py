import math

# NOTE - check results_raw.txt for the file structure

f1 = open("../results/results_raw.txt", "r")
f2 = open("../results/results_final.txt", "w")

labels = ["a", "b", "c", "d", "e", "f", "g", "h", "hellohi", "i", "j", "k", "l", "m", "n", "no", "o", "okaygoodjob", "p", "q", "r", "s", "t", "that", "u", "v", "w", "x", "y", "yes", "z"]

# number of labels
for i in range(0,len(labels)):

  f2.write("{}:\n".format(labels[i]))

  average = 0
  top_1_count = 0
  top_5_count = 0
  wrong_predictions = {}

  # number of test cases
  for j in range(0,5):

    label = f1.readline().split("\n")[0]
    
    # number of lines per test case
    for k in range(0,5):

      line = f1.readline().split(" ")

      # check if the correct label was predicted
      if label == line[0]:
        if k == 0:
          top_1_count += 1
        top_5_count += 1
        
        # the value contains a newline - split again
        average += float(line[1].split("\n")[0])

      # if the prediction was wrong - add to dictionary
      elif label != line[0] and k == 0:      
        if line[0] in wrong_predictions:
          wrong_predictions[line[0]] += 1
        else:
          wrong_predictions[line[0]] = 1

  # compute predictions
  if average != 0:
    average = (average / 5) * 100

  if top_1_count != 0:
    top_1_count = (top_1_count / 5) * 100
  
  if top_5_count != 0:
    top_5_count = (top_5_count / 5) * 100

  # print results
  f2.write("Confidence: {}\n".format(format(average, '.4f')))
  f2.write("Top-1-Accuracy: {}\n".format(top_1_count))
  f2.write("Top-5-Accuracy: {}\n".format(top_5_count))
  f2.write("Wrong Predictions: \n")
  
  wrong_string = ""
  for key, value in wrong_predictions.items():
    wrong_string += "     {}: {}\n".format(key,value)

  f2.write(wrong_string)

  f2.write("==================================================\n")
    
f1.close()
f2.close()