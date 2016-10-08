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
    Integer id;//词语id
    String content;//词语内容
    String attr;
    String docName;//所在文档名称
    Vector<Paragraph> paragraph=new Vector<>(); //在某文档下该词出现的所有段落内容集合
    Double importancy;
    WordRecord(String docName,String content,Vector<Paragraph>paragraph){
        this.content = content;this.docName = docName;
        this.paragraph = paragraph;
    }
}
class RegulationDoc{
    public String doc_name;//文档名称
    public String content; //文档全部文字内容
    public Vector<Paragraph>paragraphs; //文档全部段落集合
    public HashMap<String,WordRecord> invertedList = new HashMap<>();  //分词的词语字典，一个词语对应一个词语记录
    public void build(){
        /*
           将某篇文档按照每一段落进行分词，分词结果存入词典中
         */
        for(int i = 0;i<paragraphs.size();i++){
            List<Term>words = splitWords(paragraphs.get(i).content);
            for(Term w : words){
                if(!invertedList.containsKey(w.getName())){//该词尚未加入词典中,则插入一条新的词语记录
                    Vector<Paragraph> p=new Vector<>();
                    p.add(paragraphs.get(i));
                    WordRecord wr = new WordRecord(doc_name,w.getName(),p);
                    invertedList.put(w.getName(),wr);
                }
                else{//若词语已经加入
                    WordRecord wr = invertedList.get(w.getName());
                    if(!wr.paragraph.contains(paragraphs.get(i))){
                        /*如果该词此次出现的段落与之前插入的不同，则在wordrecord把该段落内容加入
                        （此做法保证一个段落多个相同的词只会被存储一次）
                         此判断速度可以优化，给paragraph编号后可更快速进行判断
                         */
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
        /*
        提取word文档中的文本内容
         */
        FileInputStream fis= new FileInputStream(new File(filename));
        HWPFDocument doc = new HWPFDocument(fis);
        WordExtractor wordExtractor = new WordExtractor(doc);
        String content = wordExtractor.getText();//存储文档全部内容
        String []paragraphs = wordExtractor.getParagraphText();  //按段落存储
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