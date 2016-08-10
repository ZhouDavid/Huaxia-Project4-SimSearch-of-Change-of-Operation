/**
 * Created by Zhou Jianyu on 2016/7/25.
 */
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.poi.*;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
class WordRecord{
    Integer id;
    String content;
    String attr;
    String docName;
    Vector<Paragraph> paragraph=new Vector<>();
    Double importancy;
    WordRecord(String docName,String content,Vector<Paragraph>paragraph){
        this.content = content;this.docName = docName;
        this.paragraph = paragraph;
    }
}
class RegulationDoc{
    public String doc_name;
    public String content;
    public Vector<Paragraph>paragraphs;
    public HashMap<String,WordRecord> invertedList = new HashMap<>();
    public void build(){
        for(int i = 0;i<paragraphs.size();i++){
            List<Term>words = splitWords(paragraphs.get(i).content);
            for(Term w : words){
                if(!invertedList.containsKey(w.getName())){
                    Vector<Paragraph> p=new Vector<>();
                    p.add(paragraphs.get(i));
                    WordRecord wr = new WordRecord(doc_name,w.getName(),p);
                    invertedList.put(w.getName(),wr);
                }
                else{
                    WordRecord wr = invertedList.get(w.getName());
                    if(!wr.paragraph.contains(paragraphs.get(i))){//速度可优化，给paragraph编号
                        wr.paragraph.add(paragraphs.get(i));
                        invertedList.put(w.getName(),wr);
                    }
                }
            }
        }
    }
    public List<Term>  splitWords(String content){
        List<Term>result = NlpAnalysis.parse(content);
        return result;
    }
    RegulationDoc(String doc_name,String content,Vector<Paragraph>paras){
        this.doc_name = doc_name;
        this.content = content;
        this.paragraphs = paras;
    }
    RegulationDoc(){}
    public void addInvertedList(String[] record){
        
    }


}

public class InvertedListBuilder {
    Vector<String>docNameList = new Vector<>();
    Integer gcid = new Integer(0);
    public Vector<RegulationDoc> docs=new Vector<>();
    InvertedListBuilder(String docNameListFileName)throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(docNameListFileName));
        String line = reader.readLine();
        while(line!=null){
            docNameList.add(line);
            line = reader.readLine();
        }
    }
    public void parseWord(String filename)throws Exception{
        FileInputStream fis= new FileInputStream(new File(filename));
        HWPFDocument doc = new HWPFDocument(fis);
        WordExtractor wordExtractor = new WordExtractor(doc);
        String content = wordExtractor.getText();
        String []paragraphs = wordExtractor.getParagraphText();
        int li = filename.lastIndexOf("\\");
        String title = filename.substring(li+1,filename.length());
        Vector<Paragraph> ps = new Vector<>();
        for(int i = 0;i<paragraphs.length;i++)
            ps.add(new Paragraph(paragraphs[i].replaceAll("[^(\\u4e00-\\u9fa5)a-zA-Z0-9]",""),
                    paragraphs[i],gcid++));
        docs.add(new RegulationDoc(title,content,ps));
    }

    public void buildAll()throws Exception{
        for(int i = 0;i<docNameList.size();i++){
            parseWord(docNameList.get(i));
        }
        for(int i = 0;i<docs.size();i++){
            docs.get(i).build();
        }
    }

    public void storeList()throws Exception{

    }
}