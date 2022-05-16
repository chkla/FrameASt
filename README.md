This repository provides you w/ ressources of the publication 📄 `A Framework for Second-level Agenda Setting in Parliamentary Debates through the Lense of Comparative Agenda Topics` at the [ParlaCLARIN III 2022](https://www.clarin.eu/ParlaCLARIN-III) co-located with [LREC 2022](https://lrec2022.lrec-conf.org/en/).

## 📚 Datasets in `data`
* our distant supervised `TRAIN` and `TEST` sets for topic classification
* manual annotated subset `TEST` of parliamentary debates to test our topic classification approach in detail

## 🏃‍♀️ Training scripts in `training`
* ParlaBertarian LM pre-trained on [DeuParl](https://tudatalib.ulb.tu-darmstadt.de/handle/tudatalib/2889?show=full) [![Open In Colab](https://colab.research.google.com/assets/colab-badge.svg)](https://colab.research.google.com/drive/1XVVK6bKycfzft0cRsrokhgy80HXlWGHF?usp=sharing)
* GermanBert fine-tuning on [DeuParl](https://tudatalib.ulb.tu-darmstadt.de/handle/tudatalib/2889?show=full) [![Open In Colab](https://colab.research.google.com/assets/colab-badge.svg)](https://colab.research.google.com/drive/1ZfeRODHwEdSGAda_fAatrRAO39OFVD5d?usp=sharing)
* SVM

## 🤖 Models on HuggingFace
[Pipelines](https://huggingface.co/docs/transformers/main_classes/pipelines) are an easy way to use our models for inference:
```python
from transformers import pipeline
model_name = "chkla/German-Bert-Bundestag"
tokenizer_name = "bert-base-german-cased"
text = "Angela Merkel ist eine Politikerin in Deutschland und Vorsitzende der CDU"

pipe_classification_topics = pipeline("text-classification", model=model_name, tokenizer=tokenizer_name, return_all_scores=False, device=0)
prediction = pipe_classification_topics(text)
print(prediction)

```

## 📊 Experiments in `analysis`
* segments mapping

```
@article{klamm2022selast,
  title={SeLASt: A Framework for Second-level Agenda Setting in Parliamentary Debates through the Lense of Comparative Agenda Topics},
  author={Klamm, Christopher and Rehbein, Ines and Ponzetto, Simone},
  journal={ParlaCLARIN III at LREC2022},
  year={2022}
}
```

_Contact: christopher.klamm@uni.mannheim.de_
