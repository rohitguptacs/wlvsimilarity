WLVSimilarity
=============

This project extract the features which can be used to train a semantic similarity and/or textual entailment system.
We have used these features for our system in [UoW: NLP Techniques Developed at the University of Wolverhampton for Semantic Similarity and Textual Entailment](http://alt.qcri.org/semeval2014/cdrom/pdf/SemEval139.pdf).

### External Libraries Required
- Stanford CoreNLP
- WordNet
- [PPDB Paraphases](http://paraphrase.org/#/download)

### Usage
`java -jar SimFeatures your_input_text_file`

`your_input_text_file`: The file should tab separated sentences with two sentences per line.
The output will be in file simfeatures.txt.
  
