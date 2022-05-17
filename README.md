This repository provides you w/ ressources of the publication 📄 `A Framework for Second-level Agenda Setting in Parliamentary Debates through the Lense of Comparative Agenda Topics` at the [ParlaCLARIN III 2022](https://www.clarin.eu/ParlaCLARIN-III) workshop co-located with [LREC 2022](https://lrec2022.lrec-conf.org/en/).

## 📚 Datasets in `data`
* our distant supervised `TRAIN` and `TEST` sets for topic classification
* manual annotated subset `TEST` of parliamentary debates in `data/test_annotation`
* our automatic created word-based related `SEGMENTS` of parliamentary debates

## 🧱 Segments extraction and mapping
* segments extraction
* segments mapping [![Open In Colab](https://colab.research.google.com/assets/colab-badge.svg)](https://colab.research.google.com/drive/1d2u49Wez7xPynru0f19iIqYBpndFlgxd?usp=sharing)

## 🏃‍♀️ Training scripts in `training`
* LM [GermanBert](https://huggingface.co/bert-base-german-cased) fine-tuning on [DeuParl](https://tudatalib.ulb.tu-darmstadt.de/handle/tudatalib/2889?show=full) [![Open In Colab](https://colab.research.google.com/assets/colab-badge.svg)](https://colab.research.google.com/drive/1ZfeRODHwEdSGAda_fAatrRAO39OFVD5d?usp=sharing)
* task-specific fine-tuning on topics from the [Comparative Agendas Project](https://www.comparativeagendas.net) [![Open In Colab](https://colab.research.google.com/assets/colab-badge.svg)](https://colab.research.google.com/drive/1QOkTN80OslVMPJcwsT-UwvltKore6VO2?usp=sharing)

## 🤖 Models on HuggingFace
[Pipelines](https://huggingface.co/docs/transformers/main_classes/pipelines) are an easy way to use our models for inference:
```python
from transformers import pipeline

model_name = "chkla/German-ParlBert"
tokenizer_name = "bert-base-german-cased"
text = "Sachgebiet Ausschließliche Gesetzgebungskompetenz des Bundes über die Zusammenarbeit des Bundes und der Länder zum Schutze der freiheitlichen demokratischen Grundordnung, des Bestandes und der Sicherheit des Bundes oder eines Landes Wir fragen die Bundesregierung"

pipe_classification_topics = pipeline("text-classification", model=model_name, tokenizer=tokenizer_name, return_all_scores=False, device=0)
prediction = pipe_classification_topics(text)
print(prediction)

```

### Cite
```
@article{klamm2022selast,
  title={A Framework for Second-level Agenda Setting in Parliamentary Debates through the Lense of Comparative Agenda Topics},
  author={Klamm, Christopher and Rehbein, Ines and Ponzetto, Simone},
  journal={ParlaCLARIN III at LREC2022},
  year={2022}
}
```

_Contact: christopher.klamm@uni.mannheim.de_
