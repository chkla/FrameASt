This repository provides you with ressources of the publication 📄 `FrameASt: A Framework for Second-level Agenda Setting in Parliamentary Debates through the Lense of Comparative Agenda Topics` [[Paper]](http://www.lrec-conf.org/proceedings/lrec2022/workshops/ParlaCLARINIII/pdf/2022.parlaclariniii-1.13.pdf) [[Slides]]() at the [ParlaCLARIN III 2022](https://www.clarin.eu/ParlaCLARIN-III) workshop co-located with [LREC 2022](https://lrec2022.lrec-conf.org/en/)

## 📚 01 Datasets
* `TRAIN` set for supervised topic classification ➡️ Comparative Agendas Project > [Interpellations (Germany)](https://comparativeagendas.s3.amazonaws.com/datasetfiles/anfrage_1976-2005_website-release_2.5.csv)
* manual annotated subset `TEST` of parliamentary debates in `data/test_annotation`

## 🧱 02 Segments
* segments extraction [(Glavas et al. 2016)](https://aclanthology.org/S16-2016/)
* automatic created word-based related `SEGMENTS` for parliamentary debates with distant supervised topic labels in `data/segments.0.1-1`

## 🤖 03 Models
* LM [GermanBERT](https://huggingface.co/bert-base-german-cased) fine-tuned on [DeuParl](https://tudatalib.ulb.tu-darmstadt.de/handle/tudatalib/2889?show=full) on 🤗 HuggingFace `parlbert-german`
* task-specific fine-tuning on parlamentary speeches with CAP topic labels from the [Comparative Agendas Project]([https://www.comparativeagendas.net](https://www.comparativeagendas.net/datasets_codebooks)) on 🤗 HuggingFace `parlbert-topics-german`

### Cite
```
@article{klamm2022frameast,
  title={FrameASt: A Framework for Second-level Agenda Setting in Parliamentary Debates through the Lense of Comparative Agenda Topics},
  author={Klamm, Christopher and Rehbein, Ines and Ponzetto, Simone},
  journal={ParlaCLARIN III at LREC2022},
  year={2022}
}
```

_Contact: christopher.klamm@uni.mannheim.de_
