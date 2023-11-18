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
//package bftsmart.demo.counter;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import graphql4smr.lib.GraphQL4SMR;

/**
 * Example replica that implements a BFT replicated service (a counter).
 * If the increment > 0 the counter is incremented, otherwise, the counter
 * value is read.
 * 
 * @author alysson
 */

public final class CounterServerSpark extends DefaultSingleRecoverable  {
    
    //private int counter = 0;
    private int iterations = 0;

    private GraphQL4SMR graphQL4SMR = new GraphQL4SMR();
    
    public CounterServerSpark(int id) {
    	new ServiceReplica(id, this, this);
    }
            
    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {         
        iterations++;
        System.out.println(HelloWorld.returnString() + "(" + iterations + ") Counter current value: " + graphQL4SMR.counter.getValue());
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(4);
            new DataOutputStream(out).writeUTF(graphQL4SMR.requestjson("{counter}"));
            return out.toByteArray();
        } catch (IOException ex) {
            System.err.println("Invalid request received!");
            return new byte[0];
        }
    }
  
    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        iterations++;
        try {
            String query2 = new DataInputStream(new ByteArrayInputStream(command)).readUTF();
            //counter += increment;
            System.out.println("query: " + query2);
            System.out.println("(" + iterations + ") Counter was incremented. Current value = " + graphQL4SMR.counter.getValue());
            
            ByteArrayOutputStream out = new ByteArrayOutputStream(4);
            String response = graphQL4SMR.requestjson(query2);
            System.out.println("response: " + response);
            new DataOutputStream(out).writeUTF(response);
            return out.toByteArray();
        } catch (IOException ex) {
            System.err.println("Invalid request received!");
            return new byte[0];
        }
    }

    public static void main(String[] args){
        if(args.length < 1) {
            System.out.println("Use: java CounterServer <processId>");
            System.exit(-1);
        }      
        new CounterServerSpark(Integer.parseInt(args[0]));
    }

    
    @SuppressWarnings("unchecked")
    @Override
    public void installSnapshot(byte[] state) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(state);
            ObjectInput in = new ObjectInputStream(bis);
            graphQL4SMR = (GraphQL4SMR)in.readObject();
            in.close();
            bis.close();
        } catch (IOException e) {
            System.err.println("[ERROR] Error deserializing state: "
                    + e.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException" +
                                " is caught");
        }
    }

    @Override
    public byte[] getSnapshot() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(graphQL4SMR);
            out.flush();
            bos.flush();
            out.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException ioe) {
            System.err.println("[ERROR] Error serializing state: "
                    + ioe.getMessage());
            return "ERROR".getBytes();
        }
    }
}
