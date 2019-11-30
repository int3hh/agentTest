/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tester.jartest;

import java.io.IOException;


/**
 *
 * @author int3h
 */
public class App {
    
    public static boolean  withdraw() {
        return false;
    }
    
     public static void main(String[] args) throws IOException {
         System.out.println("Hello");
         while (true) {
             System.in.read();
             if (withdraw()) {
                 System.out.println("OK OK OK");
             } else {
                 System.out.println("FAIL FAIL FAIL");
             }
         }
     }
}
