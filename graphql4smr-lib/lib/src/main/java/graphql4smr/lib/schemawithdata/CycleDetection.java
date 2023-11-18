package graphql4smr.lib.schemawithdata;

import java.util.List;

// Algorithmus wurde hier übernommen
// https://www.geeksforgeeks.org/detect-cycle-in-a-graph/
public class CycleDetection {

    InternalFormat internalFormat;
    public CycleDetection(InternalFormat internalFormat) {
        this.internalFormat = internalFormat;
    }

    private boolean iscyclic;

    public boolean isCyclic()
    {
        iscyclic = false;
        internalFormat.getTable_schema(new WriteAccess.WriteAccessInterface<List<TableSchema>>() {
            @Override
            public List<TableSchema> readAndWrite(List<TableSchema> tableSchemas) {
                int V = tableSchemas.size();
                // Mark all the vertices as not visited and
                // not part of recursion stack
                boolean[] visited = new boolean[V];
                boolean[] recStack = new boolean[V];

                // Call the recursive helper function to
                // detect cycle in different DFS trees
                for (int i = 0; i < V; i++)
                    if (isCyclicUtil(i, visited, recStack,tableSchemas)){
                        iscyclic = true;
                        return tableSchemas;
                    }
                return tableSchemas;
            }
        });


        return iscyclic;
    }

    // Function to check if cycle exists
    private boolean isCyclicUtil(int i, boolean[] visited,
                                 boolean[] recStack,List<TableSchema> tableSchemas)
    {

        // Mark the current node as visited and
        // part of recursion stack
        if (recStack[i])
            return true;

        if (visited[i])
            return false;

        visited[i] = true;

        recStack[i] = true;
        TableSchema children = tableSchemas.get(i);

        for (FieldSchema c: children.fields) {
            if(!c.field_type.equals("foreign_key"))
                continue;

            int tempint = -1;

            for (int i2 = 0; i2< tableSchemas.size(); i2++) {
                TableSchema temp = tableSchemas.get(i2);
                if (temp.table.equals(c.references_table)) {
                    tempint = i2;
                    break;
                }
            }
            if(tempint == -1)continue;
            if (isCyclicUtil(tempint, visited, recStack,tableSchemas))
                return true;
        }
        recStack[i] = false;

        return false;
    }
}
