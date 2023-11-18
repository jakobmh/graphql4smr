package graphql4smr.lib.schemawithdata;

import graphql4smr.lib.util.Util;

public class ResourceHolder {
    public String include_str(String path){return Util.include_str(getClass(), path);}

    public String example4 = include_str("example4sqldump.sql");

    public static ResourceHolder singleton = new ResourceHolder();

}
