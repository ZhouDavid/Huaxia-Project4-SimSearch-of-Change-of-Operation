import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Zhou Jianyu on 2016/10/8.
 */
class SimWords{
    Vector<String> simWords;//同义词集合
    String swid;   //同义词标号
    String attr;  //同义词，相关词，封闭词
    SimWords(String swid,String attr){
        this.swid = swid;
        this.attr = attr;
        simWords = new Vector<>();
    }
}
public class SimDictLoader {
    HashMap<String,SimWords> simWordsHashMap = new HashMap<>();
    BufferedReader reader;
    SimDictLoader(String dictName)throws IOException{
        InputStreamReader isr = new InputStreamReader(new FileInputStream(dictName));
        reader = new BufferedReader(isr);
        String line = reader.readLine();
        while(line!=null){
            String [] set = line.split(" ");
            String swid = set[0].substring(0,set[0].length()-1); //获取同义词编号，如：La06D03
            String attr = set[0].substring(set[0].length()-1,set[0].length()); //获取词的性质，如= @ #
            //
            for(int i = 1;i<set.length;i++){
                int curKeyPos = i;
                String word = set[curKeyPos];
                SimWords simWordSet = new SimWords(swid,attr);
                for(int j = 1;j<set.length;j++){
                    if(j!=curKeyPos){
                        simWordSet.simWords.add(set[j]);
                    }
                }
                simWordsHashMap.put(word,simWordSet);
            }
            line = reader.readLine();
        }
    }
}
