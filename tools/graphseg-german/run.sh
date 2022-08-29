#! /bin/bash


# You can run the segmenter on the sample file that we included in the data/input folder. The output
# is written to data/output, with the same file name.
# If you want to process new text, you can simply put the files in the input folder and run this script.
# All files in the input folder will be processed and the output will be written to the output folder.

# You can vary the relatedness-treshold RT and the minimal-segment-size MIN (see details in the README 
# and in the paper).

RT=1
MIN=1
java -cp ./source/target -jar ./source/target/graphseg-0.0.1-SNAPSHOT-jar-with-dependencies.jar ./data/input ./data/output 0.$RT $MIN

# We include a simple python script that allows you to split the segmented output and write the segments
# to separate files. You can run the script with:
#
# python script/split_files.py
#
# The script will create a new folder "segments" in the data folder and write the segments in the new folder.
# If the segments folder already exists, the script will ask you to remove or rename the folder, to prevent
# that existing results are overwritten.




