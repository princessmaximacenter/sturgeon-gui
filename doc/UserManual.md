# User Manual Sturgeon GUI

### Content

1. [How to install?](#how-to-install)
2. [How to use?](#how-to-use)
3. [FAQ](#faq)

## How to install?
1. Login using your credentials in Gridion
2. Open the folder explorer, click on other locations and select this computer
3. Go to opt -> sturgeon_gui
4. Right mouse click on sturgeon.sh and select copy to...
5. Select your Desktop folder and click select in the top right corner.
6. A copy of the file should now appear on your desktop which you can double-click to start the application.

## How to use?

### 1. Start the application
On the desktop an icon should be present named sturgeon.sh, double click this to start the GUI.

### 2. Setup
Every time you want to start a run you have to fill in the following:
* <b>Pod5 input folder:</b> folder location where the Gridion writes the pod5 files to. You can use the "Choose Folder" button to navigate to this folder.
* <b>Result folder:</b> Select a location where to make a new output folder. You can use the "Choose Folder" button to navigate to this location.
* <b>Output folder:</b> Name of the output folder that needs to be made. Note that this folder cannot exist yet in the location given in the "Result location".
* <b>Used barcode:</b> Barcode used while running Gridion.

The following settings have sensible default values. Only change if required:
* <b>Use unclassified barcodes?:</b> Allow the predictor to also look at reads that have another barcode than given.
* <b>Number of iterations before new CNV:</b> How frequent do you want sturgeon to make a new CNV plot?
* <b>Prediction Model:</b> Location of the model used by the classifier. If this value is to be changed for diagnostics runs please contact the Translation Bioinformatics team first!

If everything is filled in you can press Start. If the screen doesn't change please check the terminal if there is an error regarding the input values and change accordingly. 

### 3. Running
If the input values of the setup are approved, the screen will show the current process. 
When plots are generated, the corresponding buttons will be activated which you can click on to see the plots.
While looking at a plot, it will not automatically update the screen if a new plot is found. 
Simply click on Progress or another plot tab and go back in order to see the latest. 
If you wish to see previous plots, go to Progress and click on the Result folder button. 
This opens a file explorer to the output folder where you can find folders per iteration which contains the plot files.

### 4. Stop
When you are ready to finish the run, do the following:
1. Stop the GridION run (not required persé but good practice)
2. Click on the Stop button. (The screen should go to the Progress page and have in the title that it will finish the current iteration)
3. Click the shutdown (x) button once the terminal say it is safe to close.

It is in the current version not possible to restart the program for a new run. So for now please shut down the program and repeat the steps in this document.

### FAQ
<b>q. It won't allow me to shut down the application.</b>  
<b>a.</b> When pressing Start, the application is locked until the classifier has stopped. We do this to prevent the classifier to be running in the background while the front end is closed leading to potential issues when another run is started. To stop the application please press Stop and wait till the last iteration has finished and the terminal tells you it is safe to close it. If for some reason it is not stopping, contact the Translational Bioinformatics Team (TranslationalBioinf@prinsesmaximacentrum.nl).

<b>q. The application looks all messed up on start up.</b>    
<b>a.</b> It can happen that the setup page looks weird with labels and fields not being in the right place. This is a random bug that sometime appears. In such case you can just shut down and restart the program. Otherwise try to resize the screen. If it is still messed up please contact the Translation Bioinformatics Team (TranslationalBioinf@prinsesmaximacentrum.nl)

<b>q. I have a question / feedback / error / problem related to this program.</b>  
<b>a.</b> Please contact the Translational Bioinformatics Team (TranslationalBioinf@prinsesmaximacentrum.nl).