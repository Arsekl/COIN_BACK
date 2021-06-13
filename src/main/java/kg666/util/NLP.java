package kg666.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.collection.trie.DoubleArrayTrie;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.aopalliance.reflect.Class;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.*;

@Component
public class NLP {
    static final String pattern0 = "match (n:Movie{name:'%s'}) return n";
    private Map<Double, String> questionsPattern;
    private NaiveBayesModel model;
    private Map<String, Integer> vocabulary;
    private Map<String, String> abstractMap;
    private int modelIndex = 0;

    public NLP() throws Exception {
        loadDict("movieDict.txt", 0);
        loadDict("genreDict.txt", 1);
        loadDict("scoreDict.txt", 2);
        loadDict("languageDict.txt", 3);
        loadDict("districtDict.txt", 4);
        loadDict("personDict.txt", 5);
        loadDict("otherDict.txt", 6);
        questionsPattern = loadQuestionsPattern();
        vocabulary = loadVocabulary();
        model = loadModel();
    }

    private void loadDict(String path,Integer type) {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new ClassPathResource(path).getInputStream()));
            addCustomDictionary(br, type);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCustomDictionary(BufferedReader br, int type) {
        String word;
        try {
            while ((word = br.readLine()) != null) {
                switch (type) {
                    case 0:
                        setNatureAndFrequency(word,"nm 0");
                        break;
                    case 1:
                        setNatureAndFrequency(word,"ng 0");
                        break;
                    case 2:
                        setNatureAndFrequency(word,"x 0");
                        break;
                    case 3:
                        setNatureAndFrequency(word, "nl 0");
                        break;
                    case 4:
                        setNatureAndFrequency(word, "nd 0");
                        break;
                    case 5:
                        setNatureAndFrequency(word, "nr 0");
                    case 6:
                        setNatureAndFrequency(word, "nmo 0");
                    default:
                        break;
                }
            }
            br.close();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setNatureAndFrequency(String word, String natureWithFrequency){
        if(CustomDictionary.contains(word)){
            unsetNatureAndFrequency(word,natureWithFrequency);
        }else{
            CustomDictionary.add(word,natureWithFrequency);
        }
    }
    private void unsetNatureAndFrequency(String word, String natureWithFrequency){
        String[] natureWithFrequencyArr = natureWithFrequency.split(" ");
        Nature natureNew = null;
        int frequencyNew = 0 ;
        if(natureWithFrequencyArr!=null && natureWithFrequencyArr.length == 2){
            natureNew =  Nature.create(natureWithFrequencyArr[0]);
            frequencyNew = Integer.parseInt(natureWithFrequencyArr[1]);
        }
        DoubleArrayTrie<CoreDictionary.Attribute> dat = CustomDictionary.DEFAULT.dat;
        CoreDictionary.Attribute attribute = dat.get(word);
        if(attribute == null){
            CustomDictionary.add(word,natureWithFrequency);
            return;
        }
        Nature[] nature = attribute.nature;
        int[] frequency = attribute.frequency;
        if(natureNew!=null && nature!=null && nature.length>0){
            nature[0] = natureNew;
        }
        if(frequency!=null && frequency.length>0){
            frequency[0] = frequencyNew;
        }
    }

    public ArrayList<String> analysisQuery(String queryString) throws Exception {
        String abs = queryAbstract(queryString);
        System.out.println("Abstract：" + abs);

        String strPattern = queryClassify(abs);
        String finalPattern = queryReplace(strPattern);
        System.out.println("Replace：" + finalPattern);

        ArrayList<String> resultList = new ArrayList<String>();
        resultList.add(String.valueOf(modelIndex));
        String[] finalPatternArray = finalPattern.split(" ");
        for (String word : finalPatternArray)
            resultList.add(word);
        switch (modelIndex){
            case 0:
                System.out.println(String.format(pattern0, resultList.get(1)));
        }
        return resultList;
    }

    private String queryClassify(String sentence) throws Exception {
        double[] testArray = sentenceToVector(sentence);
        Vector v = Vectors.dense(testArray);
        double index = model.predict(v);
        modelIndex = (int)index;
        System.out.println("the model index is " + index);
        return questionsPattern.get(index);
    }

    private String queryReplace(String queryPattern) {
        Set<String> set = abstractMap.keySet();
        for (String key : set) {
            if (queryPattern.contains(key)) {
                String value = abstractMap.get(key);
                queryPattern = queryPattern.replace(key, value);
            }
        }
        abstractMap.clear();
        abstractMap = null;
        return queryPattern;
    }

    private String queryAbstract(String querySentence) {
        Segment segment = HanLP.newSegment().enableCustomDictionary(true);
        List<Term> terms = segment.seg(querySentence);
        String abstractQuery = "";
        abstractMap = new HashMap<>();
        int nrCount = 0;
        for (Term term : terms) {
            String word = term.word;
            String termStr = term.toString();
            System.out.println(termStr);
            if (termStr.contains("nm")) { //nm 电影名
                abstractQuery += "nm ";
                abstractMap.put("nm", word);
            } else if (termStr.contains("nr") && nrCount == 0) { //nr 人名 -> nnt
                abstractQuery += "nnt ";
                abstractMap.put("nnt", word);
                nrCount++;
            } else if (termStr.contains("nr") && nrCount == 1) { //nr 人名再出现一次 -> nnr
                abstractQuery += "nnr ";
                abstractMap.put("nnr", word);
                nrCount++;
            } else if (termStr.contains("x")) {  //x  评分
                abstractQuery += "x ";
                abstractMap.put("x", word);
            } else if (termStr.contains("ng")) { //ng 类型
                abstractQuery += "ng ";
                abstractMap.put("ng", word);
            } else if (termStr.contains("nl")) { //nl 语言
                abstractQuery += "nl ";
                abstractMap.put("nl", word);
            } else if (termStr.contains("nd")) { //nd 地区
                abstractQuery += "nd ";
                abstractMap.put("nd", word);
            } else if (termStr.contains("nmo")) { //nmo 别名
                abstractQuery += "nmo ";
                abstractMap.put("nmo", word);
            } else if (termStr.contains("m")) { //m 年份
                abstractQuery += "mmm ";
                abstractMap.put("mmm", word);
            }
            else {
                abstractQuery += word + " ";
            }
        }
        return abstractQuery;
    }

    private  Map<Double, String> loadQuestionsPattern() {
        Map<Double, String> questionsPattern = new HashMap<>();
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(new ClassPathResource("question/question_classification.txt").getInputStream()));
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                double index = Double.valueOf(tokens[0]);
                String pattern = tokens[1];
                questionsPattern.put(index, pattern);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionsPattern;
    }

    private Map<String, Integer> loadVocabulary() {
        Map<String, Integer> vocabulary = new HashMap<String, Integer>();
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(new ClassPathResource("question/vocabulary.txt").getInputStream()));
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                int index = Integer.parseInt(tokens[0]);
                String word = tokens[1];
                vocabulary.put(word, index);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vocabulary;
    }

    private String loadFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource(filename).getInputStream()));
        String content = "";
        String line;
        while ((line = br.readLine()) != null) {
            content += line + ",";
        }
        br.close();
        return content;
    }

    private  double[] sentenceToVector(String sentence) throws Exception {
        double[] vector = new double[vocabulary.size()];
        for (int i = 0; i < vocabulary.size(); i++) {
            vector[i] = 0;
        }
        Segment segment = HanLP.newSegment();
        List<Term> terms = segment.seg(sentence);
        for (Term term : terms) {
            String word = term.word;
            if (vocabulary.containsKey(word)) {
                int index = vocabulary.get(word);
                vector[index] = 1;
            }
        }
        return vector;
    }

    private void addTrainItem(List<LabeledPoint> trainList, String patternPath) throws Exception {
        List<String> list = new ArrayList<>();
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(ResourceUtils.CLASSPATH_URL_PREFIX+patternPath+"/*.txt");
        for(Resource resource : resources){
            String fileName = resource.getFilename();
            list.add(patternPath+"/"+fileName);
        }
        String[] sentences;
        Collections.sort(list);
        int index = 0;
        for (String filename:list) {
            String scoreQuestions = loadFile(filename);
            sentences = scoreQuestions.split(",");
            for (String sentence : sentences) {
                double[] array = sentenceToVector(sentence);
                LabeledPoint train_one = new LabeledPoint(index, Vectors.dense(array));
                trainList.add(train_one);
            }
            System.out.println(filename+" " +index);
            index++;
        }
    }

    private  NaiveBayesModel loadModel() throws Exception {
        SparkConf conf = new SparkConf().setAppName("NaiveBayesTest").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        List<LabeledPoint> trainList = new LinkedList<>();
        addTrainItem(trainList, "question/pattern");
        JavaRDD<LabeledPoint> trainingRDD = sc.parallelize(trainList);
        NaiveBayesModel nb_model = NaiveBayes.train(trainingRDD.rdd());
        sc.close();
        return nb_model;
    }


}
