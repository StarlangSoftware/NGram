# NGram
For Developers
============

## Requirements

* [Java Development Kit 8 or higher](#java), Open JDK or Oracle JDK
* [Maven](#maven)
* [Git](#git)

### Java 

To check if you have a compatible version of Java installed, use the following command:

    java -version
    
If you don't have a compatible version, you can download either [Oracle JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or [OpenJDK](https://openjdk.java.net/install/)    

### Maven
To check if you have Maven installed, use the following command:

    mvn --version
    
To install Maven, you can follow the instructions [here](https://maven.apache.org/install.html).     

### Git

Install the [latest version of Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).

## Download Code

In order to work on code, create a fork from GitHub page. 
Use Git for cloning the code to your local or below line for Ubuntu:

	git clone <your-fork-git-link>

A directory called SpellChecker will be created. Or you can use below link for exploring the code:

	git clone https://github.com/olcaytaner/NGram.git

## Open project with IntelliJ IDEA

Steps for opening the cloned project:

* Start IDE
* Select **File | Open** from main menu
* Choose `NGram/pom.xml` file
* Select open as project option
* Couple of seconds, dependencies with Maven will be downloaded. 

<!--- See the snapshot of the project at the beginning:

// put the link of ss

![Main IDE page](https://github.com/master/dev/site/images/zemberek-ide-main.png))

Search






--->
## Compile

**From IDE**

After being done with the downloading and Maven indexing, select **Build Project** 

Search





option from **Build** menu. After compilation process, user can run `NGram`.

**From Console**

Use below line to generate jar file:

     mvn install


------------------------------------------------

NGram
============
+ [Maven Usage](#maven-usage)
+ [Training NGram](#training-ngram)
+ [Saving NGram](#saving-ngram)
+ [Loading NGram](#loading-ngram)


### Maven Usage

    <groupId>NlpToolkit</groupId>
    <artifactId>NGram</artifactId>
    <version>1.0.0</version>
    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>
    <repositories>
        <repository>
            <id>NlpToolkit</id>
            <url>http://haydut.isikun.edu.tr:8081/artifactory/NlpToolkit</url>
        </repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>NlpToolkit</groupId>
            <artifactId>Sampling</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>NlpToolkit</groupId>
            <artifactId>DataStructure</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>NlpToolkit</groupId>
            <artifactId>Dictionary</artifactId>
            <version>1.0.1</version>
        </dependency>
        <dependency>
            <groupId>NlpToolkit</groupId>
            <artifactId>Math</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>

## Training NGram
     

* `NGram` can be created and trained either by using `addNGram()` method or using a `Corpus` as follows:  
    * Using `addNGram()`,
    
            Sentence sentence = new Sentence("Yarın okula gideceğim");
            NGram ngram = new NGram(1);
            ngram.addNGram(sentence.getWords().toArray());
            
    * Using a `Corpus`,
    
            Corpus corpus = new Corpus("corpus.txt"); 
            NGram ngram = new NGram(corpus.getAllWordsAsArrayList(), 1);
        
* *To use `Corpus`,  snippet below should be added to dependencies.*
        
                <dependency>
                    <groupId>NlpToolkit</groupId>
                    <artifactId>Corpus</artifactId>
                    <version>1.0.0</version>
                </dependency>        
* To calculate probabilities, there are possible smoothing methods. These smoothing methods are divided into two categories as `SimpleSmoothing` and `TrainedSmoothing`. Their usages are as follows:     
    * `SimpleSmooting` methods are:
        * `LaplaceSmoothing`
    
                ngram.calculateNGramProbabilities(new LaplaceSmoothing());
                
        * `GoodTuringSmoothing`
        
                ngram.calculateNGramProbabilities(new GoodTuringSmoothing());
                
        * `NoSmoothing`
                
                ngram.calculateNGramProbabilities(new NoSmoothing());

        * `NoSmoothingWithDictionary`
        
            A dictionary of nonrare words are required to initialize this. This can be created as follows and then probabilities can be calculated:
                
                HashSet<Symbol> dictionary = ngram.constructDictionaryWithNonRareWords(1, 0.1);
                ngram.calculateNGramProbabilities(new NoSmoothingWithDictionary(dictionary));
                
        * `NoSmoothingWithNonRareWords`
                
                ngram.calculateNGramProbabilities(new NoSmoothingWithNonRareWords(0.1));

    * `TrainedSmoothing` methods are:
        * `AdditiveSmoothing`
                
                ngram.calculateNGramProbabilities(corpus.getAllWordsAsArrayList(), new InterpolatedSmoothing());
        * `InterpolatedSmoothing`               
            
                ngram.calculateNGramProbabilities(corpus.getAllWordsAsArrayList(), new AdditiveSmoothing<>());

## Saving NGram
    
* NGrams can be saved to a file as follows:

        ngram.save("ngram.model");
              

## Loading NGram            
* Loading from an existing model:
 
        try {
            FileInputStream inFile = new FileInputStream("ngram.model");  
            ObjectInputStream inObject = new ObjectInputStream(inFile);
            NGram ngram = (NGram<Word>) inObject.readObject();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
 

            


