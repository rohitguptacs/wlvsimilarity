/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simfeatures;

/**
 *
 * @author rohit
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/* command: java -jar -Xms1500m SimFeatures.jar ../../../coding/output-textfiles-task2/349699newsML-done.xml-input.xml.out ../../../coding/parseout/349699.out ../../../coding/parseout/349699.out.xml */

import java.io.*;
import java.io.FileWriter;
import java.util.*;
import java.lang.Iterable;
import java.io.File;

import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.dcoref.sievepasses.AliasMatch;
import edu.stanford.nlp.dcoref.CorefChain.*;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.*;
import edu.stanford.nlp.dcoref.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.process.Morphology;

public class SimFeatures {

    
  int CoreNLP(List<String> s1,List<String> s2){
      
      try{
      
      PrintWriter out;
      FileWriter fw;
      FileWriter fw3;
    
   // fw= new FileWriter(args[1]);
    
  //  fw3= new FileWriter(args[3]);
   // if (args.length > 1) {
       // fw= new FileWriter(args[1]);
      //out = new PrintWriter(args[1]);
   // } else {
     // out = new PrintWriter(System.out);
    //}
    
    Properties props = new Properties();
    props.load(new FileInputStream("/home/rohit/NetBeansProjects/SimFeatures/src/simfeatures/newproperties.properties"));
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation;
    annotation = new Annotation("Ram killed Ravan");
    
    pipeline.annotate(annotation);
    //pipeline.prettyPrint(annotation, out);
    Map<Integer, CorefChain> graph = annotation.get(CorefChainAnnotation.class);
    Iterator< Map.Entry<Integer, CorefChain> > it= graph.entrySet().iterator();
    
    for(;it.hasNext();) {

          CorefChain c =   it.next().getValue();
          List<CorefMention> lcf= c.getMentionsInTextualOrder();
          Iterator<CorefMention> lit= lcf.iterator();
          while(lit.hasNext()){
            CorefMention cf= lit.next();
    //        if(lit.hasNext())fw3.write(cf.mentionSpan+" SENNO:"+cf.sentNum+" HEADINDX:"+cf.headIndex+" ||| ");
      //      else fw3.write(cf.mentionSpan+" SENNO:"+cf.sentNum+" HEADINDX:"+cf.headIndex+"");
          }
        //  fw3.write('\n');
          //fw3.write(+'\n');
          // println "ClusterId: " + entry.getKey();
          //CorefMention cm = c.getRepresentativeMention();
          //println "Representative Mention: " + aText.subSequence(cm.startIndex, cm.endIndex);

         // List<CorefMention> cms = c.getCorefMentions();
          //println  "Mentions:  ";
          //cms.each { it -> 
           //   print aText.subSequence(it.startIndex, it.endIndex) + "|"; 
          //} 
           //     println ""          
        }
       // fw3.close();
    
    
    
    // An Annotation is a Map and you can get and use the various analyses individually.
    // For instance, this gets the parse tree of the first sentence in the text.
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    //List<CoreMap> sentences = annotation.get(CoreAnnotations.LemmaAnnotation.class);
    for(CoreMap sentence: sentences) {
      // traversing the words in the current sentence
      // a CoreLabel is a CoreMap with additional token-specific methods
      String sfw="";
        //for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
        // this is the text of the token
        String word = token.get(CoreAnnotations.TextAnnotation.class);
        // Lemma annotation
        String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
        // this is the POS tag of the token
        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
        // this is the NER label of the token
        String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
       // System.out.print(word+"|"+lemma+"|"+pos+"|"+ne+' ');
        //fw.write(word+"|"+lemma+"|"+pos+"|"+ne+' ');
        WordTag wt=Morphology.stemStaticSynchronized(word, pos);
       
        LabelFactory lf;
        lf = wt.labelFactory();
        
        String gen=token.getString(CoreAnnotations.MorphoGenAnnotation.class);
        String cs=token.getString(CoreAnnotations.MorphoCaseAnnotation.class);
        String num=token.getString(CoreAnnotations.MorphoNumAnnotation.class);
        String per=token.getString(CoreAnnotations.MorphoPersAnnotation.class);
        //String per=token.get();
        sfw+=word+"|"+pos+"|"+lemma+"|"+ne+"|"+gen+"|"+cs+"|"+num+"|"+per+" ";
      }
      sfw=sfw.trim();
        System.out.println(sfw);
      
    //  fw.write(sfw+'\n');

      // this is the parse tree of the current sentence
      //Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
      // this is coreference for current sentence
     // Tree tree = sentence.ge
      
      // this is the Stanford dependency graph of the current sentence
      //SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
    }
   // fw.close();
    
    
    if (sentences != null && sentences.size() > 0) {
      CoreMap sentence = sentences.get(0);
      Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    //  out.println();
    //  out.println("The first sentence parsed is:");
      //tree.pennPrint(out);
      CoreMap sentence2=sentences.get(1);
      Tree tree2 =sentence2.get(TreeCoreAnnotations.TreeAnnotation.class);
    //  out.println();
    //  out.println("The second sentence parsed is:");
      //tree2.pennPrint(out);
    }
    
      }catch(IOException e){
          System.err.println(e);
      }
    return 0;
  }  
  
  
  
  public static void main(String[] args) throws IOException {
    Scanner sc=new Scanner(new FileReader(args[0]));
    String s1="";
    String s2="";
    List<String> id=new ArrayList();
    List<Double> relatedness=new ArrayList();
    List<String> entailment=new ArrayList();
    sc.nextLine();
    while(sc.hasNextLine()){
        String sen=sc.nextLine();
        String tmp[]=sen.split("\t");
        id.add(tmp[0]);
        s1=s1+"\n"+tmp[1].trim();
        s2=s2+"\n"+tmp[2].trim();
     //   System.out.println(tmp[3]);
      //  relatedness.add(Double.parseDouble(tmp[3].trim()));
      //  entailment.add(tmp[4].trim());
    }
    s1=s1.trim();
    s2=s2.trim();
    
    CoreNLP corenlp=new CoreNLP(s1,s2);
    List<Double> swf=corenlp.extractSurfWordFeature();
    List<Double> lf=corenlp.extractLemmaFeature();
    List<Double> pf=corenlp.extractPOSFeature();
    List<Double> ne=corenlp.extractNEFeature();
    List<Double> gdf=corenlp.extractGovDepFeature();
    corenlp.extractCorefFeature();
    corenlp.extractGrelFeature();
    corenlp.extractSurBLEUFeature();
    corenlp.extractLemmaBLEUFeature();
    corenlp.extractPOSBLEUFeature();
    //corenlp.extractCPAFeature(new File("CPA-string-network.v-0.2"));
    corenlp.extractPPFeature(args[0]);
    corenlp.extractNegFeature();
    
    
    
    corenlp.printAllFeatures(new File("simfeatures.txt"));
  
  }
    

}