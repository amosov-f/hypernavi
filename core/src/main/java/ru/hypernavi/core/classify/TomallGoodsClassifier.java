package ru.hypernavi.core.classify;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.Category;
import ru.hypernavi.core.Good;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * User: amosov-f
 * Date: 23.11.14
 * Time: 15:13
 */
public final class TomallGoodsClassifier implements GoodsClassifier {
    private static final Logger LOG = Logger.getLogger(TomallGoodsClassifier.class.getName());

    @NotNull
    private final Instances data;
    @NotNull
    private final Classifier classifier;
    @NotNull
    private final List<Category> categories;
    @NotNull
    private final StringToWordVector filter;

    public TomallGoodsClassifier() throws Exception {
        this(false);
    }

    public TomallGoodsClassifier(final boolean validate) throws Exception {
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
//        filter.setDoNotOperateOnPerClassBasis(true);
//        filter.setTFTransform(true);
        filter.setLowerCaseTokens(true);
//        filter.setStemmer(new SnowballStemmer("russian"));

        final Instances filteredData = Filter.useFilter(data, filter);
        final List<Attribute> filteredAttributes = Collections.list(filteredData.enumerateAttributes());
        filteredAttributes.stream().map(Attribute::name).forEach(LOG::fine);
        LOG.fine(String.valueOf(filteredAttributes.size()));

        final Instances learn = filteredData.trainCV(10, 0);
        final Instances test = filteredData.testCV(10, 0);

        classifier = new SMO();
        classifier.buildClassifier(filteredData);

        if (validate) {
            Evaluation evaluation = new Evaluation(learn);
            evaluation.evaluateModel(classifier, test);

            LOG.info(evaluation.toSummaryString());
            LOG.info(evaluation.toMatrixString());
            LOG.info(evaluation.toClassDetailsString());
        }
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
    private static List<Good> read() throws IOException {
        return IOUtils.readLines(TomallGoodsClassifier.class.getResourceAsStream("/tomall/goods.txt")).stream()
                .map(line -> line.split("\t"))
                .map(parts -> new Good(Integer.parseInt(parts[0]), parts[1], Objects.requireNonNull(Category.parse(parts[2]))))
                .collect(Collectors.toList());
    }

    public static void main(@NotNull final String[] args) throws Exception {
        final TomallGoodsClassifier classifier = new TomallGoodsClassifier(true);
        final Scanner cin = new Scanner(System.in);
        LOG.info("You are welcome!");
        while (true) {
            String text = cin.nextLine();
            LOG.info(classifier.classify(text).getName());
        }
    }
}
