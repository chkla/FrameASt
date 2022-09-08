import os, sys
import glob
from pathlib import Path

SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
INPUT_DIR = SCRIPT_DIR + '/../data/output/'
SEGMENT_DIR = SCRIPT_DIR + '/../data/segments/'

files = glob.glob(INPUT_DIR+'*')

path = Path(SEGMENT_DIR)

if path.is_dir():
    print(path, "already exists. Please remove or rename the folder.")
    sys.exit()
else:
    os.mkdir(path)


for f in files:
    n = 1; text = ""
    fname = f.split('/')[-1]
    with open(f, "r") as inf:
        for line in inf:
            if line.startswith("=========="):
                with open(SEGMENT_DIR + fname + "." + str(n) + ".txt", "w") as out:
                    out.write(text)
                text = ""
                n += 1
            else:
                text += line

