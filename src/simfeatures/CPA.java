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
public class CPA {
    String [] left;
    String [] right;
    String [] verbs;
    CPA(String [] left, String [] right, String [] verbs){
        this.left=left;
        this.right=right;
        this.verbs=verbs;
    }
    String [] getLeft(){
        return left;
        
    }
    String [] getRight(){
        return right;
    }
    String [] getVerbs(){
        return verbs;
    }
    double hasSameNetwork(String v1, String v2){
        int l=0;
        int r=0;
        System.out.println("v1:"+v1+" v2:"+v2);
        if(v1.equals(v2))return 1;    
        for(int i=0;i<verbs.length;i++){
         //   System.out.println("v1:"+v1+" v2:"+v2+" "+i+""+verbs[i]);
            if(v1.equals(verbs[i]))l=1;
            if(v2.equals(verbs[i]))r=1;
            if(l==1 && r==1)return 1.0;
        }
        if(l==1 && r==1)return 1.0;
        else if(l!=r)return 0;
        else return 0.5;
    }
}
