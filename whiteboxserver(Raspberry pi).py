# 소켓통신을 이용한 작품입니다.
# 처음 생각했던 이미지에서는 스마트폰이나 테블릿에서 이미지를 저장하고 전송까지 하여
# 더욱 완벽한 작품을 만들예정이였으나
# 영상처리(homography,ROI,히스토그램 등등), 소켓통신, 라즈베리파이 등등 처음 경험하는 것으로 실패도 많이 하게되고
# 얼굴을 인식하여 할 수 있도록 공부까지 하였고 성공은 하였지만 다른 문제로 다시 엎고
# 새롭게 진행하다보니 캡스톤 경영대회와 교내 대회 시간에 걸리다 보니
# 완벽하게 만들지는 못했습니다.
# 더 완벽하게 만들 예정입니다.
import socket
import os
import cv2
from PIL import Image
HOST = ""
PORT = 8888
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print ('Socket created')
s.bind((HOST, PORT))
print ('Socket bind complete')
s.listen(1)
print ('Socket now listening')
def point(x):
#point count
#pointcount 은 몇개의 좌석을 매핑했는지에 대한 카운트 갯수입니다.
#즉 inum에서 터치하고 드래그 후에 터치를 땐 후에 x1[0],y1[0],x2[0],y2[0]에 저장을 하고
#x1[1],y1[1],x2[1],y2[1]에 저장을 하는데 인덱스를 나타내는 것이 point.count입니다.
#좌석을 지정한 갯수를 뜻합니다.
	if(x==0):
		point.count +=1
	else :
		point.count =0
point.count=0
def cnum():
#cnum은 처음이미지와 마지막 이미지에서 좌석매핑 완료후에 아이나 사람, 물체가 달라졌을
#경우에 탐지되는 좌석들의 수를 카운트하는 변수입니다.
	cnum.count+=1
cnum.count=0
def inum(x):
# 1set =0,1 * 2= >reset
#inum : 스트리밍한 이미지에서 좌표를 지정할 떄 필요한 좌표로는 (x1,y1) (x2,y2)으로
#처음 터치를 하는 시작부분을 inum=1로 (x1,y1)이고
#터치를 제거를 했을 경우 inum=2로 증가시켜주면서 (x2,y2)를 저장시키고
#다른 좌석매핑에서 처음과 마지막을 구분시키기위해 inum=0으로 초기화 해줍니다.
	if x==0:
		inum.count+=1
		if(inum.count ==2):
			point(0)
			inum.count = 0
	else:
		inum.count=0

inum.count=-1
#좌표를 설정하기 위한
#스마트폰이나 테블릿에서 좌표값이 보내지면 저장하기 위한 변수 선언.
#x1,y1은 첫 스트리밍화면에서 좌석을 매핑하기 위한 변수
#x2,y2은 비교할 좌석들에 대한 스트리밍화면에서 가져온 변수들.
#배열로 구성한 이유는 각각 x1,y1,x2,y2 한 이미지당 하나씩 존재를 합니다.
#0번쨰 인덱스에서는 처음 좌석매핑한 이미지의 좌표들을 의미하며
#1번쨰 인덱스에서는 두번쨰 좌석 매핑한 이미지의 좌표들을
#2번쨰 인덱스에서는 3번쨰 좌석 매핑한 이미지의 좌표를 가지게 됩니다.
#이 저장된 좌표들은 처음에 좌석을 매핑할 때 저장해둔 이미지들과
#비교하고자 하는 이미지(스트리밍 화면)에서 같은 구역(좌표를 저장해둔)을 저장하여
#히스토그램으로 이용한 이미지 비교를 하기위해 이처럼 저장하였습니다.
x1_list=[]
y1_list=[]
x2_list=[]
y2_list=[]

#핸드폰이나 테블릿의 해상도에따라 변경해줘야 하는 부분.
#자동으로 인식할 수 있게 변경할예정.
width=1080
height=2220
path ='/home/pi/Desktop/whitebox'

def do_some_stuffs_with_input(input_string):
#라즈베리파이를 컨트롤할 명령어 설정
#input_string 변수가 java 앱에서 넘어오는 값이 같은 걸로 실행.
	if input_string == "left":
		input_string = "left."
#파이 동작 명령 추가할것
#초기화명령어
	elif input_string=='clear':
		clr_list()
#처음 스트리밍 화면에서 좌석 매핑하는
	elif input_string =="StartROI":
		startROI()
	#처음 이후에 비교할 스트리밍 화면에서 좌석 추출하는 부분.
	elif input_string =="EndROI":
		#비교할 이미지 저장하는 부분
		endROI()
	elif input_string =="histogram":
		#좌석 비교시 사용되는 히스토그램 실행하는 부분.
		#이미지 비교하는 부분으로
		input_string = hist_compare(point.count)
	else :
		#좌표를 지정해서 넘어오는 변수들을 String에서 ,로 x와y를 나누고 정수로 저장하여
		#좌표를 지정하는 부분입니다.
		search =','
		if search in input_string:
			inum(0)
			x,y= input_string.split(',')
			x1=int(x)
			y1=int(y)
			if(inum.count==0):
				x1_list.append(x1)
				y1_list.append(y1)
				print(x1_list)
			elif(inum.count==1):
				x2_list.append(x1)
				y2_list.append(y1)
				print(point.count)
				print(inum.count)
			input_string="PointSave"
		else:
			input_string ="error."
			print(input_string)

	return input_string
def startROI():
	#처음 스트리밍 화면에서 (사용자가 처음에 좌석을 매핑하기 위한 화면에서)
	#좌석을 매핑한 후 각각의 좌석들의 이미지를 ROI를 하는 부분입니다.
	i=0
	img_start=cv2.imread('/home/pi/Desktop/whitebox/startimage.jpg')
	while i<=point.count:
		now='Start_ROI'+str(i)
		subimg_start = img_start[y1_list[i]:y2_list[i], x1_list[i]:x2_list[i]]
		cv2.imwrite(os.path.join(path,str(now)+'.PNG'),subimg_start)
		#cv2.imshow('start','/home/Start_ROI1.PNG')
		i+=1
def endROI():
	#비교할 스트리밍 화면에서 처음에 지정한 좌석좌표들에 의해 ROI를 하는 부분입니다.
    i=0
    img_End=cv2.imread('/home/pi/Desktop/whitebox/Endimage.jpg')
    while i<=point.count:
        now2='End_ROI'+str(i)
        subimg_End = img_End[y1_list[i]:y2_list[i], x1_list[i]:x2_list[i]]
        cv2.imwrite(os.path.join(path,str(now2)+'.PNG'),subimg_End)
        i+=1
def hist_compare(d_count):
	#ROI한 이미지들에서 각각 매핑한 좌석들 중
	#처음 이미지와 비교할 이미지에서 매핑한 좌석만 ROI된 이미지들을 통해
	#두가지로 분류가 된 후 각각 좌표가 동일한 이미지끼리 비교를 하는 부분입니다.
	#즉 Start_ROI0 과 EndROI0이 비교가 되며
	#총 8개의 좌석을 매핑을 했다면 16개의 이미지가 각각 두 개씩 비교가 됩니다.
	#비교가 된 후  cv2.compareHist(H1,H2,cv2.HISTCMP_CORREL)을 통해서
	#값이 나오는데 이 값들은 소수점이 나옵니다.
	#참고 : cv2.compareHist(H1,H2,cv2.HISTCMP_CORREL)에서 나온 값이
	#1에 가까울 수록 유사도가 높은 것이며 1이면 같은 이미지라고 보면 될것같습니다.
	#여기서는 값에 10을 곱해 음수부터 10까지에서 6이라는 기준을 잡고
	# 6보다 크면 사람이 없는 것으로 그 이하이면 아이나 사람, 물체가 탐지되는 것을
	#구별하는 것입니다.
	i=0
	msg=''
	while i<=d_count:
		pts1=cv2.imread('/home/pi/Desktop/whitebox/Start_ROI'+str(i)+'.PNG')
		pts2=cv2.imread('/home/pi/Desktop/whitebox/End_ROI'+str(i)+'.PNG')
		H1=cv2.calcHist(images=[pts1],channels=[0],mask=None, histSize=[256],ranges=[0,256])
		cv2.normalize(H1,H1,1,0,cv2.NORM_L1)
		H2=cv2.calcHist(images=[pts2],channels=[0],mask=None,histSize=[256],ranges=[0,256])
		cv2.normalize(H2,H2,1,0,cv2.NORM_L1)
		d1 = cv2.compareHist(H1,H2,cv2.HISTCMP_CORREL)
		print('df(H1,H2,CORREL)=',d1)
		d1=d1*10
		print(d1)
		print(str(i)+':ROI compare')
		if d1>6:
			print('no Human')#
		elif d1<=6:
			print('detech')
			cnum()
		elif d1==0:
			print('detech')
			cnum()
		elif d1<0:
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

def clr_list():
	print(point.count)
	del(x1_list[0:point.count+1])
	del(y1_list[0:point.count+1])
	del(x2_list[0:point.count+1])
	del(y2_list[0:point.count+1])
	#print(x_list)
	point(1)
	inum(1)

while True:
	conn, addr = s.accept()
	print("Connected by ", addr)

	data = conn.recv(1024)
	data = data.decode("utf8").strip()
	if not data: break
	print("Received: " + data)

	res = do_some_stuffs_with_input(data)
	print("pi ac :" + res)

	conn.sendall(res.encode("utf-8"))
	conn.close()
s.close()
