Hi Christopher,
here is Goran's code. Bitte die folgenden Infos mit auf die Seite setzen:



* Link for Goran's paper

https://aclanthology.org/S16-2016

* Bibtex:

@inproceedings{glavas-etal-2016-unsupervised,
    title = "Unsupervised Text Segmentation Using Semantic Relatedness Graphs",
    author = "Glava{\v{s}}, Goran  and
      Nanni, Federico  and
      Ponzetto, Simone Paolo",
    booktitle = "Proceedings of the Fifth Joint Conference on Lexical and Computational Semantics",
    month = aug,
    year = "2016",
    address = "Berlin, Germany",
    publisher = "Association for Computational Linguistics",
    url = "https://aclanthology.org/S16-2016",
    doi = "10.18653/v1/S16-2016",
    pages = "125--130",
}

* Link zum orig code (English):

https://bitbucket.org/gg42554/graphseg/src/master/


###

Die Readme-Datei ist von Goran, das Skript run.sh ruft den Segmenter auf.
Ich habe im folder data/input eine Beispieldatei, die man segmentieren 
kann.

Im script folder ist ein Skript, das den segmentierten output in separate 
Dateien schreibt (siehe Kommentare in run.sh).
Lass mich wissen, wenn Du Fragen hast. Es wär super, wenn Du kurz probieren
könntest, ob bei Dir alles läuft (einfach run.sh laufen lassen und das
Skript zum Splitten des outputs).

Es kann sein, dass man vorher noch kompilieren muss (je nach java version):
- ins source dir gehn und "mvn package" aufrufen (oder compile_mvn.sh ;)
- das muss man auch machen, wenn man die embeddings oder stopwordlisten auswechseln möchte;
  die neuen Resourcen müssen im folder "res" liegen (wie im pom spezifiziert) und werden
  dann ausgetauscht. 
  Ich würde das aber nicht im Detail beschreiben, da das nicht unser code ist. Die
  Info ist in Gorans README.

  Die Ressourcen fürs Deutsche, die im Moment benutzt werden, sind:
	- embeddings.txt:	407614 word embeddings (300 dimensions, lower-cased); ich vermute, dass das die von Nils Reimers sind, kann es aber nicht nachprüfen, da die embeddings nicht mehr online sind (Nils alte Seite ist nicht mehr da). Aber ich finde das nicht so wichtig, da wir den Segmenter als black box benutzen und nicht weiter evaluieren. 

	- stopwords.txt:	Snowball stopword list (+ dass; 232 entries) 
		https://github.com/apache/lucene/blob/main/lucene/analysis/common/src/resources/org/apache/lucene/analysis/snowball/german_stop.txt



