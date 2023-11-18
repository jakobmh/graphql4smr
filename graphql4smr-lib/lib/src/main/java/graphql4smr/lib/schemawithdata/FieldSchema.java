package graphql4smr.lib.schemawithdata;

import java.util.Optional;

public class FieldSchema {
    public String field_type;
    public String field_name;

    public Boolean primary_key;

    public String references_table;
    public String references_field;
}
