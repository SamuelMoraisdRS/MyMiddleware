package ufrn.pd.business;
import ufrn.pd.mymiddleware.annotation.PathParam;
import ufrn.pd.mymiddleware.annotation.method.endpoint.Get;
import ufrn.pd.mymiddleware.annotation.method.endpoint.Post;
import ufrn.pd.mymiddleware.annotation.type.LifeCycle;
import ufrn.pd.mymiddleware.annotation.type.RequestMapping;
import ufrn.pd.mymiddleware.lifecyclemanager.Acquisition;
import ufrn.pd.mymiddleware.lifecyclemanager.Scope;

import java.io.*;
import java.util.*;

@LifeCycle(scope = Scope.PER_REQUEST, acquisition = Acquisition.LAZY)
@RequestMapping(route = "/bm25")
public class BM25 {
    private final List<String> corpus = Arrays.asList(
            "Java's high-performance JVM is often the backbone for distributed AI inference services.",
            "Building resilient microservices in Java is foundational for modern distributed AI applications.",
            "The Java ecosystem offers robust libraries for inter-service communication in distributed systems.",
            "Leveraging Java concurrency to handle asynchronous data streams in distributed machine learning pipelines.",
            "Distributed systems necessitate efficient Java serialization for moving AI model parameters across nodes.",
            "AI-driven resource allocation within a distributed Java application cluster.",
            "Implementing consensus algorithms in Java to ensure data consistency across a distributed AI knowledge base.",
            "Java frameworks like Spring Boot simplify the deployment of AI models as distributed RESTful services.",
            "The challenge of distributed tracing in a Java microservices architecture hosting various AI components.",
            "Scaling Java-based message queues to support high-throughput communication between distributed AI agents.",
            "AI algorithms are increasingly used to optimize network topology in distributed Java environments.",
            "Java's platform independence aids in deploying distributed AI solutions across heterogeneous cloud infrastructure.",
            "Monitoring the health and performance of a distributed system using Java telemetry and AI anomaly detection.",
            "Integrating Java big data tools like Apache Spark for distributed processing of AI training datasets.",
            "Distributed ledger technology, often implemented in Java, can provide an immutable audit trail for AI decisions.",
            "The synergy between Java's strong typing and the complexity of distributed AI data structures.",
            "Orchestrating containerized Java services that form the core of a distributed deep learning platform.",
            "Designing fault-tolerant Java components for a distributed system where AI models must remain constantly available.",
            "Using Java to develop the API gateway that routes requests to various distributed AI prediction services.",
            "The future of enterprise AI depends on reliable, distributed systems built on languages like Java."
    );
    private Map<String, Double> scores = new HashMap<>();
    private List<Map.Entry<Integer, Double>> sortedScores;
    private Map<String, Integer> df = new HashMap<>();
    private double avgDocLength = 0;
    private volatile double k1 = 1.5;
    private volatile double b = 0.75;

    public BM25()  {
        try {
            this.loadDocuments();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadDocuments() throws IOException {
        int totalLength = 0;

        System.out.println("Leitura Completa.");

        for (String document : corpus) {
            String[] terms = document.split("\\s+");

            totalLength += terms.length;

            // Atualizar document frequency apenas com os termos únicos
            Set<String> uniqueTerms = new HashSet<>();
            for (String term : terms) {
                term = term.intern(); // Reduz memória duplicada
                uniqueTerms.add(term);
            }
            for (String term : uniqueTerms) {
                df.put(term, df.getOrDefault(term, 0) + 1);
            }
            terms = null;
            uniqueTerms = null;
        }
        if (!corpus.isEmpty()) {
            avgDocLength = (double) totalLength / corpus.size();
        }

        System.out.println("Processamento do corpus finalizada.");

    }

    // Checa o score de um documento em relação a query string
    public double score(String[] doc, String term) {
        int freq = 0;
        for (String word : doc) {
            if (word.equals(term))
                freq++;
        }

        if (freq == 0) {
            return 0;
        }

        int N = corpus.size();
        int df_t = df.getOrDefault(term, 0);
        double idf = Math.log(1 + (N - df_t + 0.5) / (df_t + 0.5));

        double docLength = doc.length;
        double norm = freq * (k1 + 1) / (freq + k1 * (1 - b + b * (docLength / avgDocLength)));

        return idf * norm;
    }

    @Get(route = "/search")
    public String search(@PathParam(name = "query") String query) {
        String[] queryTerms = query.toLowerCase().split("\\s+");
        int docId = 0;
        for (String document : corpus) {
            double totalScore = 0.;
            String[] doc = document.toLowerCase().split("\\s+");
            for (int j = 0; j < queryTerms.length; j++) {
                totalScore += score(doc, queryTerms[j]);
            }
            docId++;
            scores.put(document, totalScore);
        }

        System.out.println("Busca pelo corpus finalizada.");
        var entry = scores.entrySet().stream().max((e1, e2) -> Double.compare(e1.getValue(), e2.getValue())).get();
        return String.format("Documento com maior score : %s - %.2f", entry.getKey(), entry.getValue());
    }

    @Get(route = "/properties")
    public String getModelProperties() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format("Corpus Size : %d%n", this.corpus.size()));
        buffer.append(String.format("K Factor : %.3f%n", this.k1));
        buffer.append(String.format("B Factor : %.3f%n", this.b));
        String mostFreqDoc = this.df.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
        buffer.append(String.format("Most Frequent Document : %s%n", mostFreqDoc));
        buffer.append(String.format("Average Document Length : %.3f%n", this.avgDocLength));
        return buffer.toString();
    }

    @Post(route = "/update")
    public String updateParams(@PathParam(name = "k") double newK, @PathParam(name = "b") double newB) {
        this.k1 = newK;
        this.b = newB;
        synchronized (this) {
            try {
                this.loadDocuments();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return String.format("Updated parameters :%n k1 = %.2f%nb = %.2f", this.k1, this.b);
    }

    public void clear() {
        this.corpus.clear();
        this.df.clear();
        this.scores.clear();
    }

    public static void main(String[] args) {
        BM25 bm25 = new BM25();

        try {
            bm25.loadDocuments();
            String query = "Machine learning is a subset of artificial inteligence. Its models can be applied to databases";
            long start = System.currentTimeMillis();
            bm25.search(query);
            long end = System.currentTimeMillis();
            System.out.println("tempo do search serial:" + (end - start));
            System.out.println("\nEscrevendo resultados");
            // bm25.write();

        } catch (Exception e) {
            System.out.println("Erro ao processar documentos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
