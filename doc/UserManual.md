# User Manual Sturgeon GUI

### Content

1. [Description](#description)
2. [How to install?](#how-to-install)
2. [How to use?](#how-to-use)
3. [FAQ](#faq)

### Description
This document contains information on how to install and use the Sturgeon GUI tool. To help the readability of this 
document, colors are used to highlight 
<span style="background-color: black; color:orange">buttons</span>,
<span style="background-color: black; color:lightblue">labels</span> and
<span style="background-color: black; color:lightgreen">panels</span>.

## How to install?
1. Login using your credentials in Gridion
2. Open the folder explorer, click on <span style="background-color: black; color:orange">other locations</span> and 
select <span style="background-color: black; color:orange">this computer</span>
3. Go to <span style="background-color: black; color:orange">opt</span> and then 
<span style="background-color: black; color:orange">sturgeon_gui</span>
4. Right mouse click on <span style="background-color: black; color:orange">sturgeon.sh</span>  and select 
<span style="background-color: black; color:orange">copy to...</span>
5. Select your <span style="background-color: black; color:orange">Desktop</span>  folder and click 
<span style="background-color: black; color:orange">select</span>  in the top right corner.
6. A copy of the file should now appear on your desktop which you can double-click to start the application.

## How to use?

### 1. Start the application
On the desktop an icon should be present named sturgeon.sh (if not following the [install guide](#how-to-install)), double click 
this to start the GUI. The screen contains three panels:  
* <span style="background-color: black; color:lightgreen">Menu</span>: The vertical bar on the left side of the screen 
contains the buttons used to start, stop and switch between the pages when Sturgeon is running.
  * <span style="background-color: black; color:orange">Setup</span>: This is the homepage of the tool where you can 
fill in the settings of the run.
  * <span style="background-color: black; color:orange">Start</span>: Use this button after the setup to start the run.
 This button will change to <span style="background-color: black; color:orange">Progress</span> when Sturgeon has 
started. Here you can then find the current status of the run.
  * <span style="background-color: black; color:orange">Prediction</span>: Contains the plot with the confidence score 
of all possible outcomes of an iteration.
  * <span style="background-color: black; color:orange">Confidence</span>: Contains a confidence table and 
confidence-over-time plot. Table consist of labels, total score and score of the iteration (by default sorted on 
highest total score). The plot contains all scores and how they change over time. Also contains threshold lines.
  * <span style="background-color: black; color:orange">CNV plot</span>: Contains the copy number variant/alteration 
plot.
  * <span style="background-color: black; color:orange">Stop</span>: Button to send a signal to Sturgeon to stop when 
it is done with the current iteration. 
* <span style="background-color: black; color:lightgreen">Log</span>: The horizontal bar at the bottom contains the log 
of the tool. Here you can find instructions, progression and warning/errors that appear while running the tool.
* <span style="background-color: black; color:lightgreen">Display</span>: The screen on top of the 
<span style="background-color: black; color:lightgreen">Log</span>. Here you can find the page of the currently 
selected page as selected in the <span style="background-color: black; color:lightgreen">Menu</span>.

### 2. Setup
Every time you want to start a run you have to fill in the following:
* <span style="background-color: black; color:lightblue">Pod5 input folder</span>:  folder location where the Gridion 
writes the pod5 files to. You can use the <span style="background-color: black; color:orange">Choose Folder</span> 
button to navigate to this folder.
* <span style="background-color: black; color:lightblue">Result location</span>:  Select a location where to make a new 
output folder. You can use the <span style="background-color: black; color:orange">Choose Folder</span>  button to 
navigate to this location.
* <span style="background-color: black; color:lightblue">Output folder</span>:  Name of the output folder that needs to be 
made. Note that this folder cannot exist yet in the location given in the 
<span style="background-color: black; color:lightblue">Result location</span>.
* <span style="background-color: black; color:lightblue">Used barcode</span>:  Barcode used while running Gridion. If your 
barcode is for example 09 it is fine to just fill in 9 as the 0 is added in the background if missing.

The following settings have sensible default values. Only change if required:
* <span style="background-color: black; color:lightblue">Use unclassified barcodes?</span>:  Allow the predictor to also 
look at reads that have another barcode than the given barcode.
* <span style="background-color: black; color:lightblue">Number of iterations before new CNV</span>:  How frequent do you 
want sturgeon to make a new CNV plot?
* <span style="background-color: black; color:lightblue">Prediction Model</span>:  Location of the model used by the 
classifier. If this value is to be changed for diagnostics runs please contact the Translation Bioinformatics team 
first!

If everything is filled in you can press Start. If the 
<span style="background-color: black; color:lightgreen">Display</span> doesn't change please check the 
<span style="background-color: black; color:lightgreen">Log</span> panel if there is an error regarding the input 
values and change accordingly. 

### 3. Progress
If the input values of the setup are approved, the 
<span style="background-color: black; color:lightgreen">Display</span> will show the 
<span style="background-color: black; color:orange">Progress</span> page which shows at which step it is at and which 
iteration is running. 
When plots are generated, the corresponding buttons will be activated in the 
<span style="background-color: black; color:lightgreen">Menu</span> which you can click on to see the corresponding 
plots/tables.
While looking at a plot, it **will not** automatically update the 
<span style="background-color: black; color:lightgreen">Display</span> if a new plot is created. 
Simply click on <span style="background-color: black; color:orange">Progress</span> or another plot tab, and go back in 
order to see the latest plot. 
If you wish to see previous plots, go to <span style="background-color: black; color:orange">Progress</span> and click 
on the <span style="background-color: black; color:orange">Result folder</span> button. 
This opens a file explorer to the output folder where you can find folders per iteration which contains the plot files.

### 4. Stop
When you are ready to finish the run, do the following:
1. Stop the GridION sequence run (not required persé but good practice)
2. Click on the <span style="background-color: black; color:orange">Stop</span> button and confirm you want to stop. 
(The <span style="background-color: black; color:lightgreen">Display</span> should show to the 
<span style="background-color: black; color:orange">Progress</span> page and the title is changed to mention that it 
will finish the current iteration)
3. Once the <span style="background-color: black; color:lightgreen">Log</span> panel say it is safe to close, click the 
shutdown (<span style="background-color: black; color:orange">x</span>) button.

**It is in the current version not possible to restart the program for a new run. So for now please shut down the 
program and repeat the steps in this document.**

### FAQ
**q. It won't allow me to shut down the application.**   
**a.**  When pressing <span style="background-color: black; color:orange">Start</span>, the application is locked until 
the classifier has stopped. We do this to prevent the classifier to be running in the background while the front-end is 
closed, leading to potential issues when another run is started. To stop the application, please press 
<span style="background-color: black; color:orange">Stop</span> and wait till the last iteration has finished and the 
<span style="background-color: black; color:lightgreen">Log</span> panel tells you it is safe to close it. If for some 
reason it is not stopping, contact the Translational Bioinformatics Team (TranslationalBioinf@prinsesmaximacentrum.nl).

**q. The application looks all messed up on start up.**     
**a.**  It can happen that the <span style="background-color: black; color:orange">Setup</span> page looks weird with 
labels and fields not being in the right place. This is a random bug that sometime appears. In such case you can just 
shut down and restart the program. Otherwise, try to resize the screen. If it is still messed up please contact the 
Translation Bioinformatics Team (TranslationalBioinf@prinsesmaximacentrum.nl)

**q. I have a question / feedback / error / problem related to this program.**   
**a.**  Please contact the Translational Bioinformatics Team (TranslationalBioinf@prinsesmaximacentrum.nl).