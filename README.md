# NGram

For Developers
============
You can also see [Python](https://github.com/olcaytaner/NGram-Py), [C++](https://github.com/olcaytaner/NGram-CPP), or [C#](https://github.com/olcaytaner/NGram-CS) repository.

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

## Maven Usage

        <dependency>
            <groupId>io.github.starlangsoftware</groupId>
            <artifactId>NGram</artifactId>
            <version>1.0.5</version>
        </dependency>

------------------------------------------------

Detailed Description
============
+ [Training NGram](#training-ngram)
+ [Using NGram](#using-ngram)
+ [Saving NGram](#saving-ngram)
+ [Loading NGram](#loading-ngram)

## Training NGram
     
Boş bir NGram modeli oluşturmak için

	NGram(int N)

Örneğin,

	a = NGram(2);

boş bir bigram modeli oluşturulmaktadır.

NGram'a bir cümle eklemek için

	void addNGramSentence(Symbol[] symbols)

Örneğin,

	String[] text1 = {"ali", "topu", "at", "mehmet", "ayşe", "gitti"};
	String[] text2 = {"ali", "top", "at", "ayşe", "gitti"};
	nGram = new NGram<String>(2);
	nGram.addNGramSentence(text1);
	nGram.addNGramSentence(text2);

satırları ile boş bir bigram oluşturulup, text1 ve text2 cümleleri bigram modeline 
eklenir.

NoSmoothing sınıfı smoothing için kullanılan en basit tekniktir. Eğitim gerektirmez, sadece
sayaçlar kullanılarak olasılıklar hesaplanır. Örneğin verilen bir NGram'ın NoSmoothing ile 
olasılıklarının hesaplanması için

	a.calculateNGramProbabilities(new NoSmoothing());

LaplaceSmoothing sınıfı smoothing için kullanılan basit bir yumuşatma tekniğidir. Eğitim 
gerektirmez, her sayaca 1 eklenerek olasılıklar hesaplanır. Örneğin verilen bir NGram'ın 
LaplaceSmoothing ile olasılıklarının hesaplanması için

	a.calculateNGramProbabilities(new LaplaceSmoothing());

GoodTuringSmoothing sınıfı smoothing için kullanılan eğitim gerektirmeyen karmaşık bir 
yumuşatma tekniğidir. Verilen bir NGram'ın GoodTuringSmoothing ile olasılıklarının 
hesaplanması için

	a.calculateNGramProbabilities(new GoodTuringSmoothing());

AdditiveSmoothing sınıfı smoothing için kullanılan eğitim gerektiren bir yumuşatma 
tekniğidir.

	a.calculateNGramProbabilities(new AdditiveSmoothing());

## Using NGram

Bir NGram'ın olasılığını bulmak için

	double getProbability(Symbol ... symbols)

Örneğin, bigram olasılığını bulmak için

	a.getProbability("ali", "topu")

trigram olasılığını bulmak için

	a.getProbability("ali", "topu", "at")

## Saving NGram
    
NGram modelini kaydetmek için

	void saveAsText(String fileName)

Örneğin, a modelini "model.txt" dosyasına kaydetmek için

	a.saveAsText("model.txt");              

## Loading NGram            

Var olan bir NGram modelini yüklemek için

	NGram(String fileName)

Örneğin,

	a = NGram("model.txt");

model.txt dosyasında bulunan bir NGram modelini yükler.
