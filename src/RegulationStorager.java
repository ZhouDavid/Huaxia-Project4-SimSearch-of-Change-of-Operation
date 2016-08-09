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
    RegulationRecord(String wn,String rdn,String wc,String rwc,Integer cid){
        this.wordName=wn;this.regulationDocName=rdn;
        this.wordContext = wc;this.contexId = cid;
        this.rawWordContext = rwc;
    }
}
public class RegulationStorager {
    Vector<RegulationDoc> regulationDocs;
    Vector<RegulationRecord> regulationRecords;
    RegulationStorager(Vector<RegulationDoc> rd){
        this.regulationDocs=rd;
        regulationRecords=new Vector<>();
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
                    regulationRecords.add(new RegulationRecord(wordName,regulationDocName,para.content,para.raw_content,para.id));
                }
            }
        }
    }
}
