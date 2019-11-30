/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patator.patcher;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import java.io.IOException;
import com.sun.tools.attach.VirtualMachine;
/**
 *
 * @author int3h
 */
public class MainClass {
      public static void main(String args[]) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
          System.out.println("Attaching to ... " + args[0]);
          VirtualMachine vm = VirtualMachine.attach(args[0]);
          vm.loadAgent("/home/int3h/wrk/agentExample/target/agentExample-1.0-SNAPSHOT.jar");
          
      }
}
