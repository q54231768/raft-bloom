package com.example.bloomserver.tools;

public class AlignOutput {



    public static void output(String strs[]){

        for (int i=0;i<strs.length;++i){
            String blank="";
            int rest=16-strs[i].length();
            for(int j=0;j<rest;++j){
                blank=blank+" ";
            }
            System.out.print(strs[i]+blank+"\t");
        }
        System.out.println();
    }





}
