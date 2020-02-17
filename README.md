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
