package kg666;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;

import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import kg666.util.NLP;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class NLPTest {
    @Autowired
    NLP nlp;

    @Test
    public void TestA(){
        String lineStr = "明天虽然会下雨，但是我还是会看周杰伦的演唱会。";
        try{
            Segment segment = HanLP.newSegment();
            segment.enableCustomDictionary(true);
            CustomDictionary.add("虽然会","ng 0");
            List<Term> seg = segment.seg(lineStr);
            for (Term term : seg) {
                System.out.println(term.toString());
            }
        }catch(Exception ex){
            System.out.println(ex.getClass()+","+ex.getMessage());
        }
    }

    @Test
    public void TestB(){
        SparkConf conf = new SparkConf().setAppName("NaiveBayesTest").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        Vector dense = Vectors.dense(1.0,0.0,2.0);
        System.out.println(dense);
        int len = 3;
        int[] index = new int[]{0,1};
        double[] values = new double[]{2.0,3.0};
        Vector sparse = Vectors.sparse(len, index, values);
        LabeledPoint train_one = new LabeledPoint(1.0,dense);
        LabeledPoint train_two = new LabeledPoint(2.0,sparse);
        LabeledPoint train_three = new LabeledPoint(3.0, Vectors.dense(1,2,2));
        List<LabeledPoint> train_set = new ArrayList<>();
        train_set.add(train_one);
        train_set.add(train_two);
        train_set.add(train_three);
        JavaRDD<LabeledPoint> trainedRDD = sc.parallelize(train_set);
        NaiveBayesModel model = NaiveBayes.train(trainedRDD.rdd());
        double [] dTest = {2,1,0};
        Vector vTest = Vectors.dense(dTest);
        System.err.println(model.predict(vTest));
        System.err.println(model.predictProbabilities(vTest));
        sc.close();
    }

    @Test
    public void TestC() throws Exception{
        nlp.analysisQuery("英雄的评分是多少");
        nlp.analysisQuery("阿尔芭·洛尔瓦彻主演的电影");
    }
}
