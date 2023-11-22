<h2>Contributors</h2>

<p style="text-align: center;">
<a href = "https://github.com/narcissusTheFlower/image-to-Xlsx/graphs/contributors">
  <img src = "https://contrib.rocks/image?repo=narcissusTheFlower/image-to-Xlsx"/>
</a>
</p>

<h1>Images to .xlsx</h1> 
<hr>
<h2>Intentions</h2>
Web site was made out of inspiration for a python script my groupmate developed locally.<br>
I needed a pet project I would be interested in so there it is.
<hr>
<h2>1. Prepare server</h2>
<ol>
<li>Build file structure by hand:<br>
/home/username<br>
├── /ITE<br>
&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;├── /OCR <br>
&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;├── /upload <br>
&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;├── /Excels <br>
&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;├── /python_mapper.py (downloaded from this repository)<br>
&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;├── /rus.traineddata (downloaded from this repository)<br>
</li>
  
<li>Make sure the server has JVM version 17+</li>
<li>Make sure the server has python3-pip (otherwise "sudo apt install python3-pip")</li>
<li>Make sure the server has Python3 with following packages: 
<ul>Pillow==10.1.0<br>
    openpyxl==3.1.2
</ul></li>
<li>Make sure the TESSDATA_PREFIX environment variable is set to /home/username. To do this on bash add "export TESSDATA_PREFIX=/home/username/ITE" line in "~/.profile"</li>
</ol>
<hr>
<h2>2. Running .jar on the server</h2>
After you ended up with a file structure denoted in step 1 in the "Reminder..." you can run downloaded .jar file as follows:<br>

```
$ java -jar imageToExcel-majorVersion.minorVersion.jar
```





