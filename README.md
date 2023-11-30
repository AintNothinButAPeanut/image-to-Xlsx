<h1>Images to .xlsx</h1> 
<h2>Intentions</h2>
Web site was made out of inspiration for a python script my groupmate developed locally.<br>
I needed a pet project I would be interested in so there it is.
<hr>
<h2>Step by step server preparation (Linux only)</h2>
<h3>Step 1</h3>

Clone this repository in your home directory. Run `cd ~` to get to your home directory at once and then clone it:

```
git clone https://github.com/narcissusTheFlower/image-to-Xlsx
```

<h3>Step 2</h3>
Build file structure by hand:<br>

```
/home/username
            ├── /ITE
                ├── /OCR 
                ├── /upload 
                ├── /excels 
                ├── /python_mapper.py (downloaded from this repository)
                ├── /rus.traineddata (downloaded from this repository)
                ├── /eng.traineddata (downloaded from this repository)
                ├── /imageToExcel-x.x.jar (downloaded from this repository)  
```

These commands will do the job if run from home directory:

```
mkdir ITE;
mkdir ITE/OCR;
mkdir ITE/uploads;
mkdir ITE/excels;
cp ~/image-to-Xlsx/python_mapper.py ~/ITE;
cp ~/image-to-Xlsx/rus.traineddata ~/ITE;
cp ~/image-to-Xlsx/eng.traineddata ~/ITE
```

Then copy the .jar file as we did with other files **but** change the "x" in "imageToExcel-**x**.**x**.jar" to the
version you
have downloaded:

```
cp ~/image-to-Xlsx/imageToExcel-x.x.jar ~/ITE
```

<h3>Step 3</h3>
Let us prepare the software and dependencies to run the application.
<li>Make sure the server has JVM version 17+, to check this lets run:

```
java -version
```

You should see something like this:

```
openjdk version "17.0.8.1" 2023-08-24
OpenJDK Runtime Environment (build 17.0.8.1+1-Ubuntu-0ubuntu122.04)
OpenJDK 64-Bit Server VM (build 17.0.8.1+1-Ubuntu-0ubuntu122.04, mixed mode, sharing)
```

If you get some sort of error saying there is no such command or the version is lower than **_17_** run the following:

```
sudo apt install openjdk-17-jre -y
```

This will install the **_JVM_** to run _**.jar**_ files.
<li>Make sure the server has python3, to check its presence type the following in your command prompt: 

```
python3 -V
```

You should see something like this:

```
Python 3.10.12
```

If `python3` is not present on your Linux machine run the following:

```
sudo apt install python3 -y
```

You do not need to worry about a specific version of python, python3 will suffice.<br>
Now that python is installed lets invoke the `venv` for python purposes. <br>
From your `cd ~/ITE` directory run the following commands:

```
python3 -m venv .venv;
source .venv/bin/activate
```

Now let's install the dependencies python needs:

```
pip install Pillow==10.1.0 openpyxl==3.1.2
```

<li>Now we need to set up an environment variable called <br>

```
TESSDATA_PREFIX   
```

To do this on bash add line  `export TESSDATA_PREFIX=/home/username/ITE` in

```
~/.profile
```

To quickly pull this off run this command or do it manually:

```
"export TESSDATA_PREFIX=/home/username/ITE" >> ~/.profile
```

Where `username` is the name of the user you will be launching the application under. Probably your current user.<br>
This command will append `export TESSDATA_PREFIX=/home/username/ITE` on the last line of your `.profile` configuration
file.
</li>

<hr>
<h2>Running .jar on the server</h2>

Now that the preparations are complete you can run the .jar file from `~/ITE` as follows:

```
$ java -jar imageToExcel-x.x.jar 
```





