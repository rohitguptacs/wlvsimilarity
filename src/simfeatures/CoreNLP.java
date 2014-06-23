/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simfeatures;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;

import edmatch.EDMatch;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import Measures.CommonWords;
import Measures.BLEU;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.io.File;
import java.util.Scanner;

/**
 *
 * @author rohit
 */
public class CoreNLP {
   
    private List<List<String> > poss1;
    private List<List<String> > lemmas1;
    private List<List<String> > words1;
    private List<List<String> > nes1;
    private List<List<String> > govdeps1;
    private List<List<String> > grels1;
    private List<String> root1;
    
    private final String filecontents1;
    private final String filecontents2;
    private List<List<String> > poss2;
    private List<List<String> > lemmas2;
    private List<List<String> > words2;
    private List<List<String> > nes2;
    private List<List<String> > govdeps2;
    private List<List<String> > grels2;
    private List<String> root2;

    
    private  List<Double> lemmacwv;
    private  List<Double> poscwv;
    private  List<Double> wordscwv;
    private  List<Double> nescwv;
    private  List<Double> govdepscwv;
    private List<Double> corefv;
    private List<Double> grelcwv;
    private List<Double> surbleu;
    private List<Double> posbleu;
    private List<Double> lemmableu;
    private List<Double> cpav;
    private List<Double> ppv;
    private List<Double> negv;


    
    private List<CPA> cpalist;
    
    private List< List<Double> > allfeaturevalues;
    private List<String> nameoffeatures;
    
    CoreNLP(String filecontents1,String filecontents2){
        this.filecontents1=filecontents1;
        this.filecontents2=filecontents2;
        initialize1(filecontents1);
        initialize2(filecontents2);
        allfeaturevalues=new ArrayList();
        nameoffeatures=new ArrayList();
    }
    
    List<Double> extractPPFeature(String infile){
        short [] typp={1,2,3,4};
        boolean placeholder=false;
        String ppfilename="//Users//rohit//expert//corpusparaphrase//ppdbllexlphrasal.txt";
        
        EDMatch edmatch=new EDMatch();
        edmatch.semeval(typp, placeholder, ppfilename, infile);
        ppv=new ArrayList();
        System.out.println("EDMATCHALLPHRASES:"+edmatch.allphrases1.size()+" "+edmatch.allphrases2.size());
        for(int i=0;i<edmatch.allphrases1.size() && i<edmatch.allphrases2.size();i++){
            CommonWords cw=new CommonWords(edmatch.allphrases1.get(i),edmatch.allphrases2.get(i));
            System.out.println("PPValue:"+cw.getValue());
            ppv.add(cw.getValue());
        }
        allfeaturevalues.add(ppv);
        nameoffeatures.add("PPNG");
        return ppv;
    }
    
    double calnegative(List<String> s, List<String> t){
        int sneg=0;
        int tneg=0;
        for(int i=0;i<s.size();i++){
            if(s.get(i).equals("No")||s.get(i).equals("no")||s.get(i).equals("Never")||s.get(i).equals("never")||s.get(i).equals("Not")||s.get(i).equals("not"))
             sneg=1;
        }
        for(int i=0;i<t.size();i++){
        if(t.get(i).equals("No")||t.get(i).equals("no")||t.get(i).equals("Never")||t.get(i).equals("never")||t.get(i).equals("Not")||t.get(i).equals("not"))
             tneg=1;   
        }
        if(sneg==tneg)return 1;
        else return 0;
   }
    
    List<Double> extractNegFeature(){
        negv=new ArrayList();
        for(int i=0;i<words1.size() && i<words2.size();i++){
            double negvalue=calnegative(words1.get(i),words2.get(i));
            negv.add(negvalue);
        }
        allfeaturevalues.add(negv);
        nameoffeatures.add("NEG");
        return negv;
    }
    
    List<Double> extractCorefFeature(){
        corefv=new ArrayList();
        try{
        Properties props = new Properties();
        //Users//rohit//NetBeansProjects//SimFeatures//src//simfeatures//
        props.load(new FileInputStream("corefprop.properties"));
        StanfordCoreNLP pipeline1 = new StanfordCoreNLP(props);
        Annotation annotation1;
       // annotation = new Annotation("Obama addressed today about some core issues. \n Some important matters were addressed by him.");
        String [] fc1=filecontents1.split("\n");
        String [] fc2=filecontents2.split("\n");
        for(int i=0;i<fc1.length && i<fc2.length;i++){
            String contents=fc1[i]+"\n"+fc2[i];
        annotation1 = new Annotation(contents);
        pipeline1.annotate(annotation1);
        //pipeline.prettyPrint(annotation, out);
        Map<Integer, CorefChain> graph = annotation1.get(CorefChainAnnotation.class);
        Iterator< Map.Entry<Integer, CorefChain> > it= graph.entrySet().iterator();
        HashMap<Integer,Integer> cid=new HashMap();
        int ccount=0;
            for(;it.hasNext();) {
                CorefChain c =   it.next().getValue();
                List<CorefMention> lcf= c.getMentionsInTextualOrder();
                Iterator<CorefMention> lit= lcf.iterator();
                while(lit.hasNext()){
                    CorefMention cf= lit.next();
           //         System.out.println("ClusterID:"+cf.corefClusterID+" "+cf.mentionSpan+" SENNO:"+cf.sentNum+" HEADINDX:"+cf.headIndex);
      //      if(lit.hasNext())fw3.write(cf.mentionSpan+" SENNO:"+cf.sentNum+" HEADINDX:"+cf.headIndex+" ||| ");
      //      else fw3.write(cf.mentionSpan+" SENNO:"+cf.sentNum+" HEADINDX:"+cf.headIndex+"");
                if(cid.containsKey(cf.corefClusterID)){
                    if(cid.get(cf.corefClusterID)!=cf.sentNum)ccount++;
                }else{
                    cid.put(cf.corefClusterID, cf.sentNum);
                 }
                }
            }
            if(!cid.isEmpty())
            corefv.add(Math.round(1000.0* (double)ccount/(cid.size()))/1000.0);
            else corefv.add(0.0);
         //   System.out.println();
        
        }
        }catch(IOException e){
            System.err.print(e);
        }
        nameoffeatures.add("COREF");
        allfeaturevalues.add(corefv);
        return  corefv;
    }
    
    List<Double> extractCPAFeature(File file){
        cpalist = new ArrayList();
        cpav=new ArrayList();
        try{
        Scanner sc= new Scanner(file);
            while(sc.hasNextLine()){
                String sen=sc.nextLine();
                String tmp[]=sen.split("\t");
          //      System.out.println(tmp[0]);
                String [] info=tmp[0].split(" V ");
                String [] left;
                String [] right;
                if(info.length>=2){
                    left=info[0].split("\\s+");
                    right=info[1].split("\\s+");
                }else{
                    left=info[0].split("\\s+");
                    right=new String[1];
                    right[0]="<NULL>";
                }
            //    System.out.println(tmp[1]);
                String [] verbs=tmp[1].split("\\s+");
              cpalist.add(new CPA(left,right,verbs));
            }
            for(int i=0;i<root1.size();i++){
                double value=0;
                for(CPA cpa:cpalist){
                    double current=cpa.hasSameNetwork(root1.get(i),root2.get(i));
                    if(current==1)value=current;
                }
                cpav.add(value);
            }
            
        }catch(IOException e){
            System.err.println("CPA Feature file error:"+e);}
        allfeaturevalues.add(cpav);
        nameoffeatures.add("CPA");
        return cpav;
    }
    
    List<Double> extractPOSFeature(){
        poscwv=new ArrayList();
        for(int i=0;i<poss1.size() && i<poss2.size();i++){
            CommonWords cw=new CommonWords(poss1.get(i),poss2.get(i));
            poscwv.add(cw.getValue());
        }
        allfeaturevalues.add(poscwv);
        nameoffeatures.add("POS");
        return poscwv;
    }
    
    List<Double> extractSurBLEUFeature(){
        surbleu=new ArrayList();
        for(int i=0;i<words1.size() && i<words2.size();i++){
            BLEU bleu=new BLEU(words1.get(i),words2.get(i));
            surbleu.add(bleu.getValue());
        }
        allfeaturevalues.add(surbleu);
        nameoffeatures.add("SBLEU");
        return surbleu;
    }
    
    List<Double> extractLemmaBLEUFeature(){
        lemmableu=new ArrayList();
        for(int i=0;i<words1.size() && i<words2.size();i++){
            BLEU bleu=new BLEU(words1.get(i),words2.get(i));
            lemmableu.add(bleu.getValue());
        }
        allfeaturevalues.add(lemmableu);
        nameoffeatures.add("LBLEU");
        return lemmableu;
    }
    
    List<Double> extractPOSBLEUFeature(){
        posbleu=new ArrayList();
        for(int i=0;i<words1.size() && i<words2.size();i++){
            BLEU bleu=new BLEU(words1.get(i),words2.get(i));
            posbleu.add(bleu.getValue());
        }
        allfeaturevalues.add(posbleu);
        nameoffeatures.add("PBLEU");
        return posbleu;
    }
    
    List<Double> extractGrelFeature(){
        grelcwv=new ArrayList();
        for(int i=0;i<grels1.size() && i<grels2.size();i++){
            CommonWords cw=new CommonWords(grels1.get(i),grels2.get(i));
            grelcwv.add(cw.getValue());
        }
        allfeaturevalues.add(grelcwv);
        nameoffeatures.add("GRAREL");
        return grelcwv;
    }
    
    List<Double> extractLemmaFeature(){
        lemmacwv=new ArrayList();
        for(int i=0;i<lemmas1.size() && i<lemmas2.size();i++){
            CommonWords cw=new CommonWords(lemmas1.get(i),lemmas2.get(i));
            lemmacwv.add(cw.getValue());
        }
        allfeaturevalues.add(lemmacwv);
        nameoffeatures.add("LEMMA");
        return lemmacwv;
    }
    List<Double> extractSurfWordFeature(){
        wordscwv=new ArrayList();
        for(int i=0;i<words1.size() && i<words2.size();i++){
            CommonWords cw=new CommonWords(words1.get(i),words2.get(i));
            wordscwv.add(cw.getValue());
        }
        allfeaturevalues.add(wordscwv);
        nameoffeatures.add("SURWORD");
        return wordscwv;
    }
    List<Double> extractNEFeature(){
        nescwv=new ArrayList();
        for(int i=0;i<nes1.size() && i<nes2.size();i++){
            CommonWords cw=new CommonWords(nes1.get(i),nes2.get(i));
            nescwv.add(cw.getValue());
        }
        allfeaturevalues.add(nescwv);
        nameoffeatures.add("NE");
        return nescwv;
    }
    List<Double> extractGovDepFeature(){
        govdepscwv=new ArrayList();
        for(int i=0;i<govdeps1.size() && i<govdeps2.size();i++){
            CommonWords cw=new CommonWords(govdeps1.get(i),govdeps2.get(i));
            govdepscwv.add(cw.getValue());
        }
        allfeaturevalues.add(govdepscwv);
        nameoffeatures.add("GOVDEP");
        return govdepscwv;
    }
    
    public int printAllFeatures(File file){
        try{
            FileWriter fw=new FileWriter(file);
            for(String name:nameoffeatures){   
                fw.write(name+"\t");    //print the names of features extracted
            }
            fw.write("\n");
            if(allfeaturevalues.isEmpty()){ fw.close();
                System.err.println("No features to print");
            return 0;}
            for(int i=0;i<allfeaturevalues.get(0).size();i++){
                String featurevector="";
                for(int j=0;j<allfeaturevalues.size();j++){
                    featurevector+=allfeaturevalues.get(j).get(i)+"\t";
                }
                featurevector=featurevector.trim();
                fw.write(featurevector+"\n");
            }
            fw.close();
        }catch(IOException e){
            System.out.println(e);
        }
        return 0;
    }
    
    final int initialize1(String filecontents){
    try{
        Properties props = new Properties();
        props.load(new FileInputStream("newproperties.properties"));
        StanfordCoreNLP pipeline1 = new StanfordCoreNLP(props);
        Annotation annotation1;
       // annotation = new Annotation("Obama addressed today about some core issues. \n Some important matters were addressed by him.");
        annotation1 = new Annotation(filecontents);
        pipeline1.annotate(annotation1);
        //pipeline.prettyPrint(annotation, out);
     /*   Map<Integer, CorefChain> graph = annotation1.get(CorefChainAnnotation.class);
        Iterator< Map.Entry<Integer, CorefChain> > it= graph.entrySet().iterator();
    
        for(;it.hasNext();) {
            CorefChain c =   it.next().getValue();
            List<CorefMention> lcf= c.getMentionsInTextualOrder();
            Iterator<CorefMention> lit= lcf.iterator();
            while(lit.hasNext()){
                CorefMention cf= lit.next();
                System.out.println("ClusterID:"+cf.corefClusterID+" "+cf.mentionSpan+" SENNO:"+cf.sentNum+" HEADINDX:"+cf.headIndex);
      //      if(lit.hasNext())fw3.write(cf.mentionSpan+" SENNO:"+cf.sentNum+" HEADINDX:"+cf.headIndex+" ||| ");
      //      else fw3.write(cf.mentionSpan+" SENNO:"+cf.sentNum+" HEADINDX:"+cf.headIndex+"");
            }
        } */
    // An Annotation is a Map and you can get and use the various analyses individually.
    // For instance, this gets the parse tree of the first sentence in the text.
    List<CoreMap> sentences1 = annotation1.get(CoreAnnotations.SentencesAnnotation.class);
    //List<CoreMap> sentences = annotation.get(CoreAnnotations.LemmaAnnotation.class);
     poss1=new ArrayList();
     lemmas1=new ArrayList();
     words1=new ArrayList();
     nes1=new ArrayList();
     govdeps1=new ArrayList();
     grels1=new ArrayList();
     root1=new ArrayList();

    for(CoreMap sentence: sentences1) {
      // traversing the words in the current sentence
      // a CoreLabel is a CoreMap with additional token-specific methods
        //String sfw="";
        List<String> posl=new ArrayList();
        List<String> lemmal=new ArrayList();
        List<String> wordl=new ArrayList();
        List<String> nel=new ArrayList();
        List<String> govdep=new ArrayList();
        List<String> grel=new ArrayList();
        
        for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
        // this is the text of the token
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            wordl.add(word);
        // Lemma annotation
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            lemmal.add(lemma);
        // this is the POS tag of the token
            String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            posl.add(pos);
        // this is the NER label of the token
            String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            nel.add(ne);
       // System.out.print(word+"|"+lemma+"|"+pos+"|"+ne+' ');
        //fw.write(word+"|"+lemma+"|"+pos+"|"+ne+' ');
        }
        poss1.add(posl);
        lemmas1.add(lemmal);
        words1.add(wordl);
        nes1.add(nel);
        
        Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        // Get dependency tree
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
        Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
       // System.out.println(td);

        Object[] list = td.toArray();
   //     System.out.println(list.length);
        TypedDependency typedDependency;
        for (Object object : list) {
        typedDependency = (TypedDependency) object;
    //    System.out.println("DepdencyName:"+typedDependency.dep().nodeString()+ " :: "+ "Node:"+typedDependency.reln());
            //  System.out.println("Depdency Name"typedDependency.dep().nodeString()+ " :: "+ "Node"+typedDependency.reln());
           // if (typedDependency.reln().getShortName().equals("root")) {
                 //       System.out.println(typedDependency.gov().value()+"::"+typedDependency.dep().value());
                        govdep.add(typedDependency.gov().value()+"::"+typedDependency.dep().value());
                        grel.add(typedDependency.reln().getShortName());
                        if(typedDependency.reln().getShortName().equals("root")){
                            root1.add(lemmal.get(typedDependency.dep().index()-1));
                         //   System.out.println(typedDependency.gov().index());
                            System.out.println(lemmal.get(typedDependency.dep().index()-1));
                           // System.out.println(typedDependency.dep().value()+typedDependency.dep().index());
                        
                        }
                //your code
          //  }
            //////////////////////////////
        }
        govdeps1.add(govdep);
        grels1.add(grel);
    
    }
    }catch(IOException e){
        System.err.print(e);
    }
    return 0;
    }
    
    final int initialize2(String filecontents){
    try{
        Properties props = new Properties();
        //Users//rohit//NetBeansProjects//SimFeatures//src//simfeatures//
        props.load(new FileInputStream("newproperties.properties"));
        StanfordCoreNLP pipeline1 = new StanfordCoreNLP(props);
        Annotation annotation1;
       // annotation = new Annotation("Obama addressed today about some core issues. \n Some important matters were addressed by him.");
        annotation1 = new Annotation(filecontents);
        pipeline1.annotate(annotation1);
        //pipeline.prettyPrint(annotation, out);
   /*     Map<Integer, CorefChain> graph = annotation.get(CorefChainAnnotation.class);
        Iterator< Map.Entry<Integer, CorefChain> > it= graph.entrySet().iterator();
    
        for(;it.hasNext();) {
            CorefChain c =   it.next().getValue();
            List<CorefMention> lcf= c.getMentionsInTextualOrder();
            Iterator<CorefMention> lit= lcf.iterator();
            while(lit.hasNext()){
                CorefMention cf= lit.next();
                System.out.println("ClusterID:"+cf.corefClusterID+" "+cf.mentionSpan+" SENNO:"+cf.sentNum+" HEADINDX:"+cf.headIndex);
      //      if(lit.hasNext())fw3.write(cf.mentionSpan+" SENNO:"+cf.sentNum+" HEADINDX:"+cf.headIndex+" ||| ");
      //      else fw3.write(cf.mentionSpan+" SENNO:"+cf.sentNum+" HEADINDX:"+cf.headIndex+"");
            }
        } */
    // An Annotation is a Map and you can get and use the various analyses individually.
    // For instance, this gets the parse tree of the first sentence in the text.
    List<CoreMap> sentences1 = annotation1.get(CoreAnnotations.SentencesAnnotation.class);
    //List<CoreMap> sentences = annotation.get(CoreAnnotations.LemmaAnnotation.class);
     poss2=new ArrayList();
     lemmas2=new ArrayList();
     words2=new ArrayList();
     nes2=new ArrayList();
     govdeps2=new ArrayList();
     grels2=new ArrayList();
     root2= new ArrayList();
    
    for(CoreMap sentence: sentences1) {
      // traversing the words in the current sentence
      // a CoreLabel is a CoreMap with additional token-specific methods
        //String sfw="";
        List<String> posl=new ArrayList();
        List<String> lemmal=new ArrayList();
        List<String> wordl=new ArrayList();
        List<String> nel=new ArrayList();
        List<String> govdep=new ArrayList();
        List<String> grel=new ArrayList();
        
        for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
        // this is the text of the token
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            wordl.add(word);
        // Lemma annotation
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            lemmal.add(lemma);
        // this is the POS tag of the token
            String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            posl.add(pos);
        // this is the NER label of the token
            String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            nel.add(ne);
       // System.out.print(word+"|"+lemma+"|"+pos+"|"+ne+' ');
        //fw.write(word+"|"+lemma+"|"+pos+"|"+ne+' ');
        }
        poss2.add(posl);
        lemmas2.add(lemmal);
        words2.add(wordl);
        nes2.add(nel);
        
        Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        // Get dependency tree
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
        Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
  //      System.out.println(td);

        Object[] list = td.toArray();
     //   System.out.println(list.length);
        TypedDependency typedDependency;
        for (Object object : list) {
        typedDependency = (TypedDependency) object;
     //   System.out.println("DepdencyName:"+typedDependency.dep().nodeString()+ " :: "+ "Node:"+typedDependency.reln());
            //  System.out.println("Depdency Name"typedDependency.dep().nodeString()+ " :: "+ "Node"+typedDependency.reln());
           // if (typedDependency.reln().getShortName().equals("root")||typedDependency.reln().getShortName().equals("nsubj")||typedDependency.reln().getShortName().equals("prep")) {
                //        System.out.println(typedDependency.gov().value()+"::"+typedDependency.dep().value());
                        govdep.add(typedDependency.gov().value()+"::"+typedDependency.dep().value());
                        grel.add(typedDependency.reln().getShortName());
                        if(typedDependency.reln().getShortName().equals("root")){
                            root2.add(lemmal.get(typedDependency.dep().index()-1));
                        }
                //your code
          //  }
            //////////////////////////////
        }
        govdeps2.add(govdep);
        grels2.add(grel);
    
    }
    }catch(IOException e){
        System.err.print(e);
    }
    return 0;
    }
    
    
}
