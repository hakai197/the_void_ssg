package com.thevoid.ssg.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "void")
public class EldritchProperties {

    private Output output = new Output();
    private Entropy entropy = new Entropy();
    private Entities entities = new Entities();
    private Ollama ollama = new Ollama();

    public static class Output {
        private String basePath = "./void-sites";

        public String getBasePath() { return basePath; }
        public void setBasePath(String basePath) { this.basePath = basePath; }
    }

    public static class Entropy {
        private int maxIntensity = 100;
        private List<String> symbols = List.of("⛧", "☠", "☥", "⛥", "⛤", "⚸");

        public int getMaxIntensity() { return maxIntensity; }
        public void setMaxIntensity(int maxIntensity) { this.maxIntensity = maxIntensity; }
        public List<String> getSymbols() { return symbols; }
        public void setSymbols(List<String> symbols) { this.symbols = symbols; }
    }

    public static class Entities {
        private boolean enabled = true;
        private boolean warnOnDetection = true;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public boolean isWarnOnDetection() { return warnOnDetection; }
        public void setWarnOnDetection(boolean warnOnDetection) { this.warnOnDetection = warnOnDetection; }
    }

    public static class Ollama {
        private String baseUrl = "http://localhost:11434";
        private String model = "llama3.1:8b";
        private double temperature = 1.5;
        private int maxTokens = 4096;

        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    }

    public Output getOutput() { return output; }
    public void setOutput(Output output) { this.output = output; }
    public Entropy getEntropy() { return entropy; }
    public void setEntropy(Entropy entropy) { this.entropy = entropy; }
    public Entities getEntities() { return entities; }
    public void setEntities(Entities entities) { this.entities = entities; }
    public Ollama getOllama() { return ollama; }
    public void setOllama(Ollama ollama) { this.ollama = ollama; }
}