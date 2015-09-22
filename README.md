# CiCarCamPi
<p><strong>Qué es CiCarCamPi? </strong>Es una aplicacion para el sistema operativo Android, la cual controla un Carro RC con tecnologia RaspberryPi
<br>La comunicacion es via Socket por el puerto 8080, con el servidor escrito en Python y el cliente en Java.</p>
<p>Estas son capturas de pantalla de la aplicacion, control con botones o acelerometro</p>
<div style="float:left">
  <img src="https://github.com/ramosisw/CiCarCamPi/blob/master/screenshots/control2.png" width="30%"/>
  <img src="https://github.com/ramosisw/CiCarCamPi/blob/master/screenshots/layout-2014-10-18-144858.png" width="30%"/>
</div>
<br/>
<br/>
<br/>
<p>El <b>codigo fuente</b> del servidor para la Raspberry se encuentra en este archivo: <a href="https://github.com/ramosisw/CiCarCamPi/blob/master/src-raspberry/iCarCamPi.py" target="new">iCarCamPi.py</a> , que esta dentro de la carpeta src-raspberry</p>
<p><strong>NOTA:</strong> para utilizar inalambricamente el control del carro es necesario dar de alta una señal wifi en la raspberry, el codio asume que la ip principal de la rasbperry es: <i>192.168.10.254</i>, puede cambiar esto en la linea: #146.<br>
Proximamente, subire el tutorial de como inicialice la red wifi emitida por la Raspberry-Pi. </p>
<p><b><span style="color:red;">*<span>Esta aplicacion fue desarrollada en AndroidStudio</b></p>