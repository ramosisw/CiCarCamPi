import socket
import RPi.GPIO as GPIO
import time
import os
import threading


GPIO.setmode(GPIO.BOARD)
#Pines configuration
_F=3
_B=5
_L=7
_R=8
_Lu=13
_DD=15
_DI=16
_SA=11
_SB=10
_Dir=2
global _Di=True
global _Dd=True



GPIO.setup(_F,GPIO.OUT)#FRONT
GPIO.setup(_B,GPIO.OUT)#BACK
GPIO.setup(_L,GPIO.OUT)#LEFT
GPIO.setup(_R,GPIO.OUT)#RIGHT
GPIO.setup(_Lu,GPIO.OUT)#Luces
GPIO.setup(_DD,GPIO.OUT)#Direccional Derecha
GPIO.setup(_DI,GPIO.OUT)#Direccional Izquierda
GPIO.setup(_SA,GPIO.OUT)#Stop Alto
GPIO.setup(_SB,GPIO.OUT)#Stop Bajo

serversocket=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
#print socket.gethostbyname_ex(socket.gethostname())
def forward(a):
	if(a==1):
		print "Forward"
		GPIO.output(_B,False)
		GPIO.output(_F,True)
	elif(a==0):
		print "Back"
		GPIO.output(_F,False)
		GPIO.output(_B,True)
	else:
		print "Stop"
		GPIO.output(_F,False)
		GPIO.output(_B,False)
def lStop(a):
	if(a==1):
		print "Stop bajo"
		GPIO.output(_SA,False)
		GPIO.output(_SB,True)
	else:
		print "Stop alto"
		GPIO.output(_SB,False)
		GPIO.output(_SA,True)
def luces(a):
	if(a==1):
		print "Encender Luces"
		GPIO.output(_LU,True)
	elif(a==0):
		print "Apagar Luces"
		GPIO.output(_LU,False)

def direccionalIzq():
	while(global _Di):
		print "Direccional Izquierda"
		GPIO.output(_DD,False)
		GPIO.output(_DI,True)
		time.sleep(0.5)

def direccionalDer():
	while(global _Dd):
		print "Direccional Derecha"
		GPIO.output(_DI,False)
		GPIO.output(_DD,True)
		time.sleep(0.5)

def direccionalApa():
	print "Direccional Apagada"
	GPIO.output(_DD,False)
	GPIO.output(_DI,False)

t1=threading.Thread(target=direccionalIzq,)
t2=threading.Thread(target=direccionalDer,)
def direction(a):
	
	if(a==1):
		print "Left"
		GPIO.output(_R,False)
		GPIO.output(_L,True)
		global _Di=True
		global _Dd=False
		t1.start()
	elif(a==0):
		print "Right"
		GPIO.output(_L,False)
		GPIO.output(_R,True)
		global _Dd=True
		global _Di=False
		t2.start()
		dDer.start()
	else:
		global _Di=False
		global _Dd=False
		print "Center"
		GPIO.output(_L,False)
		GPIO.output(_R,False)
		direccionalApa()

def cam(a):
	if(a==1):
		print "Cam ON"
		t=threading.Thread(target=camOn)
		t.start()
	else:
		print "Cam OFF"
		t=threading.Thread(target=camOff)
		t.start()


def prosdata(data):
	if(len(data)==2):
		if(data.find('F')==0):
			forward(int(data[1:]))		
			lStop(int(data[1:]))
		if(data.find('D')==0):
			direction(int(data[1:]))
#	if(len(data)==4):
#		if(data.find('CAM')==0):
#			cam(int(data[3:]))

def camOn():
	#FUNCION PARA ARRANAR LA CAMARA
	#os.system('./app')
	os.system("su - pi -c \"raspivid -o - -t 0 -n -w 640 -h 480 -fps 15 | cvlc -vvv stream:///dev/stdin --sout '#rtp{sdp=rtsp://:8554/}' :demux=h264\"")
	print "END PROSSES CAM ON"
def camOff():
	#FUNCION PARA APAGAR LA CAMARA
	#os.system('sudo killall app ')
	os.system('sudo killall raspivid')
	print "END PROSSES CAM OFF"
	
host='192.168.10.254'
port=8080
print(host)
print(port)

serversocket.bind((host,port))

serversocket.listen(5)

print('server started listening')
try:
	while True:
		prosdata('F3')
		prosdata('D3')
		print("Stoping Car")
		(clientsocket,address)=serversocket.accept()
		clientsocket.settimeout(15)
		print("connection established from : ",address)
		while True:
			try:
			   	data = clientsocket.recv(512).decode('utf-8')
				if(data==''):
					break;
			except socket.timeout:
				print ("connection was closed with ",address)
				clientsocket.close()
				break
			except KeyboardInterrupt:
				clientsocket.close()
				break
			except IOError:
				clientsocket.close()
				print ("error... reset server")
				break
			except Error:
				clientsocket.close()
			prosdata(data)
			if(data == 'C1'):
				print ("Connection was closed wiht ",address)
				clientsocket.close()
				break
#			try:
				#print data
				#clientsocket.send(data.encode('UTF-8'))
#			except IOError:
#				print "no data send.. errror"
#				clientsocket.close()
#				break
		print("server started listening...")
		_Bu=False
#except socket.error:
#	print ("No se puede iniciar")
except KeyboardInterrupt:
	GPIO.cleanup()
	print ("Cerrar todo")

