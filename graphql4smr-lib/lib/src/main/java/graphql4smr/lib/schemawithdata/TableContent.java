package graphql4smr.lib.schemawithdata;

import graphql4smr.lib.util.FakeReentrantLock;
import graphql4smr.lib.util.LockBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TableContent {
    private String table;
    private List<ColumnEntry> table_values;

    transient
    private Lock tablelock = new ReentrantLock();

    public TableContent() {
    }

    public TableContent(String table, List<ColumnEntry> table_values) {
        this.table = table;
        this.table_values = table_values;
    }

    public void disableLock(){
        tablelock = new FakeReentrantLock();
    }

    public TableContent(LockBuilder lockBuilder){
        this.tablelock = lockBuilder.build();
    }

    public void getTable(WriteAccess.WriteAccessInterface<String> writeAccessInterface) {
        TableContent tableContent = this;
        new WriteAccess<String>() {
            @Override
            public String getValue() {
                return tableContent.table;
            }
            @Override
            public void setValue(String table) {
                tableContent.table = table;
            }
            @Override
            public Lock getLock() {
                return tablelock;
            }
        }.setValue(writeAccessInterface);
    }

    public void getTable_unsave(WriteAccess.WriteAccessInterface<String> writeAccessInterface) {
        TableContent tableContent = this;
        new WriteAccess<String>() {
            ReentrantLock trash_lock = new ReentrantLock();
            @Override
            public String getValue() {
                return tableContent.table;
            }
            @Override
            public void setValue(String table) {
                tableContent.table = table;
            }
            @Override
            public Lock getLock() {
                return trash_lock;
            }
        }.setValue(writeAccessInterface);
    }

    public void setTable(WriteAccess.WriteAccessInterface<String> writeAccessInterface) {
        TableContent tableContent = this;
        new WriteAccess<String>() {
            @Override
            public String getValue() {
                return tableContent.table;
            }
            @Override
            public void setValue(String table) {
                tableContent.table = table;
            }
            @Override
            public Lock getLock() {
                return tablelock;
            }
        }.setValue(writeAccessInterface);
    }


    public void getTable_values(WriteAccess.WriteAccessInterface<List<ColumnEntry>> writeAccessInterface) {
        TableContent tableContent = this;
        new WriteAccess<List<ColumnEntry>>() {
            @Override
            public List<ColumnEntry> getValue() {
                return tableContent.table_values;
            }
            @Override
            public void setValue(List<ColumnEntry> table_values) {
                tableContent.table_values = table_values;
            }
            @Override
            public Lock getLock() {
                return tablelock;
            }
        }.setValue(writeAccessInterface);
    }


    public void getTable_values_unsave(WriteAccess.WriteAccessInterface<List<ColumnEntry>> writeAccessInterface) {
        TableContent tableContent = this;
        new WriteAccess<List<ColumnEntry>>() {

            ReentrantLock trash_lock = new ReentrantLock();
            @Override
            public List<ColumnEntry> getValue() {
                return tableContent.table_values;
            }
            @Override
            public void setValue(List<ColumnEntry> table_values) {
                tableContent.table_values = table_values;
            }
            @Override
            public Lock getLock() {
                return trash_lock;
            }
        }.setValue(writeAccessInterface);
    }

    public void setTable_values(WriteAccess.WriteAccessInterface<List<ColumnEntry>> writeAccessInterface) {
        TableContent tableContent = this;
        new WriteAccess<List<ColumnEntry>>() {
            @Override
            public List<ColumnEntry> getValue() {
                return tableContent.table_values;
            }
            @Override
            public void setValue(List<ColumnEntry> table_values) {
                tableContent.table_values = table_values;
            }
            @Override
            public Lock getLock() {
                return tablelock;
            }
        }.setValue(writeAccessInterface);
    }
}
