import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Zhou Jianyu on 2016/7/27.
 */
class Count{
    Integer count;
    Vector<Integer> id=new Vector<>();
    Count(Integer count,Integer id){this.count = count;this.id.add(id);}
}
class Entity implements Comparable<Entity>{//最终的查询结果类（每行一个）
    String wordName;
    String submitContext;
    String relatedDocName;
    String relatedDocContext;
    Integer scid;
    Integer rcid;
    Double similarity;
    Entity(String w,String s,String rdn,String rdc,Integer scid,Integer rcid,Double sim){
        this.wordName=w;this.submitContext=s;
        this.relatedDocName=rdn;this.relatedDocContext=rdc;
        this.scid = scid;this.rcid = rcid;
        this.similarity = sim;
    }
    public int compareTo(Entity e){
        return this.relatedDocContext.compareTo(e.relatedDocContext);
    }
}
public class SimSearcher {
    public InvertedListBuilder builder;
    public DocSubmitter submitter;
    public RegulationUploader uploader;
    public SimSearcher(String listFileName,String submitFileName)throws Exception{
        builder = new InvertedListBuilder(listFileName);
        submitter = new DocSubmitter(submitFileName);
    }
    public SimSearcher(InvertedListBuilder builder,DocSubmitter submitter){
        this.builder=builder;
        this.submitter = submitter;
    }
    public SimSearcher(RegulationUploader uploader,DocSubmitter submitter){
        this.uploader=uploader;
        this.submitter=submitter;
    }
//    public SimSearcher(){
//
//    }
    public List<Entity> filter(List<Pair<wordContext,WordRecord>> rawResult){
        //生成一条条记录
        List<Entity>totalResult=new ArrayList<>();
        for(int i = 0;i<rawResult.size();i++){
            String wn = rawResult.get(i).getKey().name;
            String ct = rawResult.get(i).getKey().context;
            Integer scid = rawResult.get(i).getKey().scid;
            Double len1 = new Double(rawResult.get(i).getKey().wordNum);

            Vector<Paragraph> paras = rawResult.get(i).getValue().paragraph;
            String dn = rawResult.get(i).getValue().docName;

            for(int j = 0;j<paras.size();j++){
                Double len2 = new Double(paras.get(j).wordNum);
                totalResult.add(new Entity(wn,ct,dn,paras.get(j).raw_content,scid,paras.get(j).id,len1+len2));
            }
        }
        int threshold = 3;   //
        HashMap<String,Count> counter=new HashMap<>();
        for(int i = 0;i<totalResult.size();i++){
            try {
                String key = totalResult.get(i).scid.toString()+","+totalResult.get(i).rcid.toString();
                if(!counter.containsKey(key)){
                    counter.put(key,new Count(1,i));
                }
                else{
                    Count tmp = counter.get(key);
                    tmp.count++;tmp.id.add(i);
                    counter.put(key,tmp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Entity> filteredResult=new ArrayList<>();
        Iterator<Map.Entry<String,Count>>entries = counter.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry<String,Count> e = entries.next();
            if(e.getValue().count>=threshold){
                for(int i = 0;i<e.getValue().id.size();i++){
                    int index = e.getValue().id.get(i);
                    totalResult.get(index).similarity = 2*new Double(e.getValue().count)/totalResult.get(index).similarity;
                    filteredResult.add(totalResult.get(index));
                }
            }
        }
        return filteredResult;
    }

    public List<Entity> search(){
        Iterator<Map.Entry<String,Vector<Paragraph>>>entries = submitter.keywordMap.entrySet().iterator();
        List<Pair<wordContext,WordRecord>>result = new ArrayList<>();
        while(entries.hasNext()){
            Map.Entry<String,Vector<Paragraph>>entry = entries.next();
            String word = entry.getKey();
            Vector<Paragraph> paras = entry.getValue();
            for(int i = 0;i<uploader.regulationDocs.size();i++){
                HashMap<String,WordRecord> map= uploader.regulationDocs.get(i).invertedList;
                if(map.containsKey(word)){
                    for(int j = 0;j<paras.size();j++){
                        Pair<wordContext,WordRecord>p=new Pair<>(new wordContext(word,paras.get(j).raw_content,paras.get(j).id,paras.get(j).wordNum),map.get(word));
                        result.add(p);
                    }
                }
            }
        }
        List<Entity>finalResult = filter(result);
        return finalResult;
    }
}
