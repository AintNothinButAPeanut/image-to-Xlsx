import os
import sys
from datetime import datetime

from PIL import Image
from openpyxl import Workbook
from openpyxl.drawing.image import Image as OpenpyxlImage
from openpyxl.styles import Alignment, Font

# Get directory with files
sourceDirectory = sys.argv[1]
targetDirectory = sys.argv[2]
identifierForController = sys.argv[3]
directory = os.fsdecode(sourceDirectory)
filesCount = len(os.listdir(directory))
pictureFileCount = 0

# Get text and picture info from each file
eachFileText = []
eachFileImage = []
maxPictureWidth = 0
maxPictureHeight = 0

for file in os.listdir(directory):
    filename = os.fsdecode(file)
    if filename.endswith(".txt"):
        thisFilesText = open(directory + "/" + filename, 'r')
        eachFileText.append(thisFilesText.read())
    elif filename.endswith(".jpg") or filename.endswith(".png") or filename.endswith(".tiff"):
        pictureFileCount += 1
        # Pillow Image class
        with Image.open(directory + "/" + filename) as image:
            eachFileImage.append(directory + "/" + filename)
            width, height = image.size
            if width > maxPictureWidth:
                maxPictureWidth = width
            elif height > maxPictureHeight:
                maxPictureHeight = height

# Initialise excel
wb = Workbook()
ws = wb.active

# Define some styles
ws.title = "Text-Image"
ws.column_dimensions['A'].width = 50
ws.column_dimensions['B'].width = maxPictureWidth * 0.1

myFont = Font(name='Courier',
              size=14,
              bold=False,
              italic=False,
              vertAlign=None,
              underline='none',
              strike=False,
              color='FF000000')

myAlignment = Alignment(horizontal='general',
                        vertical='top',
                        text_rotation=0,
                        wrap_text=True,
                        shrink_to_fit=False,
                        indent=0)

# First column
for i in range(len(eachFileText)):
    # Set cell parameters/style
    ws.row_dimensions[i + 1].height = maxPictureHeight
    ws['A' + str(i + 1)].alignment = myAlignment
    ws['A' + str(i + 1)].font = myFont

    # Write text to cell
    ws['A' + str(i + 1)] = eachFileText[i]

# Second column
for i in range(len(eachFileImage)):
    img = OpenpyxlImage(eachFileImage[i])
    ws.add_image(img, 'B' + str(i + 1))

# Works
fileName = "ITE-" + identifierForController + datetime.now().strftime('%Y-%m-%d %H:%M:%S') + ".xlsx"
wb.save(targetDirectory + "/" + fileName)
