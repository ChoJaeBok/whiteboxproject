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
	if(x==0):
		point.count +=1
	else :
		point.count =0
point.count=0
def cnum():
	cnum.count+=1
cnum.count=0
def inum(x):
#좌석 설정할 때 변수들을 저장할때 도와주는 변수.
# 1set =0,1 * 2= >reset
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
x1_list=[]
y1_list=[]
x2_list=[]
y2_list=[]
#핸드폰이나 테블릿의 해상도에따라 변경해줘야 하는 부분.
#자동으로 인식할 수 있게 변경할예정.
width=1080
height=2220
path ='D:/whitebox'#위치 바꿔주기
#파이 컨트롤 함수
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
		#시작 이미지 저장하는 부분.
		startROI()
#처음 이후에 비교할 스트리밍 화면에서 좌석 추출하는 부분.
	elif input_string =="EndROI":
		#마지막 이미지 저장하는 부분
		endROI()
#좌석 비교시 사용되는 히스토그램 실행하는 부분.
	elif input_string =="histogram":
		#이미지 비교하는
		input_string = hist_compare(point.count)

	else :#좌표 심는 구간.
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
			input_string ="errer."
			print(input_string)

	return input_string
def startROI():
	i=0
	image =Image.open('startimage.jpg')
	image2 =Image.open('Endimage.jpg')
	small = image.resize((width,height),Image.ANTIALIAS)
	large = image2.resize((width,height),Image.ANTIALIAS)
	small.save('startimage.jpg','jpeg')
	large.save('Endimage.jpg','jpeg')
	img_start=cv2.imread('startimage.jpg')
	while i<=point.count:# 처음 이미지에 ROI하는
		now='Start_ROI'+str(i)
		subimg_start = img_start[y1_list[i]:y2_list[i], x1_list[i]:x2_list[i]]
		cv2.imwrite(os.path.join(path,str(now)+'.PNG'),subimg_start)
		i+=1

def endROI():
    i=0

    img_End=cv2.imread('Endimage.jpg')
    while i<=point.count:
        # 마지막 이미지에 ROI하는
        now2='End_ROI'+str(i)
        subimg_End = img_End[y1_list[i]:y2_list[i], x1_list[i]:x2_list[i]]
        cv2.imwrite(os.path.join(path,str(now2)+'.PNG'),subimg_End)
        i+=1
def hist_compare(d_count):#비교하는 구간
	i=0
	msg=''
	while i<=d_count:
		pts1=cv2.imread('Start_ROI'+str(i)+'.PNG')
		pts2=cv2.imread('End_ROI'+str(i)+'.PNG')
		H1=cv2.calcHist(images=[pts1],channels=[0],mask=None, histSize=[256],ranges=[0,256])
		cv2.normalize(H1,H1,1,0,cv2.NORM_L1)
		H2=cv2.calcHist(images=[pts2],channels=[0],mask=None,histSize=[256],ranges=[0,256])
		cv2.normalize(H2,H2,1,0,cv2.NORM_L1)
		d1 = cv2.compareHist(H1,H2,cv2.HISTCMP_CORREL)
		print('df(H1,H2,CORREL)=',d1)
		d1=d1*10
		print(d1)
		print(str(i)+':ROI compare')
		#이부분에서 수정해주면됨. return 해줄값들, 즉 테블릿에다가 보내줄 것을 print대신 수정.
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
#저장된 좌표들 초기화 하는 부분.
def clr_list():
	print(point.count)
	del(x1_list[0:point.count+1])
	del(y1_list[0:point.count+1])
	del(x2_list[0:point.count+1])
	del(y2_list[0:point.count+1])
	#print(x_list)
	point(1)
	inum(1)
# 여기서부터 main문
while True:
	#접속 승인
	conn, addr = s.accept()
	print("Connected by ", addr)

	#데이터 수신
	data = conn.recv(1024)
	data = data.decode("utf8").strip()
	if not data: break
	print("Received: " + data)

	#수신한 데이터로 파이를 컨트롤
	res = do_some_stuffs_with_input(data)
	print("pi ac :" + res)

	#클라이언트에게 답을 보냄
	conn.sendall(res.encode("utf-8"))
	#연결 닫기
	conn.close()

s.close()
