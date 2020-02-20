## white box project

### 1.시스템

* 시스템 구성도
![system](https://user-images.githubusercontent.com/60215726/74607390-f4212080-511b-11ea-9390-ccc2a26a0772.PNG)
프로젝트 시스템 구성도로는 스쿨버스에 카메라 역할을 하는 라즈베리파이 제로kit에 카메라를 장착한 기기 2대와 
좌석 매핑에 대한 좌표를 기억하여 영상처리을 이용한 ROI, 좌석 비교 등등의 서버담을 하는 라즈베리파이 B3 1대,
스트리밍 및 좌석매핑, 아이의 탐지를 확인하는 역할인 테블릿 1대를 이용하였습니다.

* 시스템 흐름도 
![Flowchart](https://user-images.githubusercontent.com/60215726/74607680-fab09780-511d-11ea-953e-719e34f5ac82.PNG)

### 2. 서버(Raspberry pi B3:python)
![image](https://user-images.githubusercontent.com/60215726/74607811-5c253600-511f-11ea-82f4-414d0e1cf34e.png)

#### 1)Homography

Homography 기법을 사용한 이유는 3D의 이미지를 2D처럼 평면화를 해주기 위해 사용하였습니다.
카메라에서 옆으로 찍히는 좌석들 경우 대각선으로 찍히게 되는 데 성인의 경우에는 좌석에서 차지하는 비율이 많아지고 아이의 경우에는 좌석에서 차지하는 비율이 적어지게 되며 대각선으로 찍히게 되면 그 비율마저 더 작아지게 되므로 이미지 비교 시에 더 확실한 탐지를 위하여 사용하였습니다.
![ho](https://user-images.githubusercontent.com/60215726/74673767-408b5f80-51f3-11ea-9063-1f3d91e6b167.PNG)
왼쪽이미지가 원본이며 중앙에 이미지는 일반 ROI를 한 경우이며 맨 오른쪽이미지는 Homography+ROI를 한 경우입니다.
openCV에서 perspective transformation = homography 관계이며, cv2.getPerspectiveTransform( )와 cv2.findHomography( ) 로 perspective 변환과 homography를 각각 지원하는데 4개의 점만을 이용하여 변환행렬을 찾는 cv2.getPerspectiveTransform( )을 이용하였습니다.
변환 행렬을 구하기 위해서 cv2.getPerspectiveTransfom()함수를 이용하고 cv2.warpPerspective() 함수에 변환행렬값을 적용하여 최종 결과 이미지를 얻는 것입니다.
```python
#[x,y] 좌표점을 4*2의 행렬로 작성
#좌표점은 좌상->좌하->우상->우하
pts1 = np.float32([list(point_list[0]),list(point_list[1]),list(point_list[2]),list(point_list[3])])
# 좌표의 이동점
pts2 = np.float32([[0,0],[weight,0],[0,height],[weight,height]])

M = cv2.getPerspectiveTransform(pts1,pts2)

img_result = cv2.warpPerspective(img_original, M, (weight,height))
cv2.imshow("img_Homograph", img_result)
```
(단, 프로젝트 당시 코드에서는 모형으로 진행하였고 다른 문제로 인하여 Homography 부분은 제외하고 진행하였습니다. 이 코드소스는 따로 Homography.py명으로 업로드 되어있습니다.)

[참조](https://opencv-python.readthedocs.io/en/latest/doc/10.imageTransformation/imageTransformation.html)

#### 2)ROI(Region of Interest)

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

#### 3)Histogram

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

### 3. 카메라(Raspberry pi Zero)
 각각의 카메라 모듈을 장착한 라즈베리 파이 제로킷을 두 대를 사용했는데 한 대는 내부를 촬영하는 역할, 나머지 한 대는 외부를 촬영하는 역할로 사용하였습니다. 외부를 촬영하는 목적으로는 운전대 기준으로 오른쪽 차량 사이드 미러에 붙이는 것으로 체격이 작은 아이들이 탑승, 하차 할 때 보이지 않는 사각지대에서 오는 많은 위험으로부터  안전을 더 보장하기 위해 설치하였습니다.
Wifi 무선 통신을 하여 앱에서 실시간 스트리밍을 위해 실시간 스트리밍 프로토콜(RTSP)를 이용하였습니다.
또한 앱에서와 카메라 자체 내에서 미디어 처리를 위한 Gstramer를 사용해주었습니다.
#라즈베리 파이 안에서 RTSP 서버를 열어주고 카메라 스트리밍을 해주는 Bash 스크립트입니다.
**cam.sh**
```linux
#!/bin/bash 
/home/pi/gst-rtsp-server/examples/./test-launch "( rpicamsrc preview=false bitrate=2000000 keyframe-interval=30 ! video/x-h264, framerate=30/1 ! h264parse ! rtph264pay name=pay0 pt=96 )"
```
카메라 역할만 하는 제로킷들은 따로 모니터가 불필요해서 전원만 주어도 자동으로 서버와 카메라가 실행이 되도록 해주는 설정도 해주었습니다. 
여기서는 리눅스 작업 스케줄러인 crontab을 사용하였고 부팅시 켜지기 위함으로 
**crontab**
```linux
@reboot /home/pi/gst-rtsp-server/examples/cam.sh
```
추가 입력해주었습니다.
![2020-02-18-173422_1366x768_scrot](https://user-images.githubusercontent.com/60215726/74733387-91e92c80-528f-11ea-969c-58c140736f8a.png)

### 4. APP(Java)
[![Video Label](https://img.youtube.com/vi/j18SoUClJeI/0.jpg)](https://youtu.be/j18SoUClJeI)   
App에서 실행되는 시연영상입니다. 스트리밍과 서버 또한 정상작동 중입니다.

### 5. 작품 모형 및 캡스톤 경진대회
![모형](https://user-images.githubusercontent.com/60215726/74937280-1625e600-542f-11ea-8d3b-f7c1deefc52a.PNG)
![경진](https://user-images.githubusercontent.com/60215726/74937275-145c2280-542f-11ea-82e5-ae01d26da9ec.PNG)
