package kg666.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.collection.trie.DoubleArrayTrie;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class NLP {
    private final Map<Double, String> questionsPattern;
    private final LogisticRegressionModel model;
    private final Map<String, Integer> vocabulary;
    private final Map<String, String> abstractMap =new HashMap<>();
    private int modelIndex = 0;

    /**
     * Initiate model and load vocabulary and question pattern
     * g@throws Exception
     */
    public NLP() throws Exception {
        loadDict("genreDict.txt", 1);
        loadDict("scoreDict.txt", 2);
        loadDict("movieDict.txt", 0);
        loadDict("languageDict.txt", 3);
        loadDict("districtDict.txt", 4);
        loadDict("personDict.txt", 5);
        loadDict("otherDict.txt", 6);
        questionsPattern = loadQuestionsPattern();
        vocabulary = loadVocabulary();
        model = loadModel();
    }

    /**
     * Load word dict from file
     * Dict path @param path
     * Type @param type
     */
    private void loadDict(String path,Integer type) {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new ClassPathResource(path).getInputStream()));
            addCustomDictionary(br, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Put words in dict into custom dictionary
     * Bufferedreader@param br
     * Type@param type
     */
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
                        break;
                    case 6:
                        setNatureAndFrequency(word, "no 0");
                        break;
                    default:
                        break;
                }
            }
            br.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set word's nature and frequency when is put into custom dict
     * Word@param word
     * Nature With Frequency Split By Space@param natureWithFrequency
     */
    private void setNatureAndFrequency(String word, String natureWithFrequency){
        if(CustomDictionary.contains(word)){
            unsetNatureAndFrequency(word,natureWithFrequency);
        }else{
            CustomDictionary.add(word,natureWithFrequency);
        }
    }

    /**
     * Unset original nature and frequency of word if already has existed in dict
     * Word @param word
     * Nature With Frequency Split By Space@param natureWithFrequency
     */
    private void unsetNatureAndFrequency(String word, String natureWithFrequency){
        String[] natureWithFrequencyArr = natureWithFrequency.split(" ");
        Nature natureNew = null;
        int frequencyNew = 0 ;
        if(natureWithFrequencyArr.length == 2){
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

    /**
     *Compose abstract stage, classify stage and replace stage together in order
     * Question @param queryString
     * Concrete Cypher@return
     */
    public String analysisQuery(String queryString) {
        String abs = queryAbstract(queryString);
        System.out.println("Abstract???" + abs);
        String strPattern = queryClassify(abs);
        System.out.println("Classify:" + modelIndex);
        String finalPattern = queryReplace(strPattern);
        System.out.println("Replace???" + finalPattern);
        return finalPattern;
    }

    /**
     * Use model to classify question
     * Abstract Question @param sentence
     * Abstract Cypher@return
     */
    private String queryClassify(String sentence) {
        double[] testArray = sentenceToVector(sentence);
        Vector v = Vectors.dense(testArray);
        double index = model.predict(v);
        modelIndex = (int)index;
        System.out.println("the model index is " + index);
        return questionsPattern.get(index);
    }

    /**
     * Replace abstract word to get concrete query cypher
     * Abstract Cypher@param queryPattern
     * Concrete Cypher@return
     */
    private String queryReplace(String queryPattern) {
        Set<String> set = abstractMap.keySet();
        for (String key : set) {
            if (queryPattern.contains(key)) {
                String value = abstractMap.get(key);
                if (key.equals("ng"))
                    queryPattern = queryPattern.replace("name:'"+key+"'", "name:'"+value+"'");
                else queryPattern = queryPattern.replace(key, value);
            }
        }
        return queryPattern;
    }

    /**
     * Transform word in custom dict into abstract word for classify
     * Question@param querySentence
     * Abstract Question@return
     */
    private String queryAbstract(String querySentence) {
        Segment segment = HanLP.newSegment().enableCustomDictionary(true);
        List<Term> terms = segment.seg(querySentence);
        StringBuilder abstractQuery = new StringBuilder();
        int nrCount = 0;
        int nmCount = 0;
        for (Term term : terms) {
            String word = term.word;
            String termStr = term.toString();
            System.out.println(termStr);
            if (termStr.contains("nm") && nmCount ==0) { //nm ?????????
                abstractQuery.append("nm ");
                abstractMap.put("nm", word);
                nmCount++;
            } else if (termStr.contains("nr") && nrCount == 0) { //nr ?????? -> nnt
                abstractQuery.append("nnt ");
                abstractMap.put("nnt", word);
                nrCount++;
            } else if (termStr.contains("nr") && nrCount == 1) { //nr ????????????????????? -> nnr
                abstractQuery.append("nnr ");
                abstractMap.put("nnr", word);
                nrCount++;
            } else if (termStr.contains("x")) {  //x  ??????
                abstractQuery.append("x ");
                abstractMap.put("x", word);
            } else if (termStr.contains("ng")) { //ng ??????
                abstractQuery.append("ng ");
                abstractMap.put("ng", word);
            } else if (termStr.contains("nl")) { //nl ??????
                abstractQuery.append("nl ");
                abstractMap.put("nl", word);
            } else if (termStr.contains("nd")) { //nd ??????
                abstractQuery.append("nd ");
                abstractMap.put("nd", word);
            } else if (termStr.contains("no")) { //no ??????
                abstractQuery.append("no ");
                abstractMap.put("no", word);
            } else if (termStr.contains("m")) { //m ??????
                abstractQuery.append("mmm ");
                abstractMap.put("mmm", word);
            }
            else {
                abstractQuery.append(word).append(" ");
            }
        }
        return abstractQuery.toString();
    }

    /**
     * Load question pattern form file
     * Question Pattern in Map Form@return
     */
    private  Map<Double, String> loadQuestionsPattern() {
        Map<Double, String> questionsPattern = new HashMap<>();
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(new ClassPathResource("question/question_classification.txt").getInputStream()));
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                double index = Double.parseDouble(tokens[0]);
                String pattern = tokens[1];
                questionsPattern.put(index, pattern);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionsPattern;
    }

    /**
     * load vocabulary table from file
     * Vocabulary Table in Map Form @return
     */
    private Map<String, Integer> loadVocabulary() {
        Map<String, Integer> vocabulary = new HashMap<>();
        BufferedReader br;
        String line;
        int index = 0;
        try {
            br = new BufferedReader(new InputStreamReader(new ClassPathResource("question/vocabulary.txt").getInputStream()));
            while ((line = br.readLine()) != null) {
                vocabulary.put(line, index);
                index++;
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        return vocabulary;
    }

    private String loadFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource(filename).getInputStream()));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            content.append(line).append(",");
        }
        br.close();
        return content.toString();
    }

    /**
     * Transform sentence into vector for training or predicting
     * Abstract Question@param sentence
     * Vector for Trainning@return
     */
    private  double[] sentenceToVector(String sentence) {
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

    /**
     * Put file in pattern directory into training set
     * List of Labeled Point @param trainList
     * @throws Exception
     */
    private void addTrainItem(List<LabeledPoint> trainList) throws Exception {
        List<String> list = new ArrayList<>();
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(ResourceUtils.CLASSPATH_URL_PREFIX+ "question/pattern" +"/*.txt");
        for(Resource resource : resources){
            String fileName = resource.getFilename();
            list.add("question/pattern" +"/"+fileName);
        }
        String[] sentences;
        Collections.sort(list);
        int index = 0;
        for (String filename:list) {
            String scoreQuestions = loadFile(filename);
            sentences = scoreQuestions.split(",");
            for (String sentence : sentences) {
                double[] array = sentenceToVector(sentence);
                LabeledPoint trainItem = new LabeledPoint(index, Vectors.dense(array));
                trainList.add(trainItem);
            }
            System.out.println(filename+" " +index);
            index++;
        }
    }

    /**
     * Use Logistic Regression Model
     * Trained LogicRegressionModel@return
     * @throws Exception
     */
    private LogisticRegressionModel loadModel() throws Exception {
        SparkConf conf = new SparkConf().setAppName("Model").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        List<LabeledPoint> trainList = new LinkedList<>();
        addTrainItem(trainList);
        JavaRDD<LabeledPoint> trainingRDD = sc.parallelize(trainList);
        LogisticRegressionModel initModel = new LogisticRegressionWithLBFGS().setNumClasses(questionsPattern.size()).run(trainingRDD.rdd());
        sc.close();
        return initModel;
    }


}
