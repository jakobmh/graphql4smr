package graphql4smr.lib.schemawithdata;

import graphql4smr.lib.util.LockBuilder;
import org.checkerframework.checker.units.qual.C;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InternalFormat {

    //private LockBuilder lockBuilder = null;
    private Config config;

    private List<TableSchema> table_schema;
    private List<TableContent> table_content;

    transient
    private Lock globallock = new ReentrantLock();

    public InternalFormat(Config config, List<TableSchema> table_schema, List<TableContent> table_content) {
        this.config = config;
        this.table_schema = table_schema;
        this.table_content = table_content;
    }

    public InternalFormat(LockBuilder lockBuilder){
        this.config = new Config();
        this.table_schema = new LinkedList<>();
        this.table_content = new LinkedList<>();
        this.globallock = lockBuilder.build();
        //this.lockBuilder = lockBuilder;
    }
    public InternalFormat() {
        this.config = new Config();
        this.table_schema = new LinkedList<>();
        this.table_content = new LinkedList<>();
    }

    public TableSchema gettable_schemaByTableName(String tablename){

        TableSchema tableSchema= this.table_schema.stream()
                .filter(e2 -> {return e2.table.equals(tablename);})
                .findAny().get();
        return tableSchema;
    }




    public void getTable_contentByTableName(String tablename, WriteAccess.WriteAccessInterface<TableContent> writeAccessInterface){
        InternalFormat internalFormat = this;

        getTable_content(new WriteAccess.WriteAccessInterface<List<TableContent>>() {
            @Override
            public List<TableContent> readAndWrite(List<TableContent> tableContents) {
                 final List<TableContent> tableContentHolder = new LinkedList<>();
                 tableContents.stream()
                    .forEach(e2 -> {
                        e2.getTable(new WriteAccess.WriteAccessInterface<String>() {
                            @Override
                            public String readAndWrite(String s) {
                                if(s.equals(tablename)){
                                    tableContentHolder.add(e2);
                                };

                                return s;
                            }
                        });
                    });
                writeAccessInterface.readAndWrite(tableContentHolder.get(0));

                return tableContents;
            }
        });
    }


    public void getConfig(WriteAccess.WriteAccessInterface<Config> writeAccessInterface) {
        InternalFormat internalFormatsuper = this;
        new WriteAccess<Config>() {
            @Override
            public Config getValue() {
                return internalFormatsuper.config;
            }
            @Override
            public void setValue(Config config) {
                internalFormatsuper.config = config;
            }
            @Override
            public Lock getLock() {
                return globallock;
            }
        }.setValue(writeAccessInterface);
    }

    public void setConfig(WriteAccess.WriteAccessInterface<Config> writeAccessInterface) {
        InternalFormat internalFormatsuper = this;
        new WriteAccess<Config>() {
            @Override
            public Config getValue() {
                return internalFormatsuper.config;
            }
            @Override
            public void setValue(Config config) {
                internalFormatsuper.config = config;
            }
            @Override
            public Lock getLock() {
                return globallock;
            }
        }.setValue(writeAccessInterface);
    }

    public void setConfig_unsave(WriteAccess.WriteAccessInterface<Config> writeAccessInterface) {
        InternalFormat internalFormatsuper = this;
        new WriteAccess<Config>() {
            ReentrantLock trash_lock = new ReentrantLock();
            @Override
            public Config getValue() {
                return internalFormatsuper.config;
            }
            @Override
            public void setValue(Config config) {
                internalFormatsuper.config = config;
            }
            @Override
            public Lock getLock() {
                return trash_lock;
            }
        }.setValue(writeAccessInterface);
    }


    public void getTable_schema(WriteAccess.WriteAccessInterface<List<TableSchema>> writeAccessInterface) {
        InternalFormat internalFormatsuper = this;
        new WriteAccess<List<TableSchema>>() {
            @Override
            public List<TableSchema> getValue() {
                return internalFormatsuper.table_schema;
            }
            @Override
            public void setValue(List<TableSchema> table_schema) {
                internalFormatsuper.table_schema = table_schema;
            }
            @Override
            public Lock getLock() {
                return globallock;
            }
        }.setValue(writeAccessInterface);
    }


    public void getTable_schema_unsave(WriteAccess.WriteAccessInterface<List<TableSchema>> writeAccessInterface) {
        InternalFormat internalFormatsuper = this;
        new WriteAccess<List<TableSchema>>() {
            ReentrantLock trash_lock = new ReentrantLock();
            @Override
            public List<TableSchema> getValue() {
                return internalFormatsuper.table_schema;
            }
            @Override
            public void setValue(List<TableSchema> table_schema) {
                internalFormatsuper.table_schema = table_schema;
            }
            @Override
            public Lock getLock() {
                return trash_lock;
            }
        }.setValue(writeAccessInterface);
    }


    public void setTable_schema(WriteAccess.WriteAccessInterface<List<TableSchema>> writeAccessInterface ) {
        InternalFormat internalFormatsuper = this;
        new WriteAccess<List<TableSchema>>() {
            @Override
            public List<TableSchema> getValue() {
                return internalFormatsuper.table_schema;
            }
            @Override
            public void setValue(List<TableSchema> table_schema) {
                internalFormatsuper.table_schema = table_schema;
            }
            @Override
            public Lock getLock() {
                return globallock;
            }
        }.setValue(writeAccessInterface);
    }


    public void setTable_schema_unsave(WriteAccess.WriteAccessInterface<List<TableSchema>> writeAccessInterface ) {
        InternalFormat internalFormatsuper = this;
        new WriteAccess<List<TableSchema>>() {
            ReentrantLock trash_lock= new ReentrantLock();
            @Override
            public List<TableSchema> getValue() {
                return internalFormatsuper.table_schema;
            }
            @Override
            public void setValue(List<TableSchema> table_schema) {
                internalFormatsuper.table_schema = table_schema;
            }
            @Override
            public Lock getLock() {
                return trash_lock;
            } //unsave hack for init InternalFormat
        }.setValue(writeAccessInterface);
    }

    public void getTable_content(WriteAccess.WriteAccessInterface<List<TableContent>> writeAccessInterface) {
        InternalFormat internalFormatsuper = this;
        new WriteAccess<List<TableContent>>() {
            @Override
            public List<TableContent> getValue() {
                return internalFormatsuper.table_content;
            }
            @Override
            public void setValue(List<TableContent> table_content) {
                internalFormatsuper.table_content = table_content;
            }
            @Override
            public Lock getLock() {
                return globallock;
            }
        }.setValue(writeAccessInterface);
    }

    public void getTable_content_unsave(WriteAccess.WriteAccessInterface<List<TableContent>> writeAccessInterface) {
        InternalFormat internalFormatsuper = this;
        new WriteAccess<List<TableContent>>() {
            ReentrantLock trash_lock= new ReentrantLock();

            @Override
            public List<TableContent> getValue() {
                return internalFormatsuper.table_content;
            }
            @Override
            public void setValue(List<TableContent> table_content) {
                internalFormatsuper.table_content = table_content;
            }
            @Override
            public Lock getLock() {
                return trash_lock;
            }
        }.setValue(writeAccessInterface);
    }

    public void setTable_content(WriteAccess.WriteAccessInterface<List<TableContent>> writeAccessInterface) {
        InternalFormat internalFormatsuper = this;
        new WriteAccess<List<TableContent>>() {
            @Override
            public List<TableContent> getValue() {
                return internalFormatsuper.table_content;
            }
            @Override
            public void setValue(List<TableContent> table_content) {
                internalFormatsuper.table_content = table_content;
            }
            @Override
            public Lock getLock() {
                return globallock;
            }
        }.setValue(writeAccessInterface);
    }


    public void setTable_content_unsave(WriteAccess.WriteAccessInterface<List<TableContent>> writeAccessInterface) {
        InternalFormat internalFormatsuper = this;
        new WriteAccess<List<TableContent>>() {

            ReentrantLock trash_lock = new ReentrantLock();
            @Override
            public List<TableContent> getValue() {
                return internalFormatsuper.table_content;
            }
            @Override
            public void setValue(List<TableContent> table_content) {
                internalFormatsuper.table_content = table_content;
            }
            @Override
            public Lock getLock() {
                return trash_lock;
            }
        }.setValue(writeAccessInterface);
    }
}
