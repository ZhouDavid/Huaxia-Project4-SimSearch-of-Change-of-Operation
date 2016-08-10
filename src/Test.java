/**
 * Created by Zhou Jianyu on 2016/7/25.
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.List;
import java.util.Vector;


import javafx.util.Pair;
import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
public class Test {
    public static void main(String []argvs) throws Exception{
        //建立倒排列表
//        InvertedListBuilder builder = new InvertedListBuilder("./resource/listFile");
//        builder.buildAll();
        DocSubmitter submitter=new DocSubmitter("./resource/公积金系统切换操作手册.doc");

        //制度文档存储
//        RegulationStorager regulationStorager = new RegulationStorager(builder.docs);
//        regulationStorager.map2record();
//        String storeName = "store.csv";
//        FileOutputStream fileOutputStream = new FileOutputStream(storeName);
//        OutputStreamWriter storageWriter = new OutputStreamWriter(fileOutputStream);
//        for(int i = 0;i<regulationStorager.regulationRecords.size();i++){
//            RegulationRecord r = regulationStorager.regulationRecords.get(i);
//            String line = r.docId+","+r.wordName+","+r.regulationDocName+","+r.contexId+","+"\""+r.rawWordContext.substring(0,r.rawWordContext.length()-1);
//            storageWriter.append(line);
//        }
//        storageWriter.close();
        RegulationUploader uploader = new RegulationUploader("store.csv");
        submitter.build();
        SimSearcher searcher = new SimSearcher(uploader,submitter);
        List<Entity>result= searcher.search();
        String outname = "output.csv";
        FileOutputStream fout = new FileOutputStream(outname);
        OutputStreamWriter writer = new OutputStreamWriter(fout);
        for(int i = 0;i<result.size();i++){
            String wordName = result.get(i).wordName;
            String subContext = result.get(i).submitContext;
            String docName = result.get(i).relatedDocName;
            String docContext = result.get(i).relatedDocContext;
            Integer scid = result.get(i).scid;
            Integer rcid = result.get(i).rcid;
            String record = wordName+","+subContext+","+scid.toString()+","+docName+","+docContext+","+rcid.toString()+"\r\n";
            writer.append(record);
        }
        writer.close();
//        String filename =
//                "D:\\人智实验室项目\\华夏银行项目\\华夏银行运维变更合规性查验项目\\制度文档-做合规检查的应用\\华银制〔2014〕197号__华夏银行信息系统设备管理办法\\附件：1.华夏银行总行信息系统设备验收流程.doc";
//        builder.parseWord(filename);


//	    KeyWordComputer kwc = new KeyWordComputer(10);
//	    String title = "沉睡的巨龙中国";
//	    String content = "中国是世界四大文明古国之一，有着悠久的历史，距今约5000年前，以中原地区为中心开始出现聚落组织进而成国家和朝代，后历经多次演变和朝代更迭，持续时间较长的朝代有夏、商、周、汉、晋、唐、宋、元、明、清等。中原王朝历史上不断与北方游牧民族交往、征战，众多民族融合成为中华民族。20世纪初辛亥革命后，中国的君主政体退出历史舞台，取而代之的是共和政体。1949年中华人民共和国成立后，在中国大陆建立了人民代表大会制度的政体。中国有着多彩的民俗文化，传统艺术形式有诗词、戏曲、书法和国画等，春节、元宵、清明、端午、中秋、重阳等是中国重要的传统节日。";
//        Collection<Keyword> result = kwc.computeArticleTfidf(title, content);
//        System.err.println(title+":"+content);
//	        for (Keyword e:result)
//	            System.out.println(e);
    }
}

