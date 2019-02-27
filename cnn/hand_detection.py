# reference - https://github.com/lzane/Fingers-Detection-using-OpenCV-and-Python

import cv2
import numpy as np

# from default camera
capture = cv2.VideoCapture(0)

# necessary variables
bgSubThreshold = 50
bgModel = cv2.createBackgroundSubtractorMOG2(0, bgSubThreshold)
blurValue = 41
cap_region_x_begin=0.6  # start point/total width
cap_region_y_end=0.6  # start point/total width

detectedBG = False

def removeBG(frame):
    fgmask = bgModel.apply(frame, learningRate=0)
    
    kernel = np.ones((3, 3), np.uint8)
    fgmask = cv2.erode(fgmask, kernel, iterations=1)
    res = cv2.bitwise_and(frame, frame, mask=fgmask)
    return res

while True:
    ret, frame = capture.read()

    frame = cv2.bilateralFilter(frame, 5, 50, 100)  # smoothing filter
    frame = cv2.flip(frame, 1) # flip frame for better orientation

    cv2.rectangle(frame, (int(cap_region_x_begin * frame.shape[1]), 0),
                 (frame.shape[1], int(cap_region_y_end * frame.shape[0])), (255, 0, 0), 2)
    
    # after 'c' is pressed - remove background and detect the hand
    if detectedBG:
        foreground = removeBG(frame)
        foreground = foreground[0:int(cap_region_y_end * frame.shape[0]),
                        int(cap_region_x_begin * frame.shape[1]):frame.shape[1]]  # clip the ROI

        # convert the foreground into binary image
        gray = cv2.cvtColor(foreground, cv2.COLOR_BGR2GRAY)
        blur = cv2.GaussianBlur(gray, (blurValue, blurValue), 0)
        ret, binary = cv2.threshold(blur, 100, 255, cv2.THRESH_BINARY)

        # find the biggeset contour or the hand
        _,contours, hierarchy = cv2.findContours(binary, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
        length = len(contours)
        maxArea = -1

        if length > 0:
            for i in range(length):
                temp = contours[i]
                area = cv2.contourArea(temp)
                if area > maxArea:
                    maxArea = area
                    c = i

            res = contours[c]
            # fill the hand only
            hand = np.zeros(binary.shape, np.uint8)
            cv2.fillPoly(hand, [res], (255,255,255))
        
        cv2.imshow('Hand', hand)
            
    cv2.imshow('Full', frame)
    
    k = cv2.waitKey(10)
    if k == ord('q'):
        break
    elif k == ord('c'):
        detectedBG = True

capture.release()
cv2.destroyAllWindows()