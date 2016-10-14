/**
 * Created by Zhou Jianyu on 2016/7/25.
 */
import javafx.util.Pair;
import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

class wordContext{
    String name;
    String context;
    Integer scid;
    Integer wordNum;
    wordContext(String name,String context,Integer scid,Integer wordNum){
        this.name=name;this.context=context;this.scid = scid;
        this.wordNum = wordNum;
    }
}
class Paragraph {
    String raw_content;
    String content;
    Integer id;
    Integer wordNum;
    Paragraph(String content,String raw_content, Integer id,Integer wordNum) {
        this.content = content;
        this.raw_content = raw_content;
        this.id = id;this.wordNum = wordNum;
    }
}
public class DocSubmitter {
    public String docName;
    public String content;
    public Vector<String> paragraphs=new Vector<>();
    public HashMap<String,Vector<Paragraph>> wordMap;
    public HashMap<String,Vector<Paragraph>> keywordMap;
    public DocSubmitter(String name)throws Exception{
        this.docName = name;
        boolean isNewEdition = true;
        try{
            FileInputStream fin = new FileInputStream(docName);
            XWPFDocument doc = new XWPFDocument(fin);
            XWPFWordExtractor extr = new XWPFWordExtractor(doc);
            content =  extr.getText();
            List<XWPFParagraph> paras = doc.getParagraphs();
            for(int i = 0;i<paras.size();i++){
                paragraphs.add(paras.get(i).getText());
            }
        }catch(Exception e){
            isNewEdition= false;
        }
        if(isNewEdition==false){
            FileInputStream fin = new FileInputStream(docName);
            HWPFDocument doc = new HWPFDocument(fin);
            WordExtractor wordExtractor = new WordExtractor(doc);
            content = wordExtractor.getText();
            String [] paras = wordExtractor.getParagraphText();
            for(int i = 0;i<paras.length;i++){
                paragraphs.add(paras[i]);
            }
        }
        wordMap = new HashMap<>();
        keywordMap= new HashMap<>();
    }
    public void build(){
//        for(int i = 0;i<paragraphs.size();i++){
//            String raw_para = paragraphs.get(i);
//            String para = paragraphs.get(i).replaceAll("[^(\\u4e00-\\u9fa5)a-zA-Z0-9]","");
//            List<Term> result = NlpAnalysis.parse(para);
//            Integer wordNum = result.size();
//            for(Term w:result){
//                if(!wordMap.containsKey(w.getName())){
//                    Vector<Paragraph> p =new Vector<Paragraph>();
//                    p.add(new Paragraph(para,raw_para,i,wordNum));
//                    wordMap.put(w.getName(),p);
//                }
//                else{
//                    Vector<Paragraph>p= wordMap.get(w.getName());
//                    if(!p.contains(para)){
//                        p.add(new Paragraph(para,raw_para,i,wordNum));
//                        wordMap.put(w.getName(),p);
//                    }
//                }
//            }
//        }
        KeyWordComputer kwc = new KeyWordComputer(10);
        for(int i = 0;i<paragraphs.size();i++){
            String raw_para = paragraphs.get(i);
            int end = raw_para.lastIndexOf("\r");
            if(end!=-1)raw_para = raw_para.substring(0,end);
            String para = paragraphs.get(i).replaceAll("[^(\\u4e00-\\u9fa5)a-zA-Z0-9]","");
            List<Keyword> keywords = kwc.computeArticleTfidf(para);
            Integer wordNum = keywords.size();
            for(Keyword w:keywords){
                if(!keywordMap.containsKey(w.getName())){
                    Vector<Paragraph> p =new Vector<Paragraph>();
                    p.add(new Paragraph(para,raw_para,i,wordNum));
                    keywordMap.put(w.getName(),p);
                }
                else{
                    Vector<Paragraph>ps= keywordMap.get(w.getName());
                    if(!ps.contains(para)){
                        ps.add(new Paragraph(para,raw_para,i,wordNum));
                        keywordMap.put(w.getName(),ps);
                    }
                }
            }
        }
    }
}
