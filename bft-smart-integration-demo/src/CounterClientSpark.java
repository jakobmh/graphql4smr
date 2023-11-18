/**
Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


//package de.jakobmh.bftsmart.demo.counter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bftsmart.tom.ServiceProxy;


import graphql4smr.demo.App;
import graphql4smr.demo.ProxyGraphQLSMR;


/**
 * Example client that updates a BFT replicated service (a counter).
 * 
 * @author alysson
 */
public class CounterClientSpark {

    public static void main(String[] args) throws IOException {
        ServiceProxy counterProxy = new ServiceProxy(Integer.parseInt(args[0]));

        App app = new App(new ProxyGraphQLSMR(){
            public String proxy(String query){
                if (args.length < 2) {
                    System.out.println("Usage: java ... CounterClient <process id> <increment> [<number of operations>]");
                    System.out.println("       if <increment> equals 0 the request will be read-only");
                    System.out.println("       default <number of operations> equals 1000");
                    System.exit(-1);
                }

                
                
                try {

                    //int inc = 0; //Integer.parseInt(args[1]);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    new DataOutputStream(out).writeUTF(query);

                    //System.out.print("Invocation " + i);
                    byte[] reply = (false)?
                            counterProxy.invokeUnordered(out.toByteArray()):
                        counterProxy.invokeOrdered(out.toByteArray()); //magic happens here
                    
                    if(reply != null) {
                        String newValue = new DataInputStream(new ByteArrayInputStream(reply)).readUTF();
                        System.out.println(", returned value: " + newValue);
                        return newValue;
                    } else {
                        System.out.println(", ERROR! Exiting.");
                        return "error";
                    }
                } catch(IOException | NumberFormatException e){
                    counterProxy.close();
                }
                counterProxy.close();
                return "request ist working!";
            }
        });

    }
}
