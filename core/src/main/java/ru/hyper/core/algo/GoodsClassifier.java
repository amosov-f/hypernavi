package ru.hyper.core.algo;

import org.jetbrains.annotations.NotNull;
import ru.hyper.core.model.Category;
import ru.hyper.core.model.Good;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: amosov-f
 * Date: 23.11.14
 * Time: 15:13
 */
public class GoodsClassifier {
    @NotNull
    private final Instances data;
    @NotNull
    private final Classifier classifier;
    @NotNull
    private final List<Category> categories;
    @NotNull
    private final StringToWordVector filter;

    public GoodsClassifier() throws Exception {
        final Attribute textAttribute = new Attribute("text", (ArrayList<String>) null);
        final EnumSet<Category> categories = EnumSet.noneOf(Category.class);
        final List<Good> goods = read();
        for (final Good good : goods) {
            textAttribute.addStringValue(good.getName());
            categories.add(good.getCategory());
        }
        this.categories = new ArrayList<>(categories);
        final Attribute categoryAttribute = new Attribute(
                "category",
                this.categories.stream().map(Category::getName).collect(Collectors.toList())
        );

        final ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(textAttribute);
        attributes.add(categoryAttribute);

        data = new Instances("goods", attributes, goods.size());
        for (final Good good : goods) {
            final Instance instance = new DenseInstance(attributes.size());
            instance.setValue(textAttribute, good.getName());
            instance.setValue(categoryAttribute, good.getCategory().getName());
            data.add(instance);
        }
        data.setClass(categoryAttribute);

        filter = new StringToWordVector();
        filter.setInputFormat(data);
        //filter.setDoNotOperateOnPerClassBasis(true);
        //filter.setTFTransform(true);
        filter.setLowerCaseTokens(true);
        filter.setStemmer(new SnowballStemmer("russian"));

        final Instances filteredData = Filter.useFilter(data, filter);
        final List<Attribute> filteredAttributes = Collections.list(filteredData.enumerateAttributes());
        for (final Attribute attribute : filteredAttributes) {
            System.out.println(attribute.name());
        }
        System.out.println(filteredAttributes.size());

        final Instances learn = filteredData.trainCV(10, 0);
        final Instances test = filteredData.testCV(10, 0);

        classifier = new SMO();
        classifier.buildClassifier(learn);

        Evaluation evaluation = new Evaluation(learn);
        evaluation.evaluateModel(classifier, test);

        System.out.println(evaluation.toSummaryString());
        System.out.println(evaluation.toMatrixString());
        System.out.println(evaluation.toClassDetailsString());

        classifier.buildClassifier(filteredData);
    }

    @NotNull
    public Category classify(@NotNull final String text) {
        final DenseInstance instance = new DenseInstance(2);
        final Instances instances = data.stringFreeStructure();
        final Attribute textAttribute = instances.attribute("text");
        instance.setValue(textAttribute, textAttribute.addStringValue(text));
        instance.setDataset(instances);
        try {
            filter.input(instance);
            return categories.get((int) classifier.classifyInstance(filter.output()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private List<Good> read() throws IOException {
        final List<Good> goods = new ArrayList<>();
        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(GoodsClassifier.class.getResourceAsStream("/tomall/goods.txt"))
        );
        String line;
        while ((line = reader.readLine()) != null) {
            final String[] parts = line.split("\t");
            final Category category = Category.parse(parts[2]);
            assert category != null;
            goods.add(new Good(Integer.parseInt(parts[0]), parts[1], category));
        }
        return goods;
    }

    public static void main(String[] args) throws Exception {
        final GoodsClassifier classifier = new GoodsClassifier();
        final Scanner cin = new Scanner(System.in);
        System.out.println("You are welcome!");
        while (true) {
            String text = cin.nextLine();
            System.out.println(classifier.classify(text).getName());
        }
    }
}
