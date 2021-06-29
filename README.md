# Run QA system on your machine
This page provides instructions on how to run the QA system on your machine and add questions to it. The link of hackathon can be found [here](https://scdemo.techfak.uni-bielefeld.de/qahackathon/index.php/)

### Install QA system on your machine
Install docker (https://docs.docker.com/engine/install/)
1. Download the image of QA system. 
```
docker pull agsc/quegg-web:latest
```
2. Run the image as a container.
```
docker run -p "8089:8089" -e "QUEGG_ALLOW_UPLOADS=true" agsc/quegg-web:latest
```
Go to http://localhost:8089/quegg/ and the interface will be shown on your browser. Currently it is an empty QA system. It will initially be empty, a minimal example to get data into the running instance would be:

### Add questions to the QA system
The file [ExampleHackathonNounPPFrame] (https://raw.githubusercontent.com/ag-sc/QueGG-web/main/example/nounppframe.csv) contains an example of a lexical entry. Download the file.  
```
wget -O nounppframe.csv https://raw.githubusercontent.com/ag-sc/QueGG-web/main/example/nounppframe.csv
```
3. Add the quetions to the QA system
```
curl -X "POST" -F "file=@nounppframe.csv" "http://localhost:8089/quegg/import"      
```
4. To add more questions do the followings:
   a) add lexical entries at[Google XSL sheet](https://docs.google.com/spreadsheets/d/1NgH7GdFcAqQuYU3ziIXpq0Yybt4lZIR15DpPgaoXF4M/edit?usp=sharing). The guideline of writing a lexical entry for a grammar type can be seen [here](https://scdemo.techfak.uni-bielefeld.de/qahackathon/tutorial/coverage.php#id4). 
   b) Download the Google XSL sheet as csv. File>Download>Comma-seperated values(.csv, currentsheet).
   c) repeat the step 3.

Please use the following citation:
```
@inproceedings{Buono-LREC2020,
	title = {{Generating Grammars from lemon lexica for Questions Answering over Linked Data: a Preliminary Analysis}},
	author = {Viktoria Benz, Philipp Cimiano, Mohammad Fazleh Elahi, Basil Ell},
	booktitle = {In: NLIWOD workshop at ISWC 2020},
	pages = {40–55},
	year = {2020},
	link = {http://ceur-ws.org/Vol-2722/nliwod2020-paper-2.pdf}
}
```

## Developers
* **Mohammad Fazleh Elahi**
* **Frank Grimm**
## Supervisors
* **Dr. Philipp Cimiano**
* **Dr. Basil Ell**
## Acknowledgement
* **Viktoria Benz**

  

