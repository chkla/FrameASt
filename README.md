This repository provides you with ressources of the publication 📄 `FrameASt: A Framework for Second-level Agenda Setting in Parliamentary Debates through the Lense of Comparative Agenda Topics` [[Paper]](http://www.lrec-conf.org/proceedings/lrec2022/workshops/ParlaCLARINIII/pdf/2022.parlaclariniii-1.13.pdf) [[Slides]](https://docs.google.com/presentation/d/15aXOCVXIoTMlmVNoaV-aDUwsnvAzZ0F3-I0FYbpyhKQ/edit?usp=sharing) at the [ParlaCLARIN III 2022](https://www.clarin.eu/ParlaCLARIN-III) workshop co-located with [LREC 2022](https://lrec2022.lrec-conf.org/en/)

## 📚 01 Datasets
* `TRAIN` set for supervised topic classification ➡️ Comparative Agendas Project > [Interpellations (Germany)](https://comparativeagendas.s3.amazonaws.com/datasetfiles/anfrage_1976-2005_website-release_2.5.csv)
* manual annotated subset `TEST` of parliamentary debates in `data/test_annotation`

## 🧱 02 Segments
* segments extraction `tool/graphseg-german` [(Glavas et al. 2016)](https://aclanthology.org/S16-2016/) [[Code]](https://bitbucket.org/gg42554/graphseg/src/master/) - our adapted German segmentation tool [[Code]](https://drive.google.com/drive/folders/1XOpgt76HxJgXSXrTR6HzCHD9bnE97bY2?usp=sharing)
* automatic created word-based related `SEGMENTS` for parliamentary debates with distant supervised topic labels in `data/segments.0.1-1`

```
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
```

## 🤖 03 Models
* LM [GermanBERT](https://huggingface.co/bert-base-german-cased) fine-tuned on [DeuParl](https://tudatalib.ulb.tu-darmstadt.de/handle/tudatalib/2889?show=full)
* task-specific fine-tuning on parlamentary speeches with CAP topic labels from the [Comparative Agendas Project]([https://www.comparativeagendas.net](https://www.comparativeagendas.net/datasets_codebooks)) on 🤗 HuggingFace `parlbert-topics-german`

```python
from transformers import pipeline

pipeline_classification_topics = pipeline("text-classification", model="chkla/parlbert-topic-german", tokenizer="bert-base-german-cased", return_all_scores=False)

text = "Sachgebiet Ausschließliche Gesetzgebungskompetenz des Bundes über die Zusammenarbeit des Bundes und der Länder zum Schutze der freiheitlichen demokratischen Grundordnung, des Bestandes und der Sicherheit des Bundes oder eines Landes Wir fragen die Bundesregierung"

pipeline_classification_topics(text) # Government
```


### Cite
```
@article{klamm-etal-2022-frameast,
  title={FrameASt: A Framework for Second-level Agenda Setting in Parliamentary Debates through the Lense of Comparative Agenda Topics},
  author={Klamm, Christopher and Rehbein, Ines and Ponzetto, Simone},
  journal={ParlaCLARIN III at LREC2022},
  year={2022}
}
```

_Contact: christopher.klamm@uni.mannheim.de_

