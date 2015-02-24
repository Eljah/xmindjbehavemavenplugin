package xmindjbehave.jbehave.meta;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import xmindjbehave.jbehave.MetaLanguageStatements;

import java.util.ArrayList;

/**
 * Created by Ilya Evlampiev on 22.02.15.
 */
public class MetaCombinatoricsTemplateImpl implements MetaTemplate {
    public MetaLanguageStatements metaKeyword=null;

    @Override
    public void validateTemplate() {

    }

    @Override
    public String parseMeta(String string) {
        Meta meta=null;

        int index_of_keywordstart=-1;
        int index_of_semicolon=-1;
        int index_of_bound1=-1;
        int index_of_comma1=-1;
        int index_of_comma2=-1;
        int index_of_bound2=-1;

        String val1;
        String val2;
        String val3;

        for (MetaLanguageStatements mst: MetaLanguageStatements.values())
        {
            String keywordname=mst.name();
            if (string.indexOf(keywordname)!=-1) {
                index_of_keywordstart=string.indexOf(keywordname);
                index_of_bound1=index_of_keywordstart+keywordname.length()+1;
                metaKeyword=mst;
                break;}
            //the first fount statement is a statement under process; the other statements are ommited for this run
        }
        if (metaKeyword==null || index_of_bound1==-1)
        {
           return string;
        }
        else
        {
            switch (metaKeyword)
            {
                case IntCountRange: meta=new MetaIntegerWithCount();
                case IntIncrementRange: meta=new MetaIntegerWithIncrement();
                default: new NotImplementedException();
            }

            //now parse bounds and values
            //parsing first bound
            meta.setStartBound(new Bound(string.charAt(index_of_bound1)));

            //parsing second bound
            String rest=string.substring(index_of_bound1);
            for(char coursor: rest.toCharArray() )
            {
                if (coursor==')'||coursor==']') {index_of_bound2=rest.indexOf(coursor);break;}
            }
            meta.setEndBound(new Bound(rest.charAt(index_of_bound2)));

            //parsing
            String values_comma_separated=rest.substring(0,index_of_bound2);

            index_of_comma1=values_comma_separated.indexOf(',');
            index_of_comma2=values_comma_separated.indexOf(',',index_of_comma1+1);

            val1=values_comma_separated.substring(1,index_of_comma1);
            val2=values_comma_separated.substring(index_of_comma1+1,index_of_comma2);
            val3=values_comma_separated.substring(index_of_comma2+1);

            //move indexes to initial string length basis
            index_of_bound2=index_of_bound2+index_of_bound1+1;
            index_of_comma1=index_of_comma1+index_of_bound1+1;
            index_of_comma2=index_of_comma2+index_of_bound1+1;

            //todo move parsing to the meta object side
            meta.setStartValue(Integer.parseInt(val1));
            meta.setEndValue(Integer.parseInt(val2));
            meta.setThirdValue(Integer.parseInt(val3));

            //todo validate according to  meta type; should be implemented within meta itself

            meta.addValues();

            //todo move conversion to string to the meta object
            ArrayList<Integer> respList=meta.getValues();

            String toReturn="";

            for (Integer val: respList)
            {

                String toReturnSubstring=string.replace(string.substring(index_of_keywordstart,index_of_bound2),val+"");
                toReturnSubstring=parseMeta(toReturnSubstring);
                toReturn+=toReturnSubstring+"\r\n";
                }
            toReturn=toReturn.replaceAll("\r\n\r\n","\r\n");
            return toReturn;
        }

    }
}
