import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Zhou Jianyu on 2016/8/9.
 */
class RegulationRecord{
    String wordName;
    String regulationDocName;
    String wordContext;
    String rawWordContext;
    Integer contexId;
    Integer docId;
    String swid;
    Integer contextWordNum;
    RegulationRecord(String wn,String rdn,String wc,String rwc,Integer cid,Integer did,String swid,Integer contextWordNum){
        this.wordName=wn;this.regulationDocName=rdn;
        this.wordContext = wc;this.contexId = cid;
        this.rawWordContext = rwc;this.docId = did;
        this.swid = swid; this.contextWordNum = contextWordNum;
    }
}

public class RegulationStorager {
    Vector<RegulationDoc> regulationDocs;
    Vector<RegulationRecord> regulationRecords;
    HashMap<String,SimWords> simWordsDict;
    RegulationStorager(Vector<RegulationDoc> rd,SimDictLoader dictLoader){
        this.regulationDocs=rd;
        regulationRecords=new Vector<>();
        this.simWordsDict = dictLoader.simWordsHashMap;
    }
    public void map2record(){
        HashMap<String,WordRecord>wordRecordHashMap;
        for(int i = 0;i<regulationDocs.size();i++){
            wordRecordHashMap = regulationDocs.get(i).invertedList;
            Iterator<Map.Entry<String,WordRecord>> entries = wordRecordHashMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry<String,WordRecord> e = entries.next();
                String wordName = e.getKey();
                String regulationDocName = e.getValue().docName;
                Vector<Paragraph> paras= e.getValue().paragraph;
                for(int j = 0;j<paras.size();j++){
                    Paragraph para = paras.get(j);
                    if(simWordsDict.containsKey(wordName)){
                        Vector<String> simWords = simWordsDict.get(wordName).simWords;
                        String swid = simWordsDict.get(wordName).swid;
                        regulationRecords.add(new RegulationRecord(wordName,regulationDocName,para.content,para.raw_content,para.id,i,swid,para.wordNum));
                        for(int k = 0;k<simWords.size();k++){
                            regulationRecords.add(new RegulationRecord(simWords.get(k),regulationDocName,para.content,para.raw_content,para.id,i,swid,para.wordNum));
                        }
                    }
                }
            }
        }
    }
    public void store(String storeFileName)throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(storeFileName);
        OutputStreamWriter storageWriter = new OutputStreamWriter(fileOutputStream,"GBK");
        for(int i = 0;i<this.regulationRecords.size();i++){
            RegulationRecord r = this.regulationRecords.get(i);
            String line = r.docId+"@"+r.wordName+"@"+r.swid+"@"+r.regulationDocName+"@"+r.contexId+"@"+"\""+r.rawWordContext.substring(0,r.rawWordContext.length()-2)+"\"@"+r.contextWordNum+"\r\n";
            storageWriter.append(line);
        }
        storageWriter.close();
    }
}
