## white box project

### 1.시스템

* 시스템 구성도
![system](https://user-images.githubusercontent.com/60215726/74607390-f4212080-511b-11ea-9390-ccc2a26a0772.PNG)
프로젝트 시스템 구성도로는 스쿨버스에 카메라 역할을 하는 라즈베리파이 제로kit에 카메라를 장착한 기기 2대와 
좌석 매핑에 대한 좌표를 기억하여 영상처리을 이용한 ROI, 좌석 비교 등등의 서버담을 하는 라즈베리파이 B3 1대,
스트리밍 및 좌석매핑, 아이의 탐지를 확인하는 역할인 테블릿 1대를 이용하였습니다.

* 시스템 흐름도 
![Flowchart](https://user-images.githubusercontent.com/60215726/74607680-fab09780-511d-11ea-953e-719e34f5ac82.PNG)

### 2. 서버
![image](https://user-images.githubusercontent.com/60215726/74607811-5c253600-511f-11ea-82f4-414d0e1cf34e.png)
2)ROI(Region of Interest)
ROI는 원본 이미지에서 관심영역을 추출할 수 있도록 해주는 영상처리 기법입니다.
```python
def startROI():
	#서버에서 사용된 코드소스입니다.
	i=0
	img_start=cv2.imread('/home/pi/Desktop/whitebox/startimage.jpg')
	while i<=point.count:
		now='Start_ROI'+str(i)
		subimg_start = img_start[y1_list[i]:y2_list[i], x1_list[i]:x2_list[i]]
		#위의 형태로 img_start[y1_list[i]:y2_list[i], x1_list[i]:x2_list[i]]
		#이 부분에서 ROI를 해주는 것입니다.[처음 y좌표 : 마지막 y좌표, 처음 x좌표:마지막x좌표]로 
		#표시하였고 현재 프로젝트에서는 여러개의 ROI가 필요하여 list형식인 변수로 넣었습니다.
		cv2.imwrite(os.path.join(path,str(now)+'.PNG'),subimg_start)
		#cv2.imshow('start','/home/Start_ROI1.PNG')
		i+=1
```
![hoRO](https://user-images.githubusercontent.com/60215726/74665699-88a28600-51e3-11ea-83bf-0a5b51dccb27.PNG)

### 3.Histogram
프로젝트에서는 아이가 탐지가 되었는지를 구별하기 위해 사용하였습니다.
처음 이미지와 비교할 이미지의 각각의 calcHist 명령어를 통해 히스토그램을 계산을 하고 CompareHist 명령어를 통하여 비교한 수치 값과 기준점을 비교하여 아이가 탐지 여부를 파악하였습니다.
![비교](https://user-images.githubusercontent.com/60215726/74666635-35313780-51e5-11ea-8a3b-9bdd5bf3110b.PNG)
두 이미지의 히스토그램의 수치를 도표로 나타낸 것이며, 아래 이미지는 프로젝트에서 사용한  cv2.HISTCMP_CORREL(상관) 입니다.
![co](https://user-images.githubusercontent.com/60215726/74666819-8d683980-51e5-11ea-9c94-63ba4956522c.PNG)
```python
def hist_compare(d_count):
	#ROI한 이미지들에서 각각 매핑한 좌석들 중
	#처음 이미지와 비교할 이미지에서 매핑한 좌석만 ROI된 이미지들을 통해
	#두가지로 분류가 된 후 각각 좌표가 동일한 이미지끼리 비교를 하는 부분입니다.
	#즉 Start_ROI0 과 EndROI0이 비교가 되며
	#총 8개의 좌석을 매핑을 했다면 16개의 이미지가 각각 두 개씩 비교가 됩니다.
	#비교가 된 후  cv2.compareHist(H1,H2,cv2.HISTCMP_CORREL)을 통해서
	#값이 나오는데 이 값들은 소수점이 나옵니다.
	#참고 : cv2.compareHist(H1,H2,cv2.HISTCMP_CORREL)에서 나온 값이
	#1에 가까울 수록 유사도가 높은 것이며 1이면 같은 이미지라고 보면 될 것 같습니다.
	#여기서는 값에 10을 곱해 음수부터 10까지에서 6이라는 기준을 잡고
	# 6보다 크면 사람이 없는 것으로 그 이하이면 아이나 사람이 탐지되는 것을
	#구별하였습니다.
	i=0
	msg=''
	while i<=d_count:
		pts1=cv2.imread('/home/pi/Desktop/whitebox/Start_ROI'+str(i)+'.PNG')
		pts2=cv2.imread('/home/pi/Desktop/whitebox/End_ROI'+str(i)+'.PNG')
		H1=cv2.calcHist(images=[pts1],channels=[0],mask=None, histSize=[256],ranges=[0,256])
		cv2.normalize(H1,H1,1,0,cv2.NORM_L1)
		H2=cv2.calcHist(images=[pts2],channels=[0],mask=None,histSize=[256],ranges=[0,256])
		cv2.normalize(H2,H2,1,0,cv2.NORM_L1)
		#현재 아래 부분에서 히스토그램 비교를 하는 부분으로 4가지 비교 중 상관비교를 이용하였습니다.
		d1 = cv2.compareHist(H1,H2,cv2.HISTCMP_CORREL)
		print('df(H1,H2,CORREL)=',d1)
		d1=d1*10
		print(d1)
		print(str(i)+':ROI compare')
		#아래 부분에서 기준점을 나누어 탐지 여부를 정하였습니다.
		if d1>6:
			print('no Human')#
		elif d1<=6:
			print('detech')
			cnum()
		i+=1
	if cnum.count>0:
		print('cnum='+str(cnum.count))
		msg='detech'
		cnum.count=0
	else:
		msg='no people'
	return msg
``` 
[참조 OpenCV:histogram](https://docs.opencv.org/3.4/d6/dc7/group__imgproc__hist.html)
