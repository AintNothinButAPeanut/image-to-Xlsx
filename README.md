<h2>Contributors</h2>

<p style="text-align: center;">
<a href = "https://github.com/narcissusTheFlower/image-to-Xlsx/graphs/contributors">
  <img src = "https://contrib.rocks/image?repo=narcissusTheFlower/image-to-Xlsx"/>
</a>
</p>

<h1>Images to .xlsx</h1> 
<hr>
<h2>Intentions</h2>
I made this website out of inspiration for a python script my groupmate developed locally.<br>
I needed a pet project I would be interested in so there it is.
<hr>
<h2>Reminder for myself on how to set this up on a Linux server</h2>
<ol>
<li>Build file structure by hand:<br>
/home/username<br>
├── /ITE<br>
&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;├── /OCR <br>
&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;├── /upload <br>
&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;├── /Excels <br>
&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;├── /python_mapper.py <br>
&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;├── /rus.traineddata <br>
</li>
<li>Make sure the server has JVM version 17+</li>
<li>Make sure the server has Python3 with following packages: 
<ul>Pillow==10.1.0<br>openpyxl==3.1.2
</ul>
<li>Make sure the server has python3-pip (otherwise "sudo apt install python3-pip")</li></li>
<li>Make sure the TESSDATA_PREFIX environment variable is set to /home/username</li>
</ol>



