**German Segmenter**
Find our adapted graphseg-german tool here ➡️ https://drive.google.com/drive/folders/1XOpgt76HxJgXSXrTR6HzCHD9bnE97bY2?usp=sharing

**Original README:**

About
========

GraphSeg is a tool for semantic/topical segmentation of text that employs semantic relatedness and a graph-based algorithm to identify semantically coherent segments in text. 
Segmentation is performed at the sentence level (no intra-sentential segment beginnings/end)

Content
========

This repository contains: 

(1) the Java source code (as Maven project)
(2) the ready-to-use binary version of the tool (graphseg.jar in the /binary folder) 
(3) the dataset of political manifestos manually annotated with segments (used for evaluation in the research paper the GraphSeg tool accompanies). 

Usage 
========

To successfully run the GraphSeg tool you need to have Java 1.8 installed. 
The following command with four arguments runs the GraphSeg tool:

java -jar graphseg.jar <input-folder-path> <output-folder-path> <relatedness-treshold> <minimal-segment-size>

The argument (all mandatory) to be provided are:

(1) <input-folder-path> is the path to the folder (directory) containing the raw text documents that need to be topically/semantically segmented;
(2) <output-folder-path> is the path to the folder in which the semantically/topically segmented input documents are to be stored;
(3) <relatedness-treshold> is the value of the relatedness treshold (decimal number) to be used in the construction of the relatedness graph: larger values will give large number of smalled segments, whereas the smaller treshold values will provide a smaller number of coarse segments;
(4) <minimal-segment-size> defines the minimal segment size m (in number of sentences). This means that GraphSeg will not produce segments containing less than m sentences.    
     
Example command: 

java -jar graphseg.jar /home/seg-input /home/seg-output 0.25 3

The tool's correct execution depends on the resources in the /source/res directory. There are three files that need to be there: 

(1) embeddings.txt -- the word embeddings used for measuring semantic similarity between sentences. The default file used are 200-dimensional GloVe embeddings obtained on Wikipedia 2014 + Giga 5 corpus (http://nlp.stanford.edu/data/glove.6B.zip). This file is bundled into the standalone binary file graphseg.jar, but is omitted from the source/res folder due to space constraints of the repository;
(2) stopwords.txt -- the list of English stopwords (excluded from sentences when measuring semantic similarity);
(3) freqs.txt -- frequencies of English words on a large corpus, needed for the IC-weighting of word contribution.

The last two files (stopwords.txt and freqs.txt) are provided in the res folder, whereas the embeddings.txt are bundled into the binary (/binary/graphseg.jar) but omitted from the /source/res folder due to repository size constraints. You may choose to replace these default files (e.g., by using different embeddings or different stopword list), but make sure you name the new files exactly the same (i.e., embeddings.txt, stopwords.txt, and freqs.txt, respectively).  

Credit
========

In case you use GraphSeg in your research, please give approproate credit to our work by citing the following publication: 

@InProceedings{glavavs-nanni-ponzetto:2016:*SEM,
  author    = {Glava\v{s}, Goran  and  Nanni, Federico  and  Ponzetto, Simone Paolo},
  title     = {Unsupervised Text Segmentation Using Semantic Relatedness Graphs},
  booktitle = {Proceedings of the Fifth Joint Conference on Lexical and Computational Semantics},
  month     = {August},
  year      = {2016},
  address   = {Berlin, Germany},
  publisher = {Association for Computational Linguistics},
  pages     = {125--130},
  url       = {http://anthology.aclweb.org/S16-2016}
}

License
========

The GraphSeg tool is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0) license: https://creativecommons.org/licenses/by-nc-sa/4.0/

In short, this means:
 
(1) you must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use;
(2) you may not use the material for commercial purposes;
(3) if you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original  (i.e., CC BY-NC-SA 4.0).


Contact
========

Please address all questions about the GraphSeg tool and the *SEM publication to: 

Dr. Goran Glavaö
Data and Web Science Group
University of Mannheim

Email: goran@informatik.uni-mannheim.de
