/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Measures;

/**
 *
 * @author rohit
 */
import java.util.Set;
import java.util.HashSet;
import java.util.List;
public class CommonWords {
    private double value;
    
    public CommonWords(String sent1,String sent2){
        String [] s1=sent1.split("\\s+");
        String [] s2=sent2.split("\\s+");
        Set set=new HashSet();
        Set set1=new HashSet();
        for(int i=0;i<s1.length;i++){
            set1.add(s1[i]);
            set.add(s1[i]);
        }
        Set set2=new HashSet();
        for(int i=0;i<s2.length;i++){
            set2.add(s2[i]);
            set.add(s2[i]);
        }
        value= Math.round(1000.0*(double)(set1.size()+set2.size()-set.size())/(double)(set.size()))/1000.0;
    }
    
   public CommonWords(List<String> sent1,List<String> sent2){
        //String [] s1=sent1.split("\\s+");
        //String [] s2=sent2.split("\\s+");
        Set set=new HashSet();
        Set set1=new HashSet();
        for(String s:sent1){
            set1.add(s);
            set.add(s);
        }
        Set set2=new HashSet();
        for(String s:sent2){
            set2.add(s);
            set.add(s);
        }
        System.out.println(set1.size()+" "+set2.size()+" "+set.size());
        this.value= Math.round(1000.0*(double)(set1.size()+set2.size()-set.size())/(double)(set.size()))/1000.0;
        System.out.println(value+" ");
        
    }
    
    public double getValue(){
        return value;
    }
}
