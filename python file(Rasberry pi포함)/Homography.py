import numpy as np
import cv2

point_list = []
count = 0
x1_list=[]
y1_list=[]


def mouse_callback(event, x, y, flags, param):
    global point_list, count, img_original

    # 마우스 왼쪽 버튼 누를 때마다 좌표를 리스트에 저장
    if event == cv2. EVENT_LBUTTONDOWN:
        print("(%d, %d)"% (x,y))
        point_list.append((x,y))
        cv2.circle(img_original, (x,y), 3, (0,0,255), -1)

cv2.namedWindow('original')
cv2.setMouseCallback('original', mouse_callback)

#원본 이미지
img_original = cv2.imread('inner.png')

while(True):

    cv2.imshow("original", img_original)

    height, weight = img_original.shape[:2]

    if cv2.waitKey(1)%0XFF == 32: #spacebar를 누르면 루프에서 빠져나오기
        break

#좌표 순서 - 상단왼쪽 끝, 상단오른쪽 끝, 하단왼쪽 끝, 하단오른쪽 끝
pts1 = np.float32([list(point_list[0]),list(point_list[1]),list(point_list[2]),list(point_list[3])])
pts2 = np.float32([[0,0],[weight,0],[0,height],[weight,height]])

M = cv2.getPerspectiveTransform(pts1,pts2)

img_result = cv2.warpPerspective(img_original, M, (weight,height))
cv2.imshow("img_HOMOgraph", img_result)


mouse_is_pressing = False
start_x, start_y = -1, -1

def mouse_callback(event,x,y,flags,param):
    global start_x, start_y, mouse_is_pressing
    print(x,y)
    if event == cv2.EVENT_LBUTTONDOWN:
        mouse_is_pressing = True
        start_x, start_y = x,y
    elif event == cv2.EVENT_LBUTTONUP:
        mouse_is_pressing = False
        img_cat = img_result[ start_y:y, start_x:x]
        cv2.imshow("img_ROI", img_cat)
img_ROI = img_original[y1_list[0]:y1_list[3],x1_list[0]:x1_list[3]]
cv2.imshow("img_ROI",img_ROI)
cv2.imshow("img_HOMOgraph", img_result)
cv2.setMouseCallback('img_HOMOgraph',mouse_callback)
cv2.waitKey(0)
cv2.destroyWindow()
