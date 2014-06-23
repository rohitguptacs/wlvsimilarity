/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Measures;

import lingutil.bleu.BleuMeasurer;
import java.util.List;
/**
 *
 * @author rohit
 */
public class BLEU {
            double bleuscore;
           public BLEU(String sent1, String sent2){
                String [] candTokens= sent1.split("\\s+");
                String [] refTokens=sent2.split("\\s+");
                BleuMeasurer  bm = new BleuMeasurer();
                bm.addSentence(refTokens, candTokens);
                bleuscore=bm.bleu();
            }
            
           public BLEU(List<String> sent1, List<String> sent2){
                String [] candTokens= sent1.toArray(new String[sent1.size()] );
                String [] refTokens=sent2.toArray(new String[sent2.size()] );
                BleuMeasurer  bm = new BleuMeasurer();
                bm.addSentence(refTokens, candTokens);
                bleuscore=Math.round(1000.0*bm.bleu())/1000.0;
            }
            
           public double getValue(){
                return bleuscore;
            }
            
}

