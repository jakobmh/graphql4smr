package graphql4smr.lib.performancetest;

import de.uniulm.vs.art.uds.UDSLock;
import de.uniulm.vs.art.uds.UDScheduler;
import graphql.parser.ParserOptions;
import graphql4smr.lib.GraphQL4SMRStringWrapper;
import graphql4smr.lib.SleepUtil;
import graphql4smr.lib.schemawithdata.CRUDDofInternalFormat;
import graphql4smr.lib.schemawithdata.ErdosRenyiSchema;
import graphql4smr.lib.schemawithdata.GraphQLSchemaDumper;
import graphql4smr.lib.schemawithdata.InternalFormat;
import graphql4smr.lib.util.LockBuilder;

import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Performancetest {

    private UDScheduler uds = null;
    private int N = 20;
    private int n = 0;
    private int recursiondepth;

    private static final int recursiondepthmax = 9;
    private InternalFormat internalFormat;
    private CRUDDofInternalFormat crudDofInternalFormat;

    public static void main(String[] args) {
        /*
        InternalFormat internalFormat = ErdosRenyiSchema.getSchema(false);
        //System.out.println(new GraphQLSchemaDumper(internalFormat).createGraphQLSchema());

        CRUDDofInternalFormat crudDofInternalFormat = new CRUDDofInternalFormat(internalFormat);

        //System.out.println(new GraphQLSchemaDumper(internalFormat).toJsonPP());

        System.out.println(crudDofInternalFormat.schemaprinter());

        String exampleReadRequest = crudDofInternalFormat.exampleReadRequest();

        System.out.println(exampleReadRequest);
        System.out.println(crudDofInternalFormat.request(exampleReadRequest));

        {
            String exampleReadRequest2 = crudDofInternalFormat.exampleReadRequestRecursion(4);

            System.out.println(exampleReadRequest2);
            System.out.println(crudDofInternalFormat.request(exampleReadRequest2));

        }
        //System.out.println(crudDofInternalFormat.request("{select_node0(id:\"100\") {id,node3}}"));
        //System.out.println(crudDofInternalFormat.request("{read_node0 {id,node3{id}}}"));
        */
        boolean disableLocks = false;

        Performancetest performancetest = new Performancetest();
        performancetest.setup();
        if(disableLocks) performancetest.internalFormat.disablealllocks();

        System.out.println(performancetest.test(disableLocks));

        System.out.println(performancetest.testmultithreaded(disableLocks));

    }

    public Performancetest(){

        //ParserOptions.setDefaultParserOptions(ParserOptions.newParserOptions().maxTokens(15).build());


    }

    public Performancetest(UDScheduler uds){
        this.uds = uds;
    }


    private void setup(){
        if(uds!=null){
            internalFormat = ErdosRenyiSchema.getSchema(false, 0.5, N,new LockBuilder() {
                AtomicInteger atomicInteger = new AtomicInteger(10000);
                @Override
                public Lock build() {
                    System.out.println(atomicInteger.get());
                    return  new UDSLock(uds,atomicInteger.incrementAndGet());
                }
            });
        }else{
            internalFormat = ErdosRenyiSchema.getSchema(false, 0.5, N);
        }
        //System.out.println(new GraphQLSchemaDumper(internalFormat).createGraphQLSchema());

        crudDofInternalFormat = new CRUDDofInternalFormat(internalFormat);

    }

    public static String test(boolean disableLocks){

        Performancetest performancetest = new Performancetest();
        performancetest.N = 20;
        performancetest.setup();
        if(disableLocks) performancetest.internalFormat.disablealllocks();
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<recursiondepthmax;i++){
            performancetest.recursiondepth = i;
            builder.append(i + " " + performancetest.runtest(10)/10 + "\n");
            System.out.println("parsed " + i );
        }
        return builder.toString();
    }


    private long runtest(int howmanythreads){
        //setup

        String exampleReadRequest2 = crudDofInternalFormat.exampleReadRequestRecursion(recursiondepth);
        crudDofInternalFormat.cachegetGraphQLObjectType();
        crudDofInternalFormat.cacheGraphQL();

        long start = System.currentTimeMillis();

        for(int i =0; i < howmanythreads; i++){
            String requestbody = crudDofInternalFormat.request(exampleReadRequest2);
        }

        long finish = System.currentTimeMillis();
        //System.out.println(requestbody);

        long timeElapsed = finish - start;
        return timeElapsed;
    }


    private void runtestwithoutmessure(String exampleReadRequest2){
        //setup
        String requestbody = crudDofInternalFormat.request(exampleReadRequest2);
        System.out.println(requestbody);
    }

    public static String testmultithreaded(boolean disableLocks){
        Performancetest performancetest = new Performancetest();
        performancetest.N = 20;
        performancetest.setup();
        if(disableLocks) performancetest.internalFormat.disablealllocks();
        performancetest.crudDofInternalFormat.cachegetGraphQLObjectType();
        performancetest.crudDofInternalFormat.cacheGraphQL();

        StringBuilder builder = new StringBuilder();
        for(int i=0; i<recursiondepthmax;i++){
            performancetest.recursiondepth = i;
            builder.append(i + " " + performancetest.runtestmultithread(10)/10 + "\n");
            System.out.println("parsed " + i );
        }
        return builder.toString();
    }


    private long runtestmultithread(int howmanythreads){
        //setup

        String exampleReadRequest2 = crudDofInternalFormat.exampleReadRequestRecursion(recursiondepth);


        LinkedList<Thread> list = new LinkedList();
        for (int i = 0; i < howmanythreads; i++) {
            int i_copy = i;
            Runnable runnable = () -> {
                runtestwithoutmessure(exampleReadRequest2);
                System.out.println("Thread" + i_copy + "fertig !!!");
            };
            Thread tempthread = new Thread(runnable);
            tempthread.setName("Thread" + i);
            list.add(tempthread);
        }

        long start = System.currentTimeMillis();

        for (Thread thread : list) {
            thread.start();
        }

        for (Thread tempthread : list) {
            try {
                tempthread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long finish = System.currentTimeMillis();

        long timeElapsed = finish - start;
        return timeElapsed;
    }


    public static String testmultithreadeduds(boolean disableLocks){
        int counter = recursiondepthmax;

        StringBuilder builder = new StringBuilder();
        for(int i=0; i<counter;i++){
            UDScheduler uds = new UDScheduler(10);
            System.out.println("getCurrentUDSConfigurationPrimaries" + uds.getCurrentUDSConfigurationPrimaries());

            Performancetest performancetest = new Performancetest(uds);
            performancetest.N = 20;
            performancetest.setup();
            if(disableLocks) performancetest.internalFormat.disablealllocks();

            String exampleReadRequest2 = performancetest.crudDofInternalFormat.exampleReadRequestRecursion(i);
            builder.append(i + " " + performancetest.runtestmultithreaduds(exampleReadRequest2,10)/10 + "\n");
            System.out.println("parsed " + i );
        }
        return builder.toString();
    }


    private long runtestmultithreaduds(String exampleReadRequest2, int howmanythreads){

        Runnable runnable = () -> {
            runtestwithoutmessure(exampleReadRequest2);
            System.out.println("ok ich bin fertig !!!");
        };

        /*
        uds.addRequest(() -> uds.requestReconfiguration(udsPrimaries,
                udsSteps), () -> {
            System.out.println("finish reconfig");
        });
         */

        long start = System.currentTimeMillis();

        for (int i = 0; i < howmanythreads; i++) {
            String threadname = "Thread" + i;
            uds.addRequest(runnable, () -> {
                System.out.println("finsih" + threadname);
            });
        }

        uds.blockTerminate();

        long finish = System.currentTimeMillis();

        long timeElapsed = finish - start;
        return timeElapsed;
    }
}
