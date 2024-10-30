# User Manual Sturgeon GUI

### Content

1. [Description](#description)
2. [How to install?](#how-to-install)
3. [How to use?](#how-to-use)
4. [FAQ](#faq)

<div style="page-break-after: always;"></div>

### Description
This document contains information on how to install and use the Sturgeon GUI tool. To help the readability of this 
document, colors are used to highlight 
<span style="background-color: black; color:orange"><b>buttons</b></span>,
<span style="background-color: black; color:lightblue"><b>labels</b></span> and
<span style="background-color: black; color:lightgreen"><b>panels</b></span>.

<div style="page-break-after: always;"></div>

## How to install?
1. Login using your credentials in Gridion
2. Open the folder explorer, click on <span style="background-color: black; color:orange"><b>other locations</b></span> and 
select <span style="background-color: black; color:orange"><b>this computer</b></span>
3. Go to <span style="background-color: black; color:orange"><b>opt</b></span> and then 
<span style="background-color: black; color:orange"><b>sturgeon_gui</b></span>
4. Right mouse click on <span style="background-color: black; color:orange"><b>sturgeon.sh</b></span>  and select 
<span style="background-color: black; color:orange"><b>copy to...</b></span>
5. Select your <span style="background-color: black; color:orange"><b>Desktop</b></span>  folder and click 
<span style="background-color: black; color:orange"><b>select</b></span>  in the top right corner.
6. A copy of the file should now appear on your desktop which you can double-click to start the application.

<div style="page-break-after: always;"></div>

## How to use?

### 1. Start the application
On the desktop an icon should be present named sturgeon.sh (if not following the [install guide](#how-to-install)), double click 
this to start the GUI. The screen contains three panels:  
* <span style="background-color: black; color:lightgreen"><b>Menu</b></span>: The vertical bar on the left side of the screen 
contains the buttons used to start, stop and switch between the pages when Sturgeon is running.
  * <span style="background-color: black; color:orange"><b>Setup</b></span>: This is the homepage of the tool where you can 
fill in the settings of the run.
  * <span style="background-color: black; color:orange"><b>Start</b></span>: Use this button after the setup to start the run.
 This button will change to <span style="background-color: black; color:orange"><b>Progress</b></span> when Sturgeon has 
started. Here you can then find the current status of the run.
  * <span style="background-color: black; color:orange"><b>Prediction</b></span>: Contains the plot with the confidence score 
of all possible outcomes of an iteration.
  * <span style="background-color: black; color:orange"><b>Confidence</b></span>: Contains a confidence table and 
confidence-over-time plot. Table consist of labels, total score and score of the iteration (by default sorted on 
highest total score). The plot contains all scores and how they change over time. Also contains threshold lines.
  * <span style="background-color: black; color:orange"><b>CNV plot</b></span>: Contains the copy number variant/alteration 
plot.
  * <span style="background-color: black; color:orange"><b>Stop</b></span>: Button to send a signal to Sturgeon to stop when 
it is done with the current iteration. 
* <span style="background-color: black; color:lightgreen"><b>Log</b></span>: The horizontal bar at the bottom contains the log 
of the tool. Here you can find instructions, progression and warning/errors that appear while running the tool.
* <span style="background-color: black; color:lightgreen"><b>Display</b></span>: The screen on top of the 
<span style="background-color: black; color:lightgreen"><b>Log</b></span>. Here you can find the page of the currently 
selected page as selected in the <span style="background-color: black; color:lightgreen"><b>Menu</b></span>.

<div style="page-break-after: always;"></div>

### 2. Setup

![Setup page](img/setup_page.png)

Every time you want to start a run you have to fill in the following:
* <span style="background-color: black; color:lightblue"><b>Pod5 input folder</b></span>:  folder location where the Gridion 
writes the pod5 files to. You can use the <span style="background-color: black; color:orange"><b>Choose Folder</b></span> 
button to navigate to this folder.
* <span style="background-color: black; color:lightblue"><b>Result location</b></span>:  Select a location where to make a new 
output folder. You can use the <span style="background-color: black; color:orange"><b>Choose Folder</b></span>  button to 
navigate to this location.
* <span style="background-color: black; color:lightblue"><b>Output folder</b></span>:  Name of the output folder that needs to be 
made. Note that this folder cannot exist yet in the location given in the 
<span style="background-color: black; color:lightblue"><b>Result location</b></span>.
* <span style="background-color: black; color:lightblue"><b>Used barcode</b></span>:  Barcode used while running Gridion. If your 
barcode is for example 09 it is fine to just fill in 9 as the 0 is added in the background if missing.

The following settings have sensible default values. Only change if required:
* <span style="background-color: black; color:lightblue"><b>Use unclassified barcodes?</b></span>:  Allow the predictor to also 
look at reads that have another barcode than the given barcode.
* <span style="background-color: black; color:lightblue"><b>Number of iterations before new CNV</b></span>:  How frequent do you 
want sturgeon to make a new CNV plot?
* <span style="background-color: black; color:lightblue"><b>Prediction Model</b></span>:  Location of the model used by the 
classifier. If this value is to be changed for diagnostics runs please contact the Translation Bioinformatics team 
first!

If everything is filled in you can press Start. If the 
<span style="background-color: black; color:lightgreen"><b>Display</b></span> doesn't change please check the 
<span style="background-color: black; color:lightgreen"><b>Log</b></span> panel if there is an error regarding the input 
values and change accordingly. 

<div style="page-break-after: always;"></div>

### 3. Progress
![progress page](img/progres_page.png)
If the input values of the setup are approved, the 
<span style="background-color: black; color:lightgreen"><b>Display</b></span> will show the 
<span style="background-color: black; color:orange"><b>Progress</b></span> page which shows at which step it is at and which 
iteration is running. 

![result page](img/result_page.png)
When plots are generated, the corresponding buttons will be activated in the 
<span style="background-color: black; color:lightgreen"><b>Menu</b></span> which you can click on to see the corresponding 
plots/tables.
While looking at a plot, it **will not** automatically update the 
<span style="background-color: black; color:lightgreen"><b>Display</b></span> if a new plot is created. 
Simply click on <span style="background-color: black; color:orange"><b>Progress</b></span> or another plot tab, and go back in 
order to see the latest plot. 
If you wish to see previous plots, go to <span style="background-color: black; color:orange"><b>Progress</b></span> and click 
on the <span style="background-color: black; color:orange"><b>Result folder</b></span> button. 
This opens a file explorer to the output folder where you can find folders per iteration which contains the plot files.

<div style="page-break-after: always;"></div>

### 4. Stop
![confirm stop](img/confirm_stop.png)
![finish](img/finishing_run.png)
![ready to quit](img/ready_2_close.png)
When you are ready to finish the run, do the following:
1. Stop the GridION sequence run (not required persé but good practice)
2. Click on the <span style="background-color: black; color:orange"><b>Stop</b></span> button and confirm you want to stop. 
(The <span style="background-color: black; color:lightgreen"><b>Display</b></span> should show to the 
<span style="background-color: black; color:orange"><b>Progress</b></span> page and the title is changed to mention that it 
will finish the current iteration)
3. Once the <span style="background-color: black; color:lightgreen"><b>Log</b></span> panel say it is safe to close, click the 
shutdown (<span style="background-color: black; color:orange"><b>x</b></span>) button.

**It is in the current version not possible to restart the program for a new run. So for now please shut down the 
program and repeat the steps in this document.**
<br>
<br>

<div style="page-break-after: always;"></div>

### FAQ
**q. It won't allow me to shut down the application.**   
![quit error](img/close_while_finishing.png)
**a.**  When pressing <span style="background-color: black; color:orange"><b>Start</b></span>, the application is locked until 
the classifier has stopped. We do this to prevent the classifier to be running in the background while the front-end is 
closed, leading to potential issues when another run is started. To stop the application, please press 
<span style="background-color: black; color:orange"><b>Stop</b></span> and wait till the last iteration has finished and the 
<span style="background-color: black; color:lightgreen"><b>Log</b></span> panel tells you it is safe to close it. If for some 
reason it is not stopping, contact the Translational Bioinformatics Team ([TranslationalBioinf@prinsesmaximacentrum.nl](mailto:TranslationalBioinf@prinsesmaximacentrum.nl)).

**q. The application looks all messed up on start up.**  
![messed up gui](img/messed_up_gui.png)
**a.**  It can happen that the <span style="background-color: black; color:orange"><b>Setup</b></span> page looks weird with 
labels and fields not being in the right place. This is a random bug that sometime appears. In such case you can just 
shut down and restart the program. Otherwise, try to resize the screen. If it is still messed up please contact the 
Translation Bioinformatics Team ([TranslationalBioinf@prinsesmaximacentrum.nl](mailto:TranslationalBioinf@prinsesmaximacentrum.nl))

**q. I have a question / feedback / error / problem related to this program.**   
**a.**  Please contact the Translational Bioinformatics Team ([TranslationalBioinf@prinsesmaximacentrum.nl](mailto:TranslationalBioinf@prinsesmaximacentrum.nl)).