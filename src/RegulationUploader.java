import java.io.*;
import java.util.Vector;

/**
 * Created by Zhou Jianyu on 2016/8/9.
 */
public class RegulationUploader {
    Vector<RegulationDoc> regulationDocs;
    RegulationUploader(String storageFilename)throws IOException{
        regulationDocs=new Vector<>();
        BufferedReader reader;
        try{
            InputStreamReader isr = new InputStreamReader(new FileInputStream(storageFilename));
            reader = new BufferedReader(isr);
            String line = reader.readLine();
            while(line!=null){
                String []elements=line.split(",");
                Integer docId=new Integer(0);
                try{
                    docId = Integer.parseInt(elements[0]);
                }catch(NumberFormatException e){
                    int m =1;
                }
                String word = elements[1];
                String docName = elements[2];
                Paragraph para = new Paragraph(elements[4].replaceAll("[^(\\u4e00-\\u9fa5)a-zA-Z0-9]",""),elements[4],Integer.parseInt(elements[3]));
                if(regulationDocs.size()<=docId){
                    RegulationDoc rd = new RegulationDoc();
                    Vector<Paragraph> paras = new Vector<>();
                    paras.add(para);
                    WordRecord wr = new WordRecord(docName,word,paras);
                    rd.invertedList.put(word,wr);
                    regulationDocs.add(rd);
                }
                else{
                    RegulationDoc doc = regulationDocs.get(docId);
                    if(doc.invertedList.containsKey(word)){
                        WordRecord wr = doc.invertedList.get(word);
                        wr.paragraph.add(para);
                        doc.invertedList.put(word,wr);
                    }
                    else{
                        Vector<Paragraph> paras = new Vector<>();
                        paras.add(para);
                        WordRecord wr = new WordRecord(docName,word,paras);
                        doc.invertedList.put(word,wr);
                    }
                    regulationDocs.set(docId,doc);
                }
                line = reader.readLine();
            }
        }catch(FileNotFoundException e){
            System.err.println("storage file does not exsist.");
        }
    }
    public void record2map(){

    }
}
